/**
 *
 * This file is part of the https://github.com/BITPlan/can4eve open source project
 *
 * Copyright 2017 BITPlan GmbH https://github.com/BITPlan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *  You may obtain a copy of the License at
 *
 *  http:www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bitplan.obdii;

import java.io.File;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import com.bitplan.can4eve.CANInfo;
import com.bitplan.can4eve.CANValue;
import com.bitplan.can4eve.CANValue.CANRawValue;
import com.bitplan.can4eve.ErrorHandler;
import com.bitplan.can4eve.Pid;
import com.bitplan.can4eve.Vehicle;
import com.bitplan.can4eve.VehicleGroup;
import com.bitplan.can4eve.gui.javafx.CANProperty;
import com.bitplan.can4eve.gui.javafx.CANPropertyManager;
import com.bitplan.elm327.Connection;
import com.bitplan.obdii.elm327.ELM327;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

/**
 * OBD Handler
 * 
 * @author wf
 *
 */
public abstract class OBDHandler extends AbstractOBDHandler {
  protected CANPropertyManager cpm;
  public static boolean withRawValues = false;
  protected Integer mmPerRound = 261; // FIXME do we need a default here?
  protected boolean monitoring;
  protected ScheduledExecutorService displayexecutor;
  protected Runnable displayTask;
  protected SimpleLongProperty msecsRunningProperty;
  protected SimpleObjectProperty<Vehicle.State> vehicleStateProperty;
  public Date displayStart;

  public Integer getMmPerRound() {
    return mmPerRound;
  }

  public void setMmPerRound(Integer mmPerRound) {
    this.mmPerRound = mmPerRound;
  }

  public boolean isMonitoring() {
    return monitoring;
  }

  public void setMonitoring(boolean monitoring) {
    this.monitoring = monitoring;
  }

  public OBDHandler(VehicleGroup vehicleGroup) {
    super(vehicleGroup);
  }

  public OBDHandler(VehicleGroup vehicleGroup, String device, int baudRate) {
    super(vehicleGroup, device, baudRate);
  }

  public OBDHandler(VehicleGroup vehicleGroup, File file) {
    super(vehicleGroup, file);
  }

  public OBDHandler(VehicleGroup vehicleGroup, ELM327 elm) {
    super(vehicleGroup, elm);
  }

  public OBDHandler(VehicleGroup vehicleGroup, Socket socket, boolean debug)
      throws Exception {
    super(vehicleGroup, socket, debug);
  }

  /**
   * get the PID with the given PID id
   * 
   * @param pidId
   * @return
   * @throws Exception
   */
  public Pid pidByName(String pidId) throws Exception {
    Pid pid = getVehicleGroup().getPidByName(pidId);
    return pid;
  }

  /**
   * get the canInfo for the given CanInfo name
   * 
   * @param canInfoName
   * @return
   */
  protected CANInfo getCanInfo(String canInfoName) {
    CANInfo canInfo = this.getVehicleGroup().getCANInfoByName(canInfoName);
    return canInfo;
  }

  /**
   * delegate the initialization of the OBD device
   * 
   * @throws Exception
   */
  public void initOBD() throws Exception {
    this.getElm327().initOBD2();
  }

  /**
   * initialize the CanValues
   */
  public void initCanValues(String... canInfoNames) {
    cpm = new CANPropertyManager(getVehicleGroup());
    for (String canInfoName : canInfoNames) {
      cpm.addValue(canInfoName);
    }
  }

  /**
   * get the CANValue
   * 
   * @param canInfoName
   * @return the canValue
   */
  public <CT extends CANValue<T>, T> CT getValue(String canInfoName) {
    CANProperty<CT, T> property = cpm.get(canInfoName);
    if (property == null)
      throw new RuntimeException("invalid canInfoName " + canInfoName);
    return property.getCanValue();
  }

  /**
   * get the CANValues
   * 
   * @return - the array of CAN Values in order of appearance
   */
  public List<CANValue<?>> getCANValues() {
    if (canValues == null) {
      // start the canValues with the top list
      canValues = cpm.getCANValues();
      if (withRawValues) {
        // TODO check handling of raw values
        // create a map of these already added values
        HashMap<Pid, CANValue<?>> canValueMap = new HashMap<Pid, CANValue<?>>();
        for (CANValue<?> canValue : canValues) {
          CANInfo canInfo = canValue.canInfo;
          Pid pid = canInfo.getPid();
          canValueMap.put(pid, canValue);
        } // for
        // now add all raw values
        for (Pid pid : getVehicleGroup().getPids()) {
          if (pid.getIsoTp() == null) {
            CANRawValue canRawValue = this.getCanRawValues().get(pid.getPid());
            if (canRawValue == null)
              throw new RuntimeException(
                  "this can't happen - no pid raw value for PID "
                      + pid.getPid());
            // if (!canValueMap.containsKey(pid)) {
            canValues.add(canRawValue);
            canValueMap.put(pid, canRawValue);
          }
          // } // if
        } // for
      }
      for (CANValue<?> canValue : canValues) {
        canValue.activate();
      } // for
    } // if
    return canValues;
  }

  /**
   * set the ELM327 to filter the given canValues in preparation of an AT STM
   * command
   * 
   * @param canValues
   * @throws Exception
   */
  public void setSTMFilter(List<CANValue<?>> canValues) throws Exception {
    ELM327 lelm = this.getElm327();
    Set<String> pidFilter = new HashSet<String>();
    for (CANValue<?> canValue : canValues) {
      if (canValue.isRead()) {
        Pid pid = canValue.canInfo.getPid();
        if (pid.getIsoTp() == null) {
          pidFilter.add(pid.getPid());
        }
      }
    }
    lelm.sendCommand("STFAC", "OK"); // FIXME - not understood by ELM327 v2.1
    // device
    for (String pidId : pidFilter) {
      lelm.sendCommand("STFAP " + pidId + ",FFF", "OK");
    }
  }

  /**
   * stop the display
   */
  protected void stopDisplay() {
    if (displayexecutor != null) {
      displayexecutor.shutdown();
      displayexecutor = null;
    }
  }

  /**
   * start the OBD Pid Monitoring
   * 
   * @param canValues
   *          - the canValues to monitor
   * @param limit
   *          - the maximum number of frames to read
   * @throws Exception
   */
  public void pidMonitor(List<CANValue<?>> canValues, long frameLimit)
      throws Exception {
    ELM327 lelm = this.getElm327();
    Connection lcon = lelm.getCon();
    // VehicleGroup vg = this.getElm327().getVehicleGroup();
    // FIXME - china clone battery handling?
    // make available on button/menu?
    if (lelm.isSTN()) {
      for (CANValue<?> canValue : canValues) {
        if (canValue.isRead()) {
          Pid pid = canValue.canInfo.getPid();
          // handle ISO-TP based frames differently by direct reading
          if (pid.getIsoTp() != null)
            this.readPid(pid);
        }
      }
    }
    this.initOBD();
    if (lelm.isSTN()) {
      this.setSTMFilter(canValues);
      lcon.output("STM");
      setMonitoring(true);
      for (long i = 0; i < frameLimit && isMonitoring(); i++) {
        lcon.getResponse(null);
      }
    } else {
      if (debug)
        LOGGER.log(Level.INFO, "super slow China clone loop entered");
      // oh the China loop ... how ugly and slow ...
      if (debug)
        LOGGER.log(Level.INFO,
            String.format("%3d PIDs to loop thru", canValues.size()));
      setMonitoring(true);
      for (long frameIndex = 0; frameIndex < frameLimit
          && isMonitoring();) {
        for (CANValue<?> canValue : canValues) {
          if (canValue.isRead()) {
            Pid pid = canValue.canInfo.getPid();
            // handle ISO-TP based frames differently by direct reading
            if (pid.getIsoTp() == null) {
              if (debug) {
                LOGGER.log(Level.INFO, String.format("pid %s (%s) ",
                    pid.getPid(), canValue.canInfo.getTitle()));
              }
              int pidFrameLimit=canValue.canInfo.getMaxIndex()+5;
              super.monitorPid(pid.getPid(), pidFrameLimit);
              frameIndex+=pidFrameLimit;
            } // if not ISO-TP
          } // is isREAD
        } // for canValues
      } // for frames
    } // non STN
    if (debug) {
      LOGGER.log(Level.INFO, "PidMonitoring finished");
    }
  }

  /**
   * start the display
   * 
   * @param display
   * @msecs - the update frequency
   */
  public void startDisplay(final CANValueDisplay display, int msecs) {
    if (displayexecutor != null) {
      throw new IllegalStateException("display already started!");
    }
    // TODO make this more systematic
    if (display instanceof JFXTripletDisplay) {
      Map<String, ObservableValue<?>> canBindings = new HashMap<String, ObservableValue<?>>();
      // fixed bindings
      canBindings.put("msecs", this.msecsRunningProperty);
      canBindings.put("vehicleState", this.vehicleStateProperty);
      // property based bindings
      for (CANProperty<?, ?> canProperty : cpm.getCanProperties().values()) {
        String name = canProperty.getName();
        if (debug)
          LOGGER.log(Level.INFO, "binding " + name);
        canBindings.put(name, canProperty.getProperty());
        canBindings.put(name + "-max", canProperty.getMax());
        canBindings.put(name + "-avg", canProperty.getAvg());
      }
      ((JFXTripletDisplay) display).bind(canBindings);
    }
    displayexecutor = Executors.newSingleThreadScheduledExecutor();
    displayStart = new Date();
    displayTask = new Runnable() {
      public void run() {
        // Invoke method(s) to do the work
        try {
          showValues(display);
        } catch (Exception e) {
          ErrorHandler.handle(e);
        }
      }
    };
    displayexecutor.scheduleAtFixedRate(displayTask, 0, msecs,
        TimeUnit.MILLISECONDS);
  }

}

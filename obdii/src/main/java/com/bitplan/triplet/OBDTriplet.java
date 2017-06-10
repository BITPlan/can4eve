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
package com.bitplan.triplet;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import javax.swing.SwingUtilities;

import com.bitplan.obdii.CANCellStatePlot;
import com.bitplan.can4eve.CANInfo;
import com.bitplan.can4eve.CANValue;
import com.bitplan.can4eve.Pid;
import com.bitplan.can4eve.VehicleGroup;
import com.bitplan.can4eve.CANValue.*;
import com.bitplan.obdii.CANValueDisplay;
import com.bitplan.obdii.CANValueHistoryPlot;
import com.bitplan.obdii.ErrorHandler;
import com.bitplan.obdii.OBDHandler;
import com.bitplan.obdii.PIDResponse;
import com.bitplan.obdii.TripletDisplay;

/**
 * Handles OBD II communication for the Triplet cars Mitsubishi i-Miev, Peugeot
 * Ion and Citroen C-Zero
 * 
 * @author wf
 *
 */
public class OBDTriplet extends OBDHandler {

  // car parameters
  DoubleValue accelerator;
  BooleanValue blinkerLeft;
  BooleanValue blinkerRight;
  BooleanValue doorOpen;
  BooleanValue parkingLight;
  BooleanValue headLight;
  BooleanValue highBeam;

  DoubleValue breakPedal;
  BooleanValue breakPressed;
  DoubleValue cellTemperature;
  DoubleValue cellVoltage;
  IntegerValue chargertemp;
  public IntegerValue range;
  public IntegerValue odometer;
  DoubleValue tripRounds;
  DoubleValue tripOdo;
  BooleanValue key;
  IntegerValue speed;
  DoubleValue rpmSpeed;
  IntegerValue motortemp;
  IntegerValue rpm;
  public DoubleValue SOC;
  IntegerValue cellCount;
  public VINValue VIN;
  VINValue VIN2;
  DoubleValue acamps;
  DoubleValue acvolts;
  DoubleValue dcamps;
  DoubleValue dcvolts;
  ClimateValue climateValue;
  StringValue ventDirection;
  public DoubleValue steeringWheelPosition;
  public DoubleValue steeringWheelMovement;
  ShifterPositionValue shifterPositionValue;
  // meter per Round // FIXME - is vehicle dependen and needs to be configured
  private double kmPerRound = 0.261 / 1000.0;

  public boolean isWithHistory() {
    return withHistory;
  }

  public void setWithHistory(boolean withHistory) {
    this.withHistory = withHistory;
  }

  /**
   * construct me
   */
  public OBDTriplet(VehicleGroup vehicleGroup) {
    super(vehicleGroup);
    postConstruct();
  }

  /**
   * construct me
   * 
   * @param vehicleGroup
   * @param file
   */
  public OBDTriplet(VehicleGroup vehicleGroup, File file) {
    super(vehicleGroup, file);
    postConstruct();
  }

  /**
   * construct me
   * 
   * @param vehicleGroup
   * @param socket
   * @param debug
   * @throws IOException
   */
  public OBDTriplet(VehicleGroup vehicleGroup, Socket socket, boolean debug)
      throws IOException {
    super(vehicleGroup, socket, debug);
    postConstruct();
  }

  /**
   * construct me
   * 
   * @param vehicleGroup
   * @param socket
   * @throws IOException
   */
  public OBDTriplet(VehicleGroup vehicleGroup, Socket socket)
      throws IOException {
    this(vehicleGroup, socket, debug);
  }

  /**
   * get the CANInfo for the given canInfo Name
   * 
   * @param canInfoName
   * @return
   */
  protected CANInfo getCanInfo(String canInfoName) {
    CANInfo canInfo = this.getElm327().getVehicleGroup()
        .getCANInfoByName(canInfoName);
    return canInfo;
  }

  public void initCanValues() {
    // car parameters
    accelerator = new DoubleValue(getCanInfo("Accelerator"));
    blinkerLeft = new BooleanValue(getCanInfo("BlinkerLeft"), "◀", "");
    blinkerRight = new BooleanValue(getCanInfo("BlinkerRight"), "▶", "");
    doorOpen = new BooleanValue(getCanInfo("DoorOpen"), "●", "");
    parkingLight = new BooleanValue(getCanInfo("ParkingLight"), "●", "");
    headLight = new BooleanValue(getCanInfo("HeadLight"), "●", "");
    highBeam = new BooleanValue(getCanInfo("HighBeam"), "●", "");

    breakPedal = new DoubleValue(getCanInfo("BreakPedal"));
    breakPressed = new BooleanValue(getCanInfo("BreakPressed"), "⬇", "");
    cellTemperature = new DoubleValue(getCanInfo("CellTemperature"));
    cellVoltage = new DoubleValue(getCanInfo("CellVoltage"));
    chargertemp = new IntegerValue(getCanInfo("ChargerTemp"));
    range = new IntegerValue(getCanInfo("Range"));
    odometer = new IntegerValue(getCanInfo("Odometer"));
    tripRounds = new DoubleValue(getCanInfo("TripRounds"));
    tripOdo = new DoubleValue(getCanInfo("TripOdo"));
    key = new BooleanValue(getCanInfo("Key"), "◉✔", "❌◎");
    speed = new IntegerValue(getCanInfo("Speed"));
    rpmSpeed = new DoubleValue(getCanInfo("RPMSpeed"));
    motortemp = new IntegerValue(getCanInfo("MotorTemp"));
    rpm = new IntegerValue(getCanInfo("RPM"));
    SOC = new DoubleValue(getCanInfo("SOC"));
    cellCount = new IntegerValue(getCanInfo("CellCount"));
    VIN = new VINValue(getCanInfo("VIN"));
    VIN2 = new VINValue(getCanInfo("VIN"));
    acamps = new DoubleValue(getCanInfo("ACAmps"));
    acvolts = new DoubleValue(getCanInfo("ACVolts"));
    dcamps = new DoubleValue(getCanInfo("DCAmps"));
    dcvolts = new DoubleValue(getCanInfo("DCVolts"));
    climateValue = new ClimateValue(getCanInfo("Climate"));
    ventDirection = new StringValue(getCanInfo("VentDirection"));
    steeringWheelPosition = new DoubleValue(
        getCanInfo("SteeringWheelPosition"));
    steeringWheelMovement = new DoubleValue(
        getCanInfo("SteeringWheelMovement"));
    shifterPositionValue = new ShifterPositionValue(
        getCanInfo("ShifterPosition"));
  }

  /**
   * things to do / initialize after I a was constructed
   */
  public void postConstruct() {
    initCanValues();
    // add all available PIDs to the available raw values
    for (Pid pid : getElm327().getVehicleGroup().getPids()) {
      CANInfo pidInfo=pid.getFirstInfo();
      if (debug) {
        // LOGGER.log(Level.INFO,"rawValue "+pidInfo.getPid().getPid()+" added");
      }
      getCanRawValues().put(pid.getPid(), new CANRawValue(pidInfo));
    }
    VIN.activate();
  }

  /**
   * callback for responses
   * 
   * @param response
   *          - the reponse to handle
   * @param timeStamp
   *          - the datetime of the response
   */
  public void handleResponse(PIDResponse pr, Date timeStamp) {
    switch (pr.pid.getName()) {
    case "Accelerator":
      accelerator.setValue(pr.d[2] / 250.0 * 100, timeStamp);
      break;
    case "AmpsVolts":
      dcamps.setValue(((pr.d[2] * 256 + pr.d[3]) - 128 * 256) / 100.0,
          timeStamp);
      dcvolts.setValue((pr.d[4] * 256 + pr.d[5]) / 10.0, timeStamp);
      break;
    case "ACAmpsVolts":
      acvolts.setValue(pr.d[1] * 1.0, timeStamp);
      acamps.setValue(pr.d[6] / 10.0, timeStamp);
      break;
    case "BreakPedal":
      // TODO 6F FF FF FF FF FF
      breakPedal.setValue(((pr.d[2] * 256 + pr.d[3]) - 24576.0) / 640 * 100.0,
          timeStamp);
      break;
    case "BreakPressed":
      breakPressed.setValue(pr.d[4] == 2, timeStamp);
      break;
    case "ChargerTemp":
      chargertemp.setValue(pr.d[3] - 40, timeStamp);
      break;
    case "CellInfo1":
    case "CellInfo2":
    case "CellInfo3":
    case "CellInfo4":
      Pid cellInfo1 = this.getElm327().getVehicleGroup()
          .getPidByName("CellInfo1");
      int pidindex = pr.pidHex - PIDResponse.hex2decimal(cellInfo1.getPid());
      // cell monitoring unit index
      int cmu_id = pr.d[0]; // 1-12
      double temp1 = pr.d[1] - 50;
      double temp2 = pr.d[2] - 50;
      double temp3 = pr.d[3] - 50;
      double voltage1 = (pr.d[4] * 256 + pr.d[5]) / 100.0;
      double voltage2 = (pr.d[6] * 256 + pr.d[7]) / 100.0;
      int index = pidindex * 2 + (cmu_id - 1) * 8;
      // ignore voltages for cmu id 6 and 12 on 6E3 and 6E4
      boolean voltageIgnore = (pidindex == 2 || pidindex == 3)
          && (cmu_id == 6 || cmu_id == 12);
      if (index < this.cellVoltage.canInfo.getMaxIndex()) {
        if (!voltageIgnore) {
          this.cellVoltage.setValue(index, voltage1, timeStamp);
          this.cellVoltage.setValue(index + 1, voltage2, timeStamp);
        }
      }
      if (index < this.cellTemperature.canInfo.getMaxIndex()) {
        switch (pr.pid.getName()) {
        case "CellInfo1":
          this.cellTemperature.setValue(index, temp2, timeStamp);
          this.cellTemperature.setValue(index + 1, temp3, timeStamp);
          break;
        case "CellInfo2":
          this.cellTemperature.setValue(index, temp1, timeStamp);
          if (cmu_id != 6 && cmu_id != 12)
            this.cellTemperature.setValue(index + 1, temp2, timeStamp);
          break;
        case "CellInfo3":
          if (cmu_id != 6 && cmu_id != 12) {
            this.cellTemperature.setValue(index, temp1, timeStamp);
            this.cellTemperature.setValue(index + 1, temp2, timeStamp);
          }
        default:
          // ignore
        }
      }
      break;
    case "Climate":
      Climate climate = new Climate();
      climate.setClimate(pr.d[0], pr.d[1]);
      /**
       * http://myimiev.com/forum/viewtopic.php?p=31226 PID 3A4 byte 0, bits
       * 0-3: heating level (7 is off, under 7 is cooling, over 7 is heating)
       * byte 0, bit 7: AC on (ventilation dial pressed) byte 0, bit 5: MAX
       * heating (heating dial pressed) byte 0, bit 6: air recirculation
       * (ventilation direction dial pressed)
       * 
       * byte 1, bits 0-3: ventilation level (if AUTO is chosen, the
       * automatically calculated level is returned) byte 1, bits 4-7:
       * ventilation direction (1-2 face, 3 legs+face, 4 -5legs, 6
       * legs+windshield 7-9 windshield)
       */
      int ventDirVal = (pr.d[1] & 0xf0) >> 4;
      String ventDir = "?";
      switch (ventDirVal) {
      case 1:
      case 2:
        ventDir = "face";
        break;
      case 3:
      case 4:
        ventDir = "legs+face";
        break;
      case 5:
      case 6:
        ventDir = "legs";
        break;
      case 7:
      case 8:
        ventDir = "legs+windshield";
        break;
      case 9:
        ventDir = "windshield";
        break;
      }
      ventDirection.setValue(String.format("%s(%d)", ventDir, ventDirVal),
          timeStamp);
      climateValue.setValue(climate, timeStamp);
      break;
    case "Key":
      int keyVal = pr.d[0];
      key.setValue(keyVal == 4, timeStamp);
      break;
    case "Lights":
      int lightNum = pr.d[0] * 256 + pr.d[1];
      int ilightNum = pr.d[2];
      this.doorOpen.setValue((ilightNum & 0x01) != 0, timeStamp);
      this.blinkerRight.setValue((lightNum & 0x01) != 0, timeStamp);
      this.blinkerLeft.setValue((lightNum & 0x02) != 0, timeStamp);
      this.highBeam.setValue((lightNum & 0x04) != 0, timeStamp);
      this.headLight.setValue((lightNum & 0x20) != 0, timeStamp);
      this.parkingLight.setValue((lightNum & 0x40) != 0, timeStamp);
      break;
    case "MotorTemp_RPM":
      motortemp.setValue(pr.d[3] - 40, timeStamp);
      // fetch teh rounds per minute
      int rpmValue = (pr.d[6] * 256 + pr.d[7]) - 10000;
      // if we have a previous value we can start integrating
      if (rpm.getValueItem().isAvailable()) {
        // calc numerical integral - how many rounds total on this trip?
        this.tripRounds.integrate(rpm.getValueItem().getValue(),
            rpm.getValueItem().getTimeStamp(), Math.abs(rpmValue), timeStamp,
            1 / 60000.0);
        // calc distance based on rounds
        this.tripOdo.setValue(tripRounds.getValueItem().getValue() * kmPerRound,
            timeStamp);
      }
      rpm.setValue(rpmValue, timeStamp);
      if (speed.getValueItem().isAvailable()) {
        rpmSpeed.setValue(speed.getValueItem().getValue() * 1000.0 / 60
            / rpm.getValueItem().getValue(), timeStamp);
      }
      break;
    case "Odometer_Speed":
      odometer.setValue(pr.d[2] * 65536 + pr.d[3] * 256 + pr.d[4], timeStamp);
      int speedNum = pr.d[1];
      if (speedNum == 255)
        speed.setValue(null, timeStamp);
      else
        speed.setValue(speedNum, timeStamp);
      break;
    case "Range": // 0x346
      int rangeNum = pr.d[7];
      if (rangeNum == 255)
        range.setValue(null, timeStamp);
      else
        range.setValue(rangeNum, timeStamp);
      break;
    case "Steering_Wheel":
      this.steeringWheelPosition
          .setValue((pr.d[0] * 256 + pr.d[1] - 4096) / 2.0, timeStamp);
      this.steeringWheelMovement
          .setValue((pr.d[2] * 256 + pr.d[3] - 4096) / 2.0, timeStamp);
      break;
    case "ShifterPosition":
      ShifterPosition newShifterPosition = new ShifterPosition(pr.d[0]);
      shifterPositionValue.setValue(newShifterPosition, timeStamp);
      break;

    case "SOC":
      SOC.setValue(((pr.d[1]) - 10) / 2.0, timeStamp);
      break;

    case "VIN":
      int indexVal = pr.d[0];
      String partVal = pr.getString(1);
      VIN.set(indexVal, partVal, timeStamp);
      if (VIN.getValueItem().isAvailable()) {
        this.cellCount.setValue(VIN.getCellCount(), timeStamp);
      }
      break;

    case "VIN2":
      int v2indexVal = pr.d[0];
      String v2partVal = pr.getString();
      VIN2.set(v2indexVal, v2partVal, timeStamp);
      break;
    default:
      // ignore - this case is handled by the raw values below
      break;
    } // switch

    CANRawValue canRawValue = this.getCanRawValues().get(pr.pid.getPid());
    canRawValue.setRawValue(pr.getRawString(), timeStamp);
  }

  /**
   * get the CAMValues
   * 
   * @return - the array of CAN Values in order of appearance
   */
  @Override
  public List<CANValue<?>> getCANValues() {
    if (canValues == null) {
      // the top list as requested
      CANValue<?>[] top = { this.VIN, this.cellCount, this.key, this.odometer,
          this.tripOdo, this.tripRounds, this.speed, this.rpm, this.rpmSpeed,
          this.range, this.SOC, this.climateValue, this.ventDirection,
          this.acamps, this.acvolts, this.dcamps, this.dcvolts, this.motortemp,
          this.chargertemp, this.shifterPositionValue,
          this.steeringWheelPosition, this.steeringWheelMovement,
          this.accelerator, this.breakPressed, this.breakPedal,
          this.blinkerLeft, this.blinkerRight, this.doorOpen, this.parkingLight,
          this.headLight, this.highBeam, this.cellTemperature,
          this.cellVoltage };
      // start the canValues with the top list
      canValues = new ArrayList<CANValue<?>>(Arrays.asList(top));
      // TODO check handling of raw values
      // create a map of these already added values
      Map<Pid, CANValue<?>> canValueMap = new HashMap<Pid, CANValue<?>>();
      for (CANValue<?> canValue : canValues) {
        CANInfo canInfo = canValue.canInfo;
        canValueMap.put(canInfo.getPid(), canValue);
      } // for
      // now add all raw values
      for (Pid pid : this.getElm327().getVehicleGroup().getPids()) {
        CANRawValue canRawValue = this.getCanRawValues().get(pid.getPid());
        if (canRawValue == null)
          throw new RuntimeException(
              "this can't happen - no pid raw value for PID " + pid.getPid());
        // if (!canValueMap.containsKey(pid)) {
        canValues.add(canRawValue);
        canValueMap.put(pid, canRawValue);
        // } // if
      } // for
      for (CANValue<?> canValue : canValues) {
        canValue.activate();
      } // for
    } // if
    return canValues;
  }

  int dateUpdateCount = 0;
  int fpsUpdateCount = 0;
  Date latestUpdate;
  long latestTotalUpdates;
  private ScheduledExecutorService displayexecutor;
  private Runnable displayTask;
  private boolean withHistory = false;
  private Date latestHistoryUpdate;
  private Date latestCellValueUpdate;

  /**
   * show the values
   * 
   * @param display
   */
  public void showValues(final CANValueDisplay display) {
    Date now = new Date();
    String nowStr = isoDateFormatter.format(now);
    display.updateField("date", nowStr, ++dateUpdateCount);
    long totalUpdates = 0;
    for (CANValue<?> canValue : this.getCANValues()) {
      if (canValue.isDisplay()) {
        display.updateCanValueField(canValue);
        totalUpdates += canValue.getUpdateCount();
      }
    }
    if (latestUpdate == null) {
      latestUpdate = now;
      latestHistoryUpdate = now;
      latestCellValueUpdate = now;
    } else {
      // TODO configure frequency
      if (now.getTime() - latestHistoryUpdate.getTime() >= 500
          && isWithHistory()) {
        latestHistoryUpdate = showHistory(display, now);
      }
      if (now.getTime() - latestCellValueUpdate.getTime() >= 500) {
        latestCellValueUpdate = showCellValues(display, now);
      }
      long msecs = now.getTime() - latestUpdate.getTime();
      if (msecs >= 1000) {
        long updates = totalUpdates - latestTotalUpdates;
        double fps = 1000.0 * updates / msecs;
        display.updateField("fps", fps, ++fpsUpdateCount);
        latestTotalUpdates = totalUpdates;
        latestUpdate = now;
      }
    }
  }

  /**
   * show the cellValues
   * 
   * @param display
   * @param now
   * @return - the date
   */
  private Date showCellValues(CANValueDisplay display, Date now) {
    if (display instanceof TripletDisplay) {
      final TripletDisplay tripletDisplay = (TripletDisplay) display;
      String activePanelTitle = tripletDisplay.getActivePanelTitle();
      if (debug)
        LOGGER.log(Level.INFO, "active Panel is " + activePanelTitle);
      if ("cellTemperature".equals(activePanelTitle)) {
        final CANCellStatePlot cellStatePlot = new CANCellStatePlot(
            "cellTemperature", "cell", "Temperature", this.cellTemperature,
            1.0);
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            tripletDisplay.updateCellTemperature(cellStatePlot.getPanel());
          }
        });
      }
      if ("cellVoltage".equals(activePanelTitle)) {
        final CANCellStatePlot cellStatePlot = new CANCellStatePlot(
            "cellVoltage", "cell", "Voltage", this.cellVoltage, 0.01);
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            tripletDisplay.updateCellVoltage(cellStatePlot.getPanel());
          }
        });
      }
    }
    return now;
  }

  private Date showHistory(CANValueDisplay display, Date now) {
    if (display instanceof TripletDisplay) {
      final TripletDisplay tripletDisplay = (TripletDisplay) display;
      if ("history".equals(tripletDisplay.getActivePanelTitle())) {
        List<CANValue<?>> plotValues = new ArrayList<CANValue<?>>();
        plotValues.add(SOC);
        plotValues.add(range);
        final CANValueHistoryPlot valuePlot = new CANValueHistoryPlot(
            "SOC/RR over time", "time", "SOC/RR", plotValues);
        // http://stackoverflow.com/questions/218155/how-do-i-change-jpanel-inside-a-jframe-on-the-fly
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            tripletDisplay.updateHistory(valuePlot.getPanel());
          }
        });
      }
    }
    return now;
  }

  /**
   * show the given display
   * 
   * @param display
   */
  public void showDisplay(CANValueDisplay display) {
    display.addField("date", "%s", 10, 40);
    canValues = getCANValues();
    display.addCanValueFields(canValues);
    display.show();
  }

  /**
   * start the STM Monitoring
   * 
   * @param display
   *          - a display to use (if any)
   * @param canValues
   *          - the canValues to monitor
   * @param limit
   *          - the maximum number of frames to read
   * @throws Exception
   */
  public void STMMonitor(CANValueDisplay display, List<CANValue<?>> canValues,
      long frameLimit) throws Exception {
    sendCommand("STFAC", "OK"); // FIXME - not understood by ELM327 v2.1 device
    for (CANValue<?> canValue : canValues) {
      if (canValue.isRead()) {
        for (Pid pid : this.getElm327().getVehicleGroup().getPids()) {
          sendCommand("STFAP " + pid.toString() + ",FFF", "OK");
        }
      }
    }
    getElm327().output("STM");
    if (display != null)
      startDisplay(display);
    for (long i = 0; i < frameLimit; i++) {
      @SuppressWarnings("unused")
      String response = getElm327().getResponse();
    }
    if (display != null)
      stopDisplay();
  }

  /**
   * start the display
   * 
   * @param display
   */
  public void startDisplay(final CANValueDisplay display) {
    displayexecutor = Executors.newSingleThreadScheduledExecutor();
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
    // update meter value every 2 seconds
    displayexecutor.scheduleAtFixedRate(displayTask, 0, 200,
        TimeUnit.MILLISECONDS);
  }

  private void stopDisplay() {
    if (displayexecutor != null) {
      displayexecutor.shutdown();
    }

  }

}

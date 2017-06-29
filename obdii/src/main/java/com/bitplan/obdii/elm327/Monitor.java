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
package com.bitplan.obdii.elm327;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bitplan.can4eve.ErrorHandler;
import com.bitplan.can4eve.Pid;
import com.bitplan.elm327.Connection;
import com.bitplan.elm327.Packet;

/**
 * simulates MA / Monitor All command
 * 
 * @author wf
 *
 */
public class Monitor extends Thread {
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.obdii");

  public static boolean debug = false;
  private String pidFilter = null;
  private ELM327 elm;
  public static int freq = 500;
  boolean running = false;

  private boolean header;
  private boolean length;
  int vinLoop = 0; // for vin
  int vin2Loop = 0; // for vin2
  int cycle = 0;

  private List<Pid> pids;

  private Connection con;

  private File elmLogFile;

  private RandomAccessLogReader logReader;

  /**
   * create a Monitor
   * 
   * @param elm
   * @param pidFilter
   * @param header
   * @param length
   */
  public Monitor(ELM327 elm, String pidFilter, boolean header, boolean length) {
    this(elm, header, length, null);
    this.pidFilter = pidFilter;
  }

  /**
   * construct me
   * 
   * @param elm
   * @param header
   * @param length
   * @param elmLogFile
   */
  public Monitor(ELM327 elm, boolean header, boolean length, File elmLogFile) {
    this.elm = elm;
    this.con = elm.getCon();
    this.header = header;
    this.length = length;
    this.pids = elm.getVehicleGroup().getPids();
    this.elmLogFile = elmLogFile;
    if (debug)
      LOGGER.log(Level.INFO, "Monitor created for " + pids.size() + " Pids");
  }

  /**
   * get the pidFilter
   * 
   * @return
   */
  public String getPidFilter() {
    if (pidFilter != null) {
      return pidFilter;
    } else {
      int index = cycle % pids.size();
      Pid pid = pids.get(index);
      return pid.getPid();
    }
  }

  /**
   * get the VIN Sample for the given index
   * 
   * @param index
   * @return the VIN Sample
   */
  public String getVinSample(int index) {
    String sample = "?";
    switch (index) {
    case 0:
      sample = "00 56 46 33 31 4E 5A 4B";
      break;
    case 1:
      sample = "01 59 5A 48 55 39 30 30";
      break;
    case 2:
      sample = "02 37 36 39 FF FF FF FF";
      break;
    }
    return sample;
  }

  /**
   * get a sample
   * 
   * @return
   */
  public String getSample(String lPidFilter) {
    String sample = null;
    Pid pid = this.elm.getVehicleGroup().getPidById(lPidFilter);
    if (debug)
      LOGGER.log(Level.INFO, "sample pid " + lPidFilter);
    if (pid == null) {
      LOGGER.log(Level.INFO, "unknown pid " + lPidFilter);
      return null;
    }
    switch (pid.getName()) {
    case "AmpsVolts":
      sample = "BF BF 7F BA 0C 88 00 04";
      break;
    case "Climate":
      sample = "07 20 6E 87 60 50 00 67";
      break;
    case "MotorTemp_RPM":
      sample = "31 33 3C 32 33 00 27 10";
      break;
    case "Odometer_Speed":
      sample = "FE 00 00 02 D1 00 21 12";
      break;
    case "Key":
      sample = "04";
      break;
    case "Range":
      sample = "27 10 64 40 20 00 00 5F";
      break;
    case "ShifterPosition":
      sample = "50 00 00 06 00 00 00";
      break;
    case "SOC":
      sample = "D7 D2 10 FE 3C 39 5A 14";
      break;
    case "Steering_Wheel":
      sample = "0F ED 10 05 A0 00 00 0A";
      break;
    case "VIN":
      sample = getVinSample(vinLoop++ % 3);
      break;
    case "VIN2":
      sample = getVinSample(vin2Loop++ % 3);
      break;
    default:
      // ignore
    }
    return sample;
  }

  @Override
  public void run() {
    running = true;
    if (pidFilter != null)
      if (debug)
        LOGGER.log(Level.INFO, "monitoring " + pidFilter);
    if (elmLogFile != null) {
      try {
        logReader = new RandomAccessLogReader(elmLogFile);
        logReader.open();
      } catch (Exception e) {
        ErrorHandler.handle(e);
      }
    }
    while (running) {
      try {
        String sample = null;
        String lPidFilter = getPidFilter();
        if (elmLogFile == null) {
          sample = getSample(lPidFilter);
          if (sample != null) {
            if (length) {
              int len = (sample.length() + 1) / 3;
              sample = "" + len + " " + sample;
            }
            if (header)
              sample = lPidFilter + " " + sample;
          }
        } else {
          try {
            Packet p = logReader.nextPacket();
            sample = p.getData();
          } catch (Exception e) {
            ErrorHandler.handle(e);
          }
        }
        if (sample != null) {
          if (debug)
            LOGGER.log(Level.INFO, "sample: " + sample);
          con.output(sample);
        }
      } catch (IOException e) {
        LOGGER.log(Level.WARNING, "monitor output issue " + e.getMessage());
      }
      int pausemsecs = 1000 / freq;
      for (int t = 0; t <= (pausemsecs); t++) {
        con.pause(1, 0);
        if (!running)
          break;
      }
      cycle++;
    }
  }

  public void halt() {
    running = false;
  }

}

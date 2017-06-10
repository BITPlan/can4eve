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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import com.bitplan.can4eve.CANValue;
import com.bitplan.can4eve.VehicleGroup;

/**
 * simulates an ELM327
 * 
 * @author wf
 *
 */
public class ELM327SimulatorConnection extends ELM327 {

  public int delay = 0; // 0,1 millisecs delay
  public int delaynano=100; 
  private String filter;
  List<Monitor> monitors = new ArrayList<Monitor>();
  private String canprotcode;
  private String canprot;
  private String ecu;
  
  /**
   * constructor
   */
  public ELM327SimulatorConnection(VehicleGroup vehicleGroup) {
    super(vehicleGroup);
    handleResponses = true;
  }
  
  @Override
  public void setHandleResponses(boolean handleResponses) {
    // ignore trying to set e.g. to false
  }

  /**
   * run
   */
  public void run() {
    super.run();
  }

  /**
   * handle the responses
   */
  @Override
  public void handleStringResponse(String response, Date timeStamp) {
    // echo
    super.handleStringResponse(response, timeStamp);
    // ignore null response
    if (response == null) {
      log(" received null response");
      return;
    }
    String command = response.toUpperCase().trim().replace(" ","");
    log(" received command " + command);
    try {
      if (command.startsWith("AT")) {
        command = command.substring(2).trim();
        stopMonitors();
        if (command.equals("I")) {
          outputWithPrompt("ELM327 v1.3a");
        } else if (command.equals("@1")) {
          outputWithPrompt("SCANTOOL.NET LLC");
        } else if (command.equals("D")) {
          log("Setting Defaults");
          outputWithPrompt("OK");
        } else if (command.equals("Z")) {
          log("Resetting OBD");
          filter = null;
          ecu=null;
          outputWithPrompt("OK");
        } else if (command.startsWith("L")) {
          String option = command.substring(1).trim();
          log("Set Linefeed handling to " + option);
          if (option.startsWith("1")) {
            sendLineFeed = true;
          } else if (option.startsWith("0")) {
            sendLineFeed = false;
          }
          outputWithPrompt("OK");
        } else if (command.startsWith("H")) {
          String option = command.substring(1).trim();
          log("Set header handling to " + option);
          if (option.startsWith("1")) {
            header = true;
          } else if (option.startsWith("0")) {
            header = false;
          }
          outputWithPrompt("OK");
        } else if (command.equals("RV")) { 
          outputWithPrompt("14.5V");
        } else if (command.equals("D1") || command.equals("D0")) {
          String option = command.substring(1).trim();
          log("Set length handling to " + option);
          if (option.startsWith("1")) {
            length = true;
          } else if (option.startsWith("0")) {
            length = false;
          }
          outputWithPrompt("OK");
        } else if (command.equals("E1") || command.equals("E0")) {
          String option = command.substring(1).trim();
          log("Set echo handling to " + option);
          if (option.startsWith("1")) {
            echo = true;
          } else if (option.startsWith("0")) {
            echo = false;
          }
          outputWithPrompt("OK");
        } else if (command.startsWith("CAF")) {
          String option = command.substring(3).trim();
          log("Setting automatic formatting to " + option);
          // TODO implement non formatted mode e.g. with simulated DATA errors
          outputWithPrompt("OK");
        } else if (command.startsWith("FCSD")) {
          outputWithPrompt("OK");
        } else if (command.startsWith("FCSM1")) {
          outputWithPrompt("OK");
        } else if (command.startsWith("FCSH")) {
          // ECU selection
          ecu=command.substring(4);
          log("fcsh selected ecu="+ecu);
          outputWithPrompt("OK");
        } else if (command.startsWith("SH")) {
          // ECU selection
          ecu=command.substring(4);
          log("sh selected ecu="+ecu);
          outputWithPrompt("OK");
        } else if (command.startsWith("CRA")) {
          filter = command.substring(3).trim();
          outputWithPrompt("OK");
        } else if (command.startsWith("MA")) {
          Monitor monitor = new Monitor(this, filter, header, length);
          monitor.start();
          monitors.add(monitor);
        } else if (command.equals("DP")) {
          log("Reporting can protocol " + canprotcode + "=" + canprot);
          outputWithPrompt(canprot);
        } else if (command.startsWith("SP")) {
          /**
           * 0 Automatic protocol detection 1 SAE J1850 PWM (41.6 kbaud) 2 SAE
           * J1850 VPW (10.4 kbaud) 3 ISO 9141-2 (5 baud init, 10.4 kbaud) 4 ISO
           * 14230-4 KWP (5 baud init, 10.4 kbaud) 5 ISO 14230-4 KWP (fast init,
           * 10.4 kbaud) 6 ISO 15765-4 CAN (11 bit ID, 500 kbaud) 7 ISO 15765-4
           * CAN (29 bit ID, 500 kbaud) 8 ISO 15765-4 CAN (11 bit ID, 250 kbaud)
           * -
           * used mainly on utility vehicles and Volvo 9 ISO 15765-4 CAN (29 bit
           * ID, 250 kbaud) - used mainly on utility vehicles and Volvo
           */
          canprotcode = command.substring(2).trim();
          canprot = canprotcode;
          if ("1".equals(canprotcode)) {
            canprot = "SAE J1850 PWM";
          } else if ("2".equals(canprotcode)) {
            canprot = "SAE J1850 VPW";
          } else if ("3".equals(canprotcode)) {
            canprot = "ISO 9141-2";
          } else if ("4".equals(canprotcode)) {
            canprot = "ISO 14230-4 (KWP 5BAUD)";
          } else if ("5".equals(canprotcode)) {
            canprot = "ISO 14230-4 (KWP FAST)";
          } else if ("6".equals(canprotcode)) {
            canprot = "ISO 15765-4 (CAN 11/500)"; // ISO 15765-4 CAN (11 bit ID,
                                                  // 500 kbaud)";
          } else if ("7".equals(canprotcode)) {
            canprot = "ISO 15765-4 (CAN 29/500)";
          } else if ("8".equals(canprotcode)) {
            canprot = "ISO 15765-4 (CAN 11/250)";
          } else if ("9".equals(canprotcode)) {
            canprot = "ISO 15765-4 (CAN 29/250)";
          } else if ("A".equals(canprotcode)) {
            canprot = "SAE J1939 (CAN 29/250)";
          }
          log("selected CAN protocol " + canprotcode + "=" + canprot);
          outputWithPrompt("OK");
        }
      } else {
        if (command.equals("")) {
          // FIXME - this e.g. restarts MA command
          outputWithPrompt("OK");
        } else if (command.equals("STFAC")) {
          outputWithPrompt("OK");
        } else if (command.equals("STI")) {
          outputWithPrompt("STN1130 v4.0.1");
        } else if (command.equals("STDI")) {
          outputWithPrompt("OBDLink SX r4.2");
        } else if (command.startsWith("STFAP")) {
          outputWithPrompt("OK");      
        } else if (command.equals("STM")) {  
          Monitor monitor = new Monitor(this, header, length);
          monitor.start();
          monitors.add(monitor);
        } else if (command.equals("2101")) {
          outputWithPrompt("OK");
          output("762 10 2E 61 01 D2 D2 01 90\n" + 
              "762 21 00 01 8F 4A 0C D0 4E\n" + 
              "75A 03 E8 03 E8 64 64 46 45\n" + 
              "762 22 02 4B 0C 01 5E 01 5D\n" + 
              "762 23 01 2C 00 FA 00 FA 10\n" + 
              "762 24 0F 0F 01 BF 01 BF 28\n" + 
              "762 25 FE 00 00 01 8F 78 7C\n" + 
              "762 26 64 00 01 00 00 00 00\n" + 
              "75A 03 E8 03 E8 64 64 46 45\n");
        } else if (command.equals("0100")) {
          outputWithPrompt("SEARCHING ...");
          pause(2000, 0);
          outputWithPrompt("UNABLE TO CONNECT");
        } else {
          LOGGER.log(Level.WARNING, "unknown command '" + command + "'");
        }
      }
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, "handleResponse failed " + e.getMessage());
      if (debug)
        e.printStackTrace();
    }
    // TODO - check if we need a pause here
    // pause(delay, delaynano);
  }

  /**
   * output the given response followed by a prompt
   * 
   * @param response
   * @throws IOException
   */
  protected void outputWithPrompt(String response) throws IOException {
    output(response);
    output("\r\n>");
  }

  /**
   * stop the monitors
   */
  protected void stopMonitors() {
    for (Monitor monitor : monitors) {
      monitor.halt();
      ;
    }
    monitors.clear();

  }

}

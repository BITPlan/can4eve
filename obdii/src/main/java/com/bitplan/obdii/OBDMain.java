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
import java.util.logging.Level;

import org.kohsuke.args4j.Option;

import com.bitplan.can4eve.VehicleGroup;
import com.bitplan.triplet.OBDTriplet;

/**
 * main class for OBD connection to CAN bus of one of the Triplet cars
 * Mitsubishi i-MIEV, Peugeot Ion, Citroen C-Zero
 * 
 * @author wf
 *
 */
public class OBDMain extends Main {
  protected static OBDMain obd;
  protected CANValueDisplay display;

  @Option(name = "--host", aliases = {
      "--hostname" }, usage = "host\nthe host to connect to")
  String hostName = "localhost";

  @Option(name = "--vg", aliases = {
      "--vehicle-group" }, usage = "vehicleGroup\nthe vehicleGroup to connect to")
  String vehicleGroupName = "triplet";

  @Option(name = "--port", aliases = {
      "--portnumber" }, usage = "port\nthe port to connect to")
  int portNumber = 7000;

  @Option(name = "--limit", aliases = {
      "--framelimit" }, usage = "limit\nthe maximum number of frames to read")
  long frameLimit = 500 * 4800; // some 1 1/2 hours at 500fps

  @Option(name = "-l", aliases = {
      "--log" }, usage = "log\nthe logfile to write")
  String logFileName;

  enum DisplayChoice {
    None, Console, Swing
  }

  @Option(name = "--display", usage = "display\nthe display to use one of:\n None,Console,Swing")
  DisplayChoice displayChoice = DisplayChoice.None;

  @Option(name = "-p", aliases = {
      "--pid" }, usage = "pid to monitor\nthe pid to monitor")
  String pid;

  @Option(name = "-t", aliases = {
      "--timeout" }, usage = "timeout in msecs\nthe timeout for elm327 communication")
  static long timeout = 250;

  @Option(name = "-c", aliases = {
      "--conn" }, usage = "connection device\nthe connection to use")
  String device;

  private OBDTriplet obdTriplet;

  private Socket elmSocket;

  public OBDMain() {
    super.name = "CANTriplet";
  }

  /**
   * initialize the monitoring
   * 
   * @throws Exception
   */
  public void doMonitorOBD() throws Exception {
    VehicleGroup vehicleGroup=VehicleGroup.get(this.vehicleGroupName);
    if (device != null) {
      if (debug)
        LOGGER.log(Level.INFO, "using device " + device);
      obdTriplet = new OBDTriplet(vehicleGroup,new File(device));
    } else {
      if (debug)
        LOGGER.log(Level.INFO,
            "using host: " + hostName + " port " + portNumber);
      elmSocket = new Socket(hostName, portNumber);
      obdTriplet = new OBDTriplet(vehicleGroup,elmSocket);
    }
    obdTriplet.setDebug(debug);
    switch (displayChoice) {
    case Swing:
      display = new TripletDisplay();
      break;
    case Console:
      display = new ConsoleDisplay();
      break;
    default:
    }
    if (display != null)
      obdTriplet.showDisplay(display);
    // obdTriplet.elm327.debug = true;
    obdTriplet.getElm327().setTimeout(timeout);
    obdTriplet.getElm327().start();
    obdTriplet.init();
    obdTriplet.initOBD();
    if (this.logFileName != null) {
      obdTriplet.logResponses(new File(logFileName), "Triplet");
    }
    if (pid != null)
      obdTriplet.monitorPid(display, pid, frameLimit);
    else {
      obdTriplet.STMMonitor(display, obdTriplet.getCANValues(), frameLimit);
    }
  }

  /**
   * work as told by command line
   * 
   * @throws Exception
   *           if a problem occurs
   */
  public void work() throws Exception {
    if (this.showVersion || this.debug)
      showVersion();
    if (this.showHelp) {
      showHelp();
    } else {
      doMonitorOBD();
    }
  }

  /**
   * main routine
   * 
   * @param args
   */
  public static void main(String[] args) {
    obd = new OBDMain();
    int result = obd.maininstance(args);
    if (!testMode)
      System.exit(result);
  }
}

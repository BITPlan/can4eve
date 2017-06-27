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
import com.bitplan.can4eve.gui.App;
import com.bitplan.can4eve.gui.Display;
import com.bitplan.can4eve.gui.swing.Translator;
import com.bitplan.elm327.Config;
import com.bitplan.elm327.Config.ConfigMode;
import com.bitplan.elm327.Config.DeviceType;
import com.bitplan.elm327.Connection;
import com.bitplan.elm327.LogImpl;
import com.bitplan.obdii.Preferences.LangChoice;
import com.bitplan.obdii.elm327.ELM327;
import com.bitplan.obdii.javafx.JavaFXDisplay;
import com.bitplan.triplet.OBDTriplet;

/**
 * main class for OBD connection to CAN bus of one of the Triplet cars
 * Mitsubishi i-MIEV, Peugeot Ion, Citroen C-Zero
 * 
 * @author wf
 *
 */
public class OBDMain extends Main implements OBDApp {
  protected static OBDMain obd;
  protected CANValueDisplay canValueDisplay;

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
  protected long frameLimit = 500 * 4800; // some 1 1/2 hours at 500fps

  @Option(name = "-l", aliases = {
      "--log" }, usage = "log\nthe logfile to write")
  String logFileName;

  enum DisplayChoice {
    None, JavaFX
  }

  @Option(name = "--display", usage = "display\nthe display to use one of:\n None,JavaFX")
  DisplayChoice displayChoice = DisplayChoice.JavaFX;

  @Option(name = "--lang", usage = "language\nthe language to use one of:\nen,de")
  LangChoice language = LangChoice.notSet;

  @Option(name = "-p", aliases = {
      "--pid" }, usage = "pid to monitor\nthe pid to monitor")
  String pid;

  @Option(name = "-r", aliases = {
      "--report" }, usage = "create a report of all pids for this vehicle group\n")
  String reportFileName;

  @Option(name = "-m", aliases = {
      "--monitor" }, usage = "automatically start monitoring\n")
  boolean monitor = false;

  @Option(name = "-t", aliases = {
      "--timeout" }, usage = "timeout in msecs\nthe timeout for elm327 communication")
  static int timeout = 250;

  @Option(name = "-c", aliases = {
      "--conn" }, usage = "connection device\nthe connection to use")
  String device;

  private OBDTriplet obdTriplet;

  private Socket elmSocket;
  private VehicleGroup vehicleGroup;
  private ELM327 elm;

  /**
   * the url for the about dialog
   */
  @Override
  public String getUrl() {
    return "http://can4eve.bitplan.com";
  };

  /**
   * construct me
   */
  public OBDMain() {
    super.name = "CANTriplet";
  }

  @Override
  public ELM327 testConnection(Config config) throws Exception {
    prepareOBD(config);
    elm.identify();
    return elm;
  }

  /**
   * prepare the OBD connection
   * 
   * @param config
   * @throws Exception
   */
  public void prepareOBD(Config config) throws Exception {
    vehicleGroup = VehicleGroup.get(this.vehicleGroupName);
    switch (config.getDeviceType()) {
    case USB:
      if (debug)
        LOGGER.log(Level.INFO, "using device " + device);
      obdTriplet = new OBDTriplet(vehicleGroup, new File(device));
      break;
    case Bluetooth:
      break;
    case Network:
      if (debug)
        LOGGER.log(Level.INFO,
            "using host: " + hostName + " port " + portNumber);
      elmSocket = new Socket(hostName, portNumber);
      obdTriplet = new OBDTriplet(vehicleGroup, elmSocket);
      break;
    case Simulator:
      break;
    default:
      break;
    }
    obdTriplet.setDebug(config.isDebug());
    // obdTriplet.elm327.debug = true;
    elm = obdTriplet.getElm327();
    Connection con = elm.getCon();
    con.setTimeout(config.getTimeout());
    if (config.isDebug()) {
      con.setLog(new LogImpl());
    }
    con.start();
    elm.initOBD2();
  }

  /**
   * initialize the monitoring
   * 
   * @throws Exception
   */
  public void doMonitorOBD() throws Exception {
    Config config = Config.getInstance(ConfigMode.Preferences);
    if (config == null) {
      config = new Config();
      if (device != null) {
        config.setDeviceType(DeviceType.USB);
        config.setSerialDevice(device);
        // FIXME - set Baud rate!
      } else {
        config.setDeviceType(DeviceType.Network);
        config.setHostname(hostName);
        config.setPort(portNumber);
      }
      config.setDebug(debug);
      config.setTimeout(timeout);
    }
    prepareOBD(config);
    if (canValueDisplay != null) {
      obdTriplet.showDisplay(canValueDisplay);
    }
    if (this.logFileName != null) {
      obdTriplet.logResponses(new File(logFileName), "Triplet");
    }
    if (this.reportFileName != null) {
      obdTriplet.report(canValueDisplay, reportFileName, frameLimit);
    } else if (pid != null)
      obdTriplet.checkPid(canValueDisplay, pid, frameLimit);
    else {
      obdTriplet.STMMonitor(canValueDisplay, obdTriplet.getCANValues(),
          frameLimit);
    }
  }

  /**
   * work as told by command line
   * 
   * @throws Exception
   *           if a problem occurs
   */
  public void work() throws Exception {
    Preferences preferences = Preferences.getInstance();
    if (this.language != LangChoice.notSet) {
      Translator.initialize(this.language.name());
    } else {
      Translator.initialize(preferences.getLanguage().name());
    }

    if (this.showVersion || this.debug)
      showVersion();
    if (this.showHelp) {
      showHelp();
    } else {
      JavaFXDisplay jfxDisplay;
      switch (displayChoice) {
      case JavaFX:
        jfxDisplay = new JFXTripletDisplay(App.getInstance(), this, this);
        canValueDisplay = jfxDisplay;
        break;
      default:
      }
      if (this.monitor) {
        doMonitorOBD();
      } else {
        // run GUI
        if (this.canValueDisplay instanceof Display) {
          Display display = (Display) canValueDisplay;
          display.show();
          display.waitOpen();
          display.waitClose();
        } else {
          showHelp();
        }
      }
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

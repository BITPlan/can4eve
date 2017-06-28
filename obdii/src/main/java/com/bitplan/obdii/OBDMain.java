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
import java.io.FileNotFoundException;
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
import com.bitplan.obdii.elm327.ElmSimulator;
import com.bitplan.obdii.javafx.JavaFXDisplay;
import com.bitplan.triplet.OBDTriplet;

import javafx.application.Platform;

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

  @Option(name = "-b", aliases = {
      "--baud" }, usage = "port\nthe baud rate for a serial connection")
  int baudRate = 38400;

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
  private Config config;

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
      if (config.getDirect()) {
        if (config.isDebug())
          LOGGER.log(Level.INFO,
              "using device (direct)" + config.getSerialDevice());
        obdTriplet = new OBDTriplet(vehicleGroup,
            new File(config.getSerialDevice()));
      } else {
        if (config.isDebug())
          LOGGER.log(Level.INFO, String.format("using device %s at %6d baud",
              config.getSerialDevice(), config.getBaudRate()));
        obdTriplet = new OBDTriplet(vehicleGroup, config.getSerialDevice(),
            config.getBaudRate());
      }
      break;
    case Bluetooth:
      break;
    case Network:
      if (config.isDebug())
        LOGGER.log(Level.INFO, String.format("using host: %s port %5d",
            config.getHostname(), config.getPort()));
      elmSocket = new Socket(config.getHostname(), config.getPort());
      obdTriplet = new OBDTriplet(vehicleGroup, elmSocket);
      break;
    case Simulator:
      if (config.isDebug())
        LOGGER.log(Level.INFO, "Using simulator on server port %5d",
            ElmSimulator.DEFAULT_PORT);
      elm = ElmSimulator.getSimulation(vehicleGroup, config.isDebug(),
          ElmSimulator.SIMULATOR_TIMEOUT);
      obdTriplet = new OBDTriplet(vehicleGroup, elm);
      break;
    default:
      break;
    }
    if (obdTriplet == null) {
      throw new Exception(Translator.translate(I18n.INVALID_CONFIGURATION));
    }
    // the simulator is pre started and timeout and debug set
    // all other devices are configured here
    if (config.getDeviceType() != DeviceType.Simulator) {
      obdTriplet.setDebug(config.isDebug());
      elm = obdTriplet.getElm327();
      Connection con = elm.getCon();
      con.setTimeout(config.getTimeout());
      if (config.isDebug()) {
        con.setLog(new LogImpl());
        // TODO preferences debug is different then connection debug!
        elm.setLog(con.getLog());
      }
      con.start();
    }
    elm.initOBD2();
  }

  @Override
  public ELM327 start() throws Exception {
    if (elm == null)
      prepareOBD(getConfig());
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
    return elm;
  }

  @Override
  public ELM327 stop() throws Exception {
    if (elm != null)
      elm.reinitCommunication(config.getTimeout());
    return elm;
  }

  /**
   * get the configuration
   * 
   * @return
   * @throws Exception
   */
  public Config getConfig() {
    if (config == null) {
      try {
        config = Config.getInstance(ConfigMode.Preferences);
      } catch (FileNotFoundException e) {
        // ignore
      }
      if (config == null) {
        config = new Config();
        if (device != null) {
          config.setDeviceType(DeviceType.USB);
          config.setSerialDevice(device);
          config.setBaudRate(baudRate);
        } else {
          config.setDeviceType(DeviceType.Network);
          config.setHostname(hostName);
          config.setPort(portNumber);
        }
        config.setDebug(debug);
        config.setTimeout(timeout);
      }
    }
    return config;
  }

  @Override
  public void setConfig(Config config) {
    this.config = config;
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
        Platform.runLater(() -> {
          try {
            start();
          } catch (Exception e) {
            ErrorHandler.handle(e);
          }
        });
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

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
import java.util.Map;
import java.util.logging.Level;

import org.kohsuke.args4j.Option;

import com.bitplan.can4eve.CANData;
import com.bitplan.can4eve.Vehicle;
import com.bitplan.can4eve.VehicleGroup;
import com.bitplan.can4eve.util.TaskLaunch;
import com.bitplan.elm327.Config;
import com.bitplan.elm327.Config.ConfigMode;
import com.bitplan.elm327.Config.DeviceType;
import com.bitplan.elm327.Connection;
import com.bitplan.elm327.LogImpl;
import com.bitplan.elm327.util.OSCheck;
import com.bitplan.gui.App;
import com.bitplan.gui.Display;
import com.bitplan.i18n.Translator;
import com.bitplan.javafx.Main;
import com.bitplan.obdii.Preferences.LangChoice;
import com.bitplan.obdii.elm327.ELM327;
import com.bitplan.obdii.elm327.ElmSimulator;
import com.bitplan.obdii.elm327.LogPlayer;
import com.bitplan.obdii.elm327.LogPlayerImpl;
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
  static final String APP_PATH = "com/bitplan/can4eve/gui/CanTriplet.json";
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
  String vehicleGroupName = "Triplet";

  @Option(name = "--port", aliases = {
      "--portnumber" }, usage = "port\nthe port to use")
  int portNumber = 7000;

  @Option(name = "--limit", aliases = {
      "--framelimit" }, usage = "limit\nthe maximum number of frames to read")
  // FIXME make configurable e.g. in Preferences or do not use at all
  protected long frameLimit = 500 * 4800; // some 1 1/2 hours at 500fps

  @Option(name = "-l", aliases = {
      "--log" }, usage = "log\nthe logfile to use")
  String logFileName;
  
  @Option(name = "-s", aliases = {
  "--simulator" }, usage = "simulator\nrun the simulator using the given port and logfile (if any)")
  boolean simulate;

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

  @Override
  public String getSupportEMail() {
    return "support@bitplan.com";
  }

  @Override
  public String getSupportEMailPreamble() {
    String javaversion = System.getProperty("java.version");
    String os = System.getProperty("os.name");
    return String.format(
        "Dear can4eve support\nI am using version %s of the software on %s using Java %s\n",
        VERSION, os, javaversion);
  }

  @Override
  public LogPlayer getLogPlayer() {
    return LogPlayerImpl.getInstance();
  }

  @Override
  public Vehicle getVehicle() {
    return Vehicle.getInstance();
  }

  /**
   * construct me
   */
  public OBDMain() {
    super.setName("can4eve");
  }

  @Override
  public ELM327 testConnection(Config config) throws Exception {
    prepareOBD(config);
    elm.identify();
    elm.halt();
    return elm;
  }
  
  @SuppressWarnings("rawtypes")
  @Override
  public Map<String, CANData> readVehicleInfo(Config config,Vehicle vehicle) throws Exception {
    prepareOBD(config);
    Map<String, CANData> vehicleInfo = obdTriplet.readVehicleInfo(vehicle);
    return vehicleInfo;
  }

  /**
   * prepare the OBD connection
   * 
   * @param config
   * @throws Exception
   */
  public void prepareOBD(Config config) throws Exception {
    boolean doDebug=config.isDebug()||this.debug;
    vehicleGroup = VehicleGroup.get(this.vehicleGroupName);
    switch (config.getDeviceType()) {
    case USB:
      if (config.getDirect()) {
        String deviceName=config.getSerialDevice();
        switch (OSCheck.getOperatingSystemType()) {
        case MacOS:
        case Linux:
          deviceName="/dev/"+deviceName;
        default:
          break;
        }
        if (doDebug)
          LOGGER.log(Level.INFO,
              "using device (direct)" +deviceName);
        obdTriplet = new OBDTriplet(vehicleGroup,
            new File(deviceName));
      } else {
        if (doDebug)
          LOGGER.log(Level.INFO, String.format("using device %s at %6d baud",
              config.getSerialDevice(), config.getBaudRate()));
        obdTriplet = new OBDTriplet(vehicleGroup, config.getSerialDevice(),
            config.getBaudRate());
      }
      break;
    case Bluetooth:
      break;
    case Network:
      if (doDebug)
        LOGGER.log(Level.INFO, String.format("using host: %s port %5d",
            config.getHostname(), config.getPort()));
      elmSocket = new Socket(config.getHostname(), config.getPort());
      obdTriplet = new OBDTriplet(vehicleGroup, elmSocket);
      break;
    case Simulator:
      if (doDebug)
        LOGGER.log(Level.INFO, String.format(
            "Using simulator on server port %5d", ElmSimulator.DEFAULT_PORT));
      elm = ElmSimulator.getSimulation(vehicleGroup, doDebug,
          ElmSimulator.SIMULATOR_TIMEOUT);
      obdTriplet = new OBDTriplet(vehicleGroup, elm);
      break;
    default:
      break;
    }
    if (obdTriplet == null) {
      throw new Exception(Can4EveI18n.get(Can4EveI18n.INVALID_CONFIGURATION));
    }
    Vehicle vehicle = Vehicle.getInstance();
    if (vehicle != null) {
      if (vehicle.getMmPerRound() != null)
        obdTriplet.setMmPerRound(vehicle.getMmPerRound());
    }
    // the simulator is pre started and timeout and debug set
    // all other devices are configured here
    if (config.getDeviceType() != DeviceType.Simulator) {
      obdTriplet.setDebug(doDebug);
      elm = obdTriplet.getElm327();
      Connection con = elm.getCon();
      con.setTimeout(config.getTimeout());
      if (doDebug) {
        con.setLog(new LogImpl());
        // TODO preferences debug is different then connection debug!
        elm.setLog(con.getLog());
      }
      con.start();
    }
    try {
      elm.initOBD2();
    } catch (Exception e) {
      elm.getCon().halt();
      throw(e);
    }
  }

  @Override
  public ELM327 start(boolean withLog) throws Exception {
    if (elm == null || !elm.isStarted())
      prepareOBD(getConfig());
    // make can Values available
    obdTriplet.setUpCanValues();

    if (withLog) {
      if (this.logFileName != null) {
        obdTriplet.logResponses(new File(logFileName), vehicleGroup.getName());
      } else {
        Preferences pref = Preferences.getInstance();
        if (pref != null) {
          obdTriplet.logResponses(new File(pref.logDirectory), pref.logPrefix);
        }
      }
    }
    obdTriplet.startDisplay(canValueDisplay, 333);
    if (this.reportFileName != null) {
      obdTriplet.report(reportFileName, frameLimit);
    } else if (pid != null)
      obdTriplet.checkPid(pid, frameLimit);
    else {
      obdTriplet.pidMonitor(obdTriplet.getCANValues(), frameLimit);
    }
    obdTriplet.stopDisplay();
    return elm;
  }

  @Override
  public ELM327 stop() throws Exception {
    // stop monitoring;
    if (obdTriplet != null) {
      obdTriplet.setMonitoring(false);
    }
    if (elm != null) {
      int ltimeout = timeout;
      if (config != null)
        ltimeout = config.getTimeout();
      elm.reinitCommunication(ltimeout);
    }
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
      config = Config.getInstance(ConfigMode.Preferences);
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
    // a command line language setting overrides the preferences setting
    if (this.language != LangChoice.notSet) {
      Translator.initialize("can4eve",this.language.name());
    } else {
      LangChoice langChoice = preferences.getLanguage();
      String lang = null;
      if (langChoice != LangChoice.notSet)
        lang = preferences.getLanguage().name();
      Translator.initialize("can4eve",lang);
    }

    if (this.showVersion || this.debug)
      showVersion();
    if (this.showHelp) {
      showHelp();
    } else if (this.simulate) {
      // ElmSimulator.verbose=this.debug;
      String args[]={"--port",""+this.portNumber,"--vg",this.vehicleGroupName};
      if (this.logFileName!=null) {
        String args2[]={"--port",""+this.portNumber,"--vg",this.vehicleGroupName,"--file",this.logFileName};
        args=args2;
      }
      ElmSimulator.main(args);
    } else {
      JavaFXDisplay jfxDisplay;
      switch (displayChoice) {
      case JavaFX:
        App app=App.getInstance(APP_PATH);
        jfxDisplay = new JFXTripletDisplay(app, this, this);
        canValueDisplay = jfxDisplay;
        break;
      default:
      }
      if (this.monitor) {
        TaskLaunch.start(() -> {
          try {
            start(this.logFileName != null);
          } catch (Exception e) {
            ErrorHandler.handle(e);
          }
          return null;
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
    if (!testMode) {
      // LOGGER.log(Level.INFO, "System exit " + result);
      System.exit(result);
    }
  }

}

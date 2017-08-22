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

import com.bitplan.i18n.Translator;

/**
 * Translation settings
 * 
 * @author wf
 *
 */
public class I18n {
  public static final String ADDRESS_ALREADY_IN_USE = "addressAlreadyInUse"; 
  public static final String HELP = "help"; // Help
  public static final String INVALID_CONFIGURATION = "invalid_configuration";
  public static final String NEWLANGUAGE_RESTART = "newlanguage_restart"; 
  public static final String APP_NAME = "appName"; // can4eve
  public static final String HELP_MENU = "helpMenu"; // Help
  public static final String HELP_ABOUT_MENU_ITEM = "helpAboutMenuItem"; // About
  public static final String HELP_HELP_MENU_ITEM = "helpHelpMenuItem"; 
  public static final String HELP_FEEDBACK_MENU_ITEM = "helpFeedbackMenuItem"; 
  public static final String HELP_BUG_REPORT_MENU_ITEM = "helpBugReportMenuItem"; 

  public static final String SETTINGS_MENU = "settingsMenu"; // Settings
  public static final String SETTINGS_PREFERENCES_MENU_ITEM = "settingsPreferencesMenuItem"; // Preferences
  public static final String SETTINGS_SETTINGS_MENU_ITEM = "settingsSettingsMenuItem"; // OBDII
  public static final String SETTINGS_WELCOME_MENU_ITEM = "settingsWelcomeMenuItem"; // Wizard
  public static final String MENU = "Menu"; // Menu
  public static final String LANGUAGE_CHANGED_TITLE = "language_changed_title"; 
  public static final String LANGUAGE_CHANGED = "language_changed"; 
  public static final String FILE_MENU = "fileMenu"; // File
  public static final String FILE_SAVE_MENU_ITEM = "fileSaveMenuItem"; // Save
  public static final String FILE_CLOSE_MENU_ITEM = "fileCloseMenuItem"; // Close
  public static final String FILE_OPEN_MENU_ITEM = "fileOpenMenuItem"; // Open
  public static final String FILE_QUIT_MENU_ITEM = "fileQuitMenuItem"; // Quit
  public static final String VEHICLE_MENU_ITEM = "vehicleMenuItem"; // Details
  public static final String VEHICLE_MENU = "vehicleMenu"; // Vehicle
  public static final String CONNECTION_OK = "connection_ok";
  public static final String SUCCESS = "success";
  public static final String ERROR = "error";
  public static final String PROBLEM_OCCURED = "problem_occured";
  public static final String CONNECTION_FAILED = "connection_failed";
  public static final String CONNECTION_UNUSABLE = "connectionUnusable";
  public static final String SUPPORT_EMAIL = "support_email";
  public static final String MONITORING = "monitoring";
  public static final String HALTED = "halted";
  public static final String OBD_MENU = "obdMenu"; // OBD
  public static final String OBD_START_MENU_ITEM = "obdStartMenuItem"; // Start
  public static final String OBD_START_WITH_LOG_MENU_ITEM = "obdStartWithLogMenuItem"; 
  public static final String OBD_HALT_MENU_ITEM = "obdHaltMenuItem"; // Halt
  public static final String OBD_TEST_MENU_ITEM = "obdTestMenuItem"; // Test
  public static final String SORRY = "sorry";
  public static final String WE_ARE_SORRY = "weAreSorry";
  public static final String NOT_IMPLEMENTED_YET = "notImplementedYet";
  public static final String VIEW_MENU = "viewMenu"; // View
  public static final String VIEW_DASHBOARD_VIEW_MENU_ITEM = "viewDashboardViewMenuItem"; // Dashboard
  public static final String VIEW_HISTORY_VIEW_MENU_ITEM = "viewHistoryViewMenuItem"; // History
  public static final String VIEW_SETTINGS_VIEW_MENU_ITEM = "viewSettingsViewMenuItem"; // Settings.
  public static final String VIEW_MONITOR_VIEW_MENU_ITEM = "viewMonitorViewMenuItem"; // OBD
  public static final String WATCH_PARKING = "watchParking";
  public static final String WATCH_MOVING = "watchMoving";
  public static final String WATCH_CHARGING = "watchCharging";
  public static final String WATCH_TOTAL = "watchTotal";
  public static final String WATCH_TIME = "watchTime";
  public static final String SERIAL_PORT_SELECT = "serialPortSelect";
  public static final String SERIAL_PORT_NONE_FOUND = "serialPortNoneFound";
  public static final String SERIAL_PORT_PLEASE_CONNECT = "serialPortPleaseConnect";
  public static final String SERIAL_PORT_PLEASE_SELECT = "serialPortPleaseSelect";
  // public static final String SOC = "SOC";
  public static final String RPM = "rpm";
  public static final String RPM_SPEED = "rpmSpeed";
  public static final String RPM_MAX = "rpmMax";
  public static final String RPM_AVG = "rpmAvg";
  public static final String RPM_SPEED_MAX = "rpmSpeedMax";
  public static final String RPM_SPEED_AVG = "rpmSpeedAvg";
  public static final String REV_COUNT = "revCount";
  public static final String CLOCKS = "clocks";
  public static final String SOC = "soc";
  public static final String DASH_BOARD = "dashBoard";
  public static final String FULL_SCREEN = "fullScreen";
  public static final String PART_SCREEN = "partScreen";
  public static final String HIDE_MENU = "hideMenu";
  public static final String SHOW_MENU = "showMenu";
  public static final String SCREEN_SHOT = "screenShot";
  public static final String RR = "rr";
  public static final String KM = "km";
  public static final String AMPS = "amps";
  public static final String AC_AMPS = "acAmps";
  public static final String AC_VOLTS = "acVolts";
  public static final String VOLTS = "volts";
  public static final String DC_VOLTS = "dcVolts";
  public static final String DC_AMPS = "dcAmps";
  public static final String AMPER_HOURS="amperHours";
  public static final String KMH = "kmh";
  public static final String PERCENT = "percent";
  // CAN Infos
  public static final String ODO_METER = "odoMeter";
  public static final String CELL_COUNT="CellCount"; //# of Cells
  public static final String TRIP_ODO_METER = "tripOdoMeter";
  public static final String ODO_INFO = "odoInfo";
  public static final String FROM = "from";
  public static final String TO = "to";
  public static final String K_WATT = "kWatt";
  public static final String AC_POWER = "acPower";
  public static final String DC_POWER = "dcPower";
  public static final String WELCOME = "welcome";
  public static final String WELCOME_STEP = "welcomeStep"; // Step %d of %d
  public static final String WELCOME_VEHICLE = "welcomeVehicle";
  public static final String WELCOME_VEHICLE_TYPE = "welcomeVehicleType";
  public static final String WELCOME_OBD = "welcomeObd";
  public static final String WELCOME_CON = "welcomeCon";
  public static final String WELCOME_LANGUAGE = "welcomeLanguage";
  public static final String WELCOME_TEST_RESULT = "welcomeTestResult";
  public static final String WELCOME_TEST_VEHICLE = "welcomeTestVehicle";
  public static final String BAUD_RATE = "baudRate"; // baudrate
  public static final String SERIAL_DEVICE = "serialDevice"; // device
  public static final String REPORT_ISSUE = "reportIssue"; // Report Issue ...
  public static final String CONNECTION_REFUSED = "connectionRefused";
  public static final String NETWORK_IS_UNREACHABLE = "networkIsUnreachable";
  public static final String OBD_EXCEPTION = "obdException";
  public static final String SERIAL_DEVICE_TOOLTIP = "serialDeviceTooltip";
  public static final String BAUD_RATE_TOOLTIP = "baudRateTooltip";
  public static final String DIRECT_SERIAL_TOOLTIP = "directSerialTooltip";
  public static final String HOSTNAME_TOOLTIP = "hostnameTooltip";
  public static final String PORT_TOOLTIP = "portTooltip";
  public static final String VEHICLE_MODEL="vehicleModel"; //model
  public static final String MAX_SPEED="maxSpeed"; //maximum Speed (km/h)
  public static final String VEHICLE_YEAR="vehicleYear"; //year
  public static final String VEHICLE_MANUFACTURER="vehicleManufacturer"; //manufacturer
  public static final String VEHICLE_VIN_PROBLEM="vehicleVinProblem";
  public static final String BATTERY_CAPACITY = "batteryCapacity";
  public static final String AH = "amperHours";
  public static final String ELM327_CAN4EVE_COMPATIBLE = "elm327Can4eveCompatible";
  public static final String ELM327_CAN4EVE_INCOMPATIBLE = "elm327Can4eveIncompatible";
  public static final String ELM327_RECOMMENDED = "elm327Recommended";
  public static final String POWER_OFF = "powerOff";
  public static final String DASH_BOARD_TAB="dashBoardTab";
  public static final String PARKING="parking"; //Parking
  public static final String TRIP="trip"; //Trip
  public static final String CELL_TEMP="cellTemp"; //Cell Temperatures
  public static final String CONNECTION="connection"; //Connection
  public static final String MOVING="moving"; //Moving
  public static final String VEHICLE="vehicle"; //Vehicle
  public static final String PREFERENCES="preferences"; //Preferences
  public static final String SOC_RR="soc_rr"; //SOC/RR
  public static final String OBDII="obdii"; //OBDII
  public static final String INFO="info"; //Info
  public static final String HISTORY="history"; //History
  public static final String CELL_VOLTAGE="cellVoltage"; //Cell Voltages
  public static final String CHARGING="charging"; //Charging
  /**
   * Translate the given text
   * 
   * @param text
   *          - the text to translate
   * @return - the text
   */
  public static String get(String text) {
    return Translator.translate(text);
  }

  /**
   * translate the given text with the given params
   * 
   * @param text
   * @param params
   * @return the translated string
   */
  public static String get(String text, Object... params) {
    return Translator.translate(text, params);
  }

}

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

import com.bitplan.can4eve.gui.swing.Translator;

/**
 * Translation settings
 * @author wf
 *
 */
public class I18n {
  public static final String INVALID_CONFIGURATION="invalid_configuration";
  public static final String NEWLANGUAGE_RESTART="newlanguage_restart"; //To get the full effect of the change a restart of the application is necessary!
  public static final String APP_NAME="appName"; //can4eve
  public static final String HELP_MENU="helpMenu"; //Help 
  public static final String HELP_ABOUT_MENU_ITEM="helpAboutMenuItem"; //About
  public static final String HELP_HELP_MENU_ITEM="helpHelpMenuItem"; //can4eve Hilfe
  public static final String HELP_FEEDBACK_MENU_ITEM="helpFeedbackMenuItem"; //send Feedback ...
  public static final String HELP_BUG_REPORT_MENU_ITEM="helpBugReportMenuItem"; //create Bugreport ...

  public static final String SETTINGS_MENU="settingsMenu"; //Settings
  public static final String SETTINGS_PREFERENCES_MENU_ITEM="settingsPreferencesMenuItem"; //Preferences
  public static final String SETTINGS_SETTINGS_MENU_ITEM="settingsSettingsMenuItem"; //OBDII
  public static final String MENU="Menu"; //Menu
  public static final String LANGUAGE_CHANGED_TITLE="language_changed_title"; //New language
  public static final String LANGUAGE_CHANGED="language_changed"; //You have selected english as your new language
  public static final String FILE_MENU="fileMenu"; //File
  public static final String FILE_SAVE_MENU_ITEM="fileSaveMenuItem"; //Save
  public static final String FILE_OPEN_MENU_ITEM="fileOpenMenuItem"; //Open
  public static final String FILE_QUIT_MENU_ITEM="fileQuitMenuItem"; //Quit
  public static final String VEHICLE_MENU_ITEM="vehicleMenuItem"; //Details
  public static final String VEHICLE_MENU="vehicleMenu"; //Vehicle
  public static final String CONNECTION_OK = "connection_ok";
  public static final String SUCCESS = "success";
  public static final String ERROR = "error";
  public static final String PROBLEM_OCCURED = "problem_occured";
  public static final String CONNECTION_FAILED="connection_failed";
  public static final String SUPPORT_EMAIL = "support_email";
  public static final String MONITORING = "monitoring";
  public static final String HALTED = "halted";
  public static final String OBD_MENU="obdMenu"; //OBD
  public static final String OBD_START_MENU_ITEM="obdStartMenuItem"; //Start
  public static final String OBD_START_WITH_LOG_MENU_ITEM="obdStartWithLogMenuItem"; //Start with Log
  public static final String OBD_HALT_MENU_ITEM="obdHaltMenuItem"; //Halt
  public static final String OBD_TEST_MENU_ITEM="obdTestMenuItem"; //Test
  public static final String SORRY = "sorry";
  public static final String WE_ARE_SORRY = "weAreSorry";
  public static final String NOT_IMPLEMENTED_YET = "notImplementedYet";
  public static final String VIEW_MENU="viewMenu"; //View
  public static final String VIEW_HISTORY_VIEW_MENU_ITEM="viewHistoryViewMenuItem"; //History
  public static final String VIEW_SETTINGS_VIEW_MENU_ITEM="viewSettingsViewMenuItem"; //Settings.
  public static final String VIEW_MONITOR_VIEW_MENU_ITEM="viewMonitorViewMenuItem"; //OBD
  public static final String WATCH_PARKING = "watchParking";
  public static final String WATCH_MOVING = "watchMoving";
  public static final String WATCH_CHARGING = "watchCharging";
  public static final String WATCH_TOTAL = "watchTotal";
  public static final String WATCH_TIME = "watchTime";
  //public static final String SOC = "SOC";
  public static final String RPM = "rpm";
  public static final String RPM_SPEED = "rpmSpeed";
  public static final String RPM_MAX="rpmMax";
  public static final String RPM_AVG="rpmAvg";
  public static final String RPM_SPEED_MAX = "rpmSpeedMax";
  public static final String RPM_SPEED_AVG = "rpmSpeedAvg";
  public static final String REV_COUNTER = "revCount";

  /**
   * Translate the given text
   * @param text - the text to translate
   * @return - the text
   */
  public static String get(String text) {
    return Translator.translate(text);
  }
}

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
  public static final String ABOUTMENUITEM="aboutMenuItem"; //About
  public static final String HELPMENUITEM="helpMenuItem"; //can4eve Hilfe
  public static final String STARTMENUITEM="startMenuItem"; //Start
  public static final String APPNAME="appName"; //can4eve
  public static final String HELPMENU="helpMenu"; //Help 
  public static final String PREFERENCESMENUITEM="preferencesMenuItem"; //Preferences
  public static final String QUITMENUITEM="quitMenuItem"; //Quit
  public static final String SETTINGSMENUITEM="settingsMenuItem"; //OBDII
  public static final String SETTINGSMENU="settingsMenu"; //Settings
  public static final String FEEDBACKMENUITEM="feedbackMenuItem"; //send Feedback ...
  public static final String TESTMENUITEM="testMenuItem"; //Test
  public static final String HALTMENUITEM="haltMenuItem"; //Halt
  public static final String MENU="Menu"; //Menu
  public static final String LANGUAGE_CHANGED_TITLE="language_changed_title"; //New language
  public static final String LANGUAGE_CHANGED="language_changed"; //You have selected english as your new language
  public static final String FILEMENU="fileMenu"; //File
  public static final String VEHICLEMENUITEM="vehicleMenuItem"; //Details
  public static final String VEHICLEMENU="vehicleMenu"; //Vehicle
  public static final String CONNECTION_OK = "connection_ok";
  public static final String SUCCESS = "success";
  public static final String ERROR = "error";
  public static final String PROBLEM_OCCURED = "problem_occured";
  public static final String CONNECTION_FAILED="connection_failed";
  public static final String SUPPORT_EMAIL = "support_email";
  public static final String BUGREPORTMENUITEM="bugReportMenuItem"; //create Bugreport ...
  public static final String MONITORING = "monitoring";
  public static final String HALTED = "halted";
  public static final String OBDMENU="obdMenu"; //OBD
  public static final String SAVEMENUITEM="saveMenuItem"; //Save
  public static final String OPENMENUITEM="openMenuItem"; //Open

  /**
   * Translate the given text
   * @param text - the text to translate
   * @return - the text
   */
  public static String get(String text) {
    return Translator.translate(text);
  }
}

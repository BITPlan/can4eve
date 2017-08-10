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
package com.bitplan.obdii.javafx;

import java.util.logging.Level;

import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

import com.bitplan.can4eve.ErrorHandler;
import com.bitplan.obdii.I18n;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;

/**
 * the Welcome wizard
 * 
 * @author wf
 *
 */
public class WelcomeWizard extends JFXWizard {

  String carSelections[] = { "Citroën C-Zero", "Mitsubishi i-Miev",
      "Misubishi Outlander PHEV", "Peugeot Ion" };
  String carPictures[] = { "c-zero.jpg", "i-miev.jpg", "outlanderphev.jpg",
      "ion.jpg" };
  ImageSelector<String> carSelector = new ImageSelector<String>("vehicle",
      carSelections, carPictures);
  String conSelections[] = { "USB", "Wifi", "Bluetooth" };
  String conPictures[] = { "obd-usb.jpg", "obd-wifi.jpg", "obd-bluetooth.jpg" };

  ImageSelector<String> connectionSelector = new ImageSelector<String>("obd",
      conSelections, conPictures);
  private WizardPane carPane;
  private WizardPane connectionPane;
  private WizardPane conSettingsPane;

  /**
   * construct the welcome Wizard
   * 
   * @param title
   * @param pageNames
   * @throws Exception
   */
  public WelcomeWizard(String i18nTitle) {
    super();
    setTitle(I18n.get(i18nTitle));
    carPane = new JFXWizardPane(I18n.WELCOME_VEHICLE, carSelector);
    // SampleApp.createAndShow("select", selectPane, SHOW_TIME);
    carPane.setContent(carSelector);
    addPage(carPane);
    conSettingsPane = new JFXWizardPane(I18n.WELCOME_CON) {
      @Override
      public void onExitingPage(Wizard wizard) {

      }
    };
    connectionPane = new JFXWizardPane(I18n.WELCOME_OBD, connectionSelector) {
      @Override
      public void onExitingPage(Wizard wizard) {
        super.onExitingPage(wizard);
        Parent content = null;
        String con = selector.getSelection();
        if ("USB".equals(con) || "Bluetooth".equals(con)) {
          content = loadParent("usb");
        } else {
          content=loadParent("network");
        }
        if (content != null)
          conSettingsPane.setContent(content);
        else
          LOGGER.log(Level.SEVERE, "missing fxml for " + con);
      }

    };
    addPage(connectionPane);

    // wizard.setPages(pageNames);
    addPage(conSettingsPane);
    prepare();
  }

  /**
   * animate this wizard for testing purposes
   * 
   * @param showTime
   * @throws Exception
   */
  public void animate(int showTime) throws Exception {
    animateSelections(this.carSelector, showTime / 3);
    Platform.runLater(() -> this.findButton(carPane, ButtonType.NEXT).fire());
    animateSelections(this.connectionSelector, showTime / 3);
    Platform.runLater(
        () -> this.findButton(connectionPane, ButtonType.NEXT).fire());
    Thread.sleep(showTime / 6);
    Platform
        .runLater(() -> this.findButton(conSettingsPane, ButtonType.FINISH));
    Thread.sleep(showTime / 6);
  }

}

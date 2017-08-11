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

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

import com.bitplan.can4eve.gui.javafx.ExceptionController;
import com.bitplan.elm327.Config;
import com.bitplan.elm327.SerialImpl;
import com.bitplan.i18n.Translator;
import com.bitplan.obdii.I18n;
import com.bitplan.obdii.OBDApp;
import com.bitplan.obdii.elm327.ELM327;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * the Welcome wizard
 * 
 * @author wf
 *
 */
public class WelcomeWizard extends JFXWizard {
  String langs[] = { "English", "Deutsch" };
  String langPictures[] = { "en-flag.jpg", "de-flag.jpg" };
  ImageSelector<String> langSelector = new ImageSelector<String>("language",
      langs, langPictures);

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

  private JFXWizardPane carPane;
  private JFXWizardPane connectionPane;
  private JFXWizardPane conSettingsPane;
  private JFXWizardPane languagePane;
  private JFXWizardPane conTestResultPane;
  private OBDApp obdApp;
  private Config config;
  public static class NetworkController implements Initializable {
    @FXML
    TextField hostName;
    @FXML
    TextField port;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
      hostName.setText("192.168.0.10");
      port.setText("35000");
    }

  }

  public static class SerialController implements Initializable {
    @FXML
    ComboBox<String> serialDevice;
    @FXML
    ComboBox<String> baudRate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
      SerialImpl serial = SerialImpl.getInstance();
      List<String> serialPorts = serial.getSerialPorts(true);
      serialDevice.getItems().clear();
      serialDevice.getItems().addAll(serialPorts);
      if (serialPorts.size() > 0) {
        serialDevice.getSelectionModel().select(0);
      }
      String[] baudRates = { "38400", "115200", "230400" };
      baudRate.getItems().clear();
      baudRate.getItems().addAll(baudRates);
      baudRate.getSelectionModel().select(0);
    }

  }

  public static class ConnectionTestController implements Initializable {
    @FXML
    ProgressBar progressBar;

    @FXML
    TextArea textArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
      // TODO Auto-generated method stub

    }

  }

  /**
   * construct the wizard
   * 
   * @param i18nTitle
   * @param obdApp
   */
  public WelcomeWizard(String i18nTitle, OBDApp obdApp) {
    super();
    this.obdApp = obdApp;
    setTitle(I18n.get(i18nTitle));
    int steps = 5;
    languagePane = new JFXWizardPane(1, steps, I18n.WELCOME_LANGUAGE,
        langSelector) {
      @Override
      public void onExitingPage(Wizard wizard) {
        String lang = langSelector.getSelection();
        switch (lang) {
        case "English":
          lang = "en";
          break;
        case "Deutsch":
          lang = "de";
          break;
        }
        Translator.initialize(lang);
        WelcomeWizard.this.refreshI18n();
      }
    };
    addPage(languagePane);
    carPane = new JFXWizardPane(2, steps, I18n.WELCOME_VEHICLE, carSelector);
    // SampleApp.createAndShow("select", selectPane, SHOW_TIME);
    carPane.setContent(carSelector);
    addPage(carPane);
    conTestResultPane = new JFXWizardPane(5, steps, I18n.WELCOME_TEST_RESULT) {
      Button finishButton=null;
      
      public void handleException(Throwable th) {
        load("exception");
        setI18nTitle(I18n.PROBLEM_OCCURED);
        ExceptionController exceptionController = (ExceptionController) conTestResultPane.controller;
        exceptionController.handleException(th);
      }

      @Override
      public void onEnteringPage(Wizard wizard) {
        if (obdApp != null) {
          finishButton = conTestResultPane.findButton(ButtonType.FINISH);
          if (finishButton != null)
            finishButton.setVisible(false);
          load("connectiontest");
          ConnectionTestController testController=(ConnectionTestController)controller;
          Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
              try {
                ELM327 elm = obdApp.testConnection(config);
                final String info = elm.getInfo();
                Platform.runLater(()->{
                  this.updateProgress(100, 100);
                  testController.textArea.setText(info); 
                  if (finishButton != null)
                     finishButton.setVisible(true);
                });
              } catch (Throwable th) {
                Platform.runLater(()->handleException(th));
              }
              return null;
            }
          };
          testController.progressBar.progressProperty().bind(task.progressProperty());
          new Thread(task).start();
        }
      }
    };

    conSettingsPane = new JFXWizardPane(4, steps, I18n.WELCOME_CON) {
 

      @Override
      public void onExitingPage(Wizard wizard) {
        config = new Config();
        if (controller instanceof SerialController) {
          SerialController serialController = (SerialController) controller;
          config.setDeviceType(Config.DeviceType.USB);
          config.setSerialDevice(serialController.serialDevice
              .getSelectionModel().getSelectedItem());
          int serialBaud = Integer.parseInt(
              serialController.baudRate.getSelectionModel().getSelectedItem());
          config.setBaudRate(serialBaud);
        } else if (controller instanceof NetworkController) {
          NetworkController networkController = (NetworkController) controller;
          config.setDeviceType(Config.DeviceType.Network);
          config.setHostname(networkController.hostName.getText());
          config.setPort(Integer.parseInt(networkController.port.getText()));
        }
      }
    };

    connectionPane = new JFXWizardPane(3, steps, I18n.WELCOME_OBD,
        connectionSelector) {
      @Override
      public void onExitingPage(Wizard wizard) {
        super.onExitingPage(wizard);
        String con = selector.getSelection();
        if ("USB".equals(con) || "Bluetooth".equals(con)) {
          conSettingsPane.load("usb");
        } else {
          conSettingsPane.load("network");
        }
      }

    };
    addPage(connectionPane);

    // wizard.setPages(pageNames);
    addPage(conSettingsPane);
    addPage(conTestResultPane);
    prepare();
  }

  protected void refreshI18n() {
    for (WizardPane page : this.pages) {
      if (page instanceof JFXWizardPane) {
        JFXWizardPane jfxpage = (JFXWizardPane) page;
        jfxpage.refreshI18n();
      }
    }

  }

  /**
   * animate this wizard for testing purposes
   * 
   * @param showTime
   * @throws Exception
   */
  public void animate(int showTime) throws Exception {
    animateSelections(this.langSelector, showTime / 5);
    Platform.runLater(() -> languagePane.findButton(ButtonType.NEXT).fire());
    animateSelections(this.carSelector, showTime / 5);
    Platform.runLater(() -> carPane.findButton(ButtonType.NEXT).fire());
    animateSelections(this.connectionSelector, showTime / 5);
    Platform.runLater(() -> connectionPane.findButton(ButtonType.NEXT).fire());
    Thread.sleep(showTime / 10);
    Platform.runLater(() -> conSettingsPane.findButton(ButtonType.NEXT).fire());
    Thread.sleep(showTime / 10);
    Platform
        .runLater(() -> conTestResultPane.findButton(ButtonType.FINISH).fire());
    Thread.sleep(showTime / 10);
  }

}

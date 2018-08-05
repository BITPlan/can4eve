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
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;

import org.controlsfx.dialog.Wizard;

import com.bitplan.can4eve.CANData;
import com.bitplan.can4eve.Owner;
import com.bitplan.can4eve.Vehicle;
import com.bitplan.elm327.Config;
import com.bitplan.elm327.Config.ConfigMode;
import com.bitplan.elm327.OBDException;
import com.bitplan.elm327.SerialImpl;
import com.bitplan.gui.Form;
import com.bitplan.i18n.Translator;
import com.bitplan.javafx.GenericPanel;
import com.bitplan.javafx.ImageSelector;
import com.bitplan.javafx.JFXML;
import com.bitplan.javafx.JFXWizard;
import com.bitplan.javafx.JFXWizardPane;
import com.bitplan.obdii.Can4EveI18n;
import com.bitplan.obdii.OBDApp;
import com.bitplan.obdii.Preferences;
import com.bitplan.obdii.Preferences.LangChoice;
import com.bitplan.obdii.elm327.ELM327;
import com.bitplan.obdii.javafx.presenter.VehiclePresenter;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

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

  String conSelections[] = { "USB", "Wifi", "Bluetooth" };
  String conPictures[] = { "obd-usb.jpg", "obd-wifi.jpg", "obd-bluetooth.jpg" };

  ImageSelector<String> connectionSelector = new ImageSelector<String>("obd",
      conSelections, conPictures);
  public static boolean testMode=false;
  private JFXWizardPane carPane;
  private JFXWizardPane connectionPane;
  private JFXWizardPane conSettingsPane;
  private JFXWizardPane languagePane;
  private JFXWizardPane conTestResultPane;
  private OBDApp obdApp;
  private Config config;
  private JFXWizardPane vehiclePane;
  SerialController serialController;
  Vehicle vehicle = null;
  private String lang;
  private JFXWizardPane ownerPane;

  public static class NetworkController implements Initializable {
    @FXML
    TextField hostName;
    @FXML
    TextField port;
    @FXML
    Pane pane;

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
    @FXML
    CheckBox direct;
    @FXML
    Pane pane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
      SerialImpl serial = SerialImpl.getInstance();
      List<String> serialPorts = serial.getSerialPorts(true);
      serialDevice.getItems().clear();
      serialDevice.getItems().addAll(serialPorts);
      if (serialPorts.size() > 0) {
        serialDevice.getSelectionModel().select(0);
      }
      String[] baudRates = { "automatic", "19200", "38400", "57600", "115200",
          "230400", "500000" };
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

    @FXML
    TextArea hint;

    @FXML
    ImageView obdImage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    /**
     * show the info about the test Result
     * 
     * @param elm
     * @param config
     * @param imageSelector
     */
    public void testResultInfo(ELM327 elm, Config config,
        ImageSelector<String> imageSelector) {
      String info = elm.getInfo();
      ImageView imageView = null;
      switch (config.getDeviceType()) {
      case Network:
        imageView = imageSelector.getImageView("Wifi");
        break;
      case USB:
        imageView = imageSelector.getImageView("USB");
        info += "\nbaudrate: " + config.getBaudRate();
        break;
      case Bluetooth:
        imageView = imageSelector.getImageView("Bluetooth");
        info += "\nbaudrate: " + config.getBaudRate();
      default:
        break;
      }
      if (imageView != null)
        obdImage.setImage(imageView.getImage());
      textArea.setText(info);
      hint.setText(Can4EveI18n.get(Can4EveI18n.WELCOME_TEST_VEHICLE));
    }

  }

  /**
   * construct the wizard
   * 
   * @param i18nTitle
   * @param obdApp
   */
  public WelcomeWizard(String i18nTitle, OBDApp obdApp, JFXML fxml) {
    super(fxml);
    this.obdApp = obdApp;
    setTitle(Can4EveI18n.get(i18nTitle));
    int steps = 7;
    VehiclePresenter vehiclePresenter = getFxml().loadPresenter("vehicle",
        Vehicle.class, null);

    languagePane = new JFXWizardPane(this, 1, steps, Can4EveI18n.WELCOME_LANGUAGE,
        langSelector) {

      @Override
      public void onExitingPage(Wizard wizard) {
        super.onExitingPage(wizard);
        lang = langSelector.getSelection();
        switch (lang) {
        case "English":
          lang = "en";
          break;
        case "Deutsch":
          lang = "de";
          break;
        }
        Translator.initialize("can4eve", lang);
        WelcomeWizard.this.refreshI18n();
      }
    };
    Locale locale = Locale.getDefault();
    String currentLang = locale.getLanguage();
    SingleSelectionModel<String> langSelect = langSelector.getChoice()
        .getSelectionModel();
    switch (currentLang) {
    case "de":
      langSelect.select("Deutsch");
      break;
    case "en":
      langSelect.select("English");
      break;
    }
    addPage(languagePane, "http://can4eve.bitplan.com/index.php/Help/Language");

    carPane = new JFXWizardPane(this, 2, steps, Can4EveI18n.WELCOME_VEHICLE_TYPE,
        vehiclePresenter.getCarSelector()) {
      @Override
      public void onExitingPage(Wizard wizard) {
        super.onExitingPage(wizard);
        vehiclePresenter.updateModel();
      }

    };
    addPage(carPane, "http://can4eve.bitplan.com/index.php/Help/VehicleTypes");

    connectionPane = new JFXWizardPane(this, 3, steps, Can4EveI18n.WELCOME_OBD,
        connectionSelector) {
      @Override
      public void onExitingPage(Wizard wizard) {
        super.onExitingPage(wizard);
      }

    };
    addPage(connectionPane, "http://can4eve.bitplan.com/index.php/Help/OBDII");

    conSettingsPane = new JFXWizardPane(this, 4, steps, Can4EveI18n.WELCOME_CON) {
      NetworkController networkController;

      @Override
      public void onEnteringPage(Wizard wizard) {
        super.onEnteringPage(wizard);
        String con = (String) wizard.getSettings().get("obd");
        if ("USB".equals(con) || "Bluetooth".equals(con)) {
          if (serialController == null) {
            load("usb");
            serialController = (SerialController) controller;
          } else {
            this.setContentNode(serialController.pane);
            this.controller = serialController;
          }
          this.setHelp(
              "http://can4eve.bitplan.com/index.php?title=Help/SerialConnection");
        } else {
          if (networkController == null) {
            load("network");
            networkController = (NetworkController) controller;
          } else {
            this.setContentNode(networkController.pane);
            this.controller = networkController;
          }
          this.setHelp(
              "http://can4eve.bitplan.com/index.php?title=Help/NetworkConnection");
        }
      }

      @Override
      public void onExitingPage(Wizard wizard) {
        super.onExitingPage(wizard);
        config = new Config();
        if (controller instanceof SerialController) {
          SerialController serialController = (SerialController) controller;
          config.setDeviceType(Config.DeviceType.USB);
          config.setSerialDevice(serialController.serialDevice
              .getSelectionModel().getSelectedItem());
          String baudStr = serialController.baudRate.getSelectionModel()
              .getSelectedItem();
          int serialBaud = -1;
          if (!"automatic".equals(baudStr))
            serialBaud = Integer.parseInt(baudStr);
          config.setBaudRate(serialBaud);
          config.setDirect(serialController.direct.isSelected());
        } else if (controller instanceof NetworkController) {
          NetworkController networkController = (NetworkController) controller;
          config.setDeviceType(Config.DeviceType.Network);
          config.setHostname(networkController.hostName.getText());
          config.setPort(Integer.parseInt(networkController.port.getText()));
        }
      }
    };

    // wizard.setPages(pageNames);
    addPage(conSettingsPane);
    conTestResultPane = new JFXWizardPane(this, 5, steps,
        Can4EveI18n.WELCOME_TEST_RESULT) {
      ConnectionTestController testController;
      private Button nextButton = null;

      @Override
      public void onEnteringPage(Wizard wizard) {
        super.onEnteringPage(wizard);
        if (WelcomeWizard.this.obdApp != null) {
          setI18nTitle(Can4EveI18n.WELCOME_TEST_RESULT);
          load("connectiontest");
          testController = (ConnectionTestController) controller;
          nextButton = findButton(ButtonType.NEXT);
          if (nextButton != null) {
            Platform.runLater(() -> nextButton.setDisable(true));
          }
          Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
              boolean ok = false;
              boolean autoBaudRate = config.getBaudRate() < 0;
              try {
                int baudrates[];
                // automatic baudRate check
                if (autoBaudRate) {
                  int trybaudrates[] = { 115200, 230400, 57600, 38400, 19200,
                      500000 };
                  baudrates = trybaudrates;
                } else {
                  int trybaudrates[] = { config.getBaudRate() };
                  baudrates = trybaudrates;
                }
                int progressmax = baudrates.length + 1;
                int index = 0;
                for (int baudrate : baudrates) {
                  if (autoBaudRate) {
                    this.updateProgress(++index, progressmax);
                    config.setBaudRate(baudrate);
                    Platform.runLater(() -> testController.textArea
                        .setText("" + baudrate + " baud"));
                  }
                  try {
                    ELM327 elm = WelcomeWizard.this.obdApp
                        .testConnection(config);
                    if (autoBaudRate) {
                      Platform.runLater(() -> serialController.baudRate
                          .getSelectionModel().select("" + baudrate));
                    }
                    Platform.runLater(() -> {
                      this.updateProgress(progressmax, progressmax);
                      testController.testResultInfo(elm, config,
                          connectionSelector);
                    });
                    // we have a positive result
                    ok = true;
                    // leave the baud rate checking loop
                    break;
                  } catch (Throwable th0) {
                    if (th0 instanceof OBDException) {
                      // failed attempt
                    } else {
                      throw th0;
                    }
                  }
                } // for
                if (ok) {

                  if (nextButton != null)
                    Platform.runLater(() -> nextButton.setDisable(false));
                } else {
                  Platform.runLater(() -> {
                    this.updateProgress(progressmax, progressmax);
                    testController.textArea
                        .setText(Can4EveI18n.get(Can4EveI18n.CONNECTION_UNUSABLE));
                  });
                }
              } catch (Throwable th) {
                Platform.runLater(() -> handleException(th));
              }
              return null;
            }
          };
          testController.progressBar.progressProperty()
              .bind(task.progressProperty());
          new Thread(task).start();
        }
      }
    };

    addPage(conTestResultPane,
        "http://can4eve.bitplan.com/index.php/Help/ConnectionTest");

    vehiclePane = new JFXWizardPane(this, 6, steps, Can4EveI18n.WELCOME_VEHICLE) {
      private Button nextButton;

      @Override
      public void onEnteringPage(Wizard wizard) {
        super.onEnteringPage(wizard);
        if (getContentNode() == null)
          vehiclePresenter.setExceptionHandler(this);
        super.setContentNode(vehiclePresenter.getParent());
        if (vehicle == null) {
          vehicle = vehiclePresenter.updateModel();
        }
        Platform.runLater(() -> vehiclePresenter.updateView(vehicle));
        if (obdApp != null) {
          nextButton = findButton(ButtonType.NEXT);
          if (nextButton != null) {
            Platform.runLater(() -> nextButton.setDisable(true));
          }
          // vehicleForm= app.getFormById("preferencesGroup", "vehicleForm");
          Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
              try {
                int progressmax = 100;

                @SuppressWarnings("rawtypes")

                Map<String, CANData> vehicleInfo = obdApp
                    .readVehicleInfo(config, vehicle);
                this.updateProgress(progressmax, progressmax);
                Platform.runLater(() -> vehiclePresenter
                    .showVehicleInfo(vehicle, vehicleInfo, vehiclePane));
                Platform.runLater(() -> {
                  if (nextButton != null) {
                    nextButton.setDisable(false);
                  }
                });

              } catch (Throwable th) {
                Platform.runLater(() -> handleException(th));
              }
              return null;
            }
          };
          vehiclePresenter.getProgressBar().progressProperty()
              .bind(task.progressProperty());
          new Thread(task).start();
        } else {
          LOGGER.log(Level.WARNING, "obdApp is null");
        }
      } // onEnteringPage

    };
    addPage(vehiclePane,
        "http://can4eve.bitplan.com/index.php/Help/VehicleTest");
    ownerPane = new JFXWizardPane(this, 7, steps, Can4EveI18n.WELCOME_OWNER) {
      private Button finishButton;
      private Owner owner;
      private GenericPanel ownerPanel;

      @Override
      public void onEnteringPage(Wizard wizard) {
        super.onEnteringPage(wizard);
        finishButton = findButton(ButtonType.FINISH);
        if (owner == null) {
          owner = Owner.getInstance();
          Form ownerForm = fxml.getApp().getFormById("preferencesGroup",
              "ownerForm");
          ownerPanel = new GenericPanel(fxml.getStage(), ownerForm);
          this.setContent(ownerPanel);
        }
        if (finishButton != null) {
          Platform.runLater(() -> {
            if (finishButton != null) {
              // TODO - validate Owner info
              // finishButton.setDisable(true);
              // workaround for controlsfx bug not to activate onExit for
              // FinishButton
              finishButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent actionEvent) {
                  onExitingPage(wizard);
                }
              });
            }
          });
        }
      }

      @Override
      public void onExitingPage(Wizard wizard) {
        super.onExitingPage(wizard);
        try {
          if (!testMode) {
            vehicle.save();
            owner.fromMap(ownerPanel.getValueMap());
            owner.save();
            config.save(ConfigMode.Preferences);
            Preferences prefs = Preferences.getInstance();
            prefs.setLanguage(LangChoice.valueOf(lang));
            prefs.setAutoStart(true);
            prefs.save();
          }
        } catch (Throwable th) {
          handleException(th);
        }
      }
    };
    addPage(ownerPane, "http://can4eve.bitplan.com/index.php/Help/Owner");
    prepare();
  }

}

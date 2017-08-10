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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.bitplan.can4eve.gui.Form;
import com.bitplan.can4eve.gui.javafx.GenericControl;
import com.bitplan.can4eve.gui.javafx.GenericDialog;
import com.bitplan.elm327.Config;
import com.bitplan.elm327.SerialImpl;
import com.bitplan.obdii.ErrorHandler;
import com.bitplan.obdii.I18n;
import com.bitplan.obdii.OBDApp;
import com.bitplan.obdii.elm327.ELM327;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.stage.Stage;

/**
 * Settings dialog
 * 
 * @author wf
 *
 */
public class SettingsDialog extends GenericDialog {

  private OBDApp obdApp;

  // construct me
  public SettingsDialog(Stage stage, Form form, OBDApp obdApp) {
    super(stage, form);
    this.obdApp=obdApp;
  }

  @Override
  public void setup(Map<String, Object> valueMap) {
    super.setup(valueMap);
    GenericControl serialDeviceControl = super.controls.get("serialDevice");
    Button serialButton = new Button("...");
    grid.add(serialButton, 2, 2);
    serialButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        SerialImpl serial = SerialImpl.getInstance();
        List<String> serialPorts = serial.getSerialPorts(true);
        if (serialPorts.size()==0) {
          SettingsDialog.super.showAlert(I18n.get(I18n.SERIAL_PORT_SELECT), I18n.get(I18n.SERIAL_PORT_NONE_FOUND), I18n.get(I18n.SERIAL_PORT_PLEASE_CONNECT));
        } else {
          ChoiceDialog<String> serialChoices=new ChoiceDialog<String>(serialPorts.get(0),serialPorts);
          serialChoices.setTitle(I18n.get(I18n.SERIAL_PORT_SELECT));
          serialChoices.setHeaderText(I18n.get(I18n.SERIAL_PORT_PLEASE_SELECT));
          Optional<String> result = serialChoices.showAndWait();
          if (result.isPresent()) {
            serialDeviceControl.setValue(result.get());
          }
        }
      }
    });
    Button button = new Button("test Connection");
    button.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        Config config=new Config();
        config.fromMap(SettingsDialog.this.getResult());
        Platform.runLater(()->testConnection(config));
      }
    });
    grid.add(button, 2,0);
  }

  /**
   * test the connection for the given configuration
   * @param config
   */
  protected void testConnection(Config config) {
    // FIXME - use Task/Lamba instead
    Platform.runLater(() ->{
    try {
      ELM327 elm = obdApp.testConnection(config);
      String info=elm.getInfo();
      super.showAlert(I18n.get(I18n.SUCCESS),I18n.get(I18n.CONNECTION_OK), info);
    } catch (Exception e) {
      super.showError(I18n.get(I18n.ERROR), I18n.get(I18n.CONNECTION_FAILED),e.getClass().getSimpleName()+":"+e.getMessage());
      ErrorHandler.handle(e);
    }
    });
  }
}

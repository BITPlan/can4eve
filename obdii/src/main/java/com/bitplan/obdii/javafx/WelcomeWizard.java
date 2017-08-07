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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

import com.bitplan.obdii.I18n;
import com.sun.media.jfxmedia.logging.Logger;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

/**
 * the Welcome wizard
 * 
 * @author wf
 *
 */
public class WelcomeWizard extends Wizard {
  public static final String resourcePath = "/com/bitplan/can4eve/gui/";
  List<WizardPane> pages = new ArrayList<WizardPane>();

  String carSelections[] = { "Citroën C-Zero", "Mitsubishi i-Miev",
      "Misubishi Outlander PHEV", "Peugeot Ion" };
  String carPictures[] = { "c-zero.jpg", "i-miev.jpg", "outlanderphev.jpg",
      "ion.jpg" };
  ImageSelector<String> carSelector = new ImageSelector<String>(carSelections, carPictures);
  String conSelections[] = { "USB", "Wifi", "Bluetooth" };
  String conPictures[] = { "usb.jpg", "wifi.jpg", "bluetooth.jpg" };

  ImageSelector<String>connectionSelector = new ImageSelector<String>(conSelections,
      conPictures);
  private WizardPane carPane;

  /**
   * construct the welcome Wizard
   * 
   * @param title
   * @param pageNames
   * @throws Exception
   */
  public WelcomeWizard(String i18nTitle) {
    setTitle(I18n.get(i18nTitle));
    carPane = new WizardPane();
    carPane.setHeaderText(
        "Welcome to the Can4Eve software!\nPlease select a vehicle");
    // SampleApp.createAndShow("select", selectPane, SHOW_TIME);
    carPane.setContent(carSelector);
    addPage(carPane);
    WizardPane connectionPane = new WizardPane() {
      // behavior on exit of connectionPane
      @Override
      public void onExitingPage(Wizard wizard) {
        wizard.getSettings().put("connection", connectionSelector.getSelection());
      }
    };
    connectionPane.setHeaderText("Please select an OBDII Connection");
    connectionPane.setContent(connectionSelector);
    addPage(connectionPane);
    // wizard.setPages(pageNames);
    prepare();
  }

  /**
   * https://stackoverflow.com/a/45540425/1497139
   * 
   * @param pageNames
   * @throws Exception
   */
  public void setPages(String... pageNames) throws Exception {
    for (String pageName : pageNames) {
      Parent root = FXMLLoader
          .load(getClass().getResource(resourcePath + pageName + ".fxml"));
      WizardPane page = new WizardPane();
      page.setHeaderText(I18n.get(pageName));
      page.setContent(root);
      this.pages.add(page);
    }
  }

  public void addPage(WizardPane page) {
    this.pages.add(page);
  }

  public void prepare() {
    setFlow(new LinearFlow(pages));
  }

  /**
   * display the Wizard and return the results
   * @return the map of settings
   */
  public ObservableMap<String, Object> display() {
    BooleanProperty finished=new SimpleBooleanProperty();
    showAndWait().ifPresent(result -> {
      if (result == ButtonType.FINISH) {
        finished.set(true);
      }
    });
    if (!finished.get())
      this.getSettings().clear();
    return this.getSettings();
  }

  public void close() {
    getDialog().close();
  }
  
  public Dialog getDialog() {
    Field field;
    try {
      field = Wizard.class.getDeclaredField("dialog");
      field.setAccessible(true);
      @SuppressWarnings("rawtypes")
      Dialog dlg = (Dialog) field.get(this);
      return dlg;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
  
  /**
   * get the next Button for the given wizardPane
   * @param wizardPane
   * @return the button
   */
  public Button findNextButton(WizardPane wizardPane) {
    for (Node node:wizardPane.getChildren()) {
      if (node instanceof ButtonBar) {
        ButtonBar buttonBar=(ButtonBar) node;
        for (Node buttonNode:buttonBar.getButtons()) {
          Button button=(Button) buttonNode;
          if (button.getText().startsWith("N")) {
            return button;
          }
        }
      }
    }
    return null;
  }

  /**
   * animate this wizard for testing purposes
   * @param showTime
   * @throws Exception
   */
  public void animate(int showTime) throws Exception {
    for (int i = 0; i < carSelections.length; i++) {
      final int index = i;
      Platform.runLater(
          () -> carSelector.getChoice().getSelectionModel().select(index));
      Thread.sleep(showTime / 2 / carSelections.length);
    }
    Platform.runLater(()->this.findNextButton(carPane).fire());
    // Platform.runLater(()->this.getFlow().advance(carPane));
    Thread.sleep(showTime/2);
    Platform.runLater(() -> close());
  }

  /**
   * wait for the Wizard to show
   * @param mSecs
   * @throws Exception
   */
  public void waitShow(int mSecs) throws Exception {
    int count=0;
    while (getDialog()==null) {
      Thread.sleep(10);
      count+=10;
      if (count>mSecs)
        throw new Exception("dialog wait timed out");
    }
    
  }

}

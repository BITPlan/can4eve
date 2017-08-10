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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

import com.bitplan.can4eve.ErrorHandler;
import com.bitplan.i18n.Translator;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

/**
 * a generic Wizard based on the controls FX Wizard which needs some tweaks to
 * work properly as of 2017-08
 * 
 * @author wf
 *
 */
public class JFXWizard extends Wizard {
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.obdii.javafx");
  public static final String resourcePath = "/com/bitplan/can4eve/gui/";
  List<WizardPane> pages = new ArrayList<WizardPane>();

  public class LoadResult {
    Parent parent;
    Object controller;
  }
  /**
   * load the parent with the given pageName
   * 
   * @param pageName
   * @return the parent
   * @throws Exception
   */
  public LoadResult loadParent(String pageName) {
    LoadResult result=new LoadResult();
    try {
      ResourceBundle resourceBundle = Translator.getBundle();
      URL fxml = getClass().getResource(resourcePath + pageName + ".fxml");
      FXMLLoader fxmlLoader = new FXMLLoader(fxml, resourceBundle);
      result.parent = fxmlLoader.load();
      result.controller = fxmlLoader.getController();
    } catch (Throwable th) {
      ErrorHandler.handle(th);
    }
    return result;
  }

  /**
   * https://stackoverflow.com/a/45540425/1497139
   * 
   * @param pageNames
   * @throws Exception
   */
  public void setPages(String... pageNames) throws Exception {
    int step=0;
    int steps=pageNames.length;
    for (String pageName : pageNames) {
      LoadResult loadResult= loadParent(pageName);
      JFXWizardPane page = new JFXWizardPane(++step,steps, pageName);
      page.setContent(loadResult.parent);
      page.setController(loadResult.controller);
      this.pages.add(page);
    }
  }

  public void addPage(WizardPane page) {
    this.pages.add(page);
  }

  /**
   * prepare me
   */
  public void prepare() {
    setFlow(new LinearFlow(pages));
  }

  /**
   * display the Wizard and return the results
   * 
   * @return the map of settings
   */
  public ObservableMap<String, Object> display() {
    BooleanProperty finished = new SimpleBooleanProperty();
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
    getPrivateDialog().close();
  }

  @SuppressWarnings("rawtypes")
  public Dialog getPrivateDialog() {
    Field field;
    try {
      field = Wizard.class.getDeclaredField("dialog");
      field.setAccessible(true);
      Dialog dlg = (Dialog) field.get(this);
      return dlg;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

 
  /**
   * animate Selections
   * 
   * @param imageSelector
   * @param showTime
   * @throws Exception
   */
  @SuppressWarnings("rawtypes")
  public void animateSelections(ImageSelector imageSelector, int showTime)
      throws Exception {
    Object[] selections = imageSelector.getSelections();
    for (int i = 0; i < selections.length; i++) {
      final int index = i;
      Platform.runLater(
          () -> imageSelector.getChoice().getSelectionModel().select(index));
      Thread.sleep(showTime / selections.length);
    }
  }

  /**
   * wait for the Wizard to show
   * 
   * @param mSecs
   * @throws Exception
   */
  public void waitShow(int mSecs) throws Exception {
    int count = 0;
    while (getPrivateDialog() == null) {
      Thread.sleep(10);
      count += 10;
      if (count > mSecs)
        throw new Exception("dialog wait timed out");
    }

  }
}

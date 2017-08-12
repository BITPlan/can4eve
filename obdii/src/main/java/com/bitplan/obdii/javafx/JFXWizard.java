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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableMap;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

/**
 * a generic Wizard based on the controls FX Wizard which needs some tweaks to
 * work properly as of 2017-08
 * 
 * @author wf
 *
 */
public class JFXWizard extends Wizard {
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.obdii.javafx");
  List<WizardPane> pages = new ArrayList<WizardPane>();

  /**
   * construct me
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public JFXWizard() {

  }

  /**
   * https://stackoverflow.com/a/45540425/1497139
   * 
   * @param pageNames
   * @throws Exception
   */
  public void setPages(String... pageNames) throws Exception {
    int step = 0;
    int steps = pageNames.length;
    for (String pageName : pageNames) {
      JFXWizardPane page = new JFXWizardPane(this,++step, steps, pageName);
      page.load(pageName);
      this.pages.add(page);
    }
  }

  /**
   * add the given page with the given help
   * @param page
   * @param help
   */
  public void addPage(WizardPane page, String help) {
    if ((help!=null) && (page instanceof JFXWizardPane)) {
      JFXWizardPane jfxpage = (JFXWizardPane)page;
      jfxpage.setHelp(help);
    }
    this.pages.add(page);
  }
  
  public void addPage(WizardPane page) {
    addPage(page,null);
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

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public <T> T getPrivate(Class clazz,String fieldName, Object instance) {
    Field field;
    try {
      field = clazz.getDeclaredField(fieldName);
      field.setAccessible(true);
      T result = (T) field.get(instance);
      return result;
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE,"this shouldn't happen - getPrivate failed "+e.getClass().getSimpleName()+" - "+e.getMessage());
      return null;
    }
  }
  
  public Dialog<ButtonType> getPrivateDialog() {
    return getPrivate(Wizard.class,"dialog",this);
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
   * animate this wizard for testing purposes
   * 
   * @param showTime
   * @throws Exception
   */
  public void animate(int showTime) throws Exception {
    int pageTime = showTime / this.pages.size();
    for (WizardPane page : this.pages) {
      if (page instanceof JFXWizardPane) {
        JFXWizardPane jfxpage = (JFXWizardPane) page;
        if (jfxpage.selector != null) {
          animateSelections(jfxpage.selector, pageTime);
        } else {
          Thread.sleep(pageTime);
        }
        ButtonType buttonType = ButtonType.NEXT;
        if (jfxpage.getStep() == jfxpage.getSteps()) {
          buttonType = ButtonType.FINISH;
        }
        final ButtonType buttonToClick = buttonType;
        Platform.runLater(() -> jfxpage.findButton(buttonToClick).fire());
      }
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
  
  /**
   * refresh the internationalization of all wizard pages
   */
  protected void refreshI18n() {
    for (WizardPane page : this.pages) {
      if (page instanceof JFXWizardPane) {
        JFXWizardPane jfxpage = (JFXWizardPane) page;
        jfxpage.refreshI18n();
      }
    }

  }
}

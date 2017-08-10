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
import java.util.logging.Logger;

import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

import com.bitplan.can4eve.ErrorHandler;
import com.bitplan.obdii.I18n;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
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

  public class JFXWizardPane extends WizardPane {
    ImageSelector<String> selector;

    /**
     * construct me with the given title
     * @param i18nTitle
     */
    public JFXWizardPane(String i18nTitle) {
      setHeaderText(I18n.get(i18nTitle));
    }

    /**
     * construct me with the given title and selector
     * @param i18nTitle
     * @param selector
     */
    public JFXWizardPane(String i18nTitle,
        final ImageSelector<String> selector) {
      this(i18nTitle);
      this.selector=selector;
      setContent(selector);
    }

    /**
     * https://bitbucket.org/controlsfx/controlsfx/issues/769/encoding-problem-all-german-umlauts-are
     * 
     * @param wizardPane
     */
    protected void fixNextButton() {
      Button nextButton = findButton(this, ButtonType.NEXT);
      if (nextButton != null) {
        nextButton.setText(ButtonType.NEXT.getText());
      }
    }

    @Override
    public void onEnteringPage(Wizard wizard) {
      fixNextButton();
    }
    
    @Override
    public void onExitingPage(Wizard wizard) {
      if (selector!=null)
        wizard.getSettings().put(selector.getTitle(), selector.getSelection());
    }
  }
  
  /**
   * load the parent with the given pageName
   * @param pageName
   * @return the parent
   * @throws Exception
   */
  public Parent loadParent(String pageName) {
    Parent parent=null;
    try {
      parent = FXMLLoader
          .load(getClass().getResource(resourcePath + pageName + ".fxml"));
    } catch (Throwable th) {
      
      ErrorHandler.handle(th);
    }
    return parent;
  }

  /**
   * https://stackoverflow.com/a/45540425/1497139
   * 
   * @param pageNames
   * @throws Exception
   */
  public void setPages(String... pageNames) throws Exception {
    for (String pageName : pageNames) {
      Parent root=loadParent(pageName);
      WizardPane page = new JFXWizardPane(pageName);
      page.setContent(root);
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
   * get the Button for the given wizardPane
   * 
   * @param wizardPane
   * @return the button
   */
  public Button findButton(WizardPane wizardPane, ButtonType buttonType) {
    for (Node node : wizardPane.getChildren()) {
      if (node instanceof ButtonBar) {
        ButtonBar buttonBar = (ButtonBar) node;
        for (Node buttonNode : buttonBar.getButtons()) {
          Button button = (Button) buttonNode;
          // System.out.println(buttonType.getText() + "<->" +
          // button.getText());
          // https://bitbucket.org/controlsfx/controlsfx/issues/787/next-button-in-wizard-doesnt-show
          if (buttonType.getText().equals(button.getText())
              || (buttonType == ButtonType.NEXT
                  && button.getText().startsWith("N"))) {
            return button;
          }
        }
      }
    }
    return null;
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

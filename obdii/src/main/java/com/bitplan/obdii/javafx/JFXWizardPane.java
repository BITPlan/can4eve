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
import java.util.Map.Entry;
import java.util.ResourceBundle;

import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

import com.bitplan.can4eve.ErrorHandler;
import com.bitplan.i18n.Translator;
import com.bitplan.obdii.I18n;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;

/**
 * a page in a wizard
 * 
 * @author wf
 *
 */
public class JFXWizardPane extends WizardPane {
  public static final String resourcePath = "/com/bitplan/can4eve/gui/";
  /**
   * 
   */
  ImageSelector<String> selector;
  private String i18nTitle;
  private int step;
  private int steps;
  protected Object controller;
  private String pageName;

  public String getI18nTitle() {
    return i18nTitle;
  }

  public void setI18nTitle(String i18nTitle) {
    this.i18nTitle = i18nTitle;
    refreshI18n();
  }

  /**
   * construct me with the given title
   * 
   * @param i18nTitle
   */
  public JFXWizardPane(int step, int steps, String i18nTitle) {
    this.step = step;
    this.steps = steps;
    this.setI18nTitle(i18nTitle);
  }

  /**
   * refresh my Internationalization content
   */
  public void refreshI18n() {
    setHeaderText(I18n.get(I18n.WELCOME_STEP, step, steps) + "\n\n"
        + I18n.get(i18nTitle));
    this.fixButtons();
  }

  /**
   * construct me with the given title and selector
   * 
   * @param i18nTitle
   * @param selector
   */
  public JFXWizardPane(int step, int steps, String i18nTitle,
      final ImageSelector<String> selector) {
    this(step, steps, i18nTitle);
    this.selector = selector;
    setContent(selector);
  }

  /**
   * https://bitbucket.org/controlsfx/controlsfx/issues/769/encoding-problem-all-german-umlauts-are
   * 
   * @param wizardPane
   */
  protected void fixButtons() {
    ButtonType buttonTypes[] = { ButtonType.NEXT, ButtonType.PREVIOUS,
        ButtonType.CANCEL, ButtonType.FINISH };
    for (ButtonType buttonType : buttonTypes) {
      Button button = findButton(buttonType);
      if (button != null) {
        button.setText(buttonType.getText());
      }
    }
  }

  /**
   * get the Button for the given buttonType
   * @return the button
   */
  public Button findButton(ButtonType buttonType) {
    for (Node node : getChildren()) {
      if (node instanceof ButtonBar) {
        ButtonBar buttonBar = (ButtonBar) node;
        ObservableList<Node> buttons = buttonBar.getButtons();
        for (Node buttonNode : buttons) {
          Button button = (Button) buttonNode;
          @SuppressWarnings("unchecked")
          ObjectProperty<ButtonData> prop = (ObjectProperty<ButtonData>) button
              .getProperties().get("javafx.scene.control.ButtonBar.ButtonData");
          ButtonData buttonData = prop.getValue();
          if (buttonData.equals(buttonType.getButtonData())) {
            return button;
          }
        }
      }
    }
    return null;
  }

  @Override
  public void onEnteringPage(Wizard wizard) {
    fixButtons();
  }

  @Override
  public void onExitingPage(Wizard wizard) {
    if (selector != null)
      wizard.getSettings().put(selector.getTitle(), selector.getSelection());
  }

  public void setController(Object controller) {
    this.controller = controller;
  }

  /**
   * load me from the given fxml pageName
   * 
   * @param pageName
   */
  public void load(String pageName) {
    this.pageName = pageName;
    try {
      ResourceBundle resourceBundle = Translator.getBundle();
      URL fxml = getClass().getResource(resourcePath + pageName + ".fxml");
      FXMLLoader fxmlLoader = new FXMLLoader(fxml, resourceBundle);
      Parent parent = fxmlLoader.load();
      this.setContent(parent);
      controller = fxmlLoader.getController();
    } catch (Throwable th) {
      ErrorHandler.handle(th);
    }
  }
}
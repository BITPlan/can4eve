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

import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

import com.bitplan.obdii.I18n;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

/**
 * a page in a wizard
 * 
 * @author wf
 *
 */
public class JFXWizardPane extends WizardPane {
  /**
   * 
   */
  ImageSelector<String> selector;
  private String i18nTitle;
  private int step;
  private int steps;
  protected Object controller;

  /**
   * construct me with the given title
   * 
   * @param i18nTitle
   */
  public JFXWizardPane(int step, int steps, String i18nTitle) {
    this.step = step;
    this.steps = steps;
    this.i18nTitle = i18nTitle;
  }

  public void refreshI18n() {
    setHeaderText(I18n.get(i18nTitle));
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
   * get the Button for the given wizardPane
   * 
   * @param wizardPane
   * @return the button
   */
  public Button findButton(ButtonType buttonType) {
    for (Node node : getChildren()) {
      if (node instanceof ButtonBar) {
        ButtonBar buttonBar = (ButtonBar) node;
        ObservableList<Node> buttons = buttonBar.getButtons();
        Field field;
        int index = -1;
        try {
          field = ButtonType.class.getDeclaredField("key");
          field.setAccessible(true);
          String key = (String) field.get(buttonType);
          switch (key) {
          case "Dialog.next.button":
            if (step < steps)
              index = 1;
            break;
          case "Dialog.previous.button":
            index = 0;
            break;
          case "Dialog.cancel.button":
            index = 2;
            break;
          case "Dialog.finish.button":
            if (step == steps)
              index = 1;
            break;
          }
        } catch (Throwable th) {

        }
        if (index >= 0 && buttons.size() > index) {
          Node buttonNode = buttons.get(index);
          Button button = (Button) buttonNode;
          // System.out.println(buttonType.getText() + "<->" +
          // button.getText());
          // https://bitbucket.org/controlsfx/controlsfx/issues/787/next-button-in-wizard-doesnt-show
          return button;
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
}
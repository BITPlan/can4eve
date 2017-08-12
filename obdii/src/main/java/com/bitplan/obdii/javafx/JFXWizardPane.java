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
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import com.bitplan.can4eve.ErrorHandler;
import com.bitplan.can4eve.gui.Linker;
import com.bitplan.i18n.Translator;
import com.bitplan.obdii.I18n;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.GridPane;

/**
 * a page in a wizard
 * 
 * @author wf
 *
 */
public class JFXWizardPane extends WizardPane {
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.obdii.javafx");
  public static Linker linker;
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
  private GlyphFont fontAwesome;
  private JFXWizard wizard;
  private Button helpButton;
  private GridPane gridPane;
  private Node contentNode;
  private static ButtonType helpButtonType;

  public int getStep() {
    return step;
  }

  public void setStep(int step) {
    this.step = step;
  }

  public int getSteps() {
    return steps;
  }

  public void setSteps(int steps) {
    this.steps = steps;
  }

  public String getI18nTitle() {
    return i18nTitle;
  }

  public void setI18nTitle(String i18nTitle) {
    this.i18nTitle = i18nTitle;
    refreshI18n();
  }
  
  public static void setLinker(Linker pLinker) {
    linker=pLinker;
  }

  /**
   * construct me with the given title
   * 
   * @param i18nTitle
   */
  public JFXWizardPane(JFXWizard wizard,int step, int steps, String i18nTitle) {
    this.wizard=wizard;
    fontAwesome = GlyphFontRegistry.font("FontAwesome");
    if (helpButtonType == null)
      helpButtonType = new ButtonType(I18n.get(I18n.HELP),
          ButtonBar.ButtonData.HELP);
    this.setStep(step);
    this.setSteps(steps);
    gridPane=new GridPane();
    setContent(gridPane);
    this.setI18nTitle(i18nTitle);
  }

  /**
   * set the Help
   * 
   * @param help
   */
  public void setHelp(String help) {
    //this.getButtonTypes().add(helpButtonType);
    //helpButton = this.findButton(helpButtonType);
    helpButton=new Button(I18n.get(I18n.HELP));
    //wizard.getPrivateDialog().getDialogPane().getButtonTypes().add(helpButtonType);
    //getPrivateButtonBar().getButtons().add(0, helpButton);
    gridPane.add(helpButton, 0, 1);
    Glyph helpIcon = fontAwesome.create(FontAwesome.Glyph.QUESTION_CIRCLE);
    helpButton.setGraphic(helpIcon);
    
    helpButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(final ActionEvent actionEvent) {
        linker.browse(help);
        actionEvent.consume();
      }
    });
  }

  /**
   * refresh my Internationalization content
   */
  public void refreshI18n() {
    setHeaderText(I18n.get(I18n.WELCOME_STEP, getStep(), getSteps()) + "\n\n"
        + I18n.get(i18nTitle));
    this.fixButtons();
  }

  /**
   * construct me with the given title and selector
   * 
   * @param i18nTitle
   * @param selector
   */
  public JFXWizardPane(JFXWizard wizard,int step, int steps, String i18nTitle,
      final ImageSelector<String> selector) {
    this(wizard,step, steps, i18nTitle);
    this.selector = selector;
    setContentNode(selector);
  }

  /**
   * https://bitbucket.org/controlsfx/controlsfx/issues/769/encoding-problem-all-german-umlauts-are
   * 
   * @param wizardPane
   */
  protected void fixButtons() {
    ButtonType buttonTypes[] = { ButtonType.NEXT, ButtonType.PREVIOUS,
        ButtonType.CANCEL, ButtonType.FINISH };
    Glyph glyphs[] = { fontAwesome.create(FontAwesome.Glyph.CHEVRON_RIGHT),
        fontAwesome.create(FontAwesome.Glyph.CHEVRON_LEFT),
        fontAwesome.create(FontAwesome.Glyph.TIMES),
        fontAwesome.create(FontAwesome.Glyph.CHECK) };
    int index = 0;
    for (ButtonType buttonType : buttonTypes) {
      Button button = findButton(buttonType);
      if (button != null) {
        button.setText(buttonType.getText());
        button.setGraphic(glyphs[index]);
      }
      index++;
    }
    if (helpButton!=null)
      helpButton.setText(I18n.get(I18n.HELP));
  }

  /**
   * get the Button for the given buttonType
   * 
   * @return the button
   */
  @SuppressWarnings("rawtypes")
  public Button findButton(ButtonType buttonType) {
    /* Dialog dialog=wizard.getPrivateDialog();
    return (Button) dialog.getDialogPane().lookupButton(buttonType);
    */
   
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
    // LOGGER.log(Level.WARNING,"find Button failed in step "+step+" of "+steps);
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
      this.setContentNode(parent);
      controller = fxmlLoader.getController();
    } catch (Throwable th) {
      ErrorHandler.handle(th);
    }
  }
  
  public void setContentNode(Node parent) {
    if (contentNode!=null)
      gridPane.getChildren().remove(contentNode);
    gridPane.add(parent, 0, 0);
    contentNode=parent;
  }
  
  /**
   * get the button Bar
   * @return
   */
  public ButtonBar getPrivateButtonBar() {
    Node buttonBar=wizard.getPrivate(DialogPane.class,"buttonBar",this);
    return (ButtonBar) buttonBar;
  }

}
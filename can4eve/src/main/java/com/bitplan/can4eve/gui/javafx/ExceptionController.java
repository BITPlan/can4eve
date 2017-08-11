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
package com.bitplan.can4eve.gui.javafx;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bitplan.can4eve.ExceptionHandler;
import com.bitplan.can4eve.SoftwareVersion;
import com.bitplan.can4eve.gui.App;
import com.bitplan.can4eve.gui.ExceptionHelp;
import com.bitplan.can4eve.gui.Linker;
import com.bitplan.i18n.Translator;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;

/**
 * Controller for ExceptionHandling
 * @author wf
 *
 */
public class ExceptionController  implements Initializable, ExceptionHandler {
  protected static Logger LOGGER = Logger
      .getLogger("com.bitplan.can4eve.gui.javafx");
  @FXML
  DialogPane dialogPane;
  
  @FXML
  Button reportIssueButton;
  
  @FXML
  TextArea textArea;
  
  @FXML
  Label contentLabel;
  
  private static App app;
  private static SoftwareVersion softwareVersion;
  private static Linker linker;
  
  public static void setApp(App papp) {
    app=papp;
  }
  
  public static void setSoftwareVersion(SoftwareVersion pSoftwareVersion) {
    softwareVersion=pSoftwareVersion;
  }
  
  public static void setLinker(Linker pLinker) {
    linker=pLinker;
  }
  
  @Override
  public void initialize(URL location, ResourceBundle resources) {

    
  }
  
  /**
   * get the flowPane for the Exception Help
   * 
   * @param ehelp
   * @param linker
   */
  public static FlowPane getFlowPane(ExceptionHelp ehelp, Linker linker) {
    FlowPane fp = new FlowPane();
    Label lbl = new Label(Translator.translate(ehelp.getI18nHint()));
    Hyperlink link = new Hyperlink(Translator.translate("help"));

    fp.getChildren().addAll(lbl, link);
    link.setOnAction((evt) -> {
      linker.browse(ehelp.getUrl());
    });
    return fp;
  }


  /**
   * handle the given Exception
   * @param th
   * @param dialogPane
   * @param reportIssueButton
   * @param textArea
   * @param stringProperty
   */
  public static void handleException(Throwable th, DialogPane dialogPane,Button reportIssueButton, TextArea textArea, StringProperty contentProperty) {
    String exceptionText = GenericDialog.getStackTraceText(th);
    LOGGER.log(Level.INFO, exceptionText);
    reportIssueButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(final ActionEvent e) {
        GenericDialog.sendReport(softwareVersion, "can4eve issue",
            "There seems to be trouble with the exception:\n" + exceptionText);
      }
    });
    ExceptionHelp ehelp = app.getExceptionHelpByName(th.getClass().getName()+":"+th.getMessage());  
    if (ehelp != null) {
      FlowPane flowPane = getFlowPane(ehelp, linker);
      dialogPane.contentProperty().set(flowPane);
    } else {
      String errMessage = th.getClass().getSimpleName() + ":\n"
          + th.getLocalizedMessage();
      contentProperty.setValue(errMessage);
    }
    Platform.runLater(()->textArea.setText(exceptionText));
  }
  
  /**
   * handle the given exception
   */
  public void handleException(Throwable th) {
    ExceptionController.handleException(th, dialogPane, reportIssueButton, textArea, contentLabel.textProperty()); 
  }  
 
}

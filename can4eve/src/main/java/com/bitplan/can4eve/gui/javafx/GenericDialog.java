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

import java.awt.Desktop;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bitplan.can4eve.ErrorHandler;
import com.bitplan.can4eve.SoftwareVersion;
import com.bitplan.can4eve.gui.Form;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

/**
 * Generic Dialog
 * 
 * @author wf
 */
public class GenericDialog {
  protected static Logger LOGGER = Logger
      .getLogger("com.bitplan.can4eve.gui.javafx");
  private Form form;
  private Stage stage;
  protected Map<String, GenericControl> controls;
  private Dialog<Map<String, Object>> dialog;
  private ButtonType okButtonType;
  protected GridPane grid;

  /**
   * construct me from the given form description
   * 
   * @param form
   */
  public GenericDialog(Stage stage, Form form) {
    this.stage = stage;
    this.form = form;
  }

  /**
   * get the Fields for the given form
   * 
   * @param grid
   * @param form
   * @param ypos
   * @return - the Map of fields
   */
  public static Map<String, GenericControl> getFields(Stage stage,
      GridPane grid, Form form, int ypos) {
    Map<String, GenericControl> controls = new HashMap<String, GenericControl>();
    for (com.bitplan.can4eve.gui.Field field : form.getFields()) {
      GenericControl gcontrol = GenericControl.create(stage, field);
      int x=0;
      int y=ypos;
      if (field.getGridX()!=null)
        x=field.getGridX();
      if (field.getGridY()!=null)
        y=field.getGridY();
      grid.add(gcontrol.label, x, y);
      if (gcontrol.control != null) {
        grid.add(gcontrol.control, x+1, y);
      }
      if (gcontrol.button != null) {
        grid.add(gcontrol.button, x+2, y);
      }
      if (field.getGridY()==null)
        ypos++;
      controls.put(field.getId(), gcontrol);
    }
    return controls;
  }

  /**
   * setup the control according to the given valueMap
   * 
   * @param valueMap
   */
  public void setup(Map<String, Object> valueMap) {
    // Create the custom dialog.
    dialog = new Dialog<Map<String, Object>>();
    dialog.setTitle(form.getTitle());
    dialog.setHeaderText(form.getHeaderText());

    // Set the icon (must be included in the project).
    URL iconUrl = this.getClass()
        .getResource("/icons/" + form.getIcon() + ".png");
    if (iconUrl != null)
      dialog.setGraphic(new ImageView(iconUrl.toString()));

    // Set the button types.
    okButtonType = new ButtonType("Ok", ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(okButtonType,
        ButtonType.CANCEL);

    // Create labels and fields.
    grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    int ypos = 0;
    controls = getFields(stage, grid, form, ypos);
    dialog.getDialogPane().setContent(grid);
    if (valueMap != null) {
      for (GenericControl control : controls.values()) {
        control.setValue(valueMap.get(control.field.getId()));
      }
    }
  }

  /**
   * get the result
   * 
   * @return
   */
  public Map<String, Object> getResult() {
    Map<String, Object> result = new HashMap<String, Object>();
    for (com.bitplan.can4eve.gui.Field field : form.getFields()) {
      GenericControl gcontrol = controls.get(field.getId());
      result.put(field.getId(), gcontrol.getValue());
    }
    return result;
  }

  /**
   * show this form with the given values
   * 
   * @return
   */
  public Optional<Map<String, Object>> show(Map<String, Object> valueMap) {
    setup(valueMap);

    // Request focus on the first field by default.
    final GenericControl focusField = controls
        .get(form.getFields().get(0).getId());
    Platform.runLater(() -> focusField.control.requestFocus());

    // Convert the result to a username-password-pair when the login button is
    // clicked.
    dialog.setResultConverter(dialogButton -> {
      if (dialogButton == okButtonType) {
        return getResult();
      }
      return null;
    });

    Optional<Map<String, Object>> result = dialog.showAndWait();
    return result;
  }

  /**
   * show me with no values predefined
   * 
   * @return - the map of new values
   */
  public Optional<Map<String, Object>> show() {
    return show(null);
  }

  /**
   * show the given alert
   * 
   * @param title
   * @param headerText
   * @param content
   */
  public static void showAlert(String title, String headerText,
      String content) {
    showAlert(title, headerText, content, AlertType.INFORMATION);
  }

  /**
   * show an Error
   * 
   * @param title
   * @param headerText
   * @param content
   */
  public static void showError(String title, String headerText,
      String content) {
    showAlert(title, headerText, content, AlertType.ERROR);
  }

  /**
   * encode the given text
   * 
   * @param text
   * @return the given text
   */
  public static String urlEncode(String text) {
    String encoded;
    try {
      encoded = URLEncoder.encode(text, "utf-8").replace("+", "%20");
    } catch (UnsupportedEncodingException e) {
      // no way
      return "";
    }
    return encoded;
  }

  /**
   * send a Report
   * 
   * @param softwareVersion.getSupportEMail()
   * @param subject
   * @param body
   * @throws IOException
   */
  public static void sendReport(SoftwareVersion softwareVersion, String subject,
      String body) {
    Desktop desktop = Desktop.getDesktop();
    // TODO check max message body
    // if (body.length()>100) {
    try {
      StringBuilder builder = new StringBuilder();
      builder.append("mailto:" + softwareVersion.getSupportEMail() + "?subject="
          + urlEncode(subject) + "&body=");
      // encodes the stack trace in a mailto URI friendly form
      // https://stackoverflow.com/a/749829/1497139
      // https://stackoverflow.com/a/30260197/1497139
      // https://stackoverflow.com/q/9435835/1497139
      String preamble = softwareVersion.getSupportEMailPreamble();
      builder.append(urlEncode(preamble));
      builder.append(urlEncode(body));
      URI uri = URI.create(builder.toString());

      desktop.mail(uri);
    } catch (Exception e) {
      ErrorHandler.handle(e);
    }
  }

  /**
   * get the stack trace for the given exception
   * 
   * @param th
   * @return - the stack trace
   */
  public static String getStackTraceText(Throwable th) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    th.printStackTrace(pw);
    String exceptionText = sw.toString();
    return exceptionText;
  }

  /**
   * show the Exception
   * 
   * @param title
   * @param headerText
   * @param th
   * @param softwareVersion
   *          - the mail address to send exceptions to
   */
  public static void showException(String title, String headerText,
      Throwable th, SoftwareVersion softwareVersion) {
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(headerText);
    String exceptionText = getStackTraceText(th);
    LOGGER.log(Level.INFO, exceptionText);

    alert.setContentText(
        th.getClass().getSimpleName() + ":\n" + th.getLocalizedMessage());
    Label label = new Label("The exception stacktrace is:");

    Button button = new Button("Report Issue ...");
    button.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(final ActionEvent e) {
        GenericDialog.sendReport(softwareVersion, "can4eve issue",
            "There seems to be trouble with the exception:\n" + exceptionText);
      }
    });

    TextArea textArea = new TextArea(exceptionText);
    textArea.setEditable(false);
    textArea.setWrapText(true);

    textArea.setMaxWidth(Double.MAX_VALUE);
    textArea.setMaxHeight(Double.MAX_VALUE);
    GridPane.setVgrow(textArea, Priority.ALWAYS);
    GridPane.setHgrow(textArea, Priority.ALWAYS);

    GridPane expContent = new GridPane();
    expContent.setMaxWidth(Double.MAX_VALUE);

    expContent.add(button, 0, 0);
    expContent.add(label, 0, 1);
    expContent.add(textArea, 0, 2);

    // Set expandable Exception into the dialog pane.
    alert.getDialogPane().setExpandableContent(expContent);

    alert.showAndWait();
  }

  /**
   * show an alert
   * 
   * @param title
   * @param headerText
   * @param content
   * @param alertType
   */
  public static void showAlert(String title, String headerText, String content,
      AlertType alertType) {
    // make sure the showAndWait is on the FX thread - even if a little later:-)
    Alert alert = new Alert(alertType);
    alert.setTitle(title);
    alert.setHeaderText(headerText);
    alert.setContentText(content);
    alert.showAndWait();
  }

}

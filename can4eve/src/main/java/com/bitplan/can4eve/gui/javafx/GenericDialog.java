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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.bitplan.can4eve.gui.Form;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

/**
 * Generic Dialog
 * 
 * @author wf
 */
public class GenericDialog {
  private Form form;

  /**
   * construct me from the given form description
   * 
   * @param form
   */
  public GenericDialog(Form form) {
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
  public static Map<String, Control> getFields(GridPane grid, Form form,
      int ypos) {
    Map<String, Control> controls = new HashMap<String, Control>();
    for (com.bitplan.can4eve.gui.Field field : form.getFields()) {
      if (field.getFieldKind() == null) {
        Control control=null;
        if (field.getType()==null) {
          TextField tfield = new TextField();
          tfield.setPromptText(field.getTitle());
          control=tfield;
        }
        grid.add(new Label(field.getTitle() + ":"), 0, ypos);
        if (control!=null) {
          grid.add(control, 1, ypos++);
          controls.put(field.getId(), control);
        }
      }
    }
    return controls;
  }

  /**
   * show this form
   * 
   * @return
   */
  public Optional<Map<String,Object>> show() {
    // Create the custom dialog.
    Dialog<Map<String, Object>> dialog = new Dialog<>();
    dialog.setTitle(form.getTitle());
    dialog.setHeaderText(form.getHeaderText());

    // Set the icon (must be included in the project).
    URL iconUrl = this.getClass()
        .getResource("/icons/" + form.getIcon() + ".png");
    if (iconUrl != null)
      dialog.setGraphic(new ImageView(iconUrl.toString()));

    // Set the button types.
    ButtonType okButton = new ButtonType("Ok", ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(okButton,
        ButtonType.CANCEL);

    // Create labels and fields.
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    int ypos = 0;
    Map<String, Control> controls = getFields(grid, form, ypos);
    dialog.getDialogPane().setContent(grid);

    // Request focus on the first field by default.
    final Control focusField = controls
        .get(form.getFields().get(0).getId());
    Platform.runLater(() -> focusField.requestFocus());

    // Convert the result to a username-password-pair when the login button is
    // clicked.
    dialog.setResultConverter(dialogButton -> {
      if (dialogButton == okButton) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (com.bitplan.can4eve.gui.Field field : form.getFields()) {
          Control control = controls.get(field.getId());
          result.put(field.getId(), getValue(control));
        }
        return result;
      }
      return null;
    });

    Optional<Map<String, Object>> result = dialog.showAndWait();
    return result;
  }

  private Object getValue(Control control) {
    if (control instanceof TextField) {
      TextField tfield=(TextField) control;
      return tfield.getText();
    }
    return null;
  }
}

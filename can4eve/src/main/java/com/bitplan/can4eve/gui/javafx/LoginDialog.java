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
import java.util.Optional;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

/**
 * Login Dialog
 * @author wf
 * see http://code.makery.ch/blog/javafx-dialogs-official/
 */
public class LoginDialog {
  /**
   * show the Login dialog
   * @return
   */
  public Optional<Pair<String, String>> show() {
    // Create the custom dialog.
    Dialog<Pair<String, String>> dialog = new Dialog<>();
    dialog.setTitle("Login");
    dialog.setHeaderText("Choose your credentials");

    // Set the icon (must be included in the project).
    URL iconUrl = this.getClass().getResource("/icons/login.png");
    if (iconUrl!=null)
      dialog.setGraphic(new ImageView(iconUrl.toString()));

    // Set the button types.
    ButtonType loginButtonType = new ButtonType("Login", ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

    // Create the username and password labels and fields.
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    TextField username = new TextField();
    username.setPromptText("Username");
    PasswordField password = new PasswordField();
    password.setPromptText("Password");

    grid.add(new Label("Username:"), 0, 0);
    grid.add(username, 1, 0);
    grid.add(new Label("Password:"), 0, 1);
    grid.add(password, 1, 1);

    // Enable/Disable login button depending on whether a username was entered.
    Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
    loginButton.setDisable(true);

    // Do some validation (using the Java 8 lambda syntax).
    username.textProperty().addListener((observable, oldValue, newValue) -> {
        loginButton.setDisable(newValue.trim().isEmpty() || password.getText().isEmpty());
    });
    password.textProperty().addListener((observable, oldValue, newValue) -> {
      loginButton.setDisable(newValue.trim().isEmpty() || username.getText().isEmpty());
    });

    dialog.getDialogPane().setContent(grid);

    // Request focus on the username field by default.
    Platform.runLater(() -> username.requestFocus());

    // Convert the result to a username-password-pair when the login button is clicked.
    dialog.setResultConverter(dialogButton -> {
        if (dialogButton == loginButtonType) {
            return new Pair<>(username.getText(), password.getText());
        }
        return null;
    });

    Optional<Pair<String, String>> result = dialog.showAndWait();
    return result;
  }
}

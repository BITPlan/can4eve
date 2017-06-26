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
package com.bitplan.obdii;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bitplan.can4eve.CANValue;
import com.bitplan.can4eve.SoftwareVersion;
import com.bitplan.can4eve.gui.App;
import com.bitplan.can4eve.gui.Form;
import com.bitplan.can4eve.gui.javafx.GenericDialog;
import com.bitplan.can4eve.gui.javafx.GenericPanel;
//import com.bitplan.can4eve.gui.javafx.LoginDialog;
import com.bitplan.can4eve.gui.swing.JLink;
import com.bitplan.can4eve.gui.swing.Translator;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Java FX Display
 * 
 * @author wf
 *
 */
public class JavaFXDisplay extends Application
    implements Display, CANValueDisplay, EventHandler<ActionEvent> {
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.obdii");

  private static com.bitplan.can4eve.gui.App app;
  private static SoftwareVersion softwareVersion;
  private MenuBar menuBar;
  protected Stage stage;

  private VBox root;

  private TabPane tabPane;

  public SoftwareVersion getSoftwareVersion() {
    return softwareVersion;
  }

  public static void setSoftwareVersion(SoftwareVersion softwareVersion) {
    JavaFXDisplay.softwareVersion = softwareVersion;
  }

  public com.bitplan.can4eve.gui.App getApp() {
    return app;
  }

  public static void setApp(com.bitplan.can4eve.gui.App app) {
    JavaFXDisplay.app = app;
  }

  public static JavaFXDisplay instance;

  public JavaFXDisplay() {
    instance = this;
  }

  /**
   * get the instance
   * 
   * @return
   */
  public static JavaFXDisplay getInstance() {
    if (instance == null) {
      String[] args = {};
      Application.launch(JavaFXDisplay.class, args);
    }
    return instance;
  }

  @Override
  public void show() {
    // https://stackoverflow.com/a/36805921/1497139
    // Platform.runLater(() ->
    // stage.setTitle(softwareVersion.getName()+"
    // "+softwareVersion.getVersion())
    // );
  }

  @Override
  public LabelField addField(String title, String format, int labelSize,
      int fieldSize) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void updateField(String title, Object value, int updateCount) {
    // TODO Auto-generated method stub

  }

  @Override
  public void addCANValueField(CANValue<?> canValue) {
    // TODO Auto-generated method stub

  }

  @Override
  public void addCanValueFields(Collection<CANValue<?>> list) {
    // TODO Auto-generated method stub

  }

  @Override
  public void updateCanValueField(CANValue<?> canValue) {
    // TODO Auto-generated method stub

  }

  @Override
  public void waitOpen() {
    // not necessary - we are launched
  }

  @Override
  public void waitClose() {
    // not necessary?
  }

  /**
   * create the Menu Bar
   * 
   * @param scene
   */
  public void createMenuBar(Scene scene) {
    menuBar = new MenuBar();
    for (com.bitplan.can4eve.gui.Menu amenu : app.getMainMenu().getSubMenus()) {
      Menu menu = new Menu(Translator.translate(amenu.getId()));
      menuBar.getMenus().add(menu);
      for (com.bitplan.can4eve.gui.MenuItem amenuitem : amenu.getMenuItems()) {
        MenuItem menuItem = new MenuItem(
            Translator.translate(amenuitem.getId()));
        menuItem.setOnAction(this);
        menuItem.setId(amenuitem.getId());
        menu.getItems().add(menuItem);
      }
    }
    ((VBox) scene.getRoot()).getChildren().addAll(menuBar);
  }

  @Override
  public void start(Stage stage) throws Exception {
    stage.setTitle(
        softwareVersion.getName() + " " + softwareVersion.getVersion());
    this.stage = stage;
    Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
    root=new VBox();
    Scene scene = new Scene(root, primScreenBounds.getWidth() / 2,
        primScreenBounds.getHeight() / 2);
    scene.setFill(Color.OLDLACE);
    createMenuBar(scene);
    stage.setScene(scene);
    setup(app);
    stage.show();
    stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
    stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 4);
  }

  /**
   * setup the Application
   * @param app
   */
  private void setup(App app) {
    tabPane = new TabPane();
    tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
    for (Form form:app.getForms()) {
      Tab tab=new Tab();
      tab.setText(form.getTitle());
      GenericPanel panel=new GenericPanel(form);
      tab.setContent(panel);
      tabPane.getTabs().add(tab);
    }
    root.getChildren().add(tabPane);
  }

  @Override
  public void handle(ActionEvent event) {
    Object source = event.getSource();
    if (source instanceof MenuItem) {
      MenuItem menuItem = (MenuItem) source;
      if ("quitMenuItem".equals(menuItem.getId())) {
        Platform.exit();
      } else if ("aboutMenuItem".equals(menuItem.getId())) {
        showAbout();
      } else if ("feedbackMenuItem".equals(menuItem.getId())) {
        showFeedback();
      } else if ("preferencesMenuItem".equals(menuItem.getId())) {
        showPreferences();
      } else {
        LOGGER.log(Level.WARNING, "unhandled menu item " + menuItem.getId()
            + ":" + menuItem.getText());
      }
    }
  }

  /**
   * show an About dialog
   */
  private void showAbout() {
    Alert alert = new Alert(AlertType.INFORMATION);
    alert.setTitle("About");
    String title = softwareVersion.getName() + " "
        + softwareVersion.getVersion();
    alert.setHeaderText(title);
    alert.setContentText(softwareVersion.getUrl());
    alert.showAndWait();
  }

  public void showFeedback() {
    try {
      JLink.open(App.getInstance().getFeedback());
    } catch (Exception e) {
      ErrorHandler.handle(e);
    }
  }

  /**
   * show the preferences
   */
  public void showPreferences() {
    /*
     * LoginDialog loginDialog=new LoginDialog(); loginDialog.show();
     * result.ifPresent(usernamePassword -> { System.out.println("Username=" +
     * usernamePassword.getKey() + ", Password=" + usernamePassword.getValue());
     * });
     */
    GenericDialog preferencesDialog = new GenericDialog(app.getFormById("preferencesForm"));
    Optional<Map<String, String>> result = preferencesDialog.show();
  }

}

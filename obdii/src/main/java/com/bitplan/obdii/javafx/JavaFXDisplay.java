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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bitplan.can4eve.CANValue;
import com.bitplan.can4eve.SoftwareVersion;
import com.bitplan.can4eve.gui.App;
import com.bitplan.can4eve.gui.Form;
import com.bitplan.can4eve.gui.Group;
import com.bitplan.can4eve.gui.javafx.GenericControl;
import com.bitplan.can4eve.gui.javafx.GenericDialog;
import com.bitplan.can4eve.gui.javafx.GenericPanel;
import com.bitplan.can4eve.gui.javafx.WaitableApp;
//import com.bitplan.can4eve.gui.javafx.LoginDialog;
import com.bitplan.can4eve.gui.swing.JLink;
import com.bitplan.can4eve.gui.swing.Translator;
import com.bitplan.elm327.Config;
import com.bitplan.elm327.Config.ConfigMode;
import com.bitplan.obdii.CANValueDisplay;
import com.bitplan.obdii.ErrorHandler;
import com.bitplan.obdii.LabelField;
import com.bitplan.obdii.OBDApp;
import com.bitplan.obdii.Preferences;
import com.bitplan.obdii.Preferences.LangChoice;

import javafx.application.Platform;
//import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SingleSelectionModel;
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
public class JavaFXDisplay extends WaitableApp
    implements CANValueDisplay, EventHandler<ActionEvent> {
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.obdii.javafx");

  private static com.bitplan.can4eve.gui.App app;
  OBDApp obdApp;
  private static SoftwareVersion softwareVersion;
  private MenuBar menuBar;

  private VBox root;
  private TabPane tabPane;

  private Map<String, GenericControl> controls;
  protected boolean available;
  public static final boolean debug = false;

  /**
   * construct me from an abstract application description and a software
   * version
   * 
   * @param app
   *          - the generic gui application description
   * @param softwareVersion
   * @param obdApp
   */
  public JavaFXDisplay(App app, SoftwareVersion softwareVersion,
      OBDApp obdApp) {
    toolkitInit();
    this.obdApp = obdApp;
    // new JFXPanel();
    this.setApp(app);
    this.setSoftwareVersion(softwareVersion);
  }

  public SoftwareVersion getSoftwareVersion() {
    return softwareVersion;
  }

  public void setSoftwareVersion(SoftwareVersion softwareVersion) {
    JavaFXDisplay.softwareVersion = softwareVersion;
  }

  public com.bitplan.can4eve.gui.App getApp() {
    return app;
  }

  public void setApp(com.bitplan.can4eve.gui.App app) {
    JavaFXDisplay.app = app;
  }

  @Override
  public LabelField addField(String title, String format, int labelSize,
      int fieldSize) {
    // ignore this
    return null;
  }

  @Override
  public void addCANValueField(CANValue<?> canValue) {

  }

  @Override
  public void addCanValueFields(Collection<CANValue<?>> list) {
    // TODO Auto-generated method stub

  }

  @Override
  public void updateField(String title, Object value, int updateCount) {
    if (controls == null)
      return;
    GenericControl control = controls.get(title);
    if (control == null) {
      if (!title.startsWith("Raw"))
        LOGGER.log(Level.WARNING, "could not find field " + title);
    } else {
      Platform.runLater(() -> control.setValue(value));
    }

  }

  /**
   * get the title of the active Panel
   * 
   * @return - the activeTab
   */
  public Tab getActiveTab() {
    SingleSelectionModel<Tab> smodel = tabPane.getSelectionModel();
    Tab selectedTab = smodel.getSelectedItem();
    return selectedTab;
  }

  @Override
  public void updateCanValueField(CANValue<?> canValue) {
    String title = canValue.canInfo.getTitle();
    if (canValue.canInfo.getMaxIndex() == 0) {
      updateField(title, canValue.asString(), canValue.getUpdateCount());
    } else {
      // TODO - generic solution?
    }
  }

  /**
   * create the Menu Bar
   * 
   * @param scene
   */
  public void createMenuBar(Scene scene) {
    menuBar = new MenuBar();
    for (com.bitplan.can4eve.gui.Menu amenu : app.getMainMenu().getSubMenus()) {
      Menu menu = new Menu(i18n(amenu.getId()));
      menuBar.getMenus().add(menu);
      for (com.bitplan.can4eve.gui.MenuItem amenuitem : amenu.getMenuItems()) {
        MenuItem menuItem = new MenuItem(i18n(amenuitem.getId()));
        menuItem.setOnAction(this);
        menuItem.setId(amenuitem.getId());
        menu.getItems().add(menuItem);
      }
    }
    ((VBox) scene.getRoot()).getChildren().addAll(menuBar);
  }

  @Override
  public void start(Stage stage) {
    super.start(stage);
    stage.setTitle(
        softwareVersion.getName() + " " + softwareVersion.getVersion());
    this.stage = stage;
    Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
    root = new VBox();
    double screenWidth = primScreenBounds.getWidth();
    double screenHeight = primScreenBounds.getHeight();
    int screenPercent;
    try {
      screenPercent = Preferences.getInstance().getScreenPercent();
    } catch (Exception e) {
      screenPercent = 100;
    }
    double sceneWidth = screenWidth * screenPercent / 100.0;
    double sceneHeight = screenHeight * screenPercent / 100.0;

    Scene scene = new Scene(root, sceneWidth, sceneHeight);
    scene.setFill(Color.OLDLACE);
    createMenuBar(scene);
    stage.setScene(scene);
    setup(app);
    stage.show();
    stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
    stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 4);
    available = true;
  }

  /**
   * setup the Application
   * 
   * @param app
   */
  private void setup(App app) {
    tabPane = new TabPane();
    tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
    controls = new HashMap<String, GenericControl>();
    Group mainGroup = app.getGroupById("mainGroup");
    for (Form form : mainGroup.getForms()) {
      Tab tab = new Tab();
      tab.setText(form.getTitle());
      GenericPanel panel = new GenericPanel(stage, form);
      controls.putAll(panel.controls);
      tab.setContent(panel);
      tabPane.getTabs().add(tab);
    }
    root.getChildren().add(tabPane);
  }

  @Override
  public void handle(ActionEvent event) {
    try {
      Object source = event.getSource();
      if (source instanceof MenuItem) {
        MenuItem menuItem = (MenuItem) source;
        if ("quitMenuItem".equals(menuItem.getId())) {
          close();
        } else if ("aboutMenuItem".equals(menuItem.getId())) {
          showAbout();
        } else if ("feedbackMenuItem".equals(menuItem.getId())) {
          showFeedback();
        } else if ("settingsMenuItem".equals(menuItem.getId())) {
          showSettings(false);
        } else if ("startMenuItem".equals(menuItem.getId())) {
          obdApp.start();
        } else if ("stopMenuItem".equals(menuItem.getId())) {
          obdApp.stop();
        } else if ("testMenuItem".equals(menuItem.getId())) {
          showSettings(true);
        } else if ("preferencesMenuItem".equals(menuItem.getId())) {
          showPreferences();
        } else if ("vehicleMenuItem".equals(menuItem.getId())) {
          showVehicle();
        } else {
          LOGGER.log(Level.WARNING, "unhandled menu item " + menuItem.getId()
              + ":" + menuItem.getText());
        }
      }
    } catch (Exception e) {
      handle(e);
    }
  }

  private void handle(Exception e) {
    GenericDialog.showError("Error", "A problem occured", e.getClass().getSimpleName()+":\n"+e.getMessage());
  }

  /**
   * show the vehicle Dialog
   */
  private void showVehicle() {
    GenericDialog vehicleDialog = new GenericDialog(stage,
        app.getFormById("preferencesGroup", "vehicleForm"));
    Optional<Map<String, Object>> result = vehicleDialog.show();
    if (result.isPresent()) {

    }
  }

  /**
   * show an About dialog
   */
  private void showAbout() {
    String headerText = softwareVersion.getName() + " "
        + softwareVersion.getVersion();
    GenericDialog.showAlert("About", headerText, softwareVersion.getUrl());

  }

  /**
   * browse to the feedback page
   */
  public void showFeedback() {
    try {
      JLink.open(App.getInstance().getFeedback());
    } catch (Exception e) {
      ErrorHandler.handle(e);
    }
  }

  /**
   * show the Preferences
   * 
   * @throws Exception
   */
  public void showPreferences() throws Exception {
    Preferences preferences = Preferences.getInstance();
    GenericDialog preferencesDialog = new GenericDialog(stage,
        app.getFormById("preferencesGroup", "preferencesForm"));
    Optional<Map<String, Object>> result = preferencesDialog
        .show(preferences.asMap());
    if (result.isPresent()) {
      LangChoice lang = preferences.getLanguage();
      preferences.fromMap(result.get());
      preferences.save();
      if (!lang.equals(preferences.getLanguage())) {
        Translator.initialize(preferences.getLanguage().name());
        GenericDialog.showAlert(i18n("language_changed_title"),
            i18n("language_changed"), i18n("newlanguage_restart"));
      }
    }
  }

  /**
   * internationalization function
   * 
   * @param text
   * @return translated text
   */
  public String i18n(String text) {
    String i18n = Translator.translate(text);
    return i18n;
  }

  /**
   * show the preferences
   * 
   * @throws Exception
   */
  public void showSettings(boolean test) throws Exception {
    Config config = Config.getInstance(ConfigMode.Preferences);
    SettingsDialog settingsDialog = new SettingsDialog(stage,
        app.getFormById("preferencesGroup", "settingsForm"), obdApp);
    if (config == null)
      config = new Config();
    if (test)
      settingsDialog.testConnection(config);
    else {
      Optional<Map<String, Object>> result = settingsDialog
          .show(config.asMap());
      if (result.isPresent()) {
        Map<String, Object> map = result.get();
        if (debug) {
          for (Entry<String, Object> me : map.entrySet()) {
            String value = "?";
            if (me.getValue() != null)
              value = me.getValue().toString() + "("
                  + me.getValue().getClass().getSimpleName() + ")";
            LOGGER.log(Level.INFO, me.getKey() + "=" + value);
          }
        }
        config.fromMap(map);
        config.save(ConfigMode.Preferences);
      }
    }
  }

  /**
   * close this display
   */
  public void close() {
    if (stage != null)
      Platform.runLater(() -> stage.close());
  }

  /**
   * select a random tab
   */
  public void selectRandomTab() {
    if (tabPane != null) {
      SingleSelectionModel<Tab> smodel = tabPane.getSelectionModel();
      Random random = new Random();
      int tabIndex = random.nextInt(tabPane.getTabs().size());
      smodel.select(tabIndex);
    }
  }

}

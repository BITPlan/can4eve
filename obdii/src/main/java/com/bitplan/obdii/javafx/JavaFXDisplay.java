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

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.controlsfx.control.StatusBar;

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
import com.bitplan.can4eve.util.TaskLaunch;
import com.bitplan.elm327.Config;
import com.bitplan.elm327.Config.ConfigMode;
import com.bitplan.obdii.CANValueDisplay;
import com.bitplan.obdii.I18n;
import com.bitplan.obdii.LabelField;
import com.bitplan.obdii.OBDApp;
import com.bitplan.obdii.Preferences;
import com.bitplan.obdii.Preferences.LangChoice;
import com.bitplan.obdii.elm327.ElmSimulator;

import javafx.application.Platform;
import javafx.concurrent.Task;
//import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
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

  private StatusBar statusBar;

  private Label watchDogLabel;
  private Task<Void> monitortask;

  private Tab clockTab;

  private ClockPane clockPane;

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
    if (tabPane == null)
      return null;
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
    setupSpecial();
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
    statusBar = new StatusBar();
    watchDogLabel = new Label();
    watchDogLabel.setTextFill(Color.web("808080"));
    watchDogLabel.setFont(new Font("Arial", 24));
    this.setWatchDogState("?", "-");
    statusBar.getLeftItems().add(watchDogLabel);
    root.getChildren().add(statusBar);
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

  /**
   * special setup non in generic description
   */
  public void setupSpecial() {
    // TODO - restyle and rename me - then filter connection tab
    clockTab=new Tab("Clocks");
    clockPane=new ClockPane();
    clockTab.setContent(clockPane);
    this.tabPane.getTabs().add(0, clockTab);
    // disable menu items
    this.setMenuItemDisable(I18n.OBD_HALT_MENU_ITEM, true);
    
    // add menu actions
    // File / Open
    MenuItem fileOpenMenuItem=getMenuItem(I18n.FILE_OPEN_MENU_ITEM);
    fileOpenMenuItem.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(final ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        if (Config.getInstance()!=null)
          fileChooser.setInitialDirectory(new File(Config.getInstance().getLogPrefix()));
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
          ElmSimulator.fileName=file.getAbsolutePath();
        } // if
      } // handle
    });
   
    //statusBar.getRightItems().add(clock);
 
  }

  /**
   * set the disable state of the menu item with the given id
   * 
   * @param id
   * @param state
   */
  public void setMenuItemDisable(String id, boolean state) {
    MenuItem menuItem = getMenuItem(id);
    if (menuItem != null)
      menuItem.setDisable(state);
  }

  /**
   * get the menu item with the given id
   * 
   * @param id
   * @return the menu item
   */
  public MenuItem getMenuItem(String id) {
    for (Menu menu : this.menuBar.getMenus()) {
      for (MenuItem menuItem : menu.getItems()) {
        if (id.equals(menuItem.getId())) {
          return menuItem;
        }
      }
    }
    return null;
  }

  /**
   * show a message that the given feature is not implemented yet
   * 
   * @param feature
   *          - i18n string code of feature e.g. menuItem
   */
  public void notImplemented(String feature) {
    GenericDialog.showAlert(I18n.get(I18n.SORRY), I18n.get(I18n.WE_ARE_SORRY),
        I18n.get(feature) + " " + I18n.get(I18n.NOT_IMPLEMENTED_YET));
  }

  @Override
  public void handle(ActionEvent event) {
    try {
      Object source = event.getSource();
      if (source instanceof MenuItem) {
        MenuItem menuItem = (MenuItem) source;
        switch (menuItem.getId()) {
        case I18n.FILE_SAVE_MENU_ITEM:
          notImplemented(I18n.FILE_SAVE_MENU_ITEM);
          break;
        case I18n.FILE_OPEN_MENU_ITEM:
          notImplemented(I18n.FILE_OPEN_MENU_ITEM);
          break;
        case I18n.FILE_QUIT_MENU_ITEM:
          close();
          break;
        case I18n.HELP_ABOUT_MENU_ITEM:
          TaskLaunch.start(() -> showLink(App.getInstance().getHome()));
          showAbout();
          break;
        case I18n.HELP_HELP_MENU_ITEM:
          TaskLaunch.start(() -> showLink(App.getInstance().getHelp()));
          break;
        case I18n.HELP_FEEDBACK_MENU_ITEM:
          GenericDialog.sendReport(softwareVersion,
              softwareVersion.getName() + " feedback", "...");
          break;
        case I18n.HELP_BUG_REPORT_MENU_ITEM:
          TaskLaunch.start(() -> showLink(App.getInstance().getFeedback()));
          break;
        case I18n.SETTINGS_SETTINGS_MENU_ITEM:
          showSettings(false);
          break;
        case I18n.OBD_START_MENU_ITEM:
          startMonitoring();
          break;
        case I18n.OBD_HALT_MENU_ITEM:
          stopMonitoring();
          break;
        case I18n.OBD_TEST_MENU_ITEM:
          showSettings(true);
          break;
        case I18n.SETTINGS_PREFERENCES_MENU_ITEM:
          showPreferences();
          break;
        case I18n.VEHICLE_MENU_ITEM:
          showVehicle();
          break;
        case I18n.VIEW_HISTORY_VIEW_MENU_ITEM:
          notImplemented(I18n.VIEW_HISTORY_VIEW_MENU_ITEM);
          break;
        case I18n.VIEW_SETTINGS_VIEW_MENU_ITEM:
          notImplemented(I18n.SETTINGS_SETTINGS_MENU_ITEM);
          break;
        case I18n.VIEW_MONITOR_VIEW_MENU_ITEM:
          notImplemented(I18n.VIEW_MONITOR_VIEW_MENU_ITEM);
          break;
        default:
          LOGGER.log(Level.WARNING, "unhandled menu item " + menuItem.getId()
              + ":" + menuItem.getText());
        }
      }
    } catch (Exception e) {
      handle(e);
    }
  }

  /**
   * stop the monitoring
   */
  private void stopMonitoring() {
    if (monitortask == null)
      return;
    // TODO use better symbol e.g. icon
    setWatchDogState("X", I18n.get(I18n.HALTED));
    setMenuItemDisable(I18n.OBD_START_MENU_ITEM, false);
    setMenuItemDisable(I18n.OBD_TEST_MENU_ITEM, false);
    setMenuItemDisable(I18n.OBD_HALT_MENU_ITEM, true);
    Task<Void> task = new Task<Void>() {
      @Override
      public Void call() {
        try {
          obdApp.stop();
        } catch (Exception e) {
          handle(e);
        }
        return null;
      }
    };
    new Thread(task).start();
  }

  /**
   * start the monitoring
   */
  private void startMonitoring() {
    setWatchDogState("⚙", I18n.get(I18n.MONITORING));
    setMenuItemDisable(I18n.OBD_START_MENU_ITEM, true);
    setMenuItemDisable(I18n.OBD_TEST_MENU_ITEM, true);
    setMenuItemDisable(I18n.OBD_HALT_MENU_ITEM, false);
    monitortask = new Task<Void>() {
      @Override
      public Void call() {
        try {
          obdApp.start();
        } catch (Exception e) {
          handle(e);
        }
        return null;
      }
    };
    new Thread(monitortask).start();
  }

  /**
   * set the watchDog state with the given symbol and state
   * 
   * @param symbol
   * @param state
   */
  private void setWatchDogState(String symbol, String state) {
    this.watchDogLabel.setText(symbol);
    this.statusBar.setText(state);
  }

  /**
   * handle the given exception
   * 
   * @param th
   */
  private void handle(Throwable th) {
    Platform.runLater(() -> GenericDialog.showException((I18n.get(I18n.ERROR)),
        I18n.get(I18n.PROBLEM_OCCURED), th, softwareVersion));
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
   * browse to the link page
   */
  public Void showLink(String link) {
    try {
      JLink.open(link);
    } catch (Exception e) {
      handle(e);
    }
    return null;
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

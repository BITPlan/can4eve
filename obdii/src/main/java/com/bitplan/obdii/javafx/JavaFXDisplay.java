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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.logging.Level;

import org.controlsfx.control.StatusBar;
import org.controlsfx.glyphfont.FontAwesome;

import com.bitplan.can4eve.CANValue;
import com.bitplan.can4eve.Owner;
import com.bitplan.can4eve.Vehicle;
import com.bitplan.can4eve.util.TaskLaunch;
import com.bitplan.elm327.Config;
import com.bitplan.elm327.Config.ConfigMode;
import com.bitplan.error.ExceptionHandler;
import com.bitplan.error.SoftwareVersion;
import com.bitplan.gui.App;
import com.bitplan.javafx.GenericApp;
import com.bitplan.javafx.GenericControl;
import com.bitplan.javafx.GenericDialog;
import com.bitplan.javafx.GenericPanel;
import com.bitplan.javafx.WaitableApp;
import com.bitplan.obdii.CANValueDisplay;
import com.bitplan.obdii.I18n;
import com.bitplan.obdii.OBDApp;
import com.bitplan.obdii.Preferences;
import com.bitplan.obdii.Preferences.LangChoice;
import com.bitplan.obdii.elm327.LogPlayer;
import com.bitplan.obdii.javafx.presenter.PreferencesPresenter;
import com.bitplan.obdii.javafx.presenter.VehiclePresenter;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
//import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Java FX Display
 * 
 * @author wf
 *
 */
public class JavaFXDisplay extends GenericApp implements MonitorControl,
    CANValueDisplay, ExceptionHandler, EventHandler<ActionEvent> {

  OBDApp obdApp;

  private MenuBar menuBar;

  private VBox root;
  String activeView = null;
  protected boolean available;

  private StatusBar statusBar;

  private Label watchDogLabel;
  private Task<Void> monitortask;

  protected ClockPane clockPane;

  protected DashBoardPane dashBoardPane;

  protected Map<String, Property<?>> canProperties;

  protected ChargePane chargePane;
  protected OdoPane odoPane;

  private Scene scene;
  Tab chargeTab;
  Tab odoTab;
  Tab clockTab;
  Tab dashBoardTab;
  private SimulatorPane simulatorPane;

  private Preferences prefs;
  private Button fullScreenButton;
  private Button hideMenuButton;
  private Rectangle2D sceneBounds;

  // group / tabPane ids
  public static final String DASH_BOARD_GROUP = "dashBoardGroup";
  protected static final String HISTORY_GROUP = "historyGroup";
  protected static final String BATTERY_GROUP = "batteryGroup";

  public static final String RESOURCE_PATH = "/com/bitplan/can4eve/gui/";

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
    super(app, softwareVersion,RESOURCE_PATH);
    this.obdApp = obdApp;
  }

  /**
   * @return the menuBar
   */
  public MenuBar getMenuBar() {
    return menuBar;
  }

  /**
   * @param menuBar
   *          the menuBar to set
   */
  public void setMenuBar(MenuBar menuBar) {
    this.menuBar = menuBar;
  }

  public VBox getRoot() {
    return root;
  }

  public void setRoot(VBox root) {
    this.root = root;
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
      Platform.runLater(() -> {
        control.setValue(value);
        control.setToolTip(String.format("%6d", updateCount));
      });
    }
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
   * show or hide the menuBar
   * 
   * @param scene
   * @param pMenuBar
   */
  public void showMenuBar(Scene scene, MenuBar pMenuBar, boolean show) {
    Parent sroot = scene.getRoot();
    ObservableList<Node> rootChilds = null;
    if (sroot instanceof VBox)
      rootChilds = ((VBox) sroot).getChildren();
    if (rootChilds == null)
      throw new RuntimeException(
          "showMenuBar can not handle scene root of type "
              + sroot.getClass().getName());
    if (!show && rootChilds.contains(pMenuBar)) {
      rootChilds.remove(pMenuBar);
    } else if (show) {
      rootChilds.add(0, pMenuBar);
    }
    pMenuBar.setVisible(show);
    hideMenuButton
        .setText(show ? I18n.get(I18n.HIDE_MENU) : I18n.get(I18n.SHOW_MENU));
  }

  /**
   * create the Menu Bar
   * 
   * @param scene
   */
  public MenuBar createMenuBar(Scene scene, com.bitplan.gui.App app) {
    MenuBar lMenuBar = new MenuBar();
    for (com.bitplan.gui.Menu amenu : app.getMainMenu().getSubMenus()) {
      Menu menu = new Menu(i18n(amenu.getId()));
      lMenuBar.getMenus().add(menu);
      for (com.bitplan.gui.MenuItem amenuitem : amenu.getMenuItems()) {
        MenuItem menuItem = new MenuItem(i18n(amenuitem.getId()));
        menuItem.setOnAction(this);
        menuItem.setId(amenuitem.getId());
        menu.getItems().add(menuItem);
      }
    }

    hideMenuButton = new Button(I18n.get(I18n.HIDE_MENU));
    hideMenuButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        showMenuBar(scene, lMenuBar, !lMenuBar.isVisible());
      }
    });
    return lMenuBar;
  }

  /**
   * create the Scene set sceneBounds as a side effect.
   * 
   * @return the scene
   */
  public Scene createScene() {
    setRoot(new VBox());
    int screenPercent;
    try {
      prefs = Preferences.getInstance();
      screenPercent = prefs.getScreenPercent();
    } catch (Exception e) {
      screenPercent = 100;
    }
    sceneBounds = super.getSceneBounds(screenPercent, 2, 3);
    Scene newScene = new Scene(getRoot(), sceneBounds.getWidth(),
        sceneBounds.getHeight());
    newScene.setFill(Color.OLDLACE);
    return newScene;
  }

  /**
   * set up the DashBaord
   * @param xyTabPane
   */
  public void setupDashBoard() {
    TabPane dashboardPane = xyTabPane.addTabPane(DASH_BOARD_GROUP,I18n.get(I18n.DASH_BOARD_TAB),FontAwesome.Glyph.SQUARE_ALT.name());
    setupSpecial(dashboardPane);
    @SuppressWarnings("unused")
    TabPane batteryPane=xyTabPane.addTabPane(BATTERY_GROUP,I18n.get(I18n.BATTERY_TAB),"battery-three-quarters");
  }

  @Override
  public void start(Stage stage) {
    super.start(stage);
    scene = createScene();
    setMenuBar(createMenuBar(scene, app));
    showMenuBar(scene, getMenuBar(), true);
    stage.setScene(scene);
    setUpStatusBar();
    this.setupXyTabPane();
    setupDashBoard();
    setup(app);
    setupSettings();
    this.setActiveTabPane(DASH_BOARD_GROUP);
    stage.setX(sceneBounds.getMinX());
    stage.setY(sceneBounds.getMinY());
    stage.show();
    available = true;
    if (!testMode) {
      // if this is the first Start then show the Welcome Wizard
      if (prefs != null && prefs.getAutoStart()) {
        // switch to fullscreen
        Platform.runLater(() -> switchFullScreen(true));
        // hide menu bar
        Platform.runLater(() -> showMenuBar(scene, getMenuBar(), false));
        this.startMonitoring(prefs.getDebug());
      } else {
        optionalShowWelcomeWizard();
      }
    }
  }

  /**
   * setup the settings
   */
  private void setupSettings() {
    // generic Panels
    // TODO the ones that have FXML should be modified to use their
    // specific Presenters instead
    GenericPanel vehiclePanel = this.getPanels().get("vehicleForm");
    Vehicle vehicle=Vehicle.getInstance();
    vehiclePanel.setValues(vehicle.asMap());
    GenericPanel ownerPanel=this.getPanels().get("ownerForm");
    Owner owner=Owner.getInstance();
    ownerPanel.setValues(owner.asMap());
    GenericPanel preferencesPanel=this.getPanels().get("preferencesForm");
    Preferences preferences=Preferences.getInstance();
    preferencesPanel.setValues(preferences.asMap());
    GenericPanel configPanel=this.getPanels().get("settingsForm");
    Config config=Config.getInstance();
    if (config!=null)
      configPanel.setValues(config.asMap());
  }

  /**
   * check whether this is the first start of the application (that is there are
   * not stored preferences yet) and then show the welcome wizard for the
   * initial configuration
   */
  private void optionalShowWelcomeWizard() {
    try {
      Preferences preferences = Preferences.getInstance();
      if (preferences.getLanguage() == LangChoice.notSet) {
        WelcomeWizard wizard = new WelcomeWizard(I18n.WELCOME,
            this.obdApp,this.getFxml());
        wizard.display();
        if (wizard.isFinished()) {
          this.startMonitoring(false);
        }
      }
    } catch (Throwable th) {
      this.handleException(th);
    }
  }

  /**
   * setup the status bar
   */
  public void setUpStatusBar() {
    statusBar = new StatusBar();
    watchDogLabel = new Label();
    watchDogLabel.setTextFill(Color.web("808080"));
    watchDogLabel.setFont(new Font("Arial", 24));
    this.setWatchDogState("?", "-");
    statusBar.getLeftItems().add(watchDogLabel);
    getRoot().getChildren().add(statusBar);
  }

  /**
   * bind the value to the valueTo
   * 
   * @param value
   * @param valueTo
   * @param biDirectional
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected void bind(Property value, Property valueTo, boolean biDirectional) {
    if (valueTo != null) {
      if (debug && value.isBound())
        LOGGER.log(Level.WARNING, "value is already bound");
      if (biDirectional)
        value.bindBidirectional(valueTo);
      else
        value.bind(valueTo);
    }
  }

  /**
   * bind the value to the valueTo
   * 
   * @param value
   * @param valueTo
   */
  @SuppressWarnings({ "rawtypes" })
  protected void bind(Property value, Property valueTo) {
    this.bind(value, valueTo, false);
  }

  public Void saveScreenShot() {
    Preferences prefs;
    try {
      prefs = Preferences.getInstance();
      if (prefs != null) {
        File screenShotDirectory = new File(prefs.getScreenShotDirectory());
        if (!screenShotDirectory.exists()
            && !screenShotDirectory.isDirectory()) {
          screenShotDirectory.mkdirs();
        }
        Tab tab = this.getActiveTab();
        String tabName = tab.getText();
        if (null == tabName) {
          if (tab.getTooltip() != null) {
            tabName = tab.getTooltip().getText();
          } else {
            tabName = "";
          }

        }
        SimpleDateFormat lIsoDateFormatter = new SimpleDateFormat(
            "yyyy-MM-dd_HHmmss");
        String screenShotName = String.format("screenShot_%s_%s.png", tabName,
            lIsoDateFormatter.format(new Date()));
        File screenShotFile = new File(screenShotDirectory, screenShotName);
        WaitableApp.saveAsPng(stage, screenShotFile);
        showNotification(I18n.get(I18n.SCREEN_SHOT), screenShotFile.getName(),
            2000);
      }
    } catch (Exception e1) {
      handleException(e1);
    }
    return null;
  }

  /**
   * switch the fullScreen Mode
   * 
   * @param fullScreen
   */
  public void switchFullScreen(boolean fullScreen) {
    stage.setFullScreen(fullScreen);
    fullScreenButton.setText(
        fullScreen ? I18n.get(I18n.PART_SCREEN) : I18n.get(I18n.FULL_SCREEN));

  }

  /**
   * special setup non in generic description
   */
  public void setupSpecial(TabPane tabPane) {
    clockPane = new ClockPane();
    odoPane = new OdoPane();
    odoTab = xyTabPane.addTab(tabPane, "odoPane", 0, I18n.get(I18n.ODO_INFO),
        FontAwesome.Glyph.AUTOMOBILE.name(), odoPane);
    dashBoardPane = new DashBoardPane(obdApp.getVehicle());
    chargePane = new ChargePane();
    chargeTab = xyTabPane.addTab(tabPane, "chargePane", 0, I18n.get(I18n.SOC),
        FontAwesome.Glyph.PLUG.name(), chargePane);
    dashBoardTab = xyTabPane.addTab(tabPane, "dashBoardPane", 0,
        I18n.get(I18n.DASH_BOARD), FontAwesome.Glyph.TACHOMETER.name(),
        dashBoardPane);
    clockTab = xyTabPane.addTab(tabPane, "clockPane", 0, I18n.get(I18n.CLOCKS),
        FontAwesome.Glyph.CLOCK_ALT.name(), clockPane);
    // disable menu items
    this.setMenuItemDisable(I18n.OBD_HALT_MENU_ITEM, true);
    this.setMenuItemDisable(I18n.FILE_CLOSE_MENU_ITEM, true);

    Button screenShotButton = new Button(I18n.get(I18n.SCREEN_SHOT));
    screenShotButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        Platform.runLater(() -> saveScreenShot());
      }
    });

    fullScreenButton = new Button(I18n.get(I18n.FULL_SCREEN));
    fullScreenButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        switchFullScreen(!stage.isFullScreen());
      }
    });

    if (statusBar != null) {
      statusBar.getRightItems().add(screenShotButton);
      statusBar.getRightItems().add(hideMenuButton);
      statusBar.getRightItems().add(fullScreenButton);
    }
   
    Button powerButton = xyTabPane.getTopLeftButton();
    Node icon = xyTabPane.getIcon(FontAwesome.Glyph.POWER_OFF.name(),
        xyTabPane.getIconSize());
    powerButton.setTooltip(new Tooltip(I18n.get(I18n.POWER_OFF)));
    powerButton.setGraphic(icon);
    powerButton.setDisable(false);
    powerButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        close();
      }
    });
  }

  /**
   * initialize the simulation
   * 
   * @param filePath
   */
  protected void initSimulation(String filePath) {
    LogPlayer logPlayer = obdApp.getLogPlayer();
    if (simulatorPane == null) {
      simulatorPane = new SimulatorPane(logPlayer, this);
      getRoot().getChildren().add(1, simulatorPane);
      setMenuItemDisable(I18n.OBD_START_WITH_LOG_MENU_ITEM, true);
      setMenuItemDisable(I18n.FILE_CLOSE_MENU_ITEM, false);
    }
    File file = new File(filePath);
    logPlayer.setLogFile(file);
    logPlayer.open();
  }

  @Override
  public void closeSimulation() {
    if (simulatorPane != null) {
      getRoot().getChildren().remove(simulatorPane);
      simulatorPane = null;
    }

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
    if (this.getMenuBar() != null)
      for (Menu menu : this.getMenuBar().getMenus()) {
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
    GenericDialog.showAlert(stage, I18n.get(I18n.SORRY),
        I18n.get(I18n.WE_ARE_SORRY),
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
          fileOpen();
          break;
        case I18n.FILE_CLOSE_MENU_ITEM:
          fileClose();
          break;
        case I18n.FILE_QUIT_MENU_ITEM:
          close();
          break;
        case I18n.HELP_ABOUT_MENU_ITEM:
          TaskLaunch.start(() -> showLink(app.getHome()));
          showAbout();
          break;
        case I18n.HELP_HELP_MENU_ITEM:
          TaskLaunch.start(() -> showLink(app.getHelp()));
          break;
        case I18n.HELP_FEEDBACK_MENU_ITEM:
          GenericDialog.sendReport(softwareVersion,
              softwareVersion.getName() + " feedback", "...");
          break;
        case I18n.HELP_BUG_REPORT_MENU_ITEM:
          TaskLaunch.start(() -> showLink(app.getFeedback()));
          break;
        case I18n.SETTINGS_SETTINGS_MENU_ITEM:
          showSettings(false);
          break;
        case I18n.SETTINGS_WELCOME_MENU_ITEM:
          WelcomeWizard wizard = new WelcomeWizard(I18n.WELCOME,
              this.obdApp,this.getFxml());
          wizard.display();
          if (wizard.isFinished())
            startMonitoring(false);
          break;
        case I18n.OBD_START_MENU_ITEM:
          startMonitoring(false);
          break;
        case I18n.OBD_START_WITH_LOG_MENU_ITEM:
          startMonitoring(true);
          break;
        case I18n.OBD_HALT_MENU_ITEM:
          stopMonitoring();
          break;
        case I18n.OBD_TEST_MENU_ITEM:
          showSettings(true);
          break;
        case I18n.SETTINGS_PREFERENCES_MENU_ITEM:
          PreferencesPresenter preferencesPresenter=getFxml().loadPresenter("preferences", Preferences.class,this);
          preferencesPresenter.show(Preferences.getInstance());
          break;
        case I18n.VEHICLE_MENU_ITEM:
          VehiclePresenter vehiclePresenter =getFxml().loadPresenter("vehicle",
              Vehicle.class,this);
          vehiclePresenter.show(Vehicle.getInstance());
          break;
        case I18n.VIEW_DASHBOARD_VIEW_MENU_ITEM:
          this.setActiveTabPane(DASH_BOARD_GROUP);
          break;
        case I18n.VIEW_HISTORY_VIEW_MENU_ITEM:
          this.setActiveTabPane(HISTORY_GROUP);
          break;
        case I18n.VIEW_SETTINGS_VIEW_MENU_ITEM:
          this.setActiveTabPane("preferencesGroup");
          break;
        case I18n.VIEW_MONITOR_VIEW_MENU_ITEM:
          this.setActiveTabPane("mainGroup");
          break;
        default:
          LOGGER.log(Level.WARNING, "unhandled menu item " + menuItem.getId()
              + ":" + menuItem.getText());
        }
      }
    } catch (Exception e) {
      handleException(e);
    }
  }

  /**
   * close the log File
   */
  private void fileClose() {
    try {
      obdApp.getLogPlayer().close();
      this.setMenuItemDisable(I18n.FILE_CLOSE_MENU_ITEM, true);
    } catch (Exception e) {
      this.handleException(e);
    }
  }

  /**
   * open the log File
   */
  private void fileOpen() {
    FileChooser fileChooser = new FileChooser();
    if (Config.getInstance() != null)
      try {
        File logDirectory = new File(
            Preferences.getInstance().getLogDirectory());
        if (!logDirectory.exists())
          logDirectory.mkdirs();
        fileChooser.setInitialDirectory(logDirectory);
      } catch (Exception e1) {
        // Ignore
      }
    File file = fileChooser.showOpenDialog(stage);
    if (file != null) {
      initSimulation(file.getAbsolutePath());
    } // if
  }

  /**
   * stop the monitoring
   */
  public void stopMonitoring() {
    if (monitortask == null)
      return;
    // TODO use better symbol e.g. icon
    setWatchDogState("X", I18n.get(I18n.HALTED));
    setMenuItemDisable(I18n.OBD_START_MENU_ITEM, false);
    setMenuItemDisable(I18n.OBD_START_WITH_LOG_MENU_ITEM,
        simulatorPane != null);
    setMenuItemDisable(I18n.OBD_TEST_MENU_ITEM, false);
    setMenuItemDisable(I18n.OBD_HALT_MENU_ITEM, true);
    Task<Void> task = new Task<Void>() {
      @Override
      public Void call() {
        try {
          obdApp.stop();
        } catch (Exception e) {
          handleException(e);
        }
        return null;
      }
    };
    new Thread(task).start();
  }

  /**
   * start the monitoring
   * 
   * @param
   */
  public void startMonitoring(boolean withLog) {
    setWatchDogState("⚙", I18n.get(I18n.MONITORING));
    setMenuItemDisable(I18n.OBD_START_MENU_ITEM, true);
    setMenuItemDisable(I18n.OBD_START_WITH_LOG_MENU_ITEM, true);
    setMenuItemDisable(I18n.OBD_TEST_MENU_ITEM, true);
    setMenuItemDisable(I18n.OBD_HALT_MENU_ITEM, false);
    monitortask = new Task<Void>() {
      @Override
      public Void call() {
        try {
          obdApp.start(withLog);
        } catch (Exception e) {
          handleException(e);
          stopMonitoring();
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
    Platform.runLater(() -> {
      this.watchDogLabel.setText(symbol);
      this.statusBar.setText(state);
    });
  }

  /**
   * internationalization function
   * 
   * @param params
   * @param text
   * @return translated text
   */
  public String i18n(String text, Object... params) {
    String i18n = I18n.get(text, params);
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
      SettingsDialog.testConnection(stage, this.obdApp, config);
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
   * setup the xyTabPane
   */
  public void setupXyTabPane() {
    getRoot().getChildren().add(xyTabPane);
    xyTabPane.getvTabPane().prefHeightProperty().bind(getStage().heightProperty().add(-xyTabPane.getTabSize()));
    xyTabPane.getvTabPane().prefWidthProperty().bind(getStage().widthProperty().add(-xyTabPane.getTabSize()));    
  }

}

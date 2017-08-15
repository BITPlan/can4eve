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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.controlsfx.control.Notifications;
import org.controlsfx.control.StatusBar;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import com.bitplan.can4eve.CANValue;
import com.bitplan.can4eve.ExceptionHandler;
import com.bitplan.can4eve.SoftwareVersion;
import com.bitplan.can4eve.Vehicle;
import com.bitplan.can4eve.gui.App;
import com.bitplan.can4eve.gui.Form;
import com.bitplan.can4eve.gui.Group;
import com.bitplan.can4eve.gui.javafx.ExceptionController;
import com.bitplan.can4eve.gui.javafx.GenericControl;
import com.bitplan.can4eve.gui.javafx.GenericDialog;
import com.bitplan.can4eve.gui.javafx.GenericPanel;
import com.bitplan.can4eve.gui.javafx.WaitableApp;
//import com.bitplan.can4eve.gui.javafx.LoginDialog;
import com.bitplan.can4eve.gui.swing.JLink;
import com.bitplan.can4eve.util.TaskLaunch;
import com.bitplan.elm327.Config;
import com.bitplan.elm327.Config.ConfigMode;
import com.bitplan.i18n.Translator;
import com.bitplan.obdii.CANValueDisplay;
import com.bitplan.obdii.I18n;
import com.bitplan.obdii.OBDApp;
import com.bitplan.obdii.Preferences;
import com.bitplan.obdii.Preferences.LangChoice;
import com.bitplan.obdii.elm327.LogPlayer;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Java FX Display
 * 
 * @author wf
 *
 */
public class JavaFXDisplay extends WaitableApp implements MonitorControl,
    CANValueDisplay, ExceptionHandler, EventHandler<ActionEvent> {
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.obdii.javafx");
  public static boolean testMode = false;
  private static com.bitplan.can4eve.gui.App app;
  OBDApp obdApp;
  private static SoftwareVersion softwareVersion;
  private MenuBar menuBar;

  private VBox root;
  String activeView = null;
  private Map<String, TabPane> tabPaneByView = new HashMap<String, TabPane>();
  private Map<String, GenericControl> controls;
  protected boolean available;

  private StatusBar statusBar;

  private Label watchDogLabel;
  private Task<Void> monitortask;

  protected ClockPane clockPane;

  protected DashBoardPane dashBoardPane;

  protected Map<String, ObservableValue<?>> canProperties;

  protected ChargePane chargePane;
  protected OdoPane odoPane;

  private Scene scene;
  Tab chargeTab;
  Tab odoTab;
  Tab clockTab;
  Tab dashBoardTab;
  private Map<String, GenericPanel> panels = new HashMap<String, GenericPanel>();

  private TabPane activeTabPane;

  private SimulatorPane simulatorPane;

  private Form vehicleForm;
  private Preferences prefs;
  private Button fullScreenButton;
  private Button hideMenuButton;
  private Rectangle2D sceneBounds;
  private GlyphFont fontAwesome;

  public static boolean debug = false;

  public static final String DASH_BOARD_GROUP = "dashBoardGroup";

  protected static final String HISTORY_GROUP = "historyGroup";
  private static final int ICON_SIZE = 48;

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
    ExceptionController.setExceptionHelper(app);
    ExceptionController.setSoftwareVersion(softwareVersion);
    ExceptionController.setLinker(this);
    JFXWizardPane.setLinker(this);
    fontAwesome = GlyphFontRegistry.font("FontAwesome");
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

  /**
   * get the icon for the given glyph
   * 
   * @param glyph
   * @param fontSize
   *          - the fontSize of the glyph
   * @return the Glyph
   */
  public Glyph getIcon(FontAwesome.Glyph glyph, int fontSize) {
    Glyph icon = fontAwesome.create(glyph);
    icon.setFontSize(fontSize);
    return icon;
  }

  public void setTabGlyph(Tab tab,FontAwesome.Glyph glyph) {
    Glyph icon=getIcon(glyph,ICON_SIZE);
    setTabGlyph(tab,icon);
  }
  
  /**
   * set the glyph for the tab
   * @param tab
   * @param glyph
   */
  public void setTabGlyph(Tab tab, Glyph glyph) {
    tab.setGraphic(glyph);
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

  /**
   * show the given notification
   * 
   * @param title
   * @param text
   * @param milliSecs
   */
  public static void showNotification(String title, String text,
      int milliSecs) {
    Notifications notification = Notifications.create();
    notification.hideAfter(new Duration(milliSecs));
    notification.title(title);
    notification.text(text);
    Platform.runLater(() -> notification.showInformation());
  }

  /**
   * get the title of the active Panel
   * 
   * @return - the activeTab
   */
  public Tab getActiveTab() {
    TabPane tabPane = getActiveTabPane();
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
  public MenuBar createMenuBar(Scene scene, com.bitplan.can4eve.gui.App app) {
    MenuBar lMenuBar = new MenuBar();
    for (com.bitplan.can4eve.gui.Menu amenu : app.getMainMenu().getSubMenus()) {
      Menu menu = new Menu(i18n(amenu.getId()));
      lMenuBar.getMenus().add(menu);
      for (com.bitplan.can4eve.gui.MenuItem amenuitem : amenu.getMenuItems()) {
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

  public void setupDashBoard() {
    TabPane dashboardPane = this.addTabPane(DASH_BOARD_GROUP);
    this.setActiveTabPane(DASH_BOARD_GROUP);
    setupSpecial(dashboardPane);
  }

  @Override
  public void start(Stage stage) {
    super.start(stage);
    stage.setTitle(
        softwareVersion.getName() + " " + softwareVersion.getVersion());
    this.stage = stage;
    scene = createScene();
    setMenuBar(createMenuBar(scene, app));
    showMenuBar(scene, getMenuBar(), true);
    stage.setScene(scene);
    setUpStatusBar();
    setup(app);
    setupDashBoard();

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
   * check whether this is the first start of the application (that is there are
   * not stored preferences yet) and then show the welcome wizard for the
   * initial configuration
   */
  private void optionalShowWelcomeWizard() {
    try {
      Preferences preferences = Preferences.getInstance();
      if (preferences.getLanguage() == LangChoice.notSet) {
        WelcomeWizard wizard = new WelcomeWizard(I18n.WELCOME, this.obdApp);
        wizard.display();
        if (wizard.isFinished()) {
          this.startMonitoring(false);
        }
      }
    } catch (Throwable th) {
      this.handleException(th);
    }
  }

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
   * add a tabPane with the given group Id
   * 
   * @param groupId
   * @return - the tabPane
   */
  public TabPane addTabPane(String groupId) {
    TabPane tabPane = new TabPane();
    tabPane.setTabMinHeight(ICON_SIZE+4);
    tabPane.setTabMaxHeight(ICON_SIZE+4);
    // make sure it grows e.g. when Icons are set
    // https://stackoverflow.com/a/25164425/1497139
    VBox.setVgrow(tabPane, Priority.ALWAYS);
    tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
    this.tabPaneByView.put(groupId, tabPane);
    return tabPane;
  }

  /**
   * setup the given Application adds tabPanes to the tabPaneByView map
   * 
   * @param app
   */
  public void setup(App app) {
    controls = new HashMap<String, GenericControl>();
    for (Group group : app.getGroups()) {
      TabPane tabPane = this.addTabPane(group.getId());
      for (Form form : group.getForms()) {
        Tab tab = new Tab();
        tab.setText(form.getTitle());
        GenericPanel panel = new GenericPanel(stage, form);
        panels.put(form.getId(), panel);
        controls.putAll(panel.controls);
        tab.setContent(panel);
        tabPane.getTabs().add(tab);
      }
    }
  }

  /**
   * add a tab
   * 
   * @param the
   *          TabPane to add a Tab to
   * @param index
   * @param title
   * @param content
   * @return
   */
  public Tab addTab(TabPane tabPane, int index, String title,FontAwesome.Glyph glyph, Node content) {
    Tab tab = new Tab(title);
    tab.setContent(content);
    tabPane.getTabs().add(index, tab);
    if (glyph!=null)
      this.setTabGlyph(tab, glyph);
    return tab;
  }

  /**
   * bind the to values
   * 
   * @param value
   * @param valueTo
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected void bind(Property value, ObservableValue valueTo) {
    if (valueTo != null) {
      if (debug && value.isBound())
        LOGGER.log(Level.WARNING, "value is already bound");
      value.bind(valueTo);
    }
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
        String tabName = this.getActiveTab().getText();
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
    odoTab = addTab(tabPane, 0, I18n.get(I18n.ODO_INFO),FontAwesome.Glyph.AUTOMOBILE,odoPane);
    dashBoardPane = new DashBoardPane(obdApp.getVehicle());
    chargePane = new ChargePane();
    chargeTab = addTab(tabPane, 0, I18n.get(I18n.SOC), FontAwesome.Glyph.LEVEL_UP,chargePane);
    dashBoardTab = addTab(tabPane, 0, I18n.get(I18n.DASH_BOARD), FontAwesome.Glyph.TACHOMETER,dashBoardPane);
    clockTab = addTab(tabPane, 0, I18n.get(I18n.CLOCKS), FontAwesome.Glyph.CLOCK_ALT, clockPane);
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
    vehicleForm = app.getFormById("preferencesGroup", "vehicleForm");
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
          fileOpen();
          break;
        case I18n.FILE_CLOSE_MENU_ITEM:
          fileClose();
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
        case I18n.SETTINGS_WELCOME_MENU_ITEM:
          WelcomeWizard wizard = new WelcomeWizard(I18n.WELCOME, this.obdApp);
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
          showPreferences();
          break;
        case I18n.VEHICLE_MENU_ITEM:
          showVehicle();
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
   * handle the given exception
   * 
   * @param th
   */
  public void handleException(Throwable th) {
    Platform.runLater(() -> GenericDialog.showException((I18n.get(I18n.ERROR)),
        I18n.get(I18n.PROBLEM_OCCURED), th, this));
  }

  /**
   * show the vehicle Dialog
   * 
   * @throws Exception
   */
  private void showVehicle() throws Exception {
    Vehicle vehicle = Vehicle.getInstance();
    GenericDialog vehicleDialog = new GenericDialog(stage, vehicleForm);
    Optional<Map<String, Object>> result = vehicleDialog.show(vehicle.asMap());
    if (result.isPresent()) {
      vehicle.fromMap(result.get());
      vehicle.save();
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
      handleException(e);
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
      SettingsDialog.testConnection(this.obdApp, config);
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
    TabPane tabPane = getActiveTabPane();
    if (tabPane != null) {
      SingleSelectionModel<Tab> smodel = tabPane.getSelectionModel();
      Random random = new Random();
      int tabIndex = random.nextInt(tabPane.getTabs().size());
      smodel.select(tabIndex);
    }
  }

  /**
   * (re) set the active tab Pane
   * 
   * @param groupId
   */
  public TabPane setActiveTabPane(String groupId) {
    TabPane oldtabPane = this.getActiveTabPane();
    if (oldtabPane != null) {
      getRoot().getChildren().remove(oldtabPane);
    }
    this.activeView = groupId;
    activeTabPane = getActiveTabPane();
    if (activeTabPane == null)
      throw new IllegalStateException(
          "tab Pane with groupId " + groupId + " missing");
    getRoot().getChildren().add(activeTabPane);
   
    return activeTabPane;
  }

  /**
   * get the active Tab Pane
   * 
   * @return - the active Tab Pane
   */
  public TabPane getActiveTabPane() {
    TabPane activeTabPane = getTabPane(this.activeView);
    return activeTabPane;
  }

  /**
   * get the tabPane with the given view name
   * 
   * @param view
   * @return the tabPane
   */
  public TabPane getTabPane(String view) {
    TabPane tabPane = this.tabPaneByView.get(view);
    return tabPane;
  }

  /**
   * get the tab of the given view with the given id
   * 
   * @param view
   * @param tabId
   * @return - the tab
   */
  public Tab getTab(String view, String tabId) {
    TabPane tabPane = getTabPane(view);
    if (tabPane != null) {
      for (Tab tab : tabPane.getTabs()) {
        if (tabId.equals(tab.getText())) {
          return tab;
        }
      }
    }
    return null;
  }

}

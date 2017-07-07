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
import com.bitplan.can4eve.Vehicle;
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
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
//import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
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
public class JavaFXDisplay extends WaitableApp
    implements CANValueDisplay, EventHandler<ActionEvent> {
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.obdii.javafx");

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

  private Tab clockTab;

  protected ClockPane clockPane;

  protected DashBoardPane dashBoardPane;

  private Tab dashBoardTab;

  protected Map<String, ObservableValue<?>> canProperties;

  protected ChargePane chargePane;
  protected OdoPane odoPane;
  
  private Tab chargeTab;
  private Scene scene;

  private Tab odoTab;

  private Map<String, GenericPanel> panels=new HashMap<String,GenericPanel>();


  public static final boolean debug = false;

  private static final String DASH_BOARD_GROUP = "dashBoardGroup";

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
   * remove or add the menuBar
   * @param scene
   * @param pMenuBar 
   */
  public void toggleMenuBar(Scene scene, MenuBar pMenuBar) {
    ObservableList<Node> rootChilds = ((VBox) scene.getRoot()).getChildren();
    if (rootChilds.contains(pMenuBar))
      rootChilds.remove(pMenuBar);
    else
      rootChilds.add(0,pMenuBar);

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
    toggleMenuBar(scene,menuBar);
  }

  @Override
  public void start(Stage stage) {
    super.start(stage);
    stage.setTitle(
        softwareVersion.getName() + " " + softwareVersion.getVersion());
    this.stage = stage;
    root = new VBox();
    int screenPercent;
    try {
      screenPercent = Preferences.getInstance().getScreenPercent();
    } catch (Exception e) {
      screenPercent = 100;
    }
    Rectangle2D sceneBounds = super.getSceneBounds(screenPercent, 2, 3);
    scene = new Scene(root, sceneBounds.getWidth(),
        sceneBounds.getHeight());
    scene.setFill(Color.OLDLACE);
    createMenuBar(scene);
    stage.setScene(scene);
    setUpStatusBar();
    setup(app);
    TabPane dashboardPane = this.addTabPane(DASH_BOARD_GROUP);
    this.setActiveTabPane(DASH_BOARD_GROUP);
    setupSpecial(dashboardPane);
    stage.setX(sceneBounds.getMinX());
    stage.setY(sceneBounds.getMinY());
    stage.show();
    available = true;
  }

  private void setUpStatusBar() {
    statusBar = new StatusBar();
    watchDogLabel = new Label();
    watchDogLabel.setTextFill(Color.web("808080"));
    watchDogLabel.setFont(new Font("Arial", 24));
    this.setWatchDogState("?", "-");
    statusBar.getLeftItems().add(watchDogLabel);
    root.getChildren().add(statusBar);
  }

  /**
   * add a tabPane with the given group Id
   * 
   * @param groupId
   * @return - the tabPane
   */
  public TabPane addTabPane(String groupId) {
    TabPane tabPane = new TabPane();
    tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
    this.tabPaneByView.put(groupId, tabPane);
    return tabPane;
  }

  /**
   * setup the given Application adds tabPanes to the tabPaneByView map
   * 
   * @param app
   */
  private void setup(App app) {
    controls = new HashMap<String, GenericControl>();
    for (Group group : app.getGroups()) {
      TabPane tabPane = this.addTabPane(group.getId());
      for (Form form : group.getForms()) {
        Tab tab = new Tab();
        tab.setText(form.getTitle());
        GenericPanel panel = new GenericPanel(stage, form);
        panels.put(form.getId(),panel);
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
  public Tab addTab(TabPane tabPane, int index, String title, Node content) {
    Tab tab = new Tab(title);
    tab.setContent(content);
    tabPane.getTabs().add(index, tab);
    return tab;
  }

  /**
   * bind the to values
   * 
   * @param value
   * @param valueTo
   */
  protected void bind(Property value, ObservableValue valueTo) {
    if (valueTo != null) {
      if (value.isBound())
        LOGGER.log(Level.WARNING,"value is already bound");
      value.bind(valueTo);
    }
  }

  /**
   * special setup non in generic description
   */
  public void setupSpecial(TabPane tabPane) {
    clockPane = new ClockPane();
    odoPane=new OdoPane();
    odoTab = addTab(tabPane, 0, I18n.get(I18n.ODO_INFO), odoPane);
    dashBoardPane = new DashBoardPane(9200);
    chargePane = new ChargePane();
    chargeTab = addTab(tabPane, 0, I18n.get(I18n.SOC), chargePane);
    dashBoardTab = addTab(tabPane, 0, I18n.get(I18n.DASH_BOARD), dashBoardPane);
    clockTab = addTab(tabPane, 0, I18n.get(I18n.CLOCKS), clockPane);
    // disable menu items
    this.setMenuItemDisable(I18n.OBD_HALT_MENU_ITEM, true);
    this.setMenuItemDisable(I18n.FILE_SAVE_MENU_ITEM, true);

    // add menu actions
    // File / Open
    MenuItem fileOpenMenuItem = getMenuItem(I18n.FILE_OPEN_MENU_ITEM);
    fileOpenMenuItem.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(final ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        if (Config.getInstance() != null)
          try {
            fileChooser.setInitialDirectory(
                new File(Preferences.getInstance().getLogDirectory()));
          } catch (Exception e1) {
            // TODO Auto-generated catch block

          }
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
          ElmSimulator.fileName = file.getAbsolutePath();
        } // if
      } // handle
    });

    Button fullScreenButton = new Button(I18n.get(I18n.FULL_SCREEN));
    fullScreenButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        stage.setFullScreen(!stage.isFullScreen());
        fullScreenButton.setText(stage.isFullScreen()
            ? I18n.get(I18n.PART_SCREEN) : I18n.get(I18n.FULL_SCREEN));
      }
    });
    Button hideMenuButton = new Button(I18n.get(I18n.HIDE_MENU));
    hideMenuButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        toggleMenuBar(scene,menuBar);
        menuBar.setVisible(!menuBar.isVisible());
        hideMenuButton.setText(menuBar.isVisible() ? I18n.get(I18n.HIDE_MENU)
            : I18n.get(I18n.SHOW_MENU));
      }
    });
    statusBar.getRightItems().add(hideMenuButton);
    statusBar.getRightItems().add(fullScreenButton);

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
          // this can't happen
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
          this.setActiveTabPane("historyGroup");
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
   * stop the monitoring
   */
  private void stopMonitoring() {
    if (monitortask == null)
      return;
    // TODO use better symbol e.g. icon
    setWatchDogState("X", I18n.get(I18n.HALTED));
    setMenuItemDisable(I18n.OBD_START_MENU_ITEM, false);
    setMenuItemDisable(I18n.OBD_START_WITH_LOG_MENU_ITEM, false);
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
  private void startMonitoring(boolean withLog) {
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
  private void handleException(Throwable th) {
    Platform.runLater(() -> GenericDialog.showException((I18n.get(I18n.ERROR)),
        I18n.get(I18n.PROBLEM_OCCURED), th, softwareVersion));
  }

  /**
   * show the vehicle Dialog
   * 
   * @throws Exception
   */
  private void showVehicle() throws Exception {
    Vehicle vehicle = Vehicle.getInstance();
    GenericDialog vehicleDialog = new GenericDialog(stage,
        app.getFormById("preferencesGroup", "vehicleForm"));
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
  public void setActiveTabPane(String groupId) {
    TabPane oldtabPane = this.getActiveTabPane();
    if (oldtabPane != null) {
      root.getChildren().remove(oldtabPane);
    }
    this.activeView = groupId;
    TabPane newTabPane = getActiveTabPane();
    if (newTabPane == null)
      throw new IllegalStateException(
          "tab Pane with groupId " + groupId + " missing");
    root.getChildren().add(newTabPane);
  }

  /**
   * get the active Tab Pane
   * 
   * @return - the active Tab Pane
   */
  private TabPane getActiveTabPane() {
    TabPane activeTabPane = this.tabPaneByView.get(this.activeView);
    return activeTabPane;
  }

}

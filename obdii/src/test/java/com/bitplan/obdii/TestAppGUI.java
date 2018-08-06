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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.Notifications;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.Wizard.LinearFlow;
import org.controlsfx.dialog.WizardPane;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.bitplan.can4eve.CANInfo;
import com.bitplan.can4eve.CANValue.DoubleValue;
import com.bitplan.can4eve.Owner;
import com.bitplan.can4eve.Vehicle;
import com.bitplan.can4eve.VehicleGroup;
import com.bitplan.can4eve.gui.javafx.CANProperty;
import com.bitplan.can4eve.gui.javafx.CANPropertyManager;
import com.bitplan.can4eve.states.StopWatch;
import com.bitplan.error.ExceptionHandler;
import com.bitplan.gui.App;
import com.bitplan.gui.ExceptionHelp;
import com.bitplan.gui.Group;
import com.bitplan.gui.Linker;
import com.bitplan.i18n.I18n;
import com.bitplan.i18n.Translator;
import com.bitplan.javafx.BasePresenter;
import com.bitplan.javafx.ConstrainedGridPane;
import com.bitplan.javafx.GenericDialog;
import com.bitplan.javafx.JFXML;
import com.bitplan.javafx.SampleApp;
import com.bitplan.javafx.TaskLaunch;
import com.bitplan.javafx.WaitableApp;
import com.bitplan.javafx.XYTabPane;
import com.bitplan.json.JsonManager;
import com.bitplan.json.JsonManagerImpl;
import com.bitplan.obdii.Preferences.LangChoice;
import com.bitplan.obdii.elm327.LogPlayer;
import com.bitplan.obdii.elm327.LogPlayerImpl;
import com.bitplan.obdii.javafx.CANValuePane;
import com.bitplan.obdii.javafx.ChargePane;
import com.bitplan.obdii.javafx.ClockPane;
import com.bitplan.obdii.javafx.ClockPane.Watch;
import com.bitplan.obdii.javafx.JFXCanCellStatePlot;
import com.bitplan.obdii.javafx.JFXCanValueHistoryPlot;
import com.bitplan.obdii.javafx.JFXStopWatch;
import com.bitplan.obdii.javafx.JavaFXDisplay;
import com.bitplan.obdii.javafx.LCDPane;
import com.bitplan.obdii.javafx.SimulatorPane;
import com.bitplan.obdii.javafx.WelcomeWizard;
import com.bitplan.obdii.javafx.presenter.OwnerPresenter;
import com.bitplan.obdii.javafx.presenter.VehiclePresenter;

import eu.hansolo.LcdGauge.ResetableGauge;
import eu.hansolo.OverviewDemo;
import eu.hansolo.medusa.FGauge;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Gauge.NeedleSize;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.GaugeDesign;
import eu.hansolo.medusa.GaugeDesign.GaugeBackground;
import eu.hansolo.medusa.LcdDesign;
import eu.hansolo.medusa.LcdFont;
import eu.hansolo.medusa.Marker;
import eu.hansolo.medusa.Marker.MarkerType;
import eu.hansolo.medusa.Section;
import eu.hansolo.medusa.TickLabelLocation;
import eu.hansolo.medusa.TickLabelOrientation;
import eu.hansolo.medusa.TickMarkType;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.LongBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * test the descriptive application gui
 * 
 * @author wf
 *
 */
@SuppressWarnings("restriction")
public class TestAppGUI extends TestOBDII {
  public static final int SHOW_TIME = 4000;
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.obdii");

  public class FXMLSampleApp {
    Pane pane;
    SampleApp sampleApp;
    App app;
    JFXML fxml;

    public FXMLSampleApp(String title) {
      pane = new Pane();
      sampleApp = new SampleApp(title, pane);
    }

    public void showAndOpen() throws Exception {
      sampleApp.show();
      sampleApp.waitOpen();
      app = App.getInstance(OBDMain.APP_PATH);
      fxml = new JFXML(JavaFXDisplay.RESOURCE_PATH, sampleApp.getStage(), app);
    }

    public void close() {
      sampleApp.close();
    }
  }

  private ExceptionHandler exceptionHandler;

  @Before
  public void initGUI() {
    WaitableApp.toolkitInit();
    Translator.initialize("can4eve", "en");
    exceptionHandler = new ExceptionHandler() {

      @Override
      public void handleException(Throwable th) {
        ErrorHandler.handle(th);

      }

    };
  }

  class Entity {
    String name;
    Class clazz;
    String group;
    String form;

    public Entity(String name, Class clazz, String group, String form) {
      super();
      this.name = name;
      this.clazz = clazz;
      this.group = group;
      this.form = form;
    }
  }

  @Test
  public void testFXMLIntegrity() throws Exception {
    FXMLSampleApp sampleApp = new FXMLSampleApp("FXMLIntegrity");
    sampleApp.showAndOpen();
    Entity[] entities = {
        new Entity("vehicle", Vehicle.class, "preferencesGroup", "vehicleForm"),
        new Entity("owner", Owner.class, "preferencesGroup", "ownerForm"),
        new Entity("preferences", Preferences.class, "preferencesGroup",
            "preferencesForm") };
    for (Entity entity : entities) {
      BasePresenter<?> presenter = sampleApp.fxml.loadPresenter(entity.name,
          entity.clazz, exceptionHandler);
      assertNotNull(presenter);
    }
    sampleApp.close();
  }

  @Test
  public void testVehiclePresenter() throws Exception {
    FXMLSampleApp sampleApp = new FXMLSampleApp("vehiclePresenter");
    sampleApp.showAndOpen();
    VehiclePresenter vehiclePresenter = sampleApp.fxml.loadPresenter("vehicle",
        Vehicle.class, exceptionHandler);
    assertNotNull(vehiclePresenter);
    Platform.runLater(() -> {
      try {
        Vehicle vehicle = Vehicle.getInstance();
        vehicle.setNickName("empty Test Vehicle");;
        vehiclePresenter.show(vehicle);
      } catch (Exception e) {
        ErrorHandler.handle(e);
        fail("there should be no exception");
      }
    });
    Thread.sleep(SHOW_TIME);
    sampleApp.close();
  }

  @Test
  public void testOwnerPresenter() throws Exception {
    FXMLSampleApp sampleApp = new FXMLSampleApp("ownerPresenter");
    sampleApp.showAndOpen();
    OwnerPresenter ownerPresenter = sampleApp.fxml.loadPresenter("owner",
        Owner.class, exceptionHandler);
    assertNotNull(ownerPresenter);
    Platform.runLater(() -> {
      try {
        ownerPresenter.show(Owner.getInstance());
      } catch (Exception e) {
        ErrorHandler.handle(e);
        fail("there should be no exception");
      }
    });
    Thread.sleep(SHOW_TIME);
    sampleApp.close();
  }

  @Test
  public void testAppGUI() throws Exception {
    App app = App.getInstance(OBDMain.APP_PATH);
    assertNotNull(app);
    assertEquals(6, app.getMainMenu().getSubMenus().size());
    assertEquals(3, app.getGroups().size());
    int[] expected = { 6, 1, 4 };
    int i = 0;
    for (Group group : app.getGroups()) {
      assertEquals(expected[i++], group.getForms().size());
    }
  }

  @Test
  public void testJoin() {
    String langs = StringUtils.join(LangChoice.values(), ",");
    assertEquals("en,de,notSet", langs);
  }

  @Test
  public void testPreferences() {
    Preferences pref = new Preferences();
    pref.setDebug(true);
    pref.setLanguage(LangChoice.de);
    String json = pref.asJson();
    // System.out.println(json);
    assertEquals("{\n" + "  \"language\": \"de\",\n" + "  \"debug\": true,\n"
        + "  \"autoStart\": false,\n" + "  \"screenPercent\": 100,\n"
        + "  \"logDirectory\": \"can4eveLogs\",\n"
        + "  \"screenShotDirectory\": \"can4eveScreenShots\",\n"
        + "  \"logPrefix\": \"can4eve\"\n" + "}", json);
    JsonManager<Preferences> jmPreferences = new JsonManagerImpl<Preferences>(
        Preferences.class);
    Preferences pref2 = jmPreferences.fromJson(json);
    assertNotNull(pref2);
    assertEquals(pref2.getDebug(), pref.getDebug());
    assertEquals(pref2.getLanguage(), pref.getLanguage());
    Preferences pref3 = new Preferences();
    pref3.fromMap(pref2.asMap());
    assertEquals(pref3.getDebug(), pref.getDebug());
    assertEquals(pref3.getLanguage(), pref.getLanguage());
  }

  /**
   * set the PlotValues
   * 
   * @param properties
   * @param minutes
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void setPlotValues(Map<String, CANProperty> properties, int minutes) {
    Calendar date = Calendar.getInstance();
    final long ONE_MINUTE_IN_MILLIS = 60000;// millisecs
    // https://stackoverflow.com/a/9044010/1497139
    long t = date.getTimeInMillis();
    for (int i = 0; i < minutes; i++) {
      Date timeStamp = new Date(t + (i * ONE_MINUTE_IN_MILLIS));
      CANProperty SOC = properties.get("SOC");
      CANProperty RR = properties.get("Range");
      SOC.setValue(90 - i * 1.2, timeStamp);
      RR.setValue(90 - i, timeStamp);
    }
  }

  // Swing Version of things
  /*
   * @Test public void testLineChart() throws Exception { List<CANValue<?>>
   * plotValues = this.getPlotValues(); String title = "SOC/RR"; String xTitle =
   * "time"; String yTitle = "%/km"; final CANValueHistoryPlot valuePlot = new
   * CANValueHistoryPlot(title, xTitle, yTitle, plotValues); final PanelFrame
   * plotDemo = new PanelFrame(false); plotDemo.show(valuePlot.getPanel());
   * plotDemo.waitOpen(); Thread.sleep(5000); plotDemo.frame.setVisible(false);
   * }
   */

  @Test
  public void testLCDPane() throws Exception {
    int cols = 3;
    int rows = 4;
    String[] texts = new String[rows * cols];
    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        texts[row * cols + col] = String.format("row %2d col %2d", row, col);
      }
    }
    LCDPane lcdPane = new LCDPane(rows, cols, texts);
    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        lcdPane.getAt(row, col).setValue(Math.random() * 200);
      }
    }
    SampleApp.createAndShow("LCDPane", lcdPane, SHOW_TIME);
  }

  /**
   * get a 6 columns by 4 rows grid pane
   * 
   * @return
   */
  public ConstrainedGridPane getGridPane6x4() {
    ConstrainedGridPane gridPane = new ConstrainedGridPane();
    gridPane.fixColumnSizes(4, 16, 16, 16, 16, 16, 16);
    gridPane.fixRowSizes(4, 25, 25, 25, 25);
    return gridPane;
  }

  @Test
  public void testExceptionHelp() throws Exception {
    App app = App.getInstance(OBDMain.APP_PATH);
    String exception = "java.net.BindException:Address already in use (Bind failed)";
    ExceptionHelp ehelp = app.getExceptionHelpByName(exception);
    assertNotNull(ehelp);
    FlowPane fp = new FlowPane();
    Label lbl = new Label(I18n.get(ehelp.getI18nHint()));
    Hyperlink link = new Hyperlink(ehelp.getUrl());

    fp.getChildren().addAll(lbl, link);
    SampleApp sampleApp = new SampleApp("help", fp);
    final Linker linker = sampleApp;
    link.setOnAction((evt) -> {
      linker.browse(link.getText());
    });
    sampleApp.show();
    sampleApp.waitOpen();
    Thread.sleep(SHOW_TIME);
    sampleApp.close();
  }

  /**
   * set random values for the cell Temperature
   * 
   * @param cellTemp
   */
  public void randomValues(CANProperty<DoubleValue, Double> cellTemp) {
    Date timeStamp = new Date();
    for (int i = 0; i < cellTemp.getCANInfo().getMaxIndex(); i++) {
      cellTemp.setValue(i, 25 + Math.random() * 10, timeStamp);
    }
  }

  @Test
  public void testBarChartJavaFx() throws Exception {
    VehicleGroup vg = VehicleGroup.get("Triplet");
    CANInfo cellInfo = vg.getCANInfoByName("CellTemperature");
    assertNotNull(cellInfo);
    DoubleValue cellTempValue = new DoubleValue(cellInfo);
    CANProperty<DoubleValue, Double> cellTemp = new CANProperty<DoubleValue, Double>(
        cellTempValue, new SimpleDoubleProperty());
    randomValues(cellTemp);
    String title = "Cell Temperature";
    String xTitle = "cell";
    String yTitle = "° Celsius";
    final JFXCanCellStatePlot valuePlot = new JFXCanCellStatePlot(title, xTitle,
        yTitle, cellTemp, 2.0, 0.5);
    valuePlot.updateOn(cellTemp.getUpdateCountProperty());
    SampleApp sampleApp = new SampleApp("Cell Temperature",
        valuePlot.getBarChart());
    sampleApp.show();
    sampleApp.waitOpen();
    int loops = 4;
    for (int j = 0; j < loops; j++) {
      randomValues(cellTemp);
      Thread.sleep(SHOW_TIME / loops);
    }
    sampleApp.close();
  }

  @Test
  public void testMedusa() throws Exception {
    OverviewDemo demo = new OverviewDemo();
    demo.init();
    GridPane demoPane = demo.getDemoPane();
    SampleApp.createAndShow("Controls", demoPane, SHOW_TIME);
  }

  @Test
  public void testGauge() throws InterruptedException {
    GridPane pane = new GridPane();
    Gauge gauge = GaugeBuilder.create().minValue(0).maxValue(100)
        .tickLabelDecimals(0).decimals(1).autoScale(true).animated(true)
        // .backgroundPaint(Color.TRANSPARENT)
        // .borderPaint(Color.LIGHTGRAY)
        // .knobColor(Color.rgb(0, 90, 120))
        .shadowsEnabled(true)
        // .tickLabelColor(Color.rgb(0, 175, 248))
        // .ledColor(Color.rgb(0, 175, 248))
        .ledVisible(true).ledBlinking(true).sectionsVisible(true)
        .sections(new Section(75, 100, Color.rgb(139, 195, 102, 0.5)))
        .areasVisible(true)
        .areas(new Section(0.00, 25, Color.rgb(234, 83, 79, 0.5)))
        .majorTickMarkColor(Color.MAGENTA)
        // .minorTickMarkColor(Color.rgb(0, 175, 248))
        .majorTickMarkType(TickMarkType.TRAPEZOID)
        .mediumTickMarkType(TickMarkType.DOT)
        .minorTickMarkType(TickMarkType.LINE)
        .tickLabelOrientation(TickLabelOrientation.ORTHOGONAL)
        .tickMarkSections(new Section(0.25, 0.5, Color.rgb(241, 161, 71)))
        .tickMarkSectionsVisible(true)
        .markers(new Marker(0.5, "", Color.CYAN, MarkerType.TRIANGLE))
        .markersVisible(true)
        // .majorTickMarksVisible(true)
        // .minorTickMarksVisible(true)
        .tickLabelLocation(TickLabelLocation.INSIDE)
        // .tickLabelsVisible(true)
        .tickLabelSections(new Section(0.1, 0.3, Color.rgb(0, 175, 248)))
        // .tickLabelSectionsVisible(true)
        .title("SOC")
        // .titleColor(Color.rgb(223, 223, 223))
        .unit("%").lcdDesign(LcdDesign.SECTIONS).lcdVisible(true)
        .lcdFont(LcdFont.STANDARD)
        // .unitColor(Color.rgb(223, 223, 223))
        // .valueColor(Color.rgb(223, 223, 223))
        .needleSize(NeedleSize.THICK).build();
    FGauge framedGauge = new FGauge(gauge, GaugeDesign.ENZO,
        GaugeBackground.DARK_GRAY);

    pane.add(framedGauge, 0, 0);

    DoubleProperty dproperty = new SimpleDoubleProperty(85.0);

    SampleApp sampleApp = new SampleApp("Gauge", pane, 67, 2, 2);
    sampleApp.show();
    sampleApp.waitOpen();
    Stage stage = sampleApp.getStage();
    framedGauge.prefWidthProperty().bind(stage.widthProperty());
    framedGauge.prefHeightProperty().bind(stage.heightProperty());
    gauge.valueProperty().bind(dproperty);
    while (stage.isShowing()) {
      Thread.sleep(15);
      Platform.runLater(() -> dproperty.setValue(dproperty.getValue() - 0.1));
      if (dproperty.getValue() < 45)
        sampleApp.close();
    }
  }

  @Test
  public void testClockPanel() throws Exception {
    ClockPane clockPane = new ClockPane();
    clockPane.setWatch(Watch.Charging, 1320 * 1000);
    clockPane.setWatch(Watch.Parking, 390 * 1000);
    SampleApp sampleApp = new SampleApp("Clocks", clockPane);
    sampleApp.show();
    sampleApp.waitOpen();
    for (int i = 0; i < (SHOW_TIME / 1000 * 2); i++) {
      Thread.sleep(1000);
      clockPane.setWatch(Watch.Moving, i * 1000 + 300 * 1000);
    }
    sampleApp.close();
  }

  @Test
  public void testChargePanel() throws Exception {
    ChargePane chargePane = new ChargePane();
    Map<String, SimpleDoubleProperty> props = new HashMap<String, SimpleDoubleProperty>();
    String propnames[] = { "SOC", "Range", "ACVolts", "ACAmps", "DCVolts",
        "DCAmps" };
    for (String propname : propnames) {
      SimpleDoubleProperty sd = new SimpleDoubleProperty();
      props.put(propname, sd);
      chargePane.getGaugeMap().get(propname).valueProperty().bind(sd);
    }
    SampleApp sampleApp = new SampleApp("Charge", chargePane);
    sampleApp.show();
    sampleApp.waitOpen();
    int loops = SHOW_TIME / 50 * 2;
    for (int i = 0; i < loops; i++) {
      Thread.sleep(50);
      double newValue = (95 * i / loops);
      // LOGGER.log(Level.INFO, "new value "+newValue);
      Platform.runLater(() -> {
        props.get("SOC").setValue(newValue);
        props.get("Range").setValue(newValue * 0.9);
        props.get("ACVolts").setValue(224 + Math.random() * 2);
        props.get("ACAmps").setValue(14 + Math.random() * 0.2);
        props.get("DCVolts").setValue(313);
        props.get("DCAmps").setValue(7.7 + Math.random() * 0.1);
      });
    }
    sampleApp.close();
  }

  @Test
  public void testSimulatorPane() throws Exception {
    LogPlayer logPlayer = new LogPlayerImpl();
    logPlayer.setLogFile(TestSimulatorLogReader.getTestFile());
    SimulatorPane simulatorPane = new SimulatorPane(logPlayer, null);
    logPlayer.open();
    SampleApp sampleApp = new SampleApp("simulator", simulatorPane);
    sampleApp.show();
    sampleApp.waitOpen();
    Notifications notification = Notifications.create();
    notification.hideAfter(new Duration(SHOW_TIME / 2));
    notification.title("simulator running");
    notification.text("Simulation started");
    Platform.runLater(() -> notification.showInformation());
    int loops = 50;
    for (int i = 0; i < loops; i++) {
      Thread.sleep(SHOW_TIME / loops);
      double seconds = simulatorPane.getDuration().toSeconds() / loops * i;
      Platform.runLater(() -> simulatorPane.setElapsed(seconds));
    }
    sampleApp.close();
  }

  @Test
  public void testResetableGauge() throws Exception {
    CANValuePane cvpane = new CANValuePane();
    // final Gauge gauge = cvpane.addGauge("TripOdo",
    // I18n.TRIP_ODO_METER,I18n.KM,0, 0);
    // Gauge gauge=LcdGauge.createGauge(I18n.TRIP_ODO_METER, I18n.KM);
    ResetableGauge gauge = new ResetableGauge(Can4EveI18n.TRIP_ODO_METER, Can4EveI18n.KM);
    DoubleProperty odo = new SimpleDoubleProperty();
    // Gauge gauge=new Gauge(SkinType.LCD);
    // Gauge gauge=new ResetableGauge(SkinType.LCD);
    gauge.setDecimals(3);
    cvpane.addGauge("TripOdo", gauge, 0, 0);
    cvpane.fixColumnSizes(4, 100);
    cvpane.fixRowSizes(4, 100);
    /*
     * ConstrainedGridPane buttonPane = new ConstrainedGridPane();
     * buttonPane.setGridLinesVisible(true); buttonPane.fixColumnSizes(0, 25,
     * 25, 25, 25); buttonPane.fixRowSizes(0, 25, 25, 25, 25); StackPane odopane
     * = new StackPane(); ImageButton resetButton = new
     * ImageButton("resetdown.png", "resetup.png"); resetButton.setOnAction(new
     * EventHandler<ActionEvent>() {
     * 
     * @Override public void handle(ActionEvent e) { gauge.setValue(0); } });
     * buttonPane.add(resetButton, 3, 1); odopane.getChildren().add(gauge);
     * odopane.getChildren().add(buttonPane); //
     * StackPane.setAlignment(buttonPane, Pos.TOP_RIGHT); //
     * resetButton.prefWidthProperty().bind(gauge.prefWidthProperty()); //
     * resetButton.prefHeightProperty().bind(gauge.prefHeightProperty());
     * 
     */
    SampleApp sampleApp = new SampleApp("Trip odometer", cvpane);
    sampleApp.show();
    sampleApp.waitOpen();
    Platform.runLater(() -> gauge.setValue(102.723));
    int LOOPS = 100;
    for (int i = 0; i < LOOPS; i++) {
      Thread.sleep(SHOW_TIME / 2 / LOOPS);
      Platform.runLater(() -> gauge.setValue(gauge.getValue() + 0.005));
    }
    Thread.sleep(SHOW_TIME / 2);
    Platform.runLater(() -> gauge.getResetButton().fire());
    for (int i = 0; i < LOOPS; i++) {
      Thread.sleep(SHOW_TIME / 2 / LOOPS);
      Platform.runLater(() -> gauge.setValue(gauge.getValue() + 0.005));
    }
    sampleApp.close();

  }

  @SuppressWarnings("rawtypes")
  @Test
  public void testLineChartJavaFx() throws Exception {
    String title = "SOC/RR";
    String xTitle = "time";
    String yTitle = "%/km";
    VehicleGroup vg = VehicleGroup.get("Triplet");
    CANPropertyManager cpm = new CANPropertyManager(vg);
    Map<String, CANProperty> properties = cpm.getCANProperties("SOC", "Range");
    setPlotValues(properties, 1);
    final JFXCanValueHistoryPlot valuePlot = new JFXCanValueHistoryPlot(title,
        xTitle, yTitle, properties);
    SampleApp sampleApp = new SampleApp("SOC/RR", valuePlot.createLineChart());
    sampleApp.show();
    sampleApp.waitOpen();
    // valuePlot.getLineChart().getData().gt
    int minutes = 35;
    for (int i = 2; i <= minutes; i++) {
      setPlotValues(properties, i);
      valuePlot.update();
      Thread.sleep(SHOW_TIME * 2 / minutes);
    }
    sampleApp.close();
  }

  @Test
  public void testTabIcons() throws Exception {
    JavaFXDisplay display = super.getDisplay();
    VBox vbox = new VBox();
    display.setRoot(vbox);
    SampleApp sampleApp = new SampleApp("menus", vbox);
    sampleApp.show();
    sampleApp.waitOpen();
    display.setStage(sampleApp.getStage());
    Platform.runLater(() -> {
      MenuBar menuBar = display.createMenuBar(sampleApp.getScene(),
          display.getApp());
      display.setMenuBar(menuBar);
      display.showMenuBar(sampleApp.getScene(), menuBar, true);
      display.setUpStatusBar();
    });
    XYTabPane xyTabPane = display.getXyTabPane();
    Platform.runLater(() -> {
      display.setupXyTabPane();
      display.setupDashBoard();
    });
    // xyTabPane.prefHeightProperty().bind(display.getStage().heightProperty());
    display.getStage().heightProperty()
        .addListener(new ChangeListener<Number>() {

          @Override
          public void changed(ObservableValue<? extends Number> observable,
              Number oldValue, Number newValue) {
            if (xyTabPane != null)
              System.out.println("oldh: " + oldValue.intValue() + " newh: "
                  + newValue.intValue() + " xyh:" + xyTabPane.getPrefHeight()
                  + " vth:" + xyTabPane.getvTabPane().getPrefHeight());
          }
        });
    Thread.sleep(SHOW_TIME);
    sampleApp.close();
  }

  public static ArrayList<Node> getAllNodes(Parent root) {
    ArrayList<Node> nodes = new ArrayList<Node>();
    addAllDescendents(root, nodes);
    return nodes;
  }

  private static void addAllDescendents(Parent parent, ArrayList<Node> nodes) {
    for (Node node : parent.getChildrenUnmodifiable()) {
      nodes.add(node);
      if (node instanceof Parent)
        addAllDescendents((Parent) node, nodes);
    }
  }

  @Ignore
  public void testFXML() throws Exception {
    Parent root = FXMLLoader.load(
        getClass().getResource("/com/bitplan/can4eve/gui/connection.fxml"));
    assertNotNull(root);
    ArrayList<Node> nodes = getAllNodes(root);
    assertEquals(8, nodes.size());
    SampleApp.createAndShow("FXML", (Region) root, SHOW_TIME);
  }

  @Test
  public void testStopWatch() {
    StopWatch stopWatch = new JFXStopWatch(Can4EveI18n.WATCH_TOTAL);
    stopWatch.halt();
    stopWatch.reset();
    // System.out.println(stopWatch.asIsoDateStr());
    // assertEquals(0l,stopWatch.getTime());
    long times[] = { 90000 * 1000, 7200000, 0, 2000, 500, 1000, 2000 };
    for (long time : times) {
      stopWatch.setTime(time);
      // System.out.println(stopWatch.asIsoDateStr());
      assertEquals(time, stopWatch.getTime());
    }
  }

  @Ignore
  // if enable would actually call e-mail software
  public void testSupportMail() {
    try {
      throw new Exception("a problem!");
    } catch (Throwable th) {
      String exceptionText = GenericDialog.getStackTraceText(th);
      // needs software version to work!
      GenericDialog.sendReport(null, "testSupportMail", exceptionText);
    }
  }

  Integer counter = 0;
  boolean running = false;

  public Integer increment() {
    running = true;
    while (running) {
      counter++;
      try {
        Thread.sleep(1);
      } catch (InterruptedException e) {
      }
    }
    return counter;
  }

  /**
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testTaskLaunch() throws Exception {
    // https://stackoverflow.com/questions/30089593/java-fx-lambda-for-task-interface
    TaskLaunch<Integer> launch = TaskLaunch.start(() -> increment(),
        Integer.class);
    try {
      while (counter <= 10)
        Thread.sleep(20);
    } catch (InterruptedException e) {
      //
    }
    running = false;
    assertTrue(launch.getTask().get() > 10);
  }

  long calledEffect = 0;
  private LongBinding keepBinding;

  public long callMe(Number newValue) {
    calledEffect = newValue.longValue() + 1;
    return calledEffect;
  }

  @Test
  public void testBinding() {
    SimpleLongProperty lp = new SimpleLongProperty();
    lp.setValue(4711);
    keepBinding = Bindings.createLongBinding(() -> callMe(lp.get()), lp);
    lp.addListener((obs, oldValue, newValue) -> callMe(newValue));
    lp.setValue(1);
    assertEquals(2, calledEffect);
    assertEquals(2, keepBinding.get());
  }

  @Test
  public void testWelcomeWizard() throws Exception {
    WelcomeWizard.testMode=true;
    FXMLSampleApp sampleApp = new FXMLSampleApp("wizards");
    sampleApp.showAndOpen();

    WelcomeWizard[] wizards = new WelcomeWizard[1];

    Platform.runLater(() -> {
      try {
        WelcomeWizard wizard = new WelcomeWizard(Can4EveI18n.WELCOME, null,
            sampleApp.fxml);
        wizards[0] = wizard;
        wizard.display();
      } catch (Exception e) {
        fail("There should be no exception but we got " + e.getMessage());
        e.printStackTrace();
      }
    });
    while (wizards[0] == null)
      Thread.sleep(10);
    WelcomeWizard wizard = wizards[0];
    boolean animated = true;
    if (animated) {
      wizard.animate(SHOW_TIME * 2);
    } else {
      wizard.waitShow(1000);
      while (wizard.getPrivateDialog().isShowing()) {
        Thread.sleep(10);
      }
    }
    for (Entry<String, Object> setting : wizard.getSettings().entrySet()) {
      if (debug)
        System.out.println(setting.getKey() + "=" + setting.getValue());
    }
    sampleApp.close();
  }

  /**
   * run the wizard with the given title
   * 
   * @param title
   *          - of the wizard
   * @param resourcePath
   *          - where to load the fxml files from
   * @param pageNames
   *          - without .fxml extenion
   * @throws Exception
   *           - e.g. IOException
   */
  public void runWizard(String title, String resourcePath, String... pageNames)
      throws Exception {
    Wizard wizard = new Wizard();
    wizard.setTitle(title);

    WizardPane[] pages = new WizardPane[pageNames.length];
    int i = 0;
    for (String pageName : pageNames) {
      Parent root = FXMLLoader
          .load(getClass().getResource(resourcePath + pageName + ".fxml"));
      WizardPane page = new WizardPane();
      page.setHeaderText(I18n.get(pageName));
      page.setContent(root);
      pages[i++] = page;
    }
    wizard.setFlow(new LinearFlow(pages));
    wizard.showAndWait().ifPresent(result -> {
      if (result == ButtonType.FINISH) {
        System.out
            .println("Wizard finished, settings: " + wizard.getSettings());
      }
    });
  }
}

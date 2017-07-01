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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;
import org.junit.Test;

import com.bitplan.can4eve.CANInfo;
import com.bitplan.can4eve.CANValue;
import com.bitplan.can4eve.CANValue.DoubleValue;
import com.bitplan.can4eve.CANValue.IntegerValue;
import com.bitplan.can4eve.VehicleGroup;
import com.bitplan.can4eve.gui.App;
import com.bitplan.can4eve.gui.Group;
import com.bitplan.can4eve.gui.javafx.GenericDialog;
import com.bitplan.can4eve.gui.javafx.SampleApp;
import com.bitplan.can4eve.gui.javafx.WaitableApp;
import com.bitplan.can4eve.gui.swing.Translator;
import com.bitplan.can4eve.json.JsonManager;
import com.bitplan.can4eve.json.JsonManagerImpl;
import com.bitplan.can4eve.util.TaskLaunch;
import com.bitplan.obdii.Preferences.LangChoice;
import com.bitplan.obdii.javafx.ClockPane;
import com.bitplan.obdii.javafx.ClockPane.Watch;
import com.bitplan.obdii.javafx.JFXCanCellStatePlot;
import com.bitplan.obdii.javafx.JFXCanValueHistoryPlot;

import eu.hansolo.OverviewDemo;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.LcdDesign;
import eu.hansolo.medusa.LcdFont;
import eu.hansolo.medusa.Marker;
import eu.hansolo.medusa.Section;
import eu.hansolo.medusa.TickLabelLocation;
import eu.hansolo.medusa.TickLabelOrientation;
import eu.hansolo.medusa.TickMarkType;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Gauge.NeedleSize;
import eu.hansolo.medusa.Marker.MarkerType;
import javafx.application.Platform;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

/**
 * test the descriptive application gui
 * 
 * @author wf
 *
 */
public class TestAppGUI {
  public static final int SHOW_TIME=4000;
  @Test
  public void testAppGUI() throws Exception {
    App app = App.getInstance();
    assertNotNull(app);
    assertEquals(6, app.getMainMenu().getSubMenus().size());
    assertEquals(2, app.getGroups().size());
    int[] expected = { 3, 9 };
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
    pref.debug = true;
    pref.setLanguage(LangChoice.de);
    String json = pref.asJson();
    // System.out.println(json);
    assertEquals("{\n" + "  \"language\": \"de\",\n" + "  \"debug\": true,\n"
        + "  \"screenPercent\": 100\n" + "}", json);
    JsonManager<Preferences> jmPreferences = new JsonManagerImpl<Preferences>(
        Preferences.class);
    Preferences pref2 = jmPreferences.fromJson(json);
    assertNotNull(pref2);
    assertEquals(pref2.debug, pref.debug);
    assertEquals(pref2.getLanguage(), pref.getLanguage());
    Preferences pref3 = new Preferences();
    pref3.fromMap(pref2.asMap());
    assertEquals(pref3.debug, pref.debug);
    assertEquals(pref3.getLanguage(), pref.getLanguage());
  }

  /**
   * get the plot Values
   * 
   * @return
   * @throws Exception
   */
  public List<CANValue<?>> getPlotValues() throws Exception {
    VehicleGroup vg = VehicleGroup.get("triplet");
    CANInfo socInfo = vg.getCANInfoByName("SOC");
    assertNotNull(socInfo);
    DoubleValue SOC = new DoubleValue(socInfo);
    CANInfo rrInfo = vg.getCANInfoByName("Range");
    assertNotNull(rrInfo);
    IntegerValue RR = new IntegerValue(rrInfo);
    Calendar date = Calendar.getInstance();
    final long ONE_MINUTE_IN_MILLIS = 60000;// millisecs
    // https://stackoverflow.com/a/9044010/1497139
    long t = date.getTimeInMillis();
    for (int i = 0; i < 50; i++) {
      Date timeStamp = new Date(t + (i * ONE_MINUTE_IN_MILLIS));
      SOC.setValue(90 - i * 1.2, timeStamp);
      RR.setValue(90 - i, timeStamp);
    }
    List<CANValue<?>> plotValues = new ArrayList<CANValue<?>>();
    plotValues.add(SOC);
    plotValues.add(RR);
    return plotValues;
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
  public void testBarChartJavaFx() throws Exception {
    VehicleGroup vg = VehicleGroup.get("triplet");
    CANInfo cellInfo = vg.getCANInfoByName("CellTemperature");
    assertNotNull(cellInfo);
    DoubleValue cellTemp = new DoubleValue(cellInfo);
    Date timeStamp = new Date();
    for (int i = 0; i < cellTemp.canInfo.getMaxIndex(); i++) {
      cellTemp.setValue(i, 15 + Math.random() * 15, timeStamp);
    }
    String title = "Cell Temperature";
    String xTitle = "cell";
    String yTitle = "° Celsius";
    SampleApp.toolkitInit();
    final JFXCanCellStatePlot valuePlot = new JFXCanCellStatePlot(title, xTitle,
        yTitle, cellTemp, 2.0, 0.5);
    SampleApp sampleApp = new SampleApp("Cell Temperature",
        valuePlot.getBarChart());
    sampleApp.show();
    sampleApp.waitOpen();
    Thread.sleep(SHOW_TIME);
    sampleApp.close();
  }
  
  @Test
  public void testMedusa() throws InterruptedException {
    WaitableApp.toolkitInit();
    OverviewDemo demo=new OverviewDemo();
    demo.init();
    GridPane demoPane=demo.getDemoPane();
    SampleApp sampleApp=new SampleApp("Controls",demoPane);
    sampleApp.show();
    sampleApp.waitOpen();
    demo.startTimer(demoPane);
    Thread.sleep(SHOW_TIME);
    sampleApp.close();
  }
  
  @Test
  public void testGauge() throws InterruptedException {
    WaitableApp.toolkitInit();
    GridPane pane=new GridPane();
    Gauge gauge = GaugeBuilder.create()
        .minValue(0)
        .maxValue(1)
        .tickLabelDecimals(1)
        .decimals(2)
        .autoScale(true)
        .animated(true)
        //.backgroundPaint(Color.TRANSPARENT)
        //.borderPaint(Color.LIGHTGRAY)
        //.knobColor(Color.rgb(0, 90, 120))
        .shadowsEnabled(true)
        //.tickLabelColor(Color.rgb(0, 175, 248))
        //.ledColor(Color.rgb(0, 175, 248))
        .ledVisible(true)
        .ledBlinking(true)
        .sectionsVisible(true)
        .sections(new Section(0.5, 0.75, Color.rgb(139, 195, 102, 0.5)))
        .areasVisible(true)
        .areas(new Section(0.75, 1.0, Color.rgb(234, 83, 79, 0.5)))
        .majorTickMarkColor(Color.MAGENTA)
        //.minorTickMarkColor(Color.rgb(0, 175, 248))
        .majorTickMarkType(TickMarkType.TRAPEZOID)
        .mediumTickMarkType(TickMarkType.DOT)
        .minorTickMarkType(TickMarkType.LINE)
        .tickLabelOrientation(TickLabelOrientation.ORTHOGONAL)
        .tickMarkSections(new Section(0.25, 0.5, Color.rgb(241, 161, 71)))
        .tickMarkSectionsVisible(true)
        .markers(new Marker(0.5, "", Color.CYAN, MarkerType.TRIANGLE))
        .markersVisible(true)
        //.majorTickMarksVisible(true)
        //.minorTickMarksVisible(true)
        .tickLabelLocation(TickLabelLocation.INSIDE)
        //.tickLabelsVisible(true)
        .tickLabelSections(new Section(0.1, 0.3, Color.rgb(0, 175, 248)))
        //.tickLabelSectionsVisible(true)
        .title("Title")
        //.titleColor(Color.rgb(223, 223, 223))
        .unit("Unit")
        .lcdDesign(LcdDesign.SECTIONS)
        .lcdVisible(true)
        .lcdFont(LcdFont.STANDARD)
        //.unitColor(Color.rgb(223, 223, 223))
        //.valueColor(Color.rgb(223, 223, 223))
        .needleSize(NeedleSize.THICK)
        .build();
    pane.add(gauge, 0,0);
    double value = 85.0;
    SampleApp sampleApp=new SampleApp("Gauge",pane);
    sampleApp.show();
    sampleApp.waitOpen();
    while (sampleApp.getStage().isShowing()) {
      Thread.sleep(15);
      final double gaugeValue=value/100;
      Platform.runLater(()->gauge.setValue(gaugeValue));
      value = value - 0.1;
      if (value < 45)
        sampleApp.close();;
    }
  }
  
  @Test
  public void testClockPanel() throws Exception {
    WaitableApp.toolkitInit();
    Translator.initialize(Preferences.getInstance().getLanguage().name());
    ClockPane clockPane=new ClockPane();
    clockPane.setWatch(Watch.Charging,1320*1000);
    clockPane.setWatch(Watch.Parking,390*1000);
    SampleApp sampleApp=new SampleApp("Clocks",clockPane);
    sampleApp.show();
    sampleApp.waitOpen();
    for (int i=0;i<(SHOW_TIME/1000*2);i++) {
      Thread.sleep(1000);
      clockPane.setWatch(Watch.Moving,i*1000+300*1000);
    }
    sampleApp.close();
  }

  @Test
  public void testLineChartJavaFx() throws Exception {
    List<CANValue<?>> plotValues = this.getPlotValues();
    String title = "SOC/RR";
    String xTitle = "time";
    String yTitle = "%/km";
    SampleApp.toolkitInit();
    final JFXCanValueHistoryPlot valuePlot = new JFXCanValueHistoryPlot(title,
        xTitle, yTitle, plotValues);
    SampleApp sampleApp = new SampleApp("SOC/RR", valuePlot.getLineChart());
    sampleApp.show();
    sampleApp.waitOpen();
    Thread.sleep(SHOW_TIME);
  }

  @Ignore
  // if enable would actually call e-mail software
  public void testSupportMail() {
    try {
      throw new Exception("a problem!");
    } catch (Throwable th) {
      String exceptionText=GenericDialog.getStackTraceText(th);
      // needs software version to work!
      GenericDialog.sendReport(null, "testSupportMail", exceptionText);
    } 
  }
  
  Integer counter=0;
  boolean running=false;
  public Integer increment() {
    running=true;
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
    WaitableApp.toolkitInit();
    // https://stackoverflow.com/questions/30089593/java-fx-lambda-for-task-interface
    TaskLaunch<Integer> launch = TaskLaunch.start(()->increment(),Integer.class);
    try {
      while(counter<=10)
        Thread.sleep(20);
    } catch (InterruptedException e) {
      //
    }
    running=false;
    assertTrue(launch.getTask().get()>10);
  }
}

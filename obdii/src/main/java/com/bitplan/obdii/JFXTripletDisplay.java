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

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.controlsfx.glyphfont.FontAwesome;

import com.bitplan.can4eve.CANValue.DoubleValue;
import com.bitplan.can4eve.Vehicle.State;
import com.bitplan.can4eve.gui.javafx.CANProperty;
import com.bitplan.can4eve.gui.javafx.CANPropertyManager;
import com.bitplan.error.SoftwareVersion;
import com.bitplan.gui.App;
import com.bitplan.i18n.I18n;
import com.bitplan.obdii.javafx.CANValuePane;
import com.bitplan.obdii.javafx.JFXCanCellStatePlot;
import com.bitplan.obdii.javafx.JFXCanValueHistoryPlot;
import com.bitplan.obdii.javafx.JavaFXDisplay;

import eu.hansolo.medusa.Gauge;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Region;

/**
 * a Java FX based display for triplet vehicles
 * 
 * @author wf
 *
 */
public class JFXTripletDisplay extends JavaFXDisplay {

  /**
   * construct me
   * 
   * @param app
   * @param softwareVersion
   * @param obdApp
   */
  public JFXTripletDisplay(App app, SoftwareVersion softwareVersion,
      OBDApp obdApp) {
    super(app, softwareVersion, obdApp);
  }

  /**
   * update the given tab with the given region
   * 
   * @param view
   * @param tabId
   * @param region
   */
  private void updateTab(String tabId, Region region) {
    Tab tab = super.getTab(tabId);
    if (tab != null && region != null) {
      tab.setContent(region);
    } else {
      String problem = "";
      String delim = "";
      if (tab == null) {
        problem += "tab is null";
        delim = ", ";
      }
      if (region == null) {
        problem += delim + "region is null";
      }
      LOGGER.log(Level.SEVERE,
          String.format("updateTab %s: %s", tabId, problem));
    }
  }

  /**
   * set bindings
   * 
   * @param canProperties
   */
  public void bind(Map<String, Property<?>> canProperties) {
    this.canProperties = canProperties;
    // bind values by name
    CANValuePane[] canValuePanes = { chargePane, odoPane };
    for (CANValuePane canValuePane : canValuePanes) {
      if (canValuePane != null) {
        for (Entry<String, Gauge> gaugeEntry : canValuePane.getGaugeMap()
            .entrySet()) {
          Gauge gauge = gaugeEntry.getValue();
          bind(gauge.valueProperty(),
              this.canProperties.get(gaugeEntry.getKey()), true);
        }
      }
    }
    if (dashBoardPane != null) {
      bind(dashBoardPane.getRpmGauge().valueProperty(),
          this.canProperties.get("RPM"));
      bind(dashBoardPane.rpmMax.valueProperty(),
          this.canProperties.get("RPM-max"), true);
      bind(dashBoardPane.rpmAvg.valueProperty(),
          this.canProperties.get("RPM-avg"), true);
      bind(dashBoardPane.getRpmSpeedGauge().valueProperty(),
          this.canProperties.get("RPMSpeed"));
      bind(dashBoardPane.rpmSpeedMax.valueProperty(),
          this.canProperties.get("RPMSpeed-max"), true);
      bind(dashBoardPane.rpmSpeedAvg.valueProperty(),
          this.canProperties.get("RPMSpeed-avg"), true);
    }
    if (clockPane != null) {
      ObservableValue<?> vehicleState = this.canProperties.get("vehicleState");
      SimpleLongProperty msecsProperty = (SimpleLongProperty) this.canProperties
          .get("msecs");
      if (vehicleState != null && msecsProperty != null) {
        msecsProperty.addListener((obs, oldValue, newValue) -> super.clockPane
            .updateMsecs(newValue, (State) vehicleState.getValue()));
      }
    }
  }

  /**
   * setup the special parts e.g. history
   * 
   * @param cpm
   * @throws Exception
   */
  @SuppressWarnings({ "rawtypes" })
  public void setupSpecial(CANPropertyManager cpm) throws Exception {
    // resetButton handling for trip Odometer
    CANProperty<DoubleValue, Double> tripOdoValue = cpm.get("TripOdo");
    tripOdoValue.getProperty().addListener(new ChangeListener<Number>() {

      @Override
      public void changed(ObservableValue<? extends Number> observable,
          Number oldValue, Number newValue) {
        if (newValue.equals(0.0)) {
          CANProperty<DoubleValue, Double> tripRounds = cpm.get("TripRounds");
          Date timeStamp = new Date();
          tripRounds.getCanValue().setValue(0.0, timeStamp);
        }
      }

    });
    TabPane tabPane = super.getXyTabPane().getTabPane(BATTERY_GROUP);
    if (tabPane != null) {
      CANProperty<DoubleValue, Double> cellTemperature = cpm
          .get("CellTemperature");
      final JFXCanCellStatePlot cellStatePlot = new JFXCanCellStatePlot(
          "cellTemperature", "cell", "Temperature", cellTemperature, 1.0, 0.5);

      Platform.runLater(() -> super.getXyTabPane().addTab(tabPane, "cellTemp",
          I18n.get(Can4EveI18n.CELL_TEMP), "temp50", cellStatePlot.getBarChart()));
      cellStatePlot.updateOn(cellTemperature.getUpdateCountProperty());

      CANProperty<DoubleValue, Double> cellVoltage = cpm.get("CellVoltage");
      final JFXCanCellStatePlot cellVoltagePlot = new JFXCanCellStatePlot(
          "cellVoltage", "cell", "Voltage", cellVoltage, 0.01, 0.1);
      Platform.runLater(() -> super.getXyTabPane().addTab(tabPane,
          "cellVoltage", I18n.get(Can4EveI18n.CELL_VOLTAGE),
          FontAwesome.Glyph.FLASH.name(), cellVoltagePlot.getBarChart()));
      cellVoltagePlot.updateOn(cellVoltage.getUpdateCountProperty());

      // setup history
      String title = "SOC/RR";
      String xTitle = "time";
      String yTitle = "%/km";
      Map<String, CANProperty> properties = cpm.getCANProperties("SOC",
          "Range");
      final JFXCanValueHistoryPlot valuePlot = new JFXCanValueHistoryPlot(title,
          xTitle, yTitle, properties);
      // TODO - use addTab and remove from the json app declaration file
      Platform.runLater(() -> updateTab("soc_rr", valuePlot.createLineChart()));
      valuePlot.updateOn(cpm.get("SOC").getUpdateCountProperty());
    }
  }
}

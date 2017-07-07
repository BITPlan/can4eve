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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.bitplan.can4eve.CANValue;
import com.bitplan.can4eve.CANValue.DoubleValue;
import com.bitplan.can4eve.CANValue.IntegerValue;
import com.bitplan.can4eve.SoftwareVersion;
import com.bitplan.can4eve.Vehicle.State;
import com.bitplan.can4eve.gui.App;
import com.bitplan.obdii.javafx.CANValuePane;
import com.bitplan.obdii.javafx.JFXCanCellStatePlot;
import com.bitplan.obdii.javafx.JFXCanValueHistoryPlot;
import com.bitplan.obdii.javafx.JavaFXDisplay;

import eu.hansolo.medusa.Gauge;
import javafx.application.Platform;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.value.ObservableValue;
//import javafx.embed.swing.SwingNode;
import javafx.scene.control.Tab;
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
   * @param tab
   * @param region
   */
  private void updateTab(Tab tab, Region region) {
    if (region != null) {
      tab.setContent(region);
    }
  }

  /**
   * update the history
   * 
   * @param xValue
   * @param yValue
   * @param title
   * @param xTitle
   * @param yTitle
   */
  public void updateHistory(DoubleValue xValue, IntegerValue yValue,
      String title, String xTitle, String yTitle) {
    Tab activeTab = super.getActiveTab();
    if (activeTab == null)
      return;
    String activePanelTitle = activeTab.getText();
    if ("history".equals(activePanelTitle)) {
      List<CANValue<?>> plotValues = new ArrayList<CANValue<?>>();
      plotValues.add(xValue);
      plotValues.add(yValue);
      final JFXCanValueHistoryPlot valuePlot = new JFXCanValueHistoryPlot(title,
          xTitle, yTitle, plotValues);
      Platform.runLater(() -> updateTab(activeTab, valuePlot.getLineChart()));
    }
  }

  /**
   * special handling for Cell Temperature and Cell Voltage
   */
  @Override
  public void updateCanValueField(CANValue<?> canValue) {
    if (!available)
      return;
    String title = canValue.canInfo.getTitle();
    Tab activeTab = super.getActiveTab();
    if (activeTab == null)
      return;
    String activePanelTitle = activeTab.getText();
    if (title.toLowerCase().startsWith("cell")) {
      // TODO - use some kind of id to clearly identify plotable stuff
      // Cell Temp / Cell Voltage e.g. might not work in i18n
      if (title.startsWith(activePanelTitle)) {
        if (canValue.getUpdateCount() % canValue.canInfo.getMaxIndex() == 0) {
          if ("Cell Temp".equals(activePanelTitle)) {
            DoubleValue cellTemperature = (DoubleValue) canValue;
            final JFXCanCellStatePlot cellStatePlot = new JFXCanCellStatePlot(
                "cellTemperature", "cell", "Temperature", cellTemperature, 1.0,
                0.5);
            Platform.runLater(
                () -> updateTab(activeTab, cellStatePlot.getBarChart()));
          }
          if ("Cell Voltage".equals(activePanelTitle)) {
            DoubleValue cellVoltage = (DoubleValue) canValue;
            final JFXCanCellStatePlot cellStatePlot = new JFXCanCellStatePlot(
                "cellVoltage", "cell", "Voltage", cellVoltage, 0.01, 0.1);
            Platform.runLater(
                () -> updateTab(activeTab, cellStatePlot.getBarChart()));
          }
        }
      }
    } else {
      super.updateCanValueField(canValue);
    }
  }

  /**
   * set bindings
   * 
   * @param canProperties
   */
  public void bind(Map<String, ObservableValue<?>> canProperties) {
    this.canProperties = canProperties;
    // bind values by name
    CANValuePane[] canValuePanes = { chargePane };
    for (CANValuePane canValuePane : canValuePanes) {
      if (canValuePane != null) {
        for (Entry<String, Gauge> gaugeEntry : canValuePane.getGaugeMap()
            .entrySet()) {
          bind(gaugeEntry.getValue().valueProperty(),
              this.canProperties.get(gaugeEntry.getKey()));
        }
      }
    }
    if (dashBoardPane != null) {
      bind(dashBoardPane.getRpmGauge().valueProperty(),
          this.canProperties.get("RPM"));
      bind(dashBoardPane.rpmMax.valueProperty(),
          this.canProperties.get("RPM-max"));
      bind(dashBoardPane.rpmAvg.valueProperty(),
          this.canProperties.get("RPM-avg"));

      bind(dashBoardPane.getRpmSpeedGauge().valueProperty(),
          this.canProperties.get("RPMSpeed"));
      bind(dashBoardPane.rpmSpeedMax.valueProperty(),
          this.canProperties.get("RPMSpeed-max"));
      bind(dashBoardPane.rpmSpeedAvg.valueProperty(),
          this.canProperties.get("RPMSpeed-avg"));
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

}

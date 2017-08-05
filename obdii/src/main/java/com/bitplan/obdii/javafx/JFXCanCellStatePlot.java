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

import java.util.List;
import java.util.logging.Level;

import com.bitplan.can4eve.CANInfo;
import com.bitplan.can4eve.CANValue.DoubleValue;
import com.bitplan.can4eve.CANValueItem;
import com.bitplan.can4eve.gui.javafx.CANProperty;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;

/**
 * plot CellState Values
 * 
 * @author wf
 *
 */
public class JFXCanCellStatePlot extends JFXCanValuePlot
    implements CanValuePlot {
  private CANProperty<DoubleValue, Double> cellValues;
  private Double rangeExtra;
  private Double tickUnit;
  XYChart.Series<String, Number> series;

  /**
   * create a Plot for a History of CANValues
   * 
   * @param title
   * @param xTitle
   * @param yTitle
   * @param cellValues
   */
  public JFXCanCellStatePlot(String title, String xTitle, String yTitle,
      CANProperty<DoubleValue, Double> cellValues, Double rangeExtra,
      Double tickUnit) {
    super(title, xTitle, yTitle);
    this.cellValues = cellValues;
    this.rangeExtra = rangeExtra;
    this.tickUnit = tickUnit;
  }

  /**
   * get the BarChart for the cellStates (e.g. Temperature/Voltage)
   * 
   * @return - the barchart
   */
  public BarChart<String, Number> getBarChart() {
    if (cellValues == null)
      return null;
    // defining the axes
    final CategoryAxis xAxis = new CategoryAxis();
    NumberAxis yAxis;
    DoubleValue cellDoubleValues = cellValues.getCanValue();
    if ((cellDoubleValues.getMin() != null) && (cellValues.getMax() != null)) {
      yAxis = new NumberAxis(cellDoubleValues.getMin() - rangeExtra,
          cellDoubleValues.getMax() + rangeExtra, tickUnit);
    } else {
      yAxis = new NumberAxis();
    }
    yAxis.setLabel(yTitle);
    xAxis.setLabel(xTitle);
    // creating the chart
    final BarChart<String, Number> barChart = new BarChart<String, Number>(
        xAxis, yAxis);

    barChart.setTitle(title);
    barChart.setCategoryGap(0);
    barChart.setBarGap(1);
    final CANInfo canInfo = cellValues.getCANInfo();

    // defining a series
    series = new XYChart.Series<String, Number>();
    series.setName(canInfo.getTitle());
    updateSeries(series, cellValues);
    barChart.getData().add(series);
    return barChart;
  }

  /**
   * update the series
   * 
   * @param series
   * @param cellValues
   */
  private void updateSeries(Series<String, Number> series,
      CANProperty<DoubleValue, Double> cellValues) {
    DoubleValue cellDoubleValues = cellValues.getCanValue();
    List<CANValueItem<Double>> valueItems = cellDoubleValues.getValueItems();
    ObservableList<Data<String, Number>> seriesData = series.getData();
    if (debug) {
      CANInfo canInfo = cellValues.getCANInfo();
      LOGGER.log(Level.INFO, "plotting for " + canInfo.getMaxIndex()
          + " values of " + canInfo.getTitle());
    }
    int i = 0;
    for (CANValueItem<Double> valueItem : valueItems) {
      if (valueItem.isAvailable()) {
        if (seriesData.size() > i) {
          Data<String, Number> seriesEntry = seriesData.get(i);
          seriesEntry.setYValue(valueItem.getValue());
        } else {
          String cellnum = "" + (i);
          seriesData.add(
              new XYChart.Data<String, Number>(cellnum, valueItem.getValue()));
        } // if seriesEntry exists
      } // if available
      i++;
    } // for

  }

  @Override
  public void update() {
    Platform.runLater(() -> updateSeries(series, cellValues));
  }
}

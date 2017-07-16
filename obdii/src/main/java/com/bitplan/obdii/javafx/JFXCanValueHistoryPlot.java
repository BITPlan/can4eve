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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import com.bitplan.can4eve.CANValue.ValueItem;
import com.bitplan.can4eve.gui.javafx.CANProperty;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;

/**
 * plot a histor of CanValues
 * @author wf
 *
 */
public class JFXCanValueHistoryPlot {
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.obdii.javafx");
  public static boolean debug=false;
  
  String title;
  String xTitle;
  String yTitle;
  @SuppressWarnings("rawtypes")
  private Map<String, CANProperty> canProperties;
  private Map<String,XYChart.Series<Number, Number>> seriesMap=new HashMap<String,XYChart.Series<Number, Number>>();
  LineChart<Number,Number> lineChart=null;
  
  public LineChart<Number, Number> getLineChart() {
    return lineChart;
  }

  /**
   * create a Plot for a History of CANValues
   * 
   * @param title
   * @param xTitle
   * @param yTitle
   * @param properties
   */
  @SuppressWarnings("rawtypes")
  public JFXCanValueHistoryPlot(String title, String xTitle, String yTitle,
      Map<String, CANProperty> properties) {
    this.title = title;
    this.xTitle = xTitle;
    this.yTitle = yTitle;
    this.canProperties = properties;
  }
  
  /**
   * get the minute of a timeStamp value
   * @param timeStamp
   * @param start
   * @return - the minute value
   */
  public long getMinute(Date timeStamp,Date start) {
    long minute=(timeStamp.getTime()-start.getTime())/60000;
    return minute;
  }
  
  /**
   * create a series for the given CANValue
   * @param canProperty - the canValue to use as a basis
   * @return the chart data series
   */
  @SuppressWarnings("rawtypes")
  public XYChart.Series<Number, Number> createSeries(CANProperty canProperty){
    //defining a series
    XYChart.Series<Number, Number> series = new XYChart.Series<Number,Number>();
    series.setName(canProperty.getName());
    return series;
  }
  
  /**
   * update the given chart Series
   * @param series - the series to update
   * @param canProperty - the canvalue to take the data from
   */
  @SuppressWarnings("rawtypes")
  public void updateSeries(XYChart.Series<Number, Number> series, CANProperty canProperty) {
    CircularFifoQueue<?> history = canProperty.getCanValue().getHistory();
    if (debug)
      LOGGER.log(Level.INFO,
        "plotting for " + history.size() + " history values of " + canProperty.getCanValue().canInfo.getTitle());
    Date first=null;
    ObservableList<Data<Number, Number>> dataList = series.getData();
    // FIXME - full redraw?
    dataList.clear();
    for (Object historyValueObject : history) {
      ValueItem<?>historyValue = (ValueItem<?>) historyValueObject;
      
      Date timeStamp=historyValue.getTimeStamp();
      if (first==null)
        first=timeStamp;
      Double value;
      if (historyValue.getValue() instanceof Integer) {
        // http://stackoverflow.com/questions/31860761/maven-compile-error-with-using-java-generics
        int intValue=(Integer) historyValue.getValue();
        value = new Double(intValue*1.0);
      } else {
        value = (Double) historyValue.getValue();
      }
      long minute = getMinute(timeStamp,first);
      Data<Number, Number> chartData = new XYChart.Data<Number,Number>(minute,value);
      dataList.add(chartData);
    }
  }
  
  /**
   * get the LineChart for this history
   * @return - the line chart
   */
  @SuppressWarnings("rawtypes")
  public LineChart<Number, Number> createLineChart() {
    //defining the axes
    final NumberAxis xAxis = new NumberAxis();
    final NumberAxis yAxis = new NumberAxis();
    xAxis.setLabel(xTitle);
    //creating the chart
    lineChart = 
            new LineChart<Number,Number>(xAxis,yAxis);
            
    lineChart.setTitle(title);
    for (CANProperty canProperty : this.canProperties.values()) {
      Series<Number, Number> series = this.createSeries(canProperty);
      seriesMap.put(canProperty.getName(), series);
      updateSeries(series,canProperty);
      lineChart.getData().add(series);
    }
    return lineChart;
  }

  public void update() {
    for (CANProperty canProperty : this.canProperties.values()) {
      Series<Number, Number> series = seriesMap.get(canProperty.getName());
      Platform.runLater(()->updateSeries(series,canProperty));
    }
  }

}

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

import java.awt.BasicStroke;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import com.bitplan.can4eve.CANValue;
import com.bitplan.can4eve.CANValue.ValueItem;

/**
 * plot a history of the given values
 * 
 * @author wf
 *
 * @param <ValueType>
 */
public class CANValueHistoryPlot {

  protected static Logger LOGGER = Logger.getLogger("com.bitplan.obdii");
  public static boolean debug=false;
  
  String title;
  String xTitle;
  String yTitle;
  private Collection<CANValue<?>> canValues;

  /**
   * create a Plot for a History of CANValues
   * 
   * @param title
   * @param xTitle
   * @param yTitle
   * @param canValues
   */
  public CANValueHistoryPlot(String title, String xTitle, String yTitle,
      Collection<CANValue<?>> canValues) {
    this.title = title;
    this.xTitle = xTitle;
    this.yTitle = yTitle;
    this.canValues = canValues;
  }

  /**
   * get the Panel
   * 
   * @return
   */
  public ChartPanel getPanel() {
    final XYDataset dataset = createDataset();
    final JFreeChart chart = createChart(dataset);
    final ChartPanel chartPanel = new ChartPanel(chart);
    return chartPanel;
  }

  /**
   * create the data set
   * 
   * @return
   */
  @SuppressWarnings("deprecation")
  private XYDataset createDataset() {
    final TimeSeriesCollection dataset = new TimeSeriesCollection();
    dataset.setDomainIsPointsInTime(true);
    for (CANValue<?> canValue : this.canValues) {
      final TimeSeries series = new TimeSeries(canValue.canInfo.getTitle(), Minute.class);
      CircularFifoQueue<?> history = canValue.getHistory();
      if (debug)
        LOGGER.log(Level.INFO,
          "plotting for " + history.size() + " history values of " + canValue.canInfo.getTitle());
      for (Object historyValueObject : history) {
        ValueItem<?>historyValue = (ValueItem<?>) historyValueObject;
        Minute min = new Minute(historyValue.getTimeStamp());
        Double value;
        if (historyValue.getValue() instanceof Integer) {
          // http://stackoverflow.com/questions/31860761/maven-compile-error-with-using-java-generics
          int intValue=(Integer) historyValue.getValue();
          value = new Double(intValue*1.0);
        } else {
          value = (Double) historyValue.getValue();
        }
        series.addOrUpdate(min, value);

      }
      dataset.addSeries(series);
    }
    return dataset;
  }

  /**
   * Creates a chart.
   * 
   * @param dataset
   *          a dataset.
   * 
   * @return A chart.
   */
  private JFreeChart createChart(final XYDataset dataset) {

    final JFreeChart chart = ChartFactory.createTimeSeriesChart(title, xTitle,
        yTitle, dataset, true, true, false);

    chart.setBackgroundPaint(Color.white);

    // final StandardLegend sl = (StandardLegend) chart.getLegend();
    // sl.setDisplaySeriesShapes(true);

    final XYPlot plot = chart.getXYPlot();
    // plot.setOutlinePaint(null);
    plot.setBackgroundPaint(Color.lightGray);
    plot.setDomainGridlinePaint(Color.white);
    plot.setRangeGridlinePaint(Color.white);
    // plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
    plot.setDomainCrosshairVisible(true);
    plot.setRangeCrosshairVisible(false);

    final XYItemRenderer renderer = plot.getRenderer();
    if (renderer instanceof StandardXYItemRenderer) {
      renderer.setSeriesStroke(0, new BasicStroke(2.0f));
      renderer.setSeriesStroke(1, new BasicStroke(2.0f));
    }

    final DateAxis axis = (DateAxis) plot.getDomainAxis();
    axis.setDateFormatOverride(new SimpleDateFormat("HH:mm"));
    return chart;
  }

}

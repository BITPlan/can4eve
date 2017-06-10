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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.bitplan.can4eve.CANInfo;
import com.bitplan.can4eve.CANValue.DoubleValue;

/**
 * plot the state of the Cells
 * 
 * @author wf
 *
 * @param <ValueType>
 */
public class CANCellStatePlot {

  protected static Logger LOGGER = Logger.getLogger("com.bitplan.obdii");
  public static boolean debug = false;

  String title;
  String xTitle;
  String yTitle;
  private DoubleValue cellValues;
  private Double rangeExtra;

  /**
   * create a Plot for a History of CANValues
   * 
   * @param title
   * @param xTitle
   * @param yTitle
   * @param cellValues
   */
  public CANCellStatePlot(String title, String xTitle, String yTitle,
      DoubleValue cellValues,Double rangeExtra) {
    this.title = title;
    this.xTitle = xTitle;
    this.yTitle = yTitle;
    this.cellValues = cellValues;
    this.rangeExtra=rangeExtra;
  }

  /**
   * get the Panel
   * 
   * @return
   */
  public ChartPanel getPanel() {
    final IntervalXYDataset dataset = createDataset();
    final JFreeChart chart = createChart(dataset);
    final ChartPanel chartPanel = new ChartPanel(chart);
    return chartPanel;
  }

  /**
   * create the data set
   * 
   * @return
   */
  private IntervalXYDataset createDataset() {
    final XYSeriesCollection dataset = new XYSeriesCollection();
    final CANInfo canInfo = cellValues.canInfo;
    final XYSeries series = new XYSeries(canInfo.getTitle());
    if (debug)
      LOGGER.log(Level.INFO, "plotting for " + canInfo.getMaxIndex() + " values of "
          + canInfo.getTitle());
    for (int i = 0; i < canInfo.getMaxIndex(); i++) {
      if (cellValues.getValueItems() != null)
        if (cellValues.getValueItems()[i].isAvailable())
          series.add(i+1, cellValues.getValueItems()[i].getValue());
    }
    dataset.addSeries(series);
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
  private JFreeChart createChart(final IntervalXYDataset dataset) {

    final JFreeChart chart = ChartFactory.createXYBarChart(title, xTitle, true,
        yTitle, dataset);

    // chart=ChartFactory.createXYBarChart(yTitle, xAxisLabel, dateAxis,
    // yAxisLabel, dataset)
    // chart=ChartFactory.createXYBarChart(yTitle, xAxisLabel, dateAxis,
    // yAxisLabel, dataset, orientation, legend, tooltips, urls)
    chart.setBackgroundPaint(Color.white);
    // final StandardLegend sl = (StandardLegend) chart.getLegend();
    // sl.setDisplaySeriesShapes(true);

    final XYPlot plot = chart.getXYPlot();
    // set the Y-Axis
    ValueAxis rangeAxis = plot.getRangeAxis();
    Range range = DatasetUtilities.findRangeBounds(dataset);
    if (range != null) {
      range = new Range(range.getLowerBound()-rangeExtra, range.getUpperBound() + rangeExtra);
      rangeAxis.setRange(range);
    }
    final NumberAxis xAxis = new NumberAxis(xTitle);
    Range xRange = DatasetUtilities.findDomainBounds(dataset);
    xAxis.setRange(xRange);
    xAxis.setTickUnit(new NumberTickUnit(1.0));
    xAxis.setVerticalTickLabels(true);
    plot.setDomainAxis(xAxis);

    // plot.setOutlinePaint(null);
    plot.setBackgroundPaint(Color.lightGray);
    plot.setDomainGridlinePaint(Color.white);
    plot.setRangeGridlinePaint(Color.white);
    // plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
    plot.setDomainCrosshairVisible(true);
    plot.setRangeCrosshairVisible(false);
    XYItemRenderer barRenderer = plot.getRenderer();
    barRenderer.setSeriesPaint(0, Color.BLUE);

    final XYItemRenderer renderer = plot.getRenderer();
    if (renderer instanceof StandardXYItemRenderer) {
      renderer.setSeriesStroke(0, new BasicStroke(2.0f));
      renderer.setSeriesStroke(1, new BasicStroke(2.0f));
    }
    return chart;
  }

}

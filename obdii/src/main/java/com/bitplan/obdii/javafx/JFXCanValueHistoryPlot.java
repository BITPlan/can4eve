package com.bitplan.obdii.javafx;

import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import com.bitplan.can4eve.CANValue;
import com.bitplan.can4eve.CANValue.ValueItem;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

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
  private Collection<CANValue<?>> canValues;

  /**
   * create a Plot for a History of CANValues
   * 
   * @param title
   * @param xTitle
   * @param yTitle
   * @param canValues
   */
  public JFXCanValueHistoryPlot(String title, String xTitle, String yTitle,
      Collection<CANValue<?>> canValues) {
    this.title = title;
    this.xTitle = xTitle;
    this.yTitle = yTitle;
    this.canValues = canValues;
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
   * get the LineChart for this history
   * @return - the line chart
   */
  public LineChart<Number, Number> getLineChart() {
    //defining the axes
    final NumberAxis xAxis = new NumberAxis();
    final NumberAxis yAxis = new NumberAxis();
    xAxis.setLabel(xTitle);
    //creating the chart
    final LineChart<Number,Number> lineChart = 
            new LineChart<Number,Number>(xAxis,yAxis);
            
    lineChart.setTitle(title);
    for (CANValue<?> canValue : this.canValues) {
      //defining a series
      XYChart.Series<Number, Number> series = new XYChart.Series<Number,Number>();
      series.setName(canValue.canInfo.getTitle());
      CircularFifoQueue<?> history = canValue.getHistory();
      if (debug)
        LOGGER.log(Level.INFO,
          "plotting for " + history.size() + " history values of " + canValue.canInfo.getTitle());
      Date first=null;
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
        series.getData().add(new XYChart.Data<Number,Number>(getMinute(timeStamp,first),value));
      }
      lineChart.getData().add(series);
    }
    return lineChart;
  }

}

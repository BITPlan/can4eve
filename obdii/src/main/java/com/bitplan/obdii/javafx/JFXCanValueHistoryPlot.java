package com.bitplan.obdii.javafx;

import java.util.Collection;
import java.util.logging.Logger;

import com.bitplan.can4eve.CANValue;

/**
 * plot a histor of CanValues
 * @author wf
 *
 */
public class JFXCanValueHistoryPlot {
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
  public JFXCanValueHistoryPlot(String title, String xTitle, String yTitle,
      Collection<CANValue<?>> canValues) {
    this.title = title;
    this.xTitle = xTitle;
    this.yTitle = yTitle;
    this.canValues = canValues;
  }

}

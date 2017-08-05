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

import java.util.logging.Logger;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * base class for value plotting
 * @author wf
 *
 */
public abstract class JFXCanValuePlot {
 
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.obdii.javafx");
  public static boolean debug = true;
  
  IntegerProperty updateCount=new SimpleIntegerProperty();

  String title;
  String xTitle;
  String yTitle;
  
  public IntegerProperty getUpdateCount() {
    return updateCount;
  }

  /**
   * construct me with the given title, xTitle and yTitle
   * @param title
   * @param xTitle
   * @param yTitle
   */
  public JFXCanValuePlot(String title, String xTitle, String yTitle) {
    this.title = title;
    this.xTitle = xTitle;
    this.yTitle = yTitle;
    updateCount.addListener(new ChangeListener<Number>() {

      @Override
      public void changed(ObservableValue<? extends Number> observable,
          Number oldValue, Number newValue) {
        update();
      }

     });
  }

  public abstract void update();
}

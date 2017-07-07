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

import java.util.HashMap;
import java.util.Map;

import eu.hansolo.LcdGauge;
import eu.hansolo.medusa.Gauge;

/**
 * a Pane with Gauges for CANValues
 * @author wf
 *
 */
public class CANValuePane extends ConstrainedGridPane {

  Map<String,Gauge> gaugeMap=new HashMap<String,Gauge>();
  
  public Map<String, Gauge> getGaugeMap() {
    return gaugeMap;
  }

  /**
   * add the given Gauge
   * @param canInfo
   * @param gauge
   * @param col
   * @param row
   */
  public Gauge addGauge(String canInfo, Gauge gauge,int col,
      int row) {
    this.add(gauge, col, row);
    gaugeMap.put(canInfo, gauge);
    return gauge;
  }
  
  /**
   * add a new Gauge
   * @param i18nTitle
   * @param i18nUnit
   * @param col
   * @param row
   */
  public Gauge addGauge(String canInfo,String i18nTitle, String i18nUnit, int col, int row) {
    Gauge gauge = LcdGauge.create(i18nTitle, i18nUnit);
    addGauge(canInfo,gauge,col,row);
    return gauge;
  }
  
}

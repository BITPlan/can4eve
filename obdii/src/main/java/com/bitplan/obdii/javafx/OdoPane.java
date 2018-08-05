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

import com.bitplan.obdii.Can4EveI18n;

import eu.hansolo.LcdGauge.ResetableGauge;

/**
 * Odometer values
 * @author wf
 *
 */
public class OdoPane extends CANValuePane {
  /**
   * odometer info
   */
  public OdoPane() {
    super.addGauge("Odometer",Can4EveI18n.ODO_METER,Can4EveI18n.KM,0,0);
    ResetableGauge odogauge = new ResetableGauge(Can4EveI18n.get(Can4EveI18n.TRIP_ODO_METER), Can4EveI18n.get(Can4EveI18n.KM));
    super.addGauge("TripOdo",odogauge,0,1).setDecimals(3);;
    this.fixColumnSizes(4, 100);
    this.fixRowSizes(4, 50,50);
  }
}

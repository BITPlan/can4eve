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

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Gauge.SkinType;
import eu.hansolo.medusa.GaugeBuilder;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;

/**
 * a pane to show charging
 * 
 * @author wf
 *
 */
public class ChargePane extends CANValuePane {
 
  /**
   * charging info
   */
  public ChargePane() {
    Gauge socGauge = GaugeBuilder.create().skinType(SkinType.BATTERY).title("SOC")
        .titleColor(Color.WHITE).animated(true).gradientBarEnabled(true)
        .minValue(0)
        .maxValue(100)
        .tickLabelDecimals(1)
        .decimals(1)
        //.title(I18n.get(I18n.SOC))
        .titleColor(Color.WHITE)
        .gradientBarStops(new Stop(0.0, Color.RED),
            new Stop(0.25, Color.ORANGE), new Stop(0.50, Color.YELLOW),
            new Stop(0.75, Color.YELLOWGREEN), new Stop(1.0, Color.LIME))
        .build();
    
    super.addGauge("SOC",socGauge,0,0);
    super.addGauge("Range",Can4EveI18n.RR,Can4EveI18n.KM,1,0).setDecimals(1);
    super.addGauge("BatteryCapacity", Can4EveI18n.BATTERY_CAPACITY,Can4EveI18n.AH, 2,0).setDecimals(1);
    super.addGauge("ACPower",Can4EveI18n.AC_POWER,Can4EveI18n.K_WATT,0,1).setDecimals(1);
    super.addGauge("ACVolts",Can4EveI18n.AC_VOLTS,Can4EveI18n.VOLTS,1,1);
    super.addGauge("ACAmps",Can4EveI18n.AC_AMPS,Can4EveI18n.AMPS,2,1).setDecimals(1);;
    super.addGauge("DCPower",Can4EveI18n.DC_POWER,Can4EveI18n.K_WATT,0,2).setDecimals(1);    
    super.addGauge("DCVolts",Can4EveI18n.DC_VOLTS,Can4EveI18n.VOLTS,1,2);
    super.addGauge("DCAmps",Can4EveI18n.DC_AMPS,Can4EveI18n.AMPS,2,2).setDecimals(1);;    
    this.fixColumnSizes(4, 33,33,33);
    this.fixRowSizes(4, 33,33,33);
  }
}

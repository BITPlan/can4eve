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

import com.bitplan.obdii.I18n;

import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import eu.hansolo.LcdGauge;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Gauge.SkinType;
import eu.hansolo.medusa.GaugeBuilder;

/**
 * a pane to show charging
 * 
 * @author wf
 *
 */
public class ChargePane extends ConstrainedGridPane {
  private Gauge SOCGauge;
  private Gauge gaugeRR;
  private Gauge gaugeACAmps;
  private Gauge gaugeACVolts;
  private Gauge gaugeDCAmps;
  private Gauge gaugeDCVolts;

  /**
   * @return the gaugeRR
   */
  public Gauge getGaugeRR() {
    return gaugeRR;
  }

  /**
   * @param gaugeRR the gaugeRR to set
   */
  public void setGaugeRR(Gauge gaugeRR) {
    this.gaugeRR = gaugeRR;
  }

  public Gauge getSOCGauge() {
    return SOCGauge;
  }

  public void setSOCGauge(Gauge sOCGauge) {
    SOCGauge = sOCGauge;
  }

  /**
   * @return the gaugeACAmps
   */
  public Gauge getGaugeACAmps() {
    return gaugeACAmps;
  }

  /**
   * @param gaugeACAmps the gaugeACAmps to set
   */
  public void setGaugeACAmps(Gauge gaugeACAmps) {
    this.gaugeACAmps = gaugeACAmps;
  }

  /**
   * @return the gaugeACVolts
   */
  public Gauge getGaugeACVolts() {
    return gaugeACVolts;
  }

  /**
   * @param gaugeACVolts the gaugeACVolts to set
   */
  public void setGaugeACVolts(Gauge gaugeACVolts) {
    this.gaugeACVolts = gaugeACVolts;
  }

  /**
   * @return the gaugeDCAmps
   */
  public Gauge getGaugeDCAmps() {
    return gaugeDCAmps;
  }

  /**
   * @param gaugeDCAmps the gaugeDCAmps to set
   */
  public void setGaugeDCAmps(Gauge gaugeDCAmps) {
    this.gaugeDCAmps = gaugeDCAmps;
  }

  /**
   * @return the gaugeDCVolts
   */
  public Gauge getGaugeDCVolts() {
    return gaugeDCVolts;
  }

  /**
   * @param gaugeDCVolts the gaugeDCVolts to set
   */
  public void setGaugeDCVolts(Gauge gaugeDCVolts) {
    this.gaugeDCVolts = gaugeDCVolts;
  }

  /**
   * 
   */
  public ChargePane() {
    setSOCGauge(GaugeBuilder.create().skinType(SkinType.BATTERY).title("SOC")
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
        .build());
    
    setGaugeRR(LcdGauge.create(I18n.RR,I18n.KM));
    gaugeACAmps= LcdGauge.create(I18n.AC_AMPS,I18n.AMPS);
    gaugeACVolts= LcdGauge.create(I18n.AC_VOLTS,I18n.VOLTS);
    gaugeDCAmps= LcdGauge.create(I18n.DC_AMPS,I18n.AMPS);
    gaugeDCVolts= LcdGauge.create(I18n.DC_VOLTS,I18n.VOLTS);
    this.add(getSOCGauge(), 0,0);
    this.add(getGaugeRR(),1,0);
    this.add(gaugeACVolts,0,1);
    this.add(gaugeACAmps,1,1);
    this.add(gaugeDCVolts,0,2);
    this.add(gaugeDCAmps,1,2);
    
    this.fixColumnSizes(4, 50,50);
    this.fixRowSizes(4, 33,33,33);
  }
}

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

import eu.hansolo.medusa.FGauge;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Gauge.NeedleSize;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.GaugeDesign;
import eu.hansolo.medusa.GaugeDesign.GaugeBackground;
import eu.hansolo.medusa.LcdDesign;
import eu.hansolo.medusa.LcdFont;
import eu.hansolo.medusa.Section;
import eu.hansolo.medusa.TickLabelLocation;
import eu.hansolo.medusa.TickMarkType;
import javafx.scene.paint.Color;

/**
 * an experimental DashBoard
 * @author wf
 *
 */
public class DashBoardPane extends javafx.scene.layout.GridPane {
  
  private FGauge framedRPMGauge;
  private Gauge rpmGauge;
  private Gauge rpmSpeedGauge;
  public Gauge getRpmSpeedGauge() {
    return rpmSpeedGauge;
  }

  public void setRpmSpeedGauge(Gauge rpmSpeedGauge) {
    this.rpmSpeedGauge = rpmSpeedGauge;
  }

  private FGauge framedRPMSpeedGauge;

  public Gauge getRpmGauge() {
    return rpmGauge;
  }

  public void setRpmGauge(Gauge rpmGauge) {
    this.rpmGauge = rpmGauge;
  }

  public DashBoardPane() {
    rpmGauge = GaugeBuilder.create()
        .minValue(0)
        .maxValue(6000)
        .tickLabelDecimals(0)
        .decimals(0)
        .autoScale(true)
        .animated(true)
        .shadowsEnabled(true)
        .sectionsVisible(true)
        .sections(new Section(5000, 6000, Color.rgb(195, 139, 102, 0.5)))
        .majorTickMarkColor( Color.rgb(241, 161, 71))
        //.minorTickMarkColor(Color.rgb(0, 175, 248))
        .majorTickMarkType(TickMarkType.TRAPEZOID)
        .mediumTickMarkType(TickMarkType.DOT)
        .minorTickMarkType(TickMarkType.LINE)
        .tickLabelLocation(TickLabelLocation.INSIDE)
        .title("RPM")
        .unit("")
        .lcdDesign(LcdDesign.SECTIONS)
        .lcdVisible(true)
        .lcdFont(LcdFont.STANDARD)
        .needleSize(NeedleSize.THICK)
        .build();
    rpmSpeedGauge = GaugeBuilder.create()
        .minValue(0)
        .maxValue(140)
        .tickLabelDecimals(0)
        .decimals(3)
        .autoScale(true)
        .animated(true)
        .shadowsEnabled(true)
        .sectionsVisible(true)
        .sections(new Section(100, 140, Color.rgb(195, 139, 102, 0.5)))
        .majorTickMarkColor( Color.rgb(241, 161, 71))
        //.minorTickMarkColor(Color.rgb(0, 175, 248))
        .majorTickMarkType(TickMarkType.TRAPEZOID)
        .mediumTickMarkType(TickMarkType.DOT)
        .minorTickMarkType(TickMarkType.LINE)
        .tickLabelLocation(TickLabelLocation.INSIDE)
        .title("RPMSpeed")
        .unit("km/h")
        .lcdDesign(LcdDesign.SECTIONS)
        .lcdVisible(true)
        .lcdFont(LcdFont.STANDARD)
        .needleSize(NeedleSize.THICK)
        .build();
    framedRPMGauge = new FGauge(rpmGauge, GaugeDesign.ENZO, GaugeBackground.DARK_GRAY);
    this.add(framedRPMGauge,0,0);
 
    framedRPMSpeedGauge = new FGauge(rpmSpeedGauge, GaugeDesign.ENZO, GaugeBackground.DARK_GRAY);
    this.add(framedRPMSpeedGauge,1,0);
  }
}

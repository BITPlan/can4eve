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
  
  private FGauge framedGauge;
  private Gauge rpmGauge;

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
        .sections(new Section(5000, 6000, Color.rgb(139, 195, 102, 0.5)))
        .areasVisible(true)
        .areas(new Section(0.00, 25, Color.rgb(234, 83, 79, 0.5)))
        .majorTickMarkColor( Color.rgb(241, 161, 71))
        //.minorTickMarkColor(Color.rgb(0, 175, 248))
        .majorTickMarkType(TickMarkType.TRAPEZOID)
        .mediumTickMarkType(TickMarkType.DOT)
        .minorTickMarkType(TickMarkType.LINE)
        .tickLabelLocation(TickLabelLocation.INSIDE)
        .title("RPM")
        //.titleColor(Color.rgb(223, 223, 223))
        .unit("")
        .lcdDesign(LcdDesign.SECTIONS)
        .lcdVisible(true)
        .lcdFont(LcdFont.STANDARD)
        //.unitColor(Color.rgb(223, 223, 223))
        //.valueColor(Color.rgb(223, 223, 223))
        .needleSize(NeedleSize.THICK)
        .build();
    framedGauge = new FGauge(rpmGauge, GaugeDesign.ENZO, GaugeBackground.DARK_GRAY);
    this.add(framedGauge,0,0);
  }
}

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

import com.bitplan.can4eve.Vehicle;
import com.bitplan.obdii.I18n;

import eu.hansolo.LcdGauge;
import eu.hansolo.LcdGauge.ResetableGauge;
import eu.hansolo.medusa.FGauge;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Gauge.NeedleSize;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.GaugeDesign;
import eu.hansolo.medusa.GaugeDesign.GaugeBackground;
import eu.hansolo.medusa.Section;
import eu.hansolo.medusa.TickLabelLocation;
import eu.hansolo.medusa.TickMarkType;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

/**
 * an experimental DashBoard
 * 
 * @author wf
 *
 */
public class DashBoardPane extends ConstrainedGridPane  {

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
  public ResetableGauge rpmSpeedMax;
  public ResetableGauge rpmMax;
  public ResetableGauge rpmAvg;
  public ResetableGauge rpmSpeedAvg;

  public Gauge getRpmGauge() {
    return rpmGauge;
  }

  public void setRpmGauge(Gauge rpmGauge) {
    this.rpmGauge = rpmGauge;
  }

  /**
   * create a DashBoardPane
   * @param maxValue
   */
  public DashBoardPane(Vehicle vehicle) {
    //LcdFont lcdFont=LcdFont.STANDARD;
    //LcdDesign lcdDesign=LcdDesign.SECTIONS;
    
    rpmGauge = GaugeBuilder.create().minValue(0)
        // FIXME use value from Vehicle definition
        .maxValue(vehicle.getMaxRPM()).tickLabelDecimals(0).decimals(0).autoScale(true)
        .animated(true).shadowsEnabled(true).sectionsVisible(true)
        // FIXME use value form Vehicle definition
        .sections(new Section(vehicle.getMaxRPM() * 80 / 100, vehicle.getMaxRPM(),
            Color.rgb(195, 139, 102, 0.5)))
        .majorTickMarkColor(Color.rgb(241, 161, 71))
        // .minorTickMarkColor(Color.rgb(0, 175, 248))
        .majorTickMarkType(TickMarkType.TRAPEZOID)
        .mediumTickMarkType(TickMarkType.DOT)
        .minorTickMarkType(TickMarkType.LINE)
        .tickLabelLocation(TickLabelLocation.INSIDE).title(I18n.get(I18n.REV_COUNT))
        .unit(I18n.get(I18n.RPM)).lcdDesign(LcdGauge.lcdDesign).lcdVisible(true)
        .lcdFont(LcdGauge.lcdFont).needleSize(NeedleSize.THICK).build();
    
    rpmSpeedGauge = GaugeBuilder.create().minValue(0).maxValue(vehicle.getMaxSpeed())
        .tickLabelDecimals(0).decimals(1).autoScale(true).animated(true)
        .shadowsEnabled(true).sectionsVisible(true)
        .sections(new Section(vehicle.getMaxSpeed()/1.4, vehicle.getMaxSpeed(), Color.rgb(195, 139, 102, 0.5)))
        .majorTickMarkColor(Color.rgb(241, 161, 71))
        // .minorTickMarkColor(Color.rgb(0, 175, 248))
        .majorTickMarkType(TickMarkType.TRAPEZOID)
        .mediumTickMarkType(TickMarkType.DOT)
        .minorTickMarkType(TickMarkType.LINE)
        .tickLabelLocation(TickLabelLocation.INSIDE)
        .title(I18n.get(I18n.RPM_SPEED)).unit("km/h")
        .lcdDesign(LcdGauge.lcdDesign).lcdVisible(true)
        .lcdFont(LcdGauge.lcdFont).needleSize(NeedleSize.THICK).build();

    rpmMax = new ResetableGauge(I18n.RPM_MAX,I18n.RPM);
    rpmAvg = new ResetableGauge(I18n.RPM_AVG,I18n.RPM);
    // FIXME translate km/h? or allow miles/hour?
    rpmSpeedMax = new ResetableGauge(I18n.RPM_SPEED_MAX,I18n.KMH);   
    rpmSpeedAvg = new ResetableGauge(I18n.RPM_SPEED_AVG,I18n.KMH);

    framedRPMGauge = new FGauge(rpmGauge, GaugeDesign.ENZO,
        GaugeBackground.DARK_GRAY);
    this.add(framedRPMGauge, 0, 0);
    
    GridPane rpmPane=new GridPane();
    rpmPane.add(rpmMax, 0, 0);
    rpmPane.add(rpmAvg, 1, 0);
    rpmPane.setAlignment(Pos.CENTER);
    this.add(rpmPane, 0, 1);
 
    framedRPMSpeedGauge = new FGauge(rpmSpeedGauge, GaugeDesign.ENZO,
        GaugeBackground.DARK_GRAY);
    this.add(framedRPMSpeedGauge, 1, 0);
    
    GridPane rpmSpeedPane=new GridPane();
    rpmSpeedPane.add(rpmSpeedMax, 0, 0);
    rpmSpeedPane.add(rpmSpeedAvg, 1, 0);
    rpmSpeedPane.setAlignment(Pos.CENTER);
    this.add(rpmSpeedPane, 1, 1);
    
    // 75= 80 - 5% (5% for extra gap)
    fixRowSizes(6,87,13);
    fixColumnSizes(4,50,50);
  }

}

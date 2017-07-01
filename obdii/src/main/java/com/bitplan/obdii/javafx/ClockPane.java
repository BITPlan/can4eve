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

import java.net.URL;

import com.bitplan.can4eve.gui.swing.Translator;
import com.bitplan.can4eve.states.StopWatch;
import com.bitplan.obdii.I18n;
import com.bitplan.obdii.javafx.ClockPane.Watch;

import eu.hansolo.medusa.ClockBuilder;
import eu.hansolo.medusa.LcdDesign;
import eu.hansolo.medusa.Clock;
import eu.hansolo.medusa.Clock.ClockSkinType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

/*
 * Clock display
 */
public class ClockPane extends javafx.scene.layout.GridPane {
  public enum Watch {
    Moving, Charging, Parking, Total
  };

  private Clock clock;
  private JFXStopWatch[] watches;

  /**
   * create the Clock Pane
   */
  public ClockPane() {
    // add a normal running clock
    clock = ClockBuilder.create().skinType(ClockSkinType.LCD)
        .lcdDesign(LcdDesign.GRAY).title(I18n.get(I18n.WATCH_TIME)).titleVisible(true)
        .secondsVisible(true).alarmsEnabled(false).dateVisible(true)
        .running(true).autoNightMode(true).locale(Translator.getCurrentLocale())
        .build();

    String[] icons = { "car", "plug", "parking", "total" };
    String[] titles = { I18n.get(I18n.WATCH_MOVING),
        I18n.get(I18n.WATCH_CHARGING), I18n.get(I18n.WATCH_PARKING),
        I18n.get(I18n.WATCH_TOTAL) };
    watches = new JFXStopWatch[Watch.values().length];
    int index = 0;
    for (Watch watch : Watch.values()) {
      JFXStopWatch stopWatch = new JFXStopWatch(I18n.get("watch" + watch.name()));
      stopWatch.halt();
      stopWatch.reset();
      watches[index] = stopWatch;
      String icon = icons[index];
      URL iconUrl = this.getClass().getResource("/icons/" + icon + ".png");
      if (iconUrl != null) {
        stopWatch.setIcon(new ImageView(iconUrl.toString()));
      }
      if (index < Watch.Total.ordinal()) {
        this.add(stopWatch.getIcon(), 2 * index, 1);
        this.add(stopWatch.get(), 2 * index + 1, 1);
      }
      index++;
    }
    this.add(clock, 1, 0);
    this.add(getWatch(Watch.Total).get(), 3, 0);
  }

  /**
   * get the Watch for the given enum
   * 
   * @param watch
   *          - the Watch enum
   * @return - the StopWatch
   */
  public JFXStopWatch getWatch(Watch watch) {
    JFXStopWatch stopWatch = watches[watch.ordinal()];
    return stopWatch;
  }

  /**
   * set the given watch
   * @param watchType
   * @param mSecs
   */
  public void setWatch(Watch watchType, long mSecs) {
    StopWatch watch=getWatch(watchType);
    watch.setTime(mSecs);
    StopWatch total=getWatch(Watch.Total);
    total.setTime(getWatch(Watch.Parking).getTime()+getWatch(Watch.Moving).getTime()+getWatch(Watch.Charging).getTime());
  }
}

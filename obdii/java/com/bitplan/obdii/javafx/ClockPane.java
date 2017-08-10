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
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bitplan.can4eve.Vehicle.State;
import com.bitplan.can4eve.states.StopWatch;
import com.bitplan.i18n.Translator;
import com.bitplan.obdii.I18n;

import eu.hansolo.medusa.Clock;
import eu.hansolo.medusa.Clock.ClockSkinType;
import eu.hansolo.medusa.ClockBuilder;
import eu.hansolo.medusa.LcdDesign;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

/*
 * Clock display
 */
public class ClockPane extends ConstrainedGridPane {
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.obdii.javafx");
  public enum Watch {
    Moving, Charging, Parking, Total
  };

  private Clock clock;
  private JFXStopWatch[] watches;
  // relative msecs Values
  private long[] msecsStart;
  private Watch currentWatch; 

  /**
   * create the Clock Pane
   */
  public ClockPane() {
    // add a normal running clock
    clock = ClockBuilder.create().skinType(ClockSkinType.LCD)
        .lcdDesign(LcdDesign.GRAY).title(I18n.get(I18n.WATCH_TIME))
        .titleVisible(true).secondsVisible(true).alarmsEnabled(false)
        .dateVisible(true).running(true).autoNightMode(true)
        .locale(Translator.getCurrentLocale()).build();

    String[] icons = { "car", "plug", "parking", "total" };
    String[] titles = { I18n.WATCH_MOVING,
        I18n.WATCH_CHARGING, I18n.WATCH_PARKING,
        I18n.WATCH_TOTAL};
    watches = new JFXStopWatch[Watch.values().length];
    this.msecsStart=new long[Watch.values().length];
    for (Watch watch : Watch.values()) {
      int index = watch.ordinal();
      JFXStopWatch stopWatch = new JFXStopWatch(titles[index]);
      stopWatch.halt();
      stopWatch.reset();
      watches[index] = stopWatch;
      msecsStart[index] =0;
      String icon = icons[index];
      URL iconUrl = this.getClass().getResource("/icons/" + icon + ".png");
      if (iconUrl != null) {
        stopWatch.setIcon(new ImageView(iconUrl.toString()));
      }
      stopWatch.setActive(false);
      if (index < Watch.Total.ordinal()) {
        BorderPane pane = wrapImageView(stopWatch.getIcon());
        this.add(pane, 0, index);
        this.add(stopWatch.get(), 1, index);
      }
      index++;
    }
    this.add(clock, 2, 0);
    this.add(getWatch(Watch.Total).get(), 2, 1);
    fixRowSizes(4, 33, 33, 33);
    fixColumnSizes(4, 20, 40, 40);
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
   * 
   * @param watchType
   * @param mSecs
   */
  public void setWatch(Watch watchType, long mSecs) {
    StopWatch watch = getWatch(watchType);
    for (Watch lwatch : Watch.values()) {
      getWatch(lwatch)
          .setActive(watchType == lwatch || watchType == Watch.Total);
    }
    watch.setTime(mSecs);
    StopWatch total = getWatch(Watch.Total);
    total.setTime(
        (getWatch(Watch.Parking).getTime() + getWatch(Watch.Moving).getTime()
            + getWatch(Watch.Charging).getTime()));
  }

  /**
   * update the milliseconds
   * 
   * @param newValue
   * @param vehicleState
   */
  public void updateMsecs(Number newValue, State vehicleState) {
    Watch lWatch = Watch.Parking;
    if (vehicleState != null) {
      switch (vehicleState) {
      case Parking:
        lWatch = Watch.Parking;
        break;
      case Moving:
        lWatch = Watch.Moving;
        break;
      case Charging:
        lWatch = Watch.Charging;
        break;
      default:
        break;
      }
    }
    // is this a watch switch?
    if (currentWatch==null || lWatch!=currentWatch) {
      JFXStopWatch lStopWatch = this.getWatch(lWatch);
      // stopWatch may have time ts e.g. 10 secs
      // msecs might be 30 secs
      // then  remember stopWatch time - mecs time which would be 20 secs 
      // when this value is substracted the new time will be relative to the current
      // msecs but keep what was on the stopWatch
      // e.g when msecs is at 40 secs the new time will be 20 secs which is the expected 10 secs more
      this.msecsStart[lWatch.ordinal()]=newValue.longValue()-lStopWatch.getTime();
    }
    long mSecsDiff=newValue.longValue()-this.msecsStart[lWatch.ordinal()];
    if (mSecsDiff>=0)
      setWatch(lWatch, mSecsDiff);
    else
      LOGGER.log(Level.WARNING,"Invalid negative mSecsDiff "+mSecsDiff+" for stopWatch "+lWatch.name());
    currentWatch=lWatch;
  }
}

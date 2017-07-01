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

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import com.bitplan.can4eve.gui.swing.Translator;
import com.bitplan.can4eve.states.StopWatch;

import eu.hansolo.medusa.Clock;
import eu.hansolo.medusa.Clock.ClockSkinType;
import eu.hansolo.medusa.ClockBuilder;
import eu.hansolo.medusa.LcdDesign;

/**
 * as Stop Watch
 * 
 * @author wf
 *
 */
public class JFXStopWatch implements StopWatch {
  private Clock stopWatch;
  ImageView icon;
  private boolean active;
  public static double OPACITY_INACTIVE = 0.5; // opacity to show inactivity
  public static double OPACITY_ACTIVE = 1.0; // opacity to show activity

  /**
   * create a StopWatch
   * 
   * @param title
   */
  public JFXStopWatch(String title) {
    stopWatch = ClockBuilder.create().skinType(ClockSkinType.LCD)
        .lcdDesign(LcdDesign.GRAY).title(title).titleVisible(true)
        .secondsVisible(true).alarmsEnabled(true).dateVisible(false)
        .running(true).locale(Translator.getCurrentLocale()).build();
  }

  public ImageView getIcon() {
    return icon;
  }

  public void setIcon(ImageView icon) {
    this.icon = icon;
  }

  /**
   * get the clock
   * 
   * @return
   */
  public Clock get() {
    return stopWatch;
  }

  /**
   * set the time to the given milliSeconds
   * 
   * @param mSecs
   */
  public void setTime(long mSecs) {
    long epochSecond = mSecs / 1000;
    int nanoOfSecond = (int) ((mSecs % 1000) * 1000000);
    ZoneOffset zoneoffset = ZoneOffset.ofHours(0);
    LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(epochSecond,
        nanoOfSecond, zoneoffset);
    ZonedDateTime time = ZonedDateTime.of(localDateTime, zoneoffset);
    Platform.runLater(() -> stopWatch.setTime(time));
  }

  @Override
  public long getTime() {
    ZonedDateTime time = stopWatch.getTime();
    long mSecs = (time.getHour() * 3600 + time.getMinute() * 60
        + time.getSecond() + time.getNano() / 1000000) * 1000;
    return mSecs;
  }

  /**
   * reset the given clock
   */
  public void reset() {
    setTime(0);
  }

  /**
   * halt the stopWatch
   */
  public void halt() {
    stopWatch.setRunning(false);
  }

  @Override
  public void setActive(boolean active) {
    this.active = active;
    if (icon != null)
      if (active) {
        icon.setOpacity(OPACITY_ACTIVE);
      } else {
        icon.setOpacity(OPACITY_INACTIVE);
      }
  }

  @Override
  public boolean isActive() {
    return active;
  }
}

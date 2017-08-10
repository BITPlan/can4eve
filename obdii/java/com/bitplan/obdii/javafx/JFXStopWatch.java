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
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bitplan.can4eve.ErrorHandler;
import com.bitplan.can4eve.states.StopWatch;

import eu.hansolo.LcdGauge;
import eu.hansolo.medusa.Clock;
import javafx.scene.image.ImageView;

/**
 * as Stop Watch
 * 
 * @author wf
 *
 */
public class JFXStopWatch implements StopWatch {
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.obdii.javafx");
  
  private Clock stopWatch;
  ImageView icon;
  private boolean active;
  public static double OPACITY_INACTIVE = 0.5; // opacity to show inactivity
  public static double OPACITY_ACTIVE = 1.0; // opacity to show activity

  /**
   * create a StopWatch
   * 
   * @param i18nTitle
   */
  public JFXStopWatch(String i18nTitle) {
    stopWatch = LcdGauge.createClock(i18nTitle);
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

  public String asIsoDateStr() {
    String isoDateStr = stopWatch.getTime()
        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    return isoDateStr;
  }

  /**
   * set the time to the given milliSeconds
   * 
   * @param mSecs
   */
  public void setTime(long mSecs) {
    try {
      long epochSecond = mSecs / 1000;
      int nanoOfSecond = (int) ((mSecs % 1000) * 1000000);
      ZoneOffset zoneoffset = ZoneOffset.ofHours(0);
      LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(epochSecond,
          nanoOfSecond, zoneoffset);
      ZonedDateTime time = ZonedDateTime.of(localDateTime, zoneoffset);
      stopWatch.setTime(time);
    } catch (java.time.DateTimeException dte) {
      LOGGER.log(Level.WARNING, String.format("setTime with invalidTime %7d msecs",mSecs));
      ErrorHandler.handle(dte);
    }
  }

  @Override
  public long getTime() {
    ZonedDateTime time = stopWatch.getTime();
    long mSecs = ((time.getDayOfMonth() - 1) * 86400 + time.getHour() * 3600
        + time.getMinute() * 60 + time.getSecond()) * 1000
        + time.getNano() / 1000000;
    /*
     * int offset=time.getOffset().getTotalSeconds();
     * System.out.println(" d:"+(time.getDayOfMonth()-1));
     * System.out.println(" h:"+time.getHour());
     * System.out.println(" m:"+time.getMinute());
     * System.out.println(" s:"+time.getSecond());
     * System.out.println(" n:"+time.getNano());
     * System.out.println(" o:"+offset); System.out.println("ms:"+mSecs);
     */
    return mSecs;
  }

  /**
   * reset the given clock
   */
  public void reset() {
    setTime(0l);
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

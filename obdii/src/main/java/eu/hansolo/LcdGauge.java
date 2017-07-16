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
package eu.hansolo;

import java.util.Locale;

import com.bitplan.i18n.Translator;
import com.bitplan.obdii.I18n;

import eu.hansolo.medusa.Clock;
import eu.hansolo.medusa.ClockBuilder;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.LcdDesign;
import eu.hansolo.medusa.LcdFont;
import eu.hansolo.medusa.Clock.ClockSkinType;
import eu.hansolo.medusa.Gauge.SkinType;

/**
 * Helper class for LCD Gauges
 * @author wf
 *
 */
public class LcdGauge {
  
  public static LcdFont lcdFont=LcdFont.DIGITAL;
  public static LcdDesign lcdDesign=LcdDesign.GRAY;

  /**
   * create the given LCD Gauge
   * @param i18nTitle
   * @param i18nUnit
   * @return  the gauge
   */
  public static Gauge createGauge(String i18nTitle, String i18nUnit) {
    Gauge gauge = GaugeBuilder.create().skinType(SkinType.LCD).animated(true)
        .oldValueVisible(false).maxMeasuredValueVisible(false)
        .minMeasuredValueVisible(false).decimals(0).tickLabelDecimals(0)
        .title(I18n.get(i18nTitle)).unit(I18n.get(i18nUnit))
        .lcdDesign(lcdDesign).lcdFont(lcdFont).build();
    return gauge;
  }

  /**
   * create a clock with the given title
   * @param title
   * @return
   */
  public static Clock createClock(String i18nTitle) {
    String title=I18n.get(i18nTitle);
    Locale locale = Translator.getCurrentLocale();
    if (locale==null)
      locale=Locale.getDefault();
    Clock clock = ClockBuilder.create().skinType(ClockSkinType.LCD)
    .lcdDesign(lcdDesign).title(title).titleVisible(true)
    .secondsVisible(true).alarmsEnabled(true).dateVisible(false)
    .locale(locale).build();
    return clock;
  }

}

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

import java.net.URL;
import java.util.Locale;

import com.bitplan.i18n.I18n;
import com.bitplan.i18n.Translator;
import com.bitplan.javafx.ImageButton;

import eu.hansolo.medusa.Clock;
import eu.hansolo.medusa.Clock.ClockSkinType;
import eu.hansolo.medusa.ClockBuilder;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Gauge.SkinType;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.LcdDesign;
import eu.hansolo.medusa.LcdFont;
import eu.hansolo.medusa.skins.LcdSkin;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.Pane;

/**
 * Helper class for LCD Gauges
 * 
 * @author wf
 *
 */
@SuppressWarnings("restriction")
public class LcdGauge {

  public static LcdFont lcdFont = LcdFont.DIGITAL;
  public static LcdDesign lcdDesign = LcdDesign.GRAY;

  /**
   * create the given LCD Gauge
   * 
   * @param i18nTitle
   * @param i18nUnit
   * @return the gauge
   */
  public static Gauge createGauge(String i18nTitle, String i18nUnit,
      boolean resetable) {
    return createGaugeLocalized(I18n.get(i18nTitle), I18n.get(i18nUnit),
        resetable);
  }

  /**
   * create a gauge with the given title and unit
   * 
   * @param i18nTitle
   * @param i18nUnit
   * @return the Gauge
   */
  public static Gauge createGauge(String i18nTitle, String i18nUnit) {
    return createGauge(i18nTitle, i18nUnit, false);
  }

  /**
   * 
   * @param title
   * @param unit
   * @return
   */
  public static Gauge createGaugeLocalized(String title, String unit,
      boolean resetAble) {
    Gauge gauge = null;
    if (resetAble)
      gauge = new ResetableGauge(title, unit);
    else
      gauge = GaugeBuilder.create().skinType(SkinType.LCD)
          .oldValueVisible(false).maxMeasuredValueVisible(false)
          .minMeasuredValueVisible(false).decimals(0).tickLabelDecimals(0)
          .title(title).unit(unit).lcdDesign(lcdDesign).lcdFont(lcdFont)
          .build();
    return gauge;
  }

  /**
   * a resetable Gauge Skin
   * 
   * @author wf
   *
   */
  public static class ResetableGaugeSkin extends LcdSkin {

    private Pane pane;
    // private StackPane stackpane;

    /**
     * construct me for the given gauge
     * 
     * @param rgauge
     */
    public ResetableGaugeSkin(ResetableGauge rgauge) {
      super(rgauge);
      pane = (Pane) this.getChildren().get(0);
      ImageButton rb = rgauge.getResetButton();
      pane.getChildren().add(rb);
      rb.translateXProperty().set(5);
      rb.translateYProperty().set(0);
      rb.imageHeightProperty().bind(pane.heightProperty().multiply(0.225));
      rb.imageWidthProperty().bind(pane.widthProperty().multiply(0.15));
    }

  }

  /**
   * a Gauge with a ResetButton
   * 
   * @author wf
   *
   */
  public static class ResetableGauge extends Gauge {

    private ImageButton resetButton;
    private ResetableGaugeSkin skin;

    public ImageButton getResetButton() {
      return resetButton;
    }

    public void setResetButton(ImageButton resetButton) {
      this.resetButton = resetButton;
    }

    /**
     * construct me
     * 
     * @param title
     * @param unit
     */
    public ResetableGauge(String title, String unit) {
      super(SkinType.LCD);
      super.oldValueVisibleProperty().set(false);
      super.maxMeasuredValueVisibleProperty().set(false);
      super.minMeasuredValueVisibleProperty().set(false);
      super.tickLabelDecimalsProperty().set(0);

      super.decimalsProperty().set(0);
      super.titleProperty().set(title);
      super.unitProperty().set(unit);
      super.lcdDesignProperty().set(LcdGauge.lcdDesign);
      super.lcdFontProperty().set(LcdGauge.lcdFont);
      initResetButton();
      skin = new ResetableGaugeSkin(this);
      this.setSkin(skin);
    }

    /**
     * initialize my reset button
     */
    private void initResetButton() {
      setResetButton(new ImageButton("resetdown.png", "resetup.png"));
      getResetButton().setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent e) {
          DoubleProperty vp = ResetableGauge.this.valueProperty();
          if (vp.isBound()) {
            // a bound value can not be set
            
          } else {
            setValue(0);
          }
        }
      });
    }

    @Override
    public String getUserAgentStylesheet() {
      URL u = getClass().getResource("gauge.css");
      if (u != null)
        return u.toExternalForm();
      else
        return null;
    }

  }

  /**
   * create a clock with the given title
   * 
   * @param title
   * @return
   */
  public static Clock createClock(String i18nTitle) {
    String title = I18n.get(i18nTitle);
    Locale locale = Translator.getCurrentLocale();
    if (locale == null)
      locale = Locale.getDefault();
    Clock clock = ClockBuilder.create().skinType(ClockSkinType.LCD)
        .lcdDesign(lcdDesign).title(title).titleVisible(true)
        .secondsVisible(true).alarmsEnabled(true).dateVisible(false)
        .locale(locale).build();
    return clock;
  }

}

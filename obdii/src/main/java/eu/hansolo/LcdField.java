/*
 * Copyright (c) 2015-2017 by Gerrit Grunwald
 * Copyright (c) 2017 BITPlan GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.hansolo;

import eu.hansolo.medusa.Fonts;
import eu.hansolo.medusa.LcdDesign;
import eu.hansolo.medusa.LcdFont;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

/**
 * LCDField as a Control
 *
 */
public class LcdField extends Control {

  /**
   * LCDField Skin see
   * https://wiki.openjdk.java.net/display/OpenJFX/UI+Controls+Architecture
   * 
   * @author wf
   *
   */
  public class LCDFieldSkin extends SkinBase<LcdField>
      implements Skin<LcdField> {

    /**
     * construct me
     * 
     * @param lcdField
     */
    public LCDFieldSkin(LcdField lcdField) {
      super(lcdField);
      getChildren().add(lcdField.getLcd());
      getChildren().add(lcdField.getLcdText());
    }

    @Override
    public void layoutChildren(double contentX, double contentY,
        double contentWidth, double contentHeight) {
      super.layoutChildren(contentX, contentY, contentWidth, contentHeight);
      LcdField lcdField = this.getSkinnable();
      Label lcdText = lcdField.getLcdText();
      // lcdText.setTranslateX(-lcdField.getLcd().getWidth());
      lcdText.setTranslateX(-contentWidth / 2);
    }

  }

  private Rectangle lcd;
  private Label lcdText;

  public Rectangle getLcd() {
    return lcd;
  }

  public void setLcd(Rectangle lcd) {
    this.lcd = lcd;
  }

  public Label getLcdText() {
    return lcdText;
  }

  public void setLcdText(Label lcdText) {
    this.lcdText = lcdText;
  }

  /**
   * construct me from a width and height
   * 
   * @param width
   * @param height
   * @param lcdFont 
   * @param visible
   */
  public LcdField(String text, double width, double height,
      LcdDesign lcdDesign, LcdFont lcdFont) {
    lcd = createRect(width, height, lcdDesign);
    // lcd.relocate((preferredWidth - lcd.getWidth()) * 0.5, 0.44 *
    // preferredHeight);
    lcdText = createLabel(width, height, text, lcdDesign);
    this.setFont(width, height, lcdFont, true);
  }

  /**
   * create the label for the given text and LCD Design
   * 
   * @param text
   * @param lcdDesign
   * @return the label
   */
  private Label createLabel(double width, double height, String text,
      LcdDesign lcdDesign) {
    Label label = new Label(text);
    Color[] lcdColors = lcdDesign.getColors();
    label.setAlignment(Pos.CENTER_RIGHT);
    label.setTextFill(lcdColors[5]);
    label.setPadding(new Insets(0, 0.05 * width, 0, 0.05 * width));
    return label;
  }

  /**
   * create a styled rectangle
   * 
   * @param width
   * @param height
   * @param lcdDesign
   * @return - the rectangle
   */
  public Rectangle createRect(double width, double height,
      LcdDesign lcdDesign) {
    Rectangle rect = new Rectangle(width, height);
    Color[] lcdColors = lcdDesign.getColors();
    LinearGradient lcdGradient = new LinearGradient(0, 1, 0, height - 1, false,
        CycleMethod.NO_CYCLE, new Stop(0, lcdColors[0]),
        new Stop(0.03, lcdColors[1]), new Stop(0.5, lcdColors[2]),
        new Stop(0.5, lcdColors[3]), new Stop(1.0, lcdColors[4]));
    Paint lcdFramePaint;
    if (lcdDesign.name().startsWith("FLAT")) {
      lcdFramePaint = Color.WHITE;
    } else {
      lcdFramePaint = new LinearGradient(0, 0, 0, height, false,
          CycleMethod.NO_CYCLE, new Stop(0.0, Color.rgb(26, 26, 26)),
          new Stop(0.01, Color.rgb(77, 77, 77)),
          new Stop(0.99, Color.rgb(77, 77, 77)),
          new Stop(1.0, Color.rgb(221, 221, 221)));
    }
    // rect.setFill(Color.TRANSPARENT);
    rect.setFill(lcdGradient);
    rect.setStroke(lcdFramePaint);
    rect.setArcWidth(0.0125 * height);
    rect.setArcHeight(0.0125 * height);
    return rect;
  }

  /**
   * resize the LcdField
   * 
   * @param width
   * @param height
   * @param lcdFont
   * @param visible
   */
  public void setFont(double width, double height, LcdFont lcdFont,
      boolean visible) {
    if (visible) {
      switch (lcdFont) {
      case LCD:
        lcdText.setFont(Fonts.digital(height));
        break;
      case DIGITAL:
        lcdText.setFont(Fonts.digitalReadout(height));
        break;
      case DIGITAL_BOLD:
        lcdText.setFont(Fonts.digitalReadoutBold(height));
        // lcdText.setTranslateY(0.44 * height);
        break;
      case ELEKTRA:
        lcdText.setFont(Fonts.elektra(height));
        break;
      case STANDARD:
      default:
        lcdText.setFont(Fonts.robotoMedium(height));
        // lcdText.setFont(Fonts.robotoRegular(height));
        break;
      }
      lcdText.setAlignment(Pos.CENTER_RIGHT);
      lcdText.setPrefSize(width, height);
      // lcdText.setTranslateX((width - lcdText.getPrefWidth()) * 0.5);

    } else {
      lcdText.setAlignment(Pos.CENTER);
      lcdText.setFont(Fonts.robotoMedium(height));
    }

  }

  @Override
  protected Skin<LcdField> createDefaultSkin() {
    return new LCDFieldSkin(this);
  }

}

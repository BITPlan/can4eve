/*
 * Copyright (c) 2015-2017 by Gerrit Grunwald
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

import java.util.Locale;

import eu.hansolo.medusa.Fonts;
import eu.hansolo.medusa.LcdDesign;
import eu.hansolo.medusa.LcdFont;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

/**
 * LCDField as a reusable Skinelement e.g. as used in AmpSkin
 *
 */
public class LcdField extends Control {
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
   * @param preferredWidth
   * @param preferredHeight
   * @param visible
   */
  public LcdField(String text,
      double preferredWidth, double preferredHeight, LcdDesign lcdDesign) {
    lcd=createRect(preferredWidth,preferredHeight,lcdDesign);
    //lcd.relocate((preferredWidth - lcd.getWidth()) * 0.5, 0.44 * preferredHeight);
    lcdText=createLabel(preferredWidth,preferredHeight,text,lcdDesign);
  }
  
  /**
   * create the label for the given text and LCD Design
   * @param text
   * @param lcdDesign
   * @return the label
   */
  private Label createLabel(double width, double height,String text, LcdDesign lcdDesign) {
    Label label=new Label(text);
    Color[] lcdColors = lcdDesign.getColors();
    label.setAlignment(Pos.CENTER_RIGHT);
    label.setTextFill(lcdColors[5]);
    label.setPadding(new Insets(0, 0.05 * width, 0, 0.05 * width));
    return label;
  }

  /**
   * create a styled rectangle
   * @param width
   * @param height
   * @param lcdDesign
   * @return - the rectangle
   */
  public Rectangle createRect(double width, double height, LcdDesign lcdDesign) {
    Rectangle rect=new Rectangle(width,height);
    Color[] lcdColors = lcdDesign.getColors();
    LinearGradient lcdGradient = new LinearGradient(0, 1, 0,
        height - 1, false, CycleMethod.NO_CYCLE,
        new Stop(0, lcdColors[0]), new Stop(0.03, lcdColors[1]),
        new Stop(0.5, lcdColors[2]), new Stop(0.5, lcdColors[3]),
        new Stop(1.0, lcdColors[4]));
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
  public void setFont(double width, double height, LcdFont lcdFont, boolean visible) {
    if (visible) {
      switch (lcdFont) {
      case LCD:
        // was 0.108
        lcdText.setFont(Fonts.digital(0.108*height));
        lcdText.setTranslateY(0.45 * height);
        break;
      case DIGITAL:
        lcdText.setFont(Fonts.digitalReadout(0.105 * height));
        lcdText.setTranslateY(0.44 * height);
        break;
      case DIGITAL_BOLD:
        lcdText.setFont(Fonts.digitalReadoutBold(0.105 * height));
        lcdText.setTranslateY(0.44 * height);
        break;
      case ELEKTRA:
        lcdText.setFont(Fonts.elektra(0.1116 * height));
        lcdText.setTranslateY(0.435 * height);
        break;
      case STANDARD:
      default:
        lcdText.setFont(Fonts.robotoMedium(0.09 * height));
        lcdText.setTranslateY(0.43 * height);
        break;
      }
      lcdText.setAlignment(Pos.CENTER_RIGHT);
      // 0.3, 0.014
      lcdText.setPrefSize(width, height);
      //lcdText.setTranslateX((width - lcdText.getPrefWidth()) * 0.5);

    } else {
      lcdText.setAlignment(Pos.CENTER);
      lcdText.setFont(Fonts.robotoMedium(height * 0.1));
    }

  }

}

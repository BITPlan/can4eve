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

    public boolean debug = false;

    /**
     * construct me
     * 
     * @param lcdField
     */
    public LCDFieldSkin(LcdField lcdField) {
      super(lcdField);
      getChildren().add(lcdField.getLcd());
      getChildren().add(lcdField.getLcdLabel());
      getChildren().add(lcdField.getLcdText());
    }

    public void debug(String title, double x, double y, double w, double h) {
      System.out.println(
          String.format("%10s: %.0f,%.0f %.0fx%.0f", title, x, y, w, h));
    }

    public void debug(String title, Control r) {
      debug(title, r.getLayoutX(), r.getLayoutY(), r.getWidth(), r.getHeight());
    }

    public void debug(String title, Rectangle r) {
      debug(title, r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    @Override
    public void layoutChildren(double contentX, double contentY,
        double contentWidth, double contentHeight) {
      LcdField lcdField = this.getSkinnable();
      Label lcdText = lcdField.getLcdText();
      Label lcdLabel = lcdField.getLcdLabel();
      // lcdField.labelResize(lcdText,contentWidth, contentHeight);
      if (debug) {
        debug("content", contentX, contentY, contentWidth, contentHeight);
        // lcdText.setTranslateX(-lcdField.getLcd().getWidth()/2);
        debug("lcd", lcdField.getLcd());
        debug("lcdLabel", lcdLabel);
        debug("lcdText", lcdText);
      }
      lcdLabel.setTranslateX(0);
      super.layoutChildren(contentX, contentY, contentWidth, contentHeight);
      if (debug) {
        debug("lcd", lcdField.getLcd());
        debug("lcdLabel", lcdLabel);
        debug("lcdText", lcdText);
      }
    }

  }

  private Rectangle lcd;
  private Label lcdText;
  private Label lcdLabel;

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

  public Label getLcdLabel() {
    return lcdLabel;
  }

  public void setLcdLabel(Label lcdLabel) {
    this.lcdLabel = lcdLabel;
  }

  /**
   * construct me from a width and height
   * 
   * @param width
   * @param height
   * @param lcdFont
   * @param visible
   */
  public LcdField(String label,String text, double width, double height, LcdDesign lcdDesign,
      LcdFont lcdFont) {
    lcd = createRect(width, height, lcdDesign);
    lcdText = createLabel(width, height, text, lcdDesign,Pos.CENTER_RIGHT);
    lcdLabel = createLabel(width,height,label,lcdDesign,Pos.BOTTOM_LEFT);
    this.setFont(height, lcdFont, true);
  }

  /**
   * create the label for the given text and LCD Design
   * 
   * @param text
   * @param lcdDesign
   * @param pos 
   * @return the label
   */
  private Label createLabel(double width, double height, String text,
      LcdDesign lcdDesign, Pos pos) {
    Label label = new Label(text);
    Color[] lcdColors = lcdDesign.getColors();
    label.setAlignment(pos);
    label.setTextFill(lcdColors[5]);
    labelResize(label, width, height);
    return label;
  }

  /**
   * resize the label setting the insets accordingly
   * @param label
   * @param width
   * @param height
   */
  private void labelResize(Label label, double width, double height) {
    label.setPadding(new Insets(0, 0.05 * width, 0, 0.05 * width));
    label.setPrefSize(width, height);
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
   * @param height
   * @param lcdFont
   * @param visible
   */
  public void setFont(double height, LcdFont lcdFont,
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
        break;
      case ELEKTRA:
        lcdText.setFont(Fonts.elektra(height));
        break;
      case STANDARD:
      default:
        lcdText.setFont(Fonts.robotoMedium(height));
        break;
      }
    } else {
      lcdText.setFont(Fonts.robotoMedium(height));
    }
  }

  @Override
  protected Skin<LcdField> createDefaultSkin() {
    return new LCDFieldSkin(this);
  }

}

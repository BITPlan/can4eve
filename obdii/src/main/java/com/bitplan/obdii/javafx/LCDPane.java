package com.bitplan.obdii.javafx;

import java.util.ArrayList;
import java.util.List;

import eu.hansolo.LcdField;
import eu.hansolo.medusa.LcdDesign;
import eu.hansolo.medusa.LcdFont;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;

/**
 * a Grid of LCD Fields
 * 
 * @author wf
 *
 */
public class LCDPane extends ConstrainedGridPane {
  List<LcdField> lcdFields = new ArrayList<LcdField>();
  private int rows;
  private int columns;

  /**
   * LCDControl Skin see
   * http://slothsoft.de/de/content/eigene-controls-mit-javafx-erstellen-tacho
   * https://gist.github.com/jewelsea/5115901
   * 
   * @author wf
   *
   */
  public class LCDControlSkin extends SkinBase<LcdField> implements Skin<LcdField> {
    
    /**
     * construct me
     * @param lcdField
     */
    public LCDControlSkin(LcdField lcdField) {
      super(lcdField);
      getChildren().add(lcdField.getLcd());
      getChildren().add(lcdField.getLcdText());
    }
    
    @Override
    public void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
      super.layoutChildren(contentX, contentY, contentWidth, contentHeight);
      LcdField lcdField = this.getSkinnable();
      lcdField.getLcdText().setTranslateX(-contentWidth);
    }
    
  }

  /**
   * create a Pane with the given number of rows and Columns
   * 
   * @param rows
   * @param colums
   */
  public LCDPane(int rows, int columns, double width, double height,
      LcdDesign lcdDesign,String... texts) {
    this.rows = rows;
    this.columns = columns;
    int index = 0;
    for (int row = 0; row < this.rows; row++) {
      for (int column = 0; column < columns; column++) {
        LcdFont lcdFont=LcdFont.values()[column];
        LcdField newField = new LcdField(texts[index], width, height,lcdDesign);
        newField.setSkin(new LCDControlSkin(newField));
        newField.setFont(width,height,lcdFont,true);
        index++;
        this.add(newField, column, row);
        lcdFields.add(newField);
      }
    }
    int colWidths[]=new int[columns];
    for (int column = 0; column < columns; column++) {
      colWidths[column]=100/columns;
    }
    int hGap=4;
    super.fixColumnSizes(hGap, colWidths);
    int rowHeights[]=new int[rows];
    for (int row=0;row<rows;row++) {
      rowHeights[row]=100/rows;
    }
    int vGap=4;
    super.fixRowSizes(vGap, rowHeights);
  }
  
  /**
   * get the LcdField at the given row and column
   * @param row
   * @param col
   * @return the lcdField
   */
  public LcdField getAt(int row, int col) {
    // FIXME check parameters for valid bounds
    int index=(row-1)*columns+col-1;
    LcdField lcdField=lcdFields.get(index);
    return lcdField;
  }
}

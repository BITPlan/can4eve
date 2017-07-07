package com.bitplan.obdii.javafx;

import java.util.ArrayList;
import java.util.List;

import eu.hansolo.LcdField;
import eu.hansolo.medusa.LcdDesign;
import eu.hansolo.medusa.LcdFont;

/**
 * a Grid of LCD Fields
 * 
 * @author wf
 *
 */
public class LCDPane extends ConstrainedGridPane {
  List<LcdField> lcdFields = new ArrayList<LcdField>();
  private int rows;
  private int cols;

 
  /**
   * create a Pane with the given number of rows and Columns
   * 
   * @param rows
   * @param colums
   */
  public LCDPane(int rows, int cols, double width, double height,LcdFont lcdFont,String label,String... texts) {
    this.rows = rows;
    this.cols = cols;
    int index = 0;
    for (int row = 0; row < this.rows; row++) {
      for (int col = 0; col < this.cols; col++) {
        LcdField newField = new LcdField(label,texts[index], width, height,LcdDesign.SECTIONS,lcdFont);
        index++;
        this.add(newField, col, row);
        lcdFields.add(newField);
      }
    }
    int colWidths[]=new int[cols];
    for (int column = 0; column < cols; column++) {
      colWidths[column]=100/cols;
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
    int index=(row-1)*cols+col-1;
    LcdField lcdField=lcdFields.get(index);
    return lcdField;
  }
}

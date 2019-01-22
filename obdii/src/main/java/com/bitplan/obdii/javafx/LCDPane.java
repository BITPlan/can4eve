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

import java.util.ArrayList;
import java.util.List;

import com.bitplan.javafx.ConstrainedGridPane;

import eu.hansolo.LcdGauge;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.LcdDesign;

/**
 * a Grid of LCD Fields
 * 
 * @author wf
 *
 */
public class LCDPane extends ConstrainedGridPane {
  List<Gauge> lcdFields = new ArrayList<Gauge>();
  private int rows;
  private int cols;

 
  /**
   * create a Pane with the given number of rows and Columns
   * 
   * @param rows
   * @param cols
   * @param texts
   */
  public LCDPane(int rows, int cols,String... texts) {
    this.rows = rows;
    this.cols = cols;
    int index = 0;
    for (int row = 0; row < this.rows; row++) {
      for (int col = 0; col < this.cols; col++) {
        Gauge newField = LcdGauge.createGaugeLocalized(texts[index],"",false);
        newField.setLcdDesign(LcdDesign.SECTIONS);
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
  public Gauge getAt(int row, int col) {
    if (row<0 || row>=rows || col<0 || col>=cols)
      return null;
    int index=row*cols+col;
    
    Gauge lcdField=lcdFields.get(index);
    return lcdField;
  }
}

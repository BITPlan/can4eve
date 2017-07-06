package com.bitplan.obdii.javafx;

import java.util.ArrayList;
import java.util.List;

import eu.hansolo.LcdField;
import eu.hansolo.medusa.LcdFont;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Skin;

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
   * 
   * @author wf
   *
   */
  public class LCDControlSkin implements Skin<LcdField> {
    private Group rootNode;
    private final LcdField lcdField;

    public LCDControlSkin(LcdField lcdField) {
      this.lcdField = lcdField;
    }

    @Override
    public LcdField getSkinnable() {
      return lcdField;
    }

    @Override
    public Node getNode() {
      if (this.rootNode == null) {
        this.rootNode = new Group();
        redraw();
      }
      return this.rootNode;
    }
    
    public void redraw() {
      List<Node> rootChildren = new ArrayList<Node>();
      rootChildren.add(lcdField.getLcd());
      rootChildren.add(lcdField.getLcdText());
      this.rootNode.getChildren().setAll(rootChildren);
    }

    @Override
    public void dispose() {
      // nothing to do
    }
  }

  /**
   * create a Pane with the given number of rows and Columns
   * 
   * @param rows
   * @param colums
   */
  public LCDPane(int rows, int columns, double width, double height,
      LcdFont lcdFont,String... texts) {
    this.rows = rows;
    this.columns = columns;
    int index = 0;
    for (int row = 0; row < rows; row++) {
      for (int column = 0; column < columns; column++) {
        boolean visible = true;
        LcdField newField = new LcdField(texts[index], width, height, visible);
        newField.setSkin(new LCDControlSkin(newField));
        newField.resize(width, height,lcdFont,visible);
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

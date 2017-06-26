package com.bitplan.can4eve.gui.javafx;

import java.util.Map;

import com.bitplan.can4eve.gui.Form;

import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * a generic Panel
 * @author wf
 *
 */
public class GenericPanel extends GridPane {
  protected Form form;

  /**
   * construct me from the given form description
   * @param form
   */
  public GenericPanel(Form form){
    this.form=form;
    int ypos=0;
    Map<String, TextField> textFields = GenericDialog.getFields(this,form,ypos);
  }
}

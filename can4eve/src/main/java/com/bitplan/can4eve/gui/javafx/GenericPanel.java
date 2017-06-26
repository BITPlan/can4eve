package com.bitplan.can4eve.gui.javafx;

import java.util.Map;

import com.bitplan.can4eve.gui.Form;

import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * a generic Panel
 * @author wf
 *
 */
public class GenericPanel extends GridPane {
  protected Form form;
  public Map<String, TextField> textFields;

  /**
   * construct me from the given form description
   * @param form
   */
  public GenericPanel(Form form){
    this.form=form;
    setHgap(10);
    setVgap(10);
    setPadding(new Insets(20, 150, 10, 10));
    int ypos=0;
    textFields = GenericDialog.getFields(this,form,ypos);
    for (TextField textField:textFields.values()) {
      textField.setEditable(false);
    }
  }
}

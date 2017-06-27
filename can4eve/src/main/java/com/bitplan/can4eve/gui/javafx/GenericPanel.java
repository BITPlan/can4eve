package com.bitplan.can4eve.gui.javafx;

import java.util.Map;

import com.bitplan.can4eve.gui.Form;

import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * a generic Panel
 * 
 * @author wf
 *
 */
public class GenericPanel extends GridPane {
  protected Form form;
  public Map<String, Control> controls;

  /**
   * construct me from the given form description
   * 
   * @param form
   */
  public GenericPanel(Form form) {
    this.form = form;
    setHgap(10);
    setVgap(10);
    setPadding(new Insets(20, 150, 10, 10));
    int ypos = 0;
    controls = GenericDialog.getFields(this, form, ypos);
    for (Control control : controls.values()) {
      if (control instanceof TextField)
        ((TextField) control).setEditable(false);
    }
  }
}

package com.bitplan.can4eve.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * a group of forms
 * @author wf
 *
 */
public class Group {
  String id;
  String name;
  private List<Form> forms = new ArrayList<Form>();
  private Map<String,Form> formById=new HashMap<String,Form>();
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Form> getForms() {
    return forms;
  }

  public void setForms(List<Form> forms) {
    this.forms = forms;
  }
  /**
   * get the form by the given id
   * @param id
   * @return the form by id
   */
  public Form getFormById(String id) {
    Form form=this.formById.get(id);
    return form;
  }
  
  /**
   * reinitializatin of data structures
   */
  public void reinit() {
    for (Form form:this.getForms()) {
      form.reinit();
      if (form.getId()!=null)
        this.formById.put(form.getId(), form);
    }
  }
}

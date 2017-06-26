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
package com.bitplan.can4eve.gui;

import java.util.ArrayList;
import java.util.List;

/**
 * generic multi platform Form description
 * @author wf
 *
 */
public class Form {
  String id;
  String icon;
  String title;
  String headerText;
  private List<Field> fields=new ArrayList<Field>();
  
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getIcon() {
    return icon;
  }
  public void setIcon(String icon) {
    this.icon = icon;
  }
  public String getTitle() {
    return title;
  }
  public void setTitle(String title) {
    this.title = title;
  }

  public String getHeaderText() {
    return headerText;
  }
  public void setHeaderText(String headerText) {
    this.headerText = headerText;
  }
 
  public List<Field> getFields() {
    return fields;
  }
  public void setFields(List<Field> fields) {
    this.fields = fields;
  }
  
  public void reinit() {
    for (Field field:this.getFields()) {
      field.reinit();
    }
    
  }
}

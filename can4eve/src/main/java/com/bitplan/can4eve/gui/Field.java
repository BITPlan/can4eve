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

/**
 * generic multiple platform display field description
 * @author wf
 *
 */
public class Field {
  String id;
  String label;
  String hint;
  String title;
  String fieldKind;
  String type;
  String choices;
  String format;
  int labelSize;
  int fieldSize;
  Integer gridX;
  Integer gridY;
  public String getTitle() {
    return title;
  }
  public void setTitle(String title) {
    this.title = title;
  }
  public String getFieldKind() {
    return fieldKind;
  }
  public void setFieldKind(String fieldKind) {
    this.fieldKind = fieldKind;
  }
  public String getType() {
    return type;
  }
  public void setType(String type) {
    this.type = type;
  }
  public String getChoices() {
    return choices;
  }
  public void setChoices(String choices) {
    this.choices = choices;
  }
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getLabel() {
    return label;
  }
  public void setLabel(String label) {
    this.label = label;
  }
  public String getHint() {
    return hint;
  }
  public void setHint(String hint) {
    this.hint = hint;
  }
  public int getLabelSize() {
    return labelSize;
  }
  public void setLabelSize(int labelSize) {
    this.labelSize = labelSize;
  }
  public Integer getFieldSize() {
    return fieldSize;
  }
  public void setFieldSize(int fieldSize) {
    this.fieldSize = fieldSize;
  }
  /**
   * @return the gridX
   */
  public Integer getGridX() {
    return gridX;
  }
  /**
   * @param gridX the gridX to set
   */
  public void setGridX(Integer gridX) {
    this.gridX = gridX;
  }
  /**
   * @return the gridY
   */
  public Integer getGridY() {
    return gridY;
  }
  /**
   * @param gridY the gridY to set
   */
  public void setGridY(Integer gridY) {
    this.gridY = gridY;
  }
  public String getFormat() {
    return format;
  }
  public void setFormat(String format) {
    this.format = format;
  }
  public void reinit() {
    if (this.id==null) {
      this.id=title;
    }
    if (this.label==null) {
      this.label=title;
    }
    
  }
}

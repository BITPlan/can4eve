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
package com.bitplan.can4eve;

import java.util.ArrayList;
import java.util.List;

/**
 * a unit of information on the CAN bus
 * @author wf
 *
 */
public class CANInfo {
  String name;
  String title; // i18n title
  String description;
  String format;
  String unit; // i18n unit
  String type;
  String trueSymbol;  // symbol to display for true
  String falseSymbol; // symbol to display for false
  Double minValue;
  Double maxValue;

  int historyValuesPerMinute;
  int maxIndex;
  
  transient List<Pid> pids=new ArrayList<Pid>();
  
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getTitle() {
    return title;
  }
  public void setTitle(String title) {
    this.title = title;
  }
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }
  public List<Pid> getPids() {
    return pids;
  }
  public void setPids(List<Pid> pids) {
    this.pids = pids;
  }
  public String getFormat() {
    return format;
  }
  public void setFormat(String format) {
    this.format = format;
  }
  public String getUnit() {
    return unit;
  }
  public void setUnit(String unit) {
    this.unit = unit;
  }
  public String getType() {
    return type;
  }
  public void setType(String type) {
    this.type = type;
  }
  public String getTrueSymbol() {
    return trueSymbol;
  }
  public void setTrueSymbol(String trueSymbol) {
    this.trueSymbol = trueSymbol;
  }
  public String getFalseSymbol() {
    return falseSymbol;
  }
  public void setFalseSymbol(String falseSymbol) {
    this.falseSymbol = falseSymbol;
  }
  public Double getMinValue() {
    return minValue;
  }
  public void setMinValue(Double minValue) {
    this.minValue = minValue;
  }
  public Double getMaxValue() {
    return maxValue;
  }
  public void setMaxValue(Double maxValue) {
    this.maxValue = maxValue;
  }
  public int getHistoryValuesPerMinute() {
    return historyValuesPerMinute;
  }
  public void setHistoryValuesPerMinute(int historyValuesPerMinute) {
    this.historyValuesPerMinute = historyValuesPerMinute;
  }
  public int getMaxIndex() {
    return maxIndex;
  }
  public void setMaxIndex(int maxIndex) {
    this.maxIndex = maxIndex;
  }
 
}

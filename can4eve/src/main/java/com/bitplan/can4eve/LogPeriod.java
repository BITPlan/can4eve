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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.bitplan.json.JsonAble;

/**
 * trip description
 * @author wf
 *
 */
public class LogPeriod implements JsonAble {
  Date startDate;
  Date endDate;
  String VIN; 
  Double odo; // odoMeter at start
  private Map<String,Object> values=new HashMap<String,Object>();
  String logFile;

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public String getVIN() {
    return VIN;
  }

  public void setVIN(String vIN) {
    VIN = vIN;
  }

  public Double getOdo() {
    return odo;
  }

  public void setOdo(Double odo) {
    this.odo = odo;
  }

  public String getLogFile() {
    return logFile;
  }

  public void setLogFile(String logFile) {
    this.logFile = logFile;
  }

  public Map<String,Object> getValues() {
    return values;
  }

  public void setValues(Map<String,Object> values) {
    this.values = values;
  }

  @Override
  public void reinit() {
  }

  @Override
  public void fromMap(Map<String, Object> map) {
  }

}

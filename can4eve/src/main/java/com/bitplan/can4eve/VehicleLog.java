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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.bitplan.error.ErrorHandler;
import com.bitplan.json.JsonAble;
import com.bitplan.json.JsonManager;
import com.bitplan.json.JsonManagerImpl;

/**
 * the log of charge and driving activities of a Vehicle
 * 
 * @author wf
 *
 */
public class VehicleLog implements JsonAble {
  String VIN; // the VIN of the vehicle this log is for
  List<LogPeriod> logPeriods = new ArrayList<LogPeriod>();

  public String getVIN() {
    return VIN;
  }

  public void setVIN(String vIN) {
    VIN = vIN;
  }

  public List<LogPeriod> getLogPeriods() {
    return logPeriods;
  }

  public void setLogPeriods(List<LogPeriod> logPeriods) {
    this.logPeriods = logPeriods;
  }

  /**
   * compare LogPeriods by odometer
   */
  public static class OdoComparator implements Comparator<LogPeriod> {
    boolean up = true;

    public OdoComparator(boolean pup) {
      this.up = pup;
    }

    @Override
    public int compare(LogPeriod o1, LogPeriod o2) {
      if (o1.odo==null && o2.odo==null) {
        return 0;
      }
      if (o1.odo==null) return -1;
      if (o2.odo==null) return 1;
      // reverse sort by from - higher usage first
      if (up)
        return o1.odo.compareTo(o2.odo);
      else
        return o2.odo.compareTo(o1.odo);       
    }
  }

  /**
   * sort my periods
   */
  public void sort() {
    Collections.sort(logPeriods, new OdoComparator(false));
  }
  

  @Override
  public void reinit() {

  }

  @Override
  public void fromMap(Map<String, Object> map) {

  }
  private static VehicleLog instance;
  
  public static VehicleLog getInstance() {
    if (instance==null) {
      File jsonFile = JsonAble.getJsonFile(VehicleLog.class.getSimpleName());
      JsonManager<VehicleLog> jmVehicleLog = new JsonManagerImpl<VehicleLog>(
          VehicleLog.class);
      try {
        instance = jmVehicleLog.fromJsonFile(jsonFile);
      } catch (Exception e) {
        ErrorHandler.handle(e);
      }
      if (instance == null)
        instance = new VehicleLog();
    }
    return instance;
  }
}

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
import java.util.Map;

import com.bitplan.error.ErrorHandler;
import com.bitplan.json.JsonAble;
import com.bitplan.json.JsonManager;
import com.bitplan.json.JsonManagerImpl;

/**
 * POJO for Vehicle description
 * @author wf
 *
 */
public class Vehicle implements JsonAble{
  public enum State {
    Moving, Charging, Parking, Unknown
  };
  
  String nickName;
  String VIN;
  String model;
  String group;
  String picture;
  Integer year;
  Integer mmPerRound;
  Integer maxSpeed=135; // km/h
  Integer maxRPM=9060; // rounds per minute
  public String getNickName() {
    return nickName;
  }
  public void setNickName(String nickName) {
    this.nickName = nickName;
  }
  public String getVIN() {
    return VIN;
  }
  public void setVIN(String vIN) {
    VIN = vIN;
  }
  public String getModel() {
    return model;
  }
  public void setModel(String model) {
    this.model = model;
  }
  public String getGroup() {
    return group;
  }
  public void setGroup(String group) {
    this.group = group;
  }
  public String getPicture() {
    return picture;
  }
  public void setPicture(String picture) {
    this.picture = picture;
  }
 
  public Integer getYear() {
    return year;
  }
  public void setYear(Integer year) {
    this.year = year;
  }
  public Integer getMmPerRound() {
    return mmPerRound;
  }
  public void setMmPerRound(Integer mmPerRound) {
    this.mmPerRound = mmPerRound;
  }
  public Integer getMaxRPM() {
    return maxRPM;
  }
  
  public void setMaxRPM(Integer maxRPM) {
    this.maxRPM = maxRPM;
    if (this.maxSpeed!=null)
      calcMMPerRound();
  }
  private void calcMMPerRound() {
    // maxRpm = rounds / min 
    // rounds/min * 60 = round / hour
    // maxSpeed = km / hour
    // ==> maxRPM*60 / maxSpeed = round/km
    // ==> maxSpeed/maxRPM/60 = km/round
    // ==> maxSpeed/maxRPM/60*1000000 = mm /round
    this.mmPerRound=1000000*maxSpeed/maxRPM/60;
  }
  
  public Integer getMaxSpeed() {
    return maxSpeed;
  }
  public void setMaxSpeed(Integer maxSpeed) {
    this.maxSpeed = maxSpeed;
    if (this.maxRPM!=null)
      calcMMPerRound();
  }
  @Override
  public void reinit() {
    
  }
  @Override
  public void fromMap(Map<String, Object> map) {
    this.nickName=(String) map.get("nickName");
    this.model=(String)map.get("model");
    this.group=(String)map.get("group");
    this.VIN=(String)map.get("VIN");
    this.mmPerRound=(Integer)map.get("mmPerRound");
    this.maxRPM=(Integer)map.get("maxRPM");
    this.year=(Integer)map.get("year");
    this.setMaxSpeed((Integer)map.get("maxSpeed")); // forces calc of mmPerRound
  }
  
  static Vehicle instance;

  /**
   * get the vehicle Group of this Vehicle
   * @return the VehicleGroup
   * @throws Exception
   */
  public VehicleGroup getVehicleGroup() throws Exception {
    VehicleGroup vehicleGroup=VehicleGroup.get(group);
    return vehicleGroup;
  }
  
  /**
   * get an instance of the Vehicle
   * 
   * @return - the instance
   * @throws Exception
   */
  public static Vehicle getInstance()  {
    if (instance == null) {
      File jsonFile = JsonAble.getJsonFile(Vehicle.class.getSimpleName());
      JsonManager<Vehicle> jmVehicle = new JsonManagerImpl<Vehicle>(
          Vehicle.class);
      try {
        instance = jmVehicle.fromJsonFile(jsonFile);
      } catch (Exception e) {
        ErrorHandler.handle(e);
      }
      if (instance == null)
        instance = new Vehicle();
    }
    return instance;
  }
  
}

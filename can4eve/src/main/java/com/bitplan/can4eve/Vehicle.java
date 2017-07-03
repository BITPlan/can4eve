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

import com.bitplan.can4eve.json.JsonAble;
import com.bitplan.can4eve.json.JsonManager;
import com.bitplan.can4eve.json.JsonManagerImpl;

/**
 * Vehicle description
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
  Integer mmPerRound;
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
 
  public Integer getMmPerRound() {
    return mmPerRound;
  }
  public void setMmPerRound(Integer mmPerRound) {
    this.mmPerRound = mmPerRound;
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
  }
  
  static Vehicle instance;

  /**
   * get the vehicle Group of this Vehicle
   * @return
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
  public static Vehicle getInstance() throws Exception {
    if (instance == null) {
      File jsonFile = JsonAble.getJsonFile(Vehicle.class.getSimpleName());
      JsonManager<Vehicle> jmVehicle = new JsonManagerImpl<Vehicle>(
          Vehicle.class);
      instance = jmVehicle.fromJsonFile(jsonFile);
      if (instance == null)
        instance = new Vehicle();
    }
    return instance;
  }
}

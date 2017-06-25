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
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;

/**
 * a group of vehicles that share the same OBDII-Diagnosis infrastructure
 * see http://can4eve.bitplan.com/index.php/VehicleGroup
 * @author wf
 *
 */
public class VehicleGroup {
  String name;
  String description;
  private List<VehicleModel> models = new ArrayList<VehicleModel>();

  List<Pid> pids = new ArrayList<Pid>();
  transient Map<String, Pid> pidByPid = new HashMap<String, Pid>();
  transient Map<String, Pid> pidByName = new HashMap<String, Pid>();
  transient Map<String,CANInfo> canInfoByName=new HashMap<String,CANInfo>();

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void addModel(VehicleModel model) {
    getModels().add(model);
  }

  public List<VehicleModel> getModels() {
    return models;
  }

  public void setModels(List<VehicleModel> models) {
    this.models = models;
  }

  /**
   * add the given pid
   * 
   * @param pid
   */
  public void addPid(Pid pid) {
    pids.add(pid);
    addToMaps(pid);
  }

  public void addToMaps(Pid pid) {
    pidByPid.put(pid.getPid(), pid);
    pidByName.put(pid.getName(), pid);
    for (CANInfo canInfo:pid.getCaninfos()) {
      this.canInfoByName.put(canInfo.getName(), canInfo);
    }
  }

  public List<Pid> getPids() {
    return pids;
  }

  public void setPids(List<Pid> pids) {
    this.pids = pids;
  }

  public void reinit() {
    this.pidByPid.clear();
    for (Pid pid : pids) {
      addToMaps(pid);
      for (CANInfo caninfo:pid.caninfos) {
        caninfo.pid=pid;
      }
    }
  }

  /**
   * get a Vehicle Group from the given Json File
   * 
   * @param jsonFile
   *          - the json File to get the group from
   * @return - the Vehicle Group
   * @throws Exception
   *           of reading from json fails
   */
  public static VehicleGroup fromJsonFile(File jsonFile) throws Exception {
    return fromJsonStream(new FileInputStream(jsonFile));
  }

  /**
   * get the VehicleGroup from the given Json Stream
   * 
   * @param jsonStream
   * @return
   * @throws Exception
   */
  public static VehicleGroup fromJsonStream(InputStream jsonStream)
      throws Exception {
    Gson gson = new Gson();
    VehicleGroup vehicleGroup = gson.fromJson(new InputStreamReader(jsonStream),
        VehicleGroup.class);
    vehicleGroup.reinit();
    return vehicleGroup;
  }

  static Map<String, VehicleGroup> vehicleGroupsByName = new HashMap<String, VehicleGroup>();

  /**
   * get the VehicleGroup for the given name
   * @param name
   * @return
   * @throws Exception
   */
  public static VehicleGroup get(String name) throws Exception {
    VehicleGroup vehicleGroup = vehicleGroupsByName.get(name);
    if (vehicleGroup == null) {
      InputStream jsonStream = VehicleGroup.class.getClassLoader()
          .getResourceAsStream("com/bitplan/can4eve/" + name + ".json");
      if (jsonStream==null) {
        throw new Exception(String.format("Could not load VehicleGroup %s.json from classpath",name));
      }
      vehicleGroup = VehicleGroup.fromJsonStream(jsonStream);
    }
    return vehicleGroup;
  }

  /**
   * lookup the given PID by Id
   * 
   * @param pidId
   * @return the Pid
   */
  public Pid getPidById(String pidId) {
    return this.pidByPid.get(pidId);
  }

  /**
   * lookup the given PID byname
   * 
   * @param pidName
   * @return the Pid
   */
  public Pid getPidByName(String pidName) {
    return this.pidByName.get(pidName);
  }

  /**
   * lookup the canInfo by it's name
   * @param canInfoName
   * @return the CANInfo
   */
  public CANInfo getCANInfoByName(String canInfoName) {
    CANInfo result=this.canInfoByName.get(canInfoName);
   return result;
  }

}

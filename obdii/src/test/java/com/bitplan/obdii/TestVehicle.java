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
package com.bitplan.obdii;

import static org.junit.Assert.*;

import org.junit.Test;

import com.bitplan.can4eve.VehicleGroup;
import com.bitplan.can4eve.VehicleModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
// import com.google.gson.graph.GraphAdapterBuilder;
import com.bitplan.can4eve.Pid;
import com.bitplan.can4eve.CANInfo;

/**
 * test the loadable vehicle concept
 * @author wf
 *
 */
public class TestVehicle extends TestOBDII {
  
  /**
   * convert the old enum based pids to a new gson loadable pid
   * @param opid
   * @return
   */
  public Pid convert(com.bitplan.triplet.pids.Pid opid) {
    Pid pid=new Pid();
    pid.setName(opid.name());
    pid.setPid(opid.getPid());
    pid.setFreq(opid.freq);
    pid.setExamples(opid.examples);
    for (com.bitplan.triplet.pids.CANInfo oinfo:opid.getInfos()) {
      CANInfo caninfo=new CANInfo();
      caninfo.setName(oinfo.name());
      caninfo.setPid(pid);
      caninfo.setTitle(oinfo.title);
      caninfo.setDescription(oinfo.description);
      caninfo.setFormat(oinfo.format);
      caninfo.setHistoryValuesPerMinute(oinfo.historyValuesPerMinute);
      caninfo.setMaxIndex(oinfo.maxIndex);
      caninfo.setType(oinfo.getType().getSimpleName());
      pid.addCANInfo(caninfo);
    }
    return pid;
  }

  @Test
  public void testVehicle() {
    VehicleGroup vehicleGroup=new VehicleGroup();
    vehicleGroup.setName("Triplet");
    vehicleGroup.setDescription("i-Miev,Ion and C-Zero");
    VehicleModel m1=new VehicleModel();
    m1.setMaker("Mitsubishi");
    m1.setModel("i-Miev");
    m1.setWIM("JA3");
    m1.setPicture("https://www.mitsubishi-motors.at/uploadedImages/Contents/Models/i-MiEV_MY12/i-MiEV_MY12i-MiEV/360_View/360-view//i-MiEV_0f4aa833-ea41-40a3-aa54-c35304bce685_6dab5e75-54b2-46bc-bc2b-728bcb18e7ec_3079_01.png");
    vehicleGroup.addModel(m1);
    VehicleModel m2=new VehicleModel();
    m2.setMaker("Peugeot");
    m2.setModel("Ion");
    m2.setWIM("VF3");
    m2.setPicture("http://media.peugeot.fr/image/63/5/voiture-electrique-peugeot-ion-design-exterieur.17635.43.jpg");
    vehicleGroup.addModel(m2);
    VehicleModel m3=new VehicleModel();
    m3.setMaker("Citroën");
    m3.setModel("C-Zero");
    m3.setWIM("VF7");
    m3.setPicture("http://media.citroen.de/image/62/2/citroen-c-zero.14622.png");
    vehicleGroup.addModel(m3);
    for (com.bitplan.triplet.pids.Pid opid:com.bitplan.triplet.pids.Pid.values()) {
      Pid pid=convert(opid);
      vehicleGroup.addPid(pid);
    }
    GsonBuilder gsonBuilder = new GsonBuilder();
    // new GraphAdapterBuilder().addType(Pid.class).registerOn(gsonBuilder);
    Gson gson = gsonBuilder.setPrettyPrinting().create();;
    String json=gson.toJson(vehicleGroup);
    assertNotNull(json);
    if (debug)
      System.out.println(json);
    
    VehicleGroup vg=gson.fromJson(json, VehicleGroup.class);
    assertEquals(3,vg.getModels().size());
    assertEquals(51,vg.getPids().size());
  }
  
  @Test
  public void testVehicleGroupFromClasspath() throws Exception {
    VehicleGroup vg=getVehicleGroup();
    assertEquals(3,vg.getModels().size());
    assertEquals(51,vg.getPids().size());
  }
  
  @Test
  public void testBatteryCapacityPid() throws Exception {
    VehicleGroup vg=getVehicleGroup();
    CANInfo bc = vg.getCANInfoByName("BatteryCapacity");
    assertNotNull("There should be a pid for the battery capacity",bc);
    assertTrue("BatteryCapacity should be an ISO-TP frame based info",bc.getPid().isIsoTp());
  }
}

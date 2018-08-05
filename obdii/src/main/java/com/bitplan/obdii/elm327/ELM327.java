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
package com.bitplan.obdii.elm327;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import com.bitplan.can4eve.CANValue;
import com.bitplan.can4eve.CANValue.CANRawValue;
import com.bitplan.obdii.Can4EveI18n;
import com.bitplan.can4eve.Pid;
import com.bitplan.can4eve.VehicleGroup;

/**
 * 
 * @author wf
 *
 */
public class ELM327 extends com.bitplan.elm327.ELM327Impl  {

  VehicleGroup vehicleGroup;
  private List<CANValue<?>> canValues;
  
  public VehicleGroup getVehicleGroup() {
    return vehicleGroup;
  }

  public void setVehicleGroup(VehicleGroup vehicleGroup) {
    this.vehicleGroup = vehicleGroup;
  }

  /**
   * construct me from the given Vehicle Group
   * @param vehicleGroup
   */
  public ELM327(VehicleGroup vehicleGroup) {
    super();
    this.vehicleGroup=vehicleGroup;
  }

  public List<CANValue<?>> getCANValues() {
    if (canValues == null) {
      canValues = new ArrayList<CANValue<?>>();
      for (Pid pid : this.getVehicleGroup().getPids()) {
        CANRawValue canRawValue = new CANRawValue(pid.getFirstInfo());
        canValues.add(canRawValue);
     }
    }
    return canValues;
  }

  /**
   * respond to a Buffer Overrun by sending a character (in a separate thread
   * ...)
   */
  public void respondToBufferOverrun() {
    Executors.newSingleThreadExecutor().execute(new Runnable() {
      @Override
      public void run() {
        try {
          // send an empty line to reactive last ST .. command
          send("");
        } catch (Exception e) {
          // ignore
        }
      }
    });
  }
  
  /**
   * get the info for the device
   */
  public String getInfo() {
    String info=Can4EveI18n.get(Can4EveI18n.ELM327_CAN4EVE_INCOMPATIBLE);
    if (useable) {
      info=Can4EveI18n.get(Can4EveI18n.ELM327_CAN4EVE_COMPATIBLE);
    }
      
    if (id!=null) {
      info+="\n"+id;
    }
    if (description!=null) {
      info+="\n"+this.getDescription();
    }
    if (STN) {
      info+="\n\n"+Can4EveI18n.get(Can4EveI18n.ELM327_RECOMMENDED);
      info+="\nfirmware: "+this.firmwareId;
      info+="\nhardware: "+this.hardwareId;
    }
    return info;
  }
  
}
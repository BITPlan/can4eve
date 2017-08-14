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
package com.bitplan.elm327;

import com.bitplan.csv.CSVUtil;

/**
 * ELM327 Device information
 * @author wf
 *
 */
public class ELM327DeviceImpl implements ELM327Device {
  protected boolean useable;
  protected boolean STN=false;

  // id as returned by AT I
  protected String id;

  // description as returned by AT @1
  protected String description;

  // device id as returned by AT @2
  protected String deviceId;

  protected String hardwareId;
  protected String firmwareId;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  public String getHardwareId() {
    return hardwareId;
  }

  public void setHardwareId(String hardwareId) {
    this.hardwareId = hardwareId;
  }

  public String getFirmwareId() {
    return firmwareId;
  }

  public void setFirmwareId(String firmwareId) {
    this.firmwareId = firmwareId;
  }
  public boolean isUsable() {
    return useable;
  }

  public boolean isSTN() {
    return STN;
  }
  
  /**
   * get the info for the device
   */
  public String getInfo() {
    String info="ELM327 device detected which might not be compatible";
    if (useable) {
      info="can4eve compatible ELM327 device detected";
    }
      
    if (id!=null) {
      info+="\n"+id;
    }
    if (description!=null) {
      info+="\n"+this.getDescription();
    }
    if (STN) {
      info+="\n\nthis device is using the recommended STN chip";
      info+="\nfirmware: "+this.firmwareId;
      info+="\nhardware: "+this.hardwareId;
    }
    return info;
  }

  
  /**
   * get my CSV description
   * @return a CSV snippet
   */
  public String asCSV() {
    String csv="";
    csv+=CSVUtil.csv("id",this.getId());
    csv+=CSVUtil.csv("description",this.getDescription());
    csv+=CSVUtil.csv("firmwareId",this.getFirmwareId());
    csv+=CSVUtil.csv("hardwareId", this.getHardwareId());
    return csv;
  }
}

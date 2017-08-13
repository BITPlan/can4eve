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
package com.bitplan.triplet;

import java.util.Date;
import java.util.logging.Level;

import com.bitplan.can4eve.CANInfo;
import com.bitplan.can4eve.CANValue;
import com.bitplan.can4eve.json.AsJson;

/**
 * Vehicle identification number handling
 * 
 * @author wf
 *
 */
public class VINValue extends CANValue<String> implements AsJson {

  /**
   * construct me
   * @param canInfo
   */
  public VINValue(CANInfo canInfo) {
    super(canInfo, String.class);
  }

  transient String[] vinPart = new String[3];
  public int  year;
  public String factory = "";
  public String wmi;
  public String manufacturer;
  public int cellCount=80;
  public String vin;

  /**
   * set part of the VIN
   * 
   * @param index
   * @param partVal
   * @param timeStamp
   */
  public void set(int index, String partVal, Date timeStamp) {
    if (index >= 3) {
      LOGGER.log(Level.WARNING, "invalid VIN index " + index);
      return;
    }
    vinPart[index] = partVal;
    int parts = 0;
    for (int i = 0; i < vinPart.length; i++) {
      if (vinPart[i] != null) {
        parts++;
      }
    }
    if (parts == 3) {
      vin = vinPart[0] + vinPart[1] + vinPart[2];
      analyze(vin);
      setValue(vin, timeStamp);
    }
  }

  /**
   * set my details with the given VIN
   * @param vin
   */
  public void analyze(String vin) {
    this.vin=vin;
    // zb. VF31 N ZKZ Z B U 8XXXXX
    /*
     * VF71 und VF31 PSA
     * 
     * N = Limousine 5 Türen
     * 
     * ZKZ=180Nm Motor 88 Zellen
     * 
     * ZKY=196 Nm Motor 80 Zellen
     * 
     * Z= Untersetzungsgetriebe
     * 
     * B 2011 C 2012 D 2013 E 2014 F 2015 G 2016 H 2017 Modelljahr ......
     * 
     * U= Produktionsort (Mizushima)
     */

    // https://en.wikibooks.org/wiki/Vehicle_Identification_Numbers_(VIN_codes)/World_Manufacturer_Identifier_(WMI)
    wmi = vin.substring(0, 3);
    if (wmi.equals("JA3")) {
      manufacturer = "Mitsubishi";
      setCellCount(88);
    } else if (wmi.equals("VF3")) {
      manufacturer = "Peugeot";
    } else if (wmi.equals("VF7")) {
      manufacturer = "Citroën";
    }
    char cellcode = vin.charAt(7);
    if (wmi.startsWith("VF") && cellcode=='Z') {
      setCellCount(88);
    }
    year = (int) vin.charAt(9) - 72 + 2017;
    if (vin.charAt(10) == 'U') {
      factory = "Mizushima";
    }
    
  }

  /**
   * get my string representation
   */
  public String asString() {
    String result = "?";
    if (getValueItem().isAvailable()) {
      result = String.format("%s - year: %4d", getValueItem().getValue(), year);
      if (manufacturer != null)
        result += "/" + manufacturer;
      if (factory != null)
        result += "/" + factory;
    }
    return result;
  }

  public int getCellCount() {
    return cellCount;
  }

  public void setCellCount(int cellCount) {
    this.cellCount = cellCount;
  }

}
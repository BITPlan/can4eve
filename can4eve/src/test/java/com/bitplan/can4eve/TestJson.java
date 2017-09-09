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

import org.junit.Test;

/**
 * tests for the json persistence of can4eve relevant
 * entities
 * @author wf
 *
 */
public class TestJson {

  @Test
  public void testVehicleLog() {
    VehicleLog vehicleLog=new VehicleLog();
    vehicleLog.VIN="";
    int periods=5;
    Date now=new Date();
    for (int i=0;i<=periods;i++) {
      LogPeriod period=new LogPeriod();
      vehicleLog.logPeriods.add(period);
      Date start=new Date(now.getTime()-((i+1)*86400000));
      Date end =new Date(now.getTime()+(i+1)*36000000);
      period.startDate=start;
      period.endDate=end;
      period.logFile="/tmp/VehicleLog"+i;
    }
    String json=vehicleLog.asJson();
    System.out.println(json);
  }
  
}

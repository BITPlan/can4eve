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

import com.bitplan.can4eve.Pid;
import com.bitplan.can4eve.VehicleGroup;

/**
 * base test class for OBDII related tests
 * @author wf
 *
 */
public abstract class TestOBDII {
  static VehicleGroup vehicleGroup;
  boolean debug=false;
  
  /**
   * get the VehicleGroup
   * @return
   * @throws Exception
   */
  public static VehicleGroup getVehicleGroup() throws Exception {
    return VehicleGroup.get("Triplet");
  }
  
  /**
   * get the PID with the given PID id
   * @param pidId
   * @return
   * @throws Exception
   */
  public static Pid byName(String pidId) throws Exception {
    Pid pid=getVehicleGroup().getPidByName(pidId);
    return pid;
  }
}

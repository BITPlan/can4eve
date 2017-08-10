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

import com.bitplan.can4eve.CANValue;

/**
 * ShifterPosition
 * 
 * @author wf
 *
 */
public class ShifterPosition {
  public ShifterPosition(int shifterNum) {
    // 50 -> 20 -> 52 -> 4E -> 44
    switch (shifterNum) {
    case 0x20:
      shiftPosition = ShiftPosition.between;
      break;
    case 0x44:
      shiftPosition = ShiftPosition.D;
      break;
    case 0x4E:
      shiftPosition = ShiftPosition.N;
      break;
    case 0x50:
      shiftPosition = ShiftPosition.P;
      break;
    case 0x52:
      shiftPosition = ShiftPosition.R;
      break;
    default:
      shiftPosition = ShiftPosition.undefined;
    }
    //if (debug)
    //  LOGGER.log(Level.INFO, "Shifternum is " + shifterNum);
  }

  enum ShiftPosition {
    P, R, N, D, between, undefined
  };

  public ShifterPosition.ShiftPosition shiftPosition = ShiftPosition.undefined;
}
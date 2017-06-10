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

public class Climate {
  int ventLevel;
  String climateLevel;

  /**
   * http://myimiev.com/forum/viewtopic.php?p=31226 PID 3A4 byte 0, bits 0-3:
   * heating level (7 is off, under 7 is cooling, over 7 is heating) byte 0,
   * bit 7: AC on (ventilation dial pressed) byte 0, bit 5: MAX heating
   * (heating dial pressed) byte 0, bit 6: air recirculation (ventilation
   * direction dial pressed)
   * 
   * byte 1, bits 0-3: ventilation level (if AUTO is chosen, the automatically
   * calculated level is returned) byte 1, bits 4-7: ventilation direction
   * (1-2 face, 3 legs+face, 4 -5legs, 6 legs+windshield 7-9 windshield)
   */
  public void setClimate(int byte0, int byte1) {
    int heatLevel = byte0 & 0xf;
    if (heatLevel < 7)
      climateLevel = String.format("cooling %3d", 7 - heatLevel);
    else if (heatLevel > 7)
      climateLevel = String.format("heating %3d", heatLevel - 7);
    else
      climateLevel = "off";

    ventLevel = byte1 & 0xf;
  }
}
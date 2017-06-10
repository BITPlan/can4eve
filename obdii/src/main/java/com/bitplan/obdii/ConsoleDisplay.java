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

import java.util.Collection;

import com.bitplan.can4eve.CANValue;

public class ConsoleDisplay  implements CANValueDisplay {

  @Override
  public void show() {
    
  }

  @Override
  public void updateCanValueField(CANValue<?> canValue) {   
    System.out.println(canValue.canInfo.getTitle()+"="+canValue.asString());
  }

  @Override
  public void updateField(String title, Object value, int updateCount) {
    System.out.println(title+" "+value+"("+updateCount+")");
  }

  @Override
  public void addCANValueField(CANValue<?> canValue) {
    // not relevant
  }

  @Override
  public void addCanValueFields(Collection<CANValue<?>> canValues) {
    // not relevant
  }

  @Override
  public LabelField addField(String title, String format, int labelSize, int fieldSize) {
    return null;
  }  

}

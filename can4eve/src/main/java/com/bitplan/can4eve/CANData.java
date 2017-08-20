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

import com.bitplan.json.AsJson;

/**
 * generic CANData interface for "translated" values e.g.
 * a speed value. The CANData may be indexed - an list of values
 * may be behind a single CANData entry
 * 
 * @author wf
 *
 * @param <T>
 */
public interface CANData<T> extends AsJson {
  /**
   * set a value at the given time e.g. the speed in km/h
   * 
   * @param value
   * @param timeStamp
   */
  public void setValue(T value, Date timeStamp);

  /**
   * set an indexed value e.g. the temperature of a cell with the given index
   * 
   * @param index
   * @param value
   * @param timeStamp
   */
  public void setValue(int index, T value, Date timeStamp);

  /**
   * get the value
   * 
   * @return the value
   */
  public T getValue();

  /**
   * check whether the value is available - the value must be set and non null
   * 
   * @return false if not
   */
  public boolean isAvailable();

  /**
   * get the CANInfo - the field information about this value
   * e.g. the name of the value and the PID it is derived from
   * 
   * @return
   */
  public CANInfo getCANInfo();

  /**
   * get the time stamp of the value
   * @return the timestamp
   */
  public Date getTimeStamp();

  /**
   * get the update counter - how often has the value been set so far?
   * @return the updateCounter
   */
  public int getUpdateCount();

}

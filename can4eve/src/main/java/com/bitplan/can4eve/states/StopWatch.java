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
package com.bitplan.can4eve.states;

/**
 * generic StopWatch
 * @author wf
 *
 */
public interface StopWatch {
  /**
   * set the time to the given milliSeconds
   * @param mSecs
   */
  public void setTime(long mSecs);
  /**
   * get the time in msecs
   */
  public long getTime();
  
  // set stopwatch to all zeros
  public void reset();
  
  // halt the stopwatch
  public void halt();
  
  /**
   * set the active status of this stopWatch
   * @param active
   */
  public void setActive(boolean active);
  public boolean isActive();
  
  /**
   * get my ISO representation
   * @return
   */
  String asIsoDateStr();
}

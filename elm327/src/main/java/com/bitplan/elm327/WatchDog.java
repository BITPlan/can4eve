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

/**
 * a WatchDog that will call a restart
 * @author wf
 *
 */
public interface WatchDog {
  /**
   * add a Watchable to this watch Dog 
   * @param watchable - the watchable to control
   */
  public void addWatchable(Watchable watchable);
  
  /**
   * send a ping to the WatchDog
   * @param watchable
   */
  public void ping(Watchable watchable);
  
}

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

import com.bitplan.elm327.Config;
import com.bitplan.obdii.elm327.ELM327;

/**
 * 
 * @author wf
 *
 */
public interface OBDApp {
  public void setConfig(Config config);
  public Config getConfig();
  public ELM327 testConnection(Config config) throws Exception;
  public ELM327 start() throws Exception;
  public ELM327 stop() throws Exception;
}

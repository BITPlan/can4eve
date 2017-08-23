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

import java.io.File;
import java.util.Map;

import com.bitplan.error.ErrorHandler;
import com.bitplan.json.JsonAble;
import com.bitplan.json.JsonManager;
import com.bitplan.json.JsonManagerImpl;

/**
 * owner information
 * @author wf
 *
 */
public class Owner implements JsonAble{

  String nickName;
  String email;
  String country;
  String zipcode;
  
  @Override
  public void reinit() {
  }

  @Override
  public void fromMap(Map<String, Object> map) {
    this.email=(String) map.get("email");
    this.nickName=(String) map.get("nickName");
    this.country=(String) map.get("country");
    this.zipcode=(String) map.get("zipcode");
  }
  
  static Owner instance;
  
  public static Owner getInstance() {
    if (instance == null) {
      File jsonFile = JsonAble.getJsonFile(Owner.class.getSimpleName());
      JsonManager<Owner> jmOwner = new JsonManagerImpl<Owner>(
          Owner.class);
      try {
        instance = jmOwner.fromJsonFile(jsonFile);
      } catch (Exception e) {
        ErrorHandler.handle(e);
      }
      if (instance == null)
        instance = new Owner();
    }
    return instance;
  }

}

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
package com.bitplan.can4eve.json;

import java.io.File;
import java.io.InputStream;
import com.google.gson.Gson;

/**
 * manager of Json Object of Type t
 * 
 * @author wf
 *
 * @param <T>
 */
public interface JsonManager<T extends JsonAble> {

  /**
   * get a T from the given Json File
   * 
   * @param jsonFile
   *          - the json File to get the T from
   * @return - the T
   * @throws Exception
   *           of reading from json fails
   */
  public T fromJsonFile(File jsonFile) throws Exception;

  /**
   * get a  T from the given jsonStream
   * @param jsonStream - the jsonStream to get the data from
   * @return - the T
   * @throws Exception 
   */
  public T fromJsonStream(InputStream jsonStream) throws Exception;
  
  /**
   * get a T from the given Json string
   * @param json
   * @return - the T
   */
  public T fromJson(String json);

  /**
   * get the Gson implementation
   * @return
   */
  public Gson getGson();

}

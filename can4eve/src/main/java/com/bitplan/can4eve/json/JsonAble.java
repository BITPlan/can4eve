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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

/**
 * for jsonable POJOs
 * @author wf
 *
 */
public interface JsonAble extends AsJson {
  public static String appName="can4eve";
  /**
   * reinitialize me after being reloaded from json
   */
  public void reinit();
  
  /**
   * save me to my Json File
   * @throws IOException
   */
  default void save() throws IOException {
    File jsonFile=getJsonFile();
    if (!jsonFile.getParentFile().isDirectory())
      jsonFile.getParentFile().mkdirs();
    FileUtils.writeStringToFile(jsonFile, this.asJson(),"UTF-8");
  }
  
  default File getJsonFile() {
    File jsonFile=getJsonFile(this.getClass().getSimpleName());
    return jsonFile; 
  }
  
  static File getJsonFile(String name) {
    String home = System.getProperty("user.home");
    File configDirectory=new File(home+"/."+appName+"/");
    String jsonFileName = name+".json";
    File jsonFile = new File(configDirectory, jsonFileName);
    return jsonFile;
  }

  /**
   * get my values a a map
   * @return
   */
  @SuppressWarnings("unchecked")
  default Map<String,Object> asMap() {
    Map<String,Object> map=new HashMap<String,Object>();
    map = (Map<String,Object>) getGson().fromJson(this.asJson(), map.getClass());
    return map;
  }
  
  /**
   * initialize my values from the given map
   * @param map
   */
  public void fromMap(Map<String, Object> map);
  
  
}

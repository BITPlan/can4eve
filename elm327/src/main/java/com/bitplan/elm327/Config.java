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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Test Configuration
 * 
 * @author wf
 *
 */
public class Config {
  String serialDevice; // e.g. cu.usbserial-113010822821 on MacOSx
  int serialBaud;      // e.g. 115200
  String ip;           // e.g. 192.168.1.30
  int port;            // e.g. 35000
  int timeout;         // e.g. 500 (for 1/2 sec)
  
  public String getSerialDevice() {
    return serialDevice;
  }

  public void setSerialDevice(String serialDevice) {
    this.serialDevice = serialDevice;
  }

  public int getSerialBaud() {
    return serialBaud;
  }

  public void setSerialBaud(int serialBaud) {
    this.serialBaud = serialBaud;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public int getTimeout() {
    return timeout;
  }

  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }

  public boolean isDebug() {
    return debug;
  }

  public void setDebug(boolean debug) {
    this.debug = debug;
  }

  public static void setInstance(Config instance) {
    Config.instance = instance;
  }

  boolean debug;       // e.g. true 
  

  static Config instance;

  public static Gson getGson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    // new GraphAdapterBuilder().addType(Pid.class).registerOn(gsonBuilder);
    Gson gson = gsonBuilder.setPrettyPrinting().create();
    return gson;
  }

  /**
   * return me as a Json file
   * 
   * @return
   */
  public String asJson() {
    String json = getGson().toJson(this);
    return json;
  }
  
  public static File getConfigFile() {
    String home = System.getProperty("user.home");
    String configFilename = home + "/.can4eve/testconfig.json";
    File configFile = new File(configFilename);
    return configFile;
  }

  /**
   * get the 
   * @return
   * @throws FileNotFoundException
   */
  public static Config getInstance() throws FileNotFoundException {
    if (instance == null) {
      File configFile=getConfigFile();
      if (configFile.canRead()) {
        FileReader jsonReader = new FileReader(configFile);
        instance = getGson().fromJson(jsonReader, Config.class);
      }
    }
    return instance;
  }
}

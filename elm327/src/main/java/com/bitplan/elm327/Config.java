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
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Test Configuration
 * 
 * @author wf
 *
 */
public class Config {
  public enum ConfigMode {Test,Preferences}
  public enum DeviceType {USB,Bluetooth,Network,Simulator}
  DeviceType deviceType;   // e.g. USB
  String serialDevice;     // e.g. cu.usbserial-113010822821 on MacOSx
  Integer baudRate=38400;  // e.g. 115200
  Boolean direct=false;    // e.g. true if the device needs no serial configuration
  String hostname;         // e.g. 192.168.1.30
  String logPrefix;        // e.g. my Ion
  Integer port=35000;      // e.g. 35000
  Integer timeout;         // e.g. 500 (for 1/2 sec)
  Boolean debug=false;      // e.g. true
  private static Map<ConfigMode,Config> configs=new HashMap<ConfigMode,Config>();

  public DeviceType getDeviceType() {
    return deviceType;
  }

  public void setDeviceType(DeviceType deviceType) {
    this.deviceType = deviceType;
  }

  public String getSerialDevice() {
    return serialDevice;
  }

  public void setSerialDevice(String serialDevice) {
    this.serialDevice = serialDevice;
  }

  public int getBaudRate() {
    return baudRate;
  }

  public void setBaudRate(int serialBaud) {
    this.baudRate = serialBaud;
  }

  public Boolean getDirect() {
    return direct;
  }

  public void setDirect(Boolean direct) {
    this.direct = direct;
  }

  public String getHostname() {
    return hostname;
  }

  public void setHostname(String hostname) {
    this.hostname = hostname;
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

  public void setDebug(boolean debug) {
    this.debug = debug;
  }

  public boolean isDebug() {
    return debug;
  }

  public String getLogPrefix() {
    return logPrefix;
  }

  public void setLogPrefix(String logPrefix) {
    this.logPrefix = logPrefix;
  }

  /**
   * get the Gson
   * @return
   */
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
  
  /**
   * get the Config directory 
   * @return the config directory
   */
  public static File getConfigDirectory() {
    String home = System.getProperty("user.home");
    File configDirectory=new File(home+"/.can4eve/");
    return configDirectory;
  }
  
  /**
   * get the config file
   * @param configMode 
   * @return
   */
  public static File getConfigFile(ConfigMode configMode) {
    String filename="obdii";
    switch (configMode) {
    case Test:
      filename="testobdii";
    default:
      break;
    }
    String configFilename =filename+".json";
    File configFile = new File(getConfigDirectory(),configFilename);
    return configFile;
  }
  
  /**
   * save the settings
   * @throws Exception
   */
  public void save(ConfigMode configMode) throws Exception {
    File configFile=getConfigFile(configMode);
    // create the config directory if it does not exist yet
    if (!configFile.getParentFile().isDirectory())
      configFile.getParentFile().mkdirs();
    FileUtils.writeStringToFile(configFile, this.asJson(),"UTF-8");
  }

  /**
   * get the 
   * @param configMode 
   * @return
   * @throws FileNotFoundException
   */
  public static Config getInstance(ConfigMode configMode) throws FileNotFoundException {
    Config instance=configs.get(configMode);
    if (instance == null) {
      File configFile=getConfigFile(configMode);
      if (configFile.canRead()) {
        FileReader jsonReader = new FileReader(configFile);
        instance = getGson().fromJson(jsonReader, Config.class);
        configs.put(configMode, instance);
      }
    }
    return instance;
  }

  /**
   * get me as a map
   * @return
   */
  @SuppressWarnings("unchecked")
  public Map<String,Object> asMap() {
    Map<String,Object> map=new HashMap<String,Object>();
    map = (Map<String,Object>) getGson().fromJson(this.asJson(), map.getClass());
    return map;
  }

  /**
   * set my values from the given map
   * @param map
   */
  public void fromMap(Map<String, Object> map) {
    String dType=(String)map.get("deviceType");
    if (dType!=null)
      this.deviceType=DeviceType.valueOf(dType);
    this.hostname=(String) map.get("hostname");
    this.port=(Integer) map.get("port");
    this.serialDevice=(String) map.get("serialDevice");
    this.baudRate=(Integer) map.get("baudRate");
    this.timeout=(Integer) map.get("timeout");
    this.direct=(Boolean)map.get("direct");
    this.debug=(Boolean)map.get("debug");
    this.logPrefix=(String) map.get("logPrefix");
  }
}

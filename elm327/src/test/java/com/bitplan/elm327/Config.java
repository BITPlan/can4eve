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

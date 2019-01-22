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

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

import org.apache.commons.io.FileUtils;

/**
 * Raspberry PI helper class
 * 
 * @author wf
 *
 */
public class Raspberry {

  public static boolean debug = false;

  /**
   * check if this java vm runs on a raspberry PI
   * https://stackoverflow.com/questions/37053271/the-ideal-way-to-detect-a-raspberry-pi-from-java-jar
   * 
   * @return true if this is running on a Raspbian Linux
   */
  public static boolean isPi() {
    String osRelease = osRelease();
    return osRelease != null && osRelease.contains("Raspbian");
  }

  /**
   * get the operating System release
   * 
   * @return the first line from /etc/os-release or null
   * @throws IOException
   */
  public static String osRelease() {
    String os = System.getProperty("os.name");
    if (os.startsWith("Linux")) {
      final File osRelease = new File("/etc", "os-release");
      try {
        return FileUtils.readFileToString(osRelease, "UTF-8");
      } catch (IOException e) {
        if (debug)
          e.printStackTrace();
      }
    }
    return null;
  }

  protected final static String brightnessFilename = "/sys/class/backlight/rpi_backlight/brightness";

  /**
   * get the display brightness
   * 
   * @return null or the brightness value
   */
  public static Integer getBrightness() {
    Integer brightness = null;
    File brightnessFile = new File(brightnessFilename);
    if (brightnessFile.canRead()) {
      try {
        String bStr = FileUtils.readFileToString(brightnessFile, "UTF-8");
        brightness = Integer.parseInt(bStr.trim());
      } catch (IOException e) {

      }
    }
    return brightness;
  }

  /**
   * set the brightness to the given level
   * @param level
   */
  public static void setBrightness(int level) {
    if (level < 0 || level > 255)
      throw new InvalidParameterException(
          "level " + level + " out of range 0-255");
    File brightnessFile = new File(brightnessFilename);
    if (brightnessFile.canWrite()) {
      String levelStr = "" + level;
      try {
        FileUtils.writeStringToFile(brightnessFile, levelStr, "UTF-8");
      } catch (IOException e) {
        if (debug)
          e.printStackTrace();
      }
    }
  }

  /**
   * entry point to call from command line
   */
  public static void main(String args[]) {
    if (isPi()) {
      System.out.println(osRelease());
      System.out.println("brightness is "+getBrightness());
      if (args.length>0) {
        setBrightness(Integer.parseInt(args[0]));
      }
    }
  }
}
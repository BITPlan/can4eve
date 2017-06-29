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
package com.bitplan.obdii.elm327;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.bitplan.elm327.Packet;
import com.bitplan.elm327.PacketImpl;
import com.bitplan.elm327.ResponseHandler;

/**
 * reader for ELM327 raw log files
 * 
 * @author wf
 *
 */
public class LogReader {
  public static boolean debug = false;
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.obdii");
  private BufferedReader logReader;
  private ResponseHandler responseHandler;

  int index = 0;
  public static SimpleDateFormat captureDateFormatter = new SimpleDateFormat(
      "yyyy-MM-dd hh:mm:ss a");
  public static SimpleDateFormat logDateFormatter = new SimpleDateFormat(
      "yyyy-MM-dd hh:mm:ss.SSS");
  private ZipFile zipFile;

  public abstract class LogListener {
    /**
     * handler for updates
     * 
     * @param line
     * @param len
     * @param index
     * @param count
     * @return true to continue - false to interrupt
     */
    public abstract boolean onUpdate(String line, int len, int index,
        int count);
  }

  /**
   * construct a Log Reader for the given logFile
   * 
   * @param logFile
   * @param responseHandler
   * @throws Exception
   */
  public LogReader(File logFile, ResponseHandler responseHandler)
      throws Exception {
    // this.logFile = logFile;
    this.responseHandler = responseHandler;
    InputStream inputStream;
    if (logFile.getName().endsWith(".zip")) {
      zipFile = new ZipFile(logFile);
      Enumeration<? extends ZipEntry> entries = zipFile.entries();
      if (entries.hasMoreElements()) {
        ZipEntry entry = entries.nextElement();
        inputStream = zipFile.getInputStream(entry);
      } else {
        throw new RuntimeException("No zip content in " + logFile.getName());
      }
    } else {
      inputStream = new FileInputStream(logFile);
    }
    logReader = new BufferedReader(new InputStreamReader(inputStream));
  }

  /**
   * get a packet from the given line
   * 
   * @param line
   * @return a Packet
   * @throws Exception
   */
  public static Packet lineAsPacket(String line) throws Exception {
    String tsVal = ""; // timestamp raw string value
    String canLine = null;
    int len = line.length();

    if (line.startsWith("20")) {
      tsVal = line.substring(0, 23);
      Date timeStamp = logDateFormatter.parse(tsVal);
      canLine = line.substring(24) + "\n";
      if (canLine.length() > 5) {
        Packet p = new PacketImpl(canLine, timeStamp);
        return p;
      }
    } else if (len >= 42 && len <= 44) {
      String[] parts = line.split(",");
      if (parts.length == 2) {
        tsVal = parts[0].replace("\"", "");
        canLine = parts[1];
        Date timeStamp = captureDateFormatter.parse(tsVal);
        String pid = canLine.substring(0, 3);
        StringBuffer buf = new StringBuffer();
        buf.append(pid);
        buf.append(" 8 ");
        for (int i = 3; i < canLine.length(); i += 2) {
          buf.append(canLine.charAt(i));
          buf.append(canLine.charAt(i + 1));
          buf.append(" ");
        }
        Packet p = new PacketImpl(buf.toString(), timeStamp);
        return p;
      }
    } else {
      // LOGGER.log(Level.INFO, "len: " + len);
    }
    return null;
  }

  /**
   * read the given logfile
   * 
   * @throws Exception
   */
  public void read(LogListener logListener) throws Exception {
    String line; // a line read
    int count = 0;
    while ((line = logReader.readLine()) != null) {
      count++;
      Packet p = lineAsPacket(line);
      if (p != null)
        this.responseHandler.handleResponse(p);
      if (!logListener.onUpdate(line, line.length(), index, count))
        break;
    }
    logReader.close();
    if (zipFile != null)
      zipFile.close();
    index++;
  }
}

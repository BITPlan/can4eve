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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bitplan.can4eve.Pid;
import com.bitplan.elm327.Packet;
import com.bitplan.obdii.elm327.ELM327;

/**
 * https://en.wikipedia.org/wiki/OBD-II_PIDs
 * 
 * @author wf
 *
 */
public class PIDResponse {
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.obdii");
  public static boolean debug = false;

  public static final Pattern PID_LINE_PATTERN = Pattern
      .compile("([0-9A-F]{3})\\s+([0-9]|[0-9A_F]{2})\\s(([0-9A-F]{2}\\s)+)");

  // data representation
  // FIXME use byte?
  public int[] d;
  public String pidId;
  public Pid pid;
  public int pidHex;
  int len;
  private Packet response;
  boolean valid = true;

  /**
   * create this PIDResponse from the given Packet
   * 
   * @param elm327
   * @param response
   */
  public PIDResponse(ELM327 elm327, Packet response) {
    String line = response.getData();
    Matcher pmatcher = PIDResponse.PID_LINE_PATTERN.matcher(line+" ");
    if (pmatcher.matches()) {
      if (debug) {
        LOGGER.log(Level.INFO, "creating PIDResponse for " + line);
      }
      this.setResponse(response);
      pidId = pmatcher.group(1);
      String lenStr = pmatcher.group(2);
      boolean isotp = lenStr.length() == 2;
      String data;
      if (isotp) {
        data = lenStr + " " + pmatcher.group(3);
      } else {
        data = pmatcher.group(3);
      }
      String[] ds = data.split("\\s");
      pidHex = hex2decimal(pidId);
      pid = elm327.getVehicleGroup().getPidById(pidId);
      if (pid == null) {
        LOGGER.log(Level.WARNING, "Unknown PID " + pidId);
      } else {
        log("pid=" + pid.toString());
        if (elm327.isLength() && !isotp) {
          len = hex2decimal(lenStr);
        } else {
          len = 8; // FIXME - what if length not set? - e.g look for next three
                   // letter PID String?
        }
        if (ds.length != len) {
          if (debug)
            LOGGER.log(Level.WARNING,
                String.format(
                    "length mismatch reported len %3d != found len %3d for %s",
                    len, ds.length, response));
          valid = false;
        } else {
          d = new int[len];

          for (int i = 0; i < d.length; i++) {
            d[i] = hex2decimal(ds[i]);
          }
        }
      }
    }
  }

  /**
   * get the decimal equivalent to the given hex value
   * 
   * @param s
   *          - the hex value
   * @return - the integer equivalent
   */
  public static int hex2decimal(String s) {
    String digits = "0123456789ABCDEF";
    s = s.toUpperCase();
    int val = 0;
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      int d = digits.indexOf(c);
      val = 16 * val + d;
    }
    return val;
  }

  /**
   * analyze the given OBDII response string and split it into its PIDRresponse
   * 
   * @param elm327
   *          - the device for reading
   * @param response
   * @return
   */
  public static List<PIDResponse> fromResponse(ELM327 elm327, Packet response) {
    if (debug)
      LOGGER.log(Level.INFO,
          "handling pid response '" + response.asString() + "'");
    List<PIDResponse> responses = new ArrayList<PIDResponse>();
    Matcher matcher = null;
    if (elm327.isHeader() && elm327.isLength()) {
      matcher = PIDResponse.PID_LINE_PATTERN.matcher(response.getData() + " ");
    }
    if (matcher == null)
      throw new RuntimeException(
          "PIDResponse currently only works with header and length enabled!");
    while (matcher.find()) {
      String pidline = matcher.group();
      if (debug)
        LOGGER.log(Level.INFO, "handling pidline '" + pidline + "'");
      PIDResponse pidResponse = new PIDResponse(elm327, response);
      if (pidResponse.valid)
        responses.add(pidResponse);
    }
    return responses;
  }

  public Packet getResponse() {
    return response;
  }

  public void setResponse(Packet response) {
    this.response = response;
  }

  /**
   * optionally log the given message
   * 
   * @param msg
   *          - the message to log
   */
  public static void log(String msg) {
    if (debug)
      LOGGER.log(Level.INFO, msg);
  }

  /**
   * convert my integer data to a string
   * 
   * @return the string
   */
  public String getString() {
    return getString(0);
  }

  /**
   * get the string from the given index
   * 
   * @param fromIndex
   * @return the String for the given index
   */
  public String getString(int fromIndex) {
    StringBuffer s = new StringBuffer();
    for (int i = fromIndex; i < d.length; i++) {
      int c = d[i];
      if (c != 255)
        s.append((char) c);
    }
    String str = s.toString();
    return str;
  }

  public String getRawString() {
    return this.getResponse().getData();
  }
}

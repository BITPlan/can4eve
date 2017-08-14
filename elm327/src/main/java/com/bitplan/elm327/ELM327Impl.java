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


/**
 * ELM 327 handling
 *
 */
public class ELM327Impl extends ELM327DeviceImpl implements ELM327 {
  public static long INIT_TIMEOUT = 150; // initialization operates with a faster
                                         // timeout
  Connection con;

  boolean header;
  boolean length;
  boolean echo;
  boolean sendLineFeed;
  boolean debug;
  boolean started = false;
  String carVoltage;

  public boolean isEcho() {
    return echo;
  }

  public void setEcho(boolean echo) {
    this.echo = echo;
  }

  public boolean isSendLineFeed() {
    return sendLineFeed;
  }

  public void setSendLineFeed(boolean sendLineFeed) {
    this.sendLineFeed = sendLineFeed;
  }

  public String getCarVoltage() {
    return carVoltage;
  }

  public boolean isHeader() {
    return header;
  }

  public void setHeader(boolean header) {
    this.header = header;
  }

  public boolean isLength() {
    return length;
  }

  public void setLength(boolean length) {
    this.length = length;
  }

  @Override
  public boolean isDebug() {
    return con.isDebug();
  }

  /**
   * construct me
   */
  public ELM327Impl() {
    this.con = new ConnectionImpl();
  }

  /**
   * show the debug information for the given packet
   * 
   * @param p
   *          - the packet
   */
  protected void showDebug(Packet p) {
    if (this.isDebug() && p != null) {
      String data = p.getRawData();
      int len = 0;
      if (data != null)
        len = data.length();
      String display = data;
      if (!con.isReceiveLineFeed()) {
        if (display != null)
          display = display.replace("\r", "\r\n");
      }
      String msg = String.format("%3d msecs - data: '%s' (%3d)",
          p.getResponseTime(), display, len);
      log(msg);
    }
  }

  @Override
  public Connection getCon() {
    return con;
  }

  @Override
  public void setCon(Connection con) {
    this.con = con;
    con.setRestarter(this);
  }

  /**
   * send the given message
   *
   * @param msg
   *          - the message to send
   * @return - the package received
   * @throws Exception
   */
  public Packet send(String msg) throws Exception {
    Packet r = con.send(msg);
    showDebug(r);
    return r;
  }

  /**
   * send the command with the expected response
   *
   * @param command
   * @param expectedResponse
   * @param allowNull
   *          - as a response
   * @return the response
   * @throws Exception
   */
  public Packet sendCommand(String command, String expectedResponse,
      boolean allowNull) throws Exception {
    Packet response = send(command);
    checkResponse(command, response, expectedResponse, allowNull);
    return response;
  }

  /**
   *
   * @param command
   * @param expectedResponse
   * @return the response
   * @throws Exception
   */
  public Packet sendCommand(String command, String expectedResponse)
      throws Exception {
    Packet response = sendCommand(command, expectedResponse, false);
    return response;
  }

  /**
   * check the response for the given command
   *
   * @param command
   *          - the command that was sent
   * @param response
   *          - the response received
   * @param expectedResponse
   *          - the regulare expression
   * @param allowNull
   * @throws Exception
   *           - if the check failed
   */
  private void checkResponse(String command, Packet response,
      String expectedResponse, boolean allowNull) throws Exception {
    String data = response.getData();
    if (allowNull && data == null)
      return;
    if (data == null || (!data.matches(expectedResponse))) {
      String msg = String.format("sendCommand %s failed expected '%s' but",
          command, expectedResponse);
      if (response.isTimeOut())
        msg += String.format(" timed out after %4d msecs",
            response.getResponseTime());
      else {
        msg += String.format(" got '%s' ", response.getData());
        if (response.getRequest() != null) {
          msg += String.format(" for request '%s'",
              response.getRequest().getData());
        }
      }
      throw new OBDException(msg, response);
    }
  }

  /**
   * check the given response
   * 
   * @param command
   * @param response
   * @param expectedResponse
   * @throws Exception
   */
  public void checkResponse(String command, Packet response,
      String expectedResponse) throws Exception {
    checkResponse(command, response, expectedResponse, false);
  }
  
  /**
   * flush response queue
   * @throws Exception
   */
  public void flushResponseQueue() throws Exception {
    // send a CR to stop current monitoring command like STM
    Packet r;
    // sendCommand("", ".*", true);
    int retries=0;
    int MAX_RETRIES=5;
    String data=null;
    // "eat" data that is still in the buffer
    do {
      r = send("");
      data = r.getData();
    } while (data!=null  && (++retries<MAX_RETRIES));
  }

  @Override
  public void reinitCommunication(long timeOutMsecs) throws Exception {
    started = true;
    // keep the old timeout
    long timeout = con.getTimeout();
    // operate with a much lower timeout to quickly reinitialize
    con.setTimeout(1500);
    // assume the device has not been configured to return linefeeds (yet)
    con.setReceiveLineFeed(false);
    con.setSendLineFeed(true);
 
    // reset
    send("AT Z");
    con.setTimeout(timeOutMsecs);
    // turn the ECHO off
    send("AT E0");
    // turn the Line feed on
    send("AT L1");
    con.setReceiveLineFeed(true);
    // restore old timeout value
    con.setTimeout(timeout);
  }

  /**
   * identify this device set id, description
   * 
   * @throws Exception
   */
  public void identify() throws Exception {
    Packet r = sendCommand("AT I", "ELM327 v.*");
    id = r.getData();
    r = send("AT @1");
    description = r.getData();
    r = send("AT @2");
    deviceId = r.getData();
    carVoltage = sendCommand("AT RV", ".*").getData(); // Car VoltageAT R
    if (id != null && description != null
        && description.startsWith("SCANTOOL")) {
      useable = true;
      STN = true; // this device has STM command
      hardwareId = sendCommand("STDI", ".*").getData(); // Hardware ID string
      firmwareId = sendCommand("STI", ".*").getData(); // Firmware ID
    }
  }

  /**
   * initialize the OBD
   */
  public void initOBD2() throws Exception {
    initOBD2(INIT_TIMEOUT);
  }

  /**
   * init OBD 2 communication
   */
  public void initOBD2(long timeOutMsecs) throws Exception {
    reinitCommunication(timeOutMsecs);
    identify();
    sendCommand("AT SP 6", "OK"); // select ISO 15765-4 CAN (11 bit ID, 500
    // kbaud)
    sendCommand("AT DP", "ISO 15765-4.*"); // ISO 15765-4 (CAN 11/500)
    sendCommand("AT H1", "(OK)?"); // set Headers
    setHeader(true);
    sendCommand("AT D1", "(OK)?"); // display length
    setLength(true);
    // sendCommand("AT CAF0", "OK"); // switch off automatic formatting
    this.getCon().setHandleResponses(true);
  }

  @Override
  public void log(String msg) {
    con.log(msg);
  }

  @Override
  public void setLog(Log log) {
    con.setLog(log);
  }

  @Override
  public Log getLog() {
    return con.getLog();
  }

  @Override
  public void handle(String msg, Throwable th) {
    con.handle(msg, th);
  }

  @Override
  public void restart() throws Exception {
    this.reinitCommunication(this.getCon().getTimeout());
  }

  @Override
  public void halt() {
    this.getCon().halt();
    started = false;
  }

  @Override
  public boolean isStarted() {
    return started;
  }

}

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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bitplan.can4eve.CANValue;
import com.bitplan.can4eve.CANValue.CANRawValue;
import com.bitplan.can4eve.VehicleGroup;
import com.bitplan.obdii.elm327.ELM327;

/**
 * general OBDII communication to any vehicle
 * 
 * @author wf
 *
 */
public abstract class OBDHandler implements ResponseHandler {
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.obdii");
  public static SimpleDateFormat isoDateFormatter = new SimpleDateFormat(
      "yyyy-MM-dd HH:mm:ss");
  public static SimpleDateFormat timeStampIsoDateFormatter = new SimpleDateFormat(
      "yyyy-MM-dd HH:mm:ss.SSS");
  public static SimpleDateFormat logIsoDateFormatter = new SimpleDateFormat(
      "yyyy-MM-dd_HHmmss");

  public static final int SOCKET_TIMEOUT = 10000;
  public static boolean debug = false;
  private ELM327 elm327;

  private File device;
  private File logFile;
  protected PrintWriter logWriter;
  protected List<CANValue<?>> canValues;
  private Map<String, CANRawValue> canRawValues = new HashMap<String, CANRawValue>();

  String hardwareId;
  String firmwareId;
  String carVoltage;
  int bufferOverruns = 0;

  public ELM327 getElm327() {
    return elm327;
  }

  public void setElm327(ELM327 elm327) {
    this.elm327 = elm327;
  }

  /**
   * get the CAN Raw values
   * @return
   */
  public Map<String, CANRawValue> getCanRawValues() {
    return canRawValues;
  }

  public void setCanRawValues(Map<String, CANRawValue> canRawValues) {
    this.canRawValues = canRawValues;
  }

  /**
   * create an OBDII handler
   */
  public OBDHandler(VehicleGroup vehicleGroup) {
    // create a thread for the communication
    setElm327(new ELM327(vehicleGroup));
    // handle responses with me
    getElm327().setResponseHandler(this);
  }

  /**
   * create an OBD handler from the given device
   * 
   * @param pDevice
   *          - the device to connect to
   */
  public OBDHandler(VehicleGroup vehicleGroup,File pDevice) {
    this(vehicleGroup);
    this.device = pDevice;
    if (!device.exists())
      throw new IllegalArgumentException(
          "device " + device.getPath() + " does not exist");
    try {
      getElm327().setInput(new FileInputStream(device));
      getElm327().setOutput(new FileOutputStream(device));
    } catch (FileNotFoundException e) {
      // this shouldn't be possible we checked above that the file exists
      throw new RuntimeException("this can't happen: " + e.getMessage());
    }
  }

  /**
   * create an OBDTriplet connection via the given socket
   * 
   * @param elmSocket
   * @throws IOException
   */
  public OBDHandler(VehicleGroup vehicleGroup,Socket elmSocket) throws IOException {
    this(vehicleGroup);
    elmSocket.setSoTimeout(SOCKET_TIMEOUT);
    if (debug) {
      LOGGER.log(Level.INFO,
          "connecting with " + elmSocket.getRemoteSocketAddress().toString());
    }
    if (debug) {
      LOGGER.log(Level.INFO, "receiveBuffer=" + elmSocket.getReceiveBufferSize()
          + " sendBuffer=" + elmSocket.getSendBufferSize());
    }
    getElm327().connect(elmSocket);
  }

  /**
   * initialize me from the given socket and then set the debugging
   * 
   * @param elmSocket
   * @param debug
   * @throws IOException
   */
  public OBDHandler(VehicleGroup vehicleGroup,Socket elmSocket, boolean debug) throws IOException {
    this(vehicleGroup,elmSocket);
    setDebug(debug);
  }

  /**
   * set the debuging
   * 
   * @param pDebug
   */
  public void setDebug(boolean pDebug) {
    OBDHandler.debug = pDebug;
    this.getElm327().debug = pDebug;
  }

  /**
   * optionally log the given message
   * 
   * @param msg
   *          - the message to log
   */
  public void log(String msg) {
    if (debug)
      LOGGER.log(Level.INFO, this.getClass().getSimpleName() + " " + msg);
  }

  /**
   * send the command with the expected response
   * 
   * @param command
   * @param expectedResponse
   * @param allowNull
   *          - as a response
   * @return
   * @throws IOException
   */
  public String sendCommand(String command, String expectedResponse,
      boolean allowNull) throws Exception {
    String response = getElm327().send(command);
    checkResponse(command, response, expectedResponse, allowNull);
    return response;
  }

  /**
   * 
   * @param command
   * @param expectedResponse
   * @return
   * @throws Exception
   */
  public String sendCommand(String command, String expectedResponse)
      throws Exception {
    String response = sendCommand(command, expectedResponse, false);
    return response;
  }

  /**
   * check the response for the given command
   * 
   * @param command
   * @param response
   * @param expectedResponse
   * @param allowNull
   * @throws Exception
   */
  private void checkResponse(String command, String response,
      String expectedResponse, boolean allowNull) throws Exception {
    if (allowNull && response == null)
      return;
    if (response == null || (!response.matches(expectedResponse))) {
      throw new Exception("sendCommand '" + command + "' failed, expected '"
          + expectedResponse + "' but got '" + response + "'");
    }
  }

  private void checkResponse(String command, String response,
      String expectedResponse) throws Exception {
    checkResponse(command, response, expectedResponse, false);
  }

  /**
   * reinitialize the communication
   */
  public void reinitCommunication() {
    getElm327().setHandleResponses(false);
    long timeOut = getElm327().getTimeout();
    long initTimeOut = 50;
    getElm327().setReceiveLineFeed(false);
    getElm327().setTimeout(initTimeOut);
    getElm327().pause(2, 0);
    String response;
    try {
      getElm327().output("\t");
      getElm327().output("ATL1");
      getElm327().output("ATE0");
      getElm327().output("ATL1");
      getElm327().output("ATE0");
      response = getElm327().getResponse();
      log("first reinit response is " + response);
      int count = 0;
      // make sure while loop is entered
      response = null;
      while ((response == null || response.matches("((ATL1|ATE0)?(OK|\\?)*)?"))
          && count < 6) {
        response = getElm327().getResponse();
        count++;
        log(String.format("reinit response #%2d is '%s'", count, response));
      }
    } catch (Exception e) {
      LOGGER.log(Level.WARNING,
          "reinit communication throws " + e.getMessage());
    } // stop any communication
    getElm327().setTimeout(timeOut);
    getElm327().setReceiveLineFeed(true);
  }

  /**
   * initialize OBD communication
   * 
   * @throws IOException
   */
  public void init() throws Exception {
    reinitCommunication();
  }

  // full init
  public void init2() throws Exception {
    getElm327().output("AT Z"); // Reset OBD
    String response = null;
    int count = 0;
    do {
      getElm327().output("");
      response = getElm327().getResponse();
      if (response == null)
        response = "";
      count++;
    } while (count <= 3 && !response.matches("ELM327 v1.3a"));
    long timeOut = getElm327().getTimeout();
    getElm327().setTimeout(50);
    count = 0;
    do {
      response = getElm327().getResponse();
    } while (count <= 3);
    getElm327().setTimeout(timeOut);
    // sendCommand("AT D","OK|AT D|AT DOK|OKAT DOK|ELM327 v1.3a"); // Set all to
    // defaults
    String result = sendCommand("AT E0", "(OK|AT E0|AT E0OK)?"); // Switch off
                                                                 // echo
    if (result.equals("AT E0")) {
      String expectOk = getElm327().getResponse();
      checkResponse("AT E0 - OK", expectOk, "OK");
    }
    sendCommand("AT L1", "(OK|AT E0OK|AT L1OK)?"); // set LineFeed on - should
                                                   // give
    // first linefeed/OK
    getElm327().setReceiveLineFeed(true);
  }

  /**
   * initialize OBD handling
   * 
   * @throws Exception
   */
  public void initOBD() throws Exception {
    sendCommand("AT I", "ELM327 v.*"); // get information on device
    sendCommand("AT @1", ".*"); // scanTool LLC ?
    // https://gist.github.com/JamesHagerman/18047979da6f4fd680eb
    // FIXME - supply this data on the gui
    hardwareId = sendCommand("STDI", ".*"); // Hardware ID string
    firmwareId = sendCommand("STI", ".*"); // Firmware ID
    carVoltage = sendCommand("AT RV", ".*"); // Car VoltageAT R
    sendCommand("AT SP 6", "OK"); // select ISO 15765-4 CAN (11 bit ID, 500
                                  // kbaud)
    sendCommand("AT DP", "ISO 15765-4.*"); // ISO 15765-4 (CAN 11/500)
    sendCommand("AT H1", "(OK)?"); // set Headers
    getElm327().setHeader(true);
    sendCommand("AT D1", "(OK)?"); // display length
    getElm327().setLength(true);
    sendCommand("AT CAF0", "OK"); // switch off automatic formatting
    getElm327().setHandleResponses(true);
  }

  /**
   * monitor the given pid
   * 
   * @param display
   * 
   * @param pid
   * @param timeOut
   * @throws Exception
   */
  public void monitorPid(CANValueDisplay display, String pid, long frameLimit)
      throws Exception {
    sendCommand("", ".*", true);
    sendCommand("AT L1", ".*");
    getElm327().send("AT CRA " + pid);
    getElm327().send("AT MA");
    for (long i = 0; i < frameLimit; i++) {
      @SuppressWarnings("unused")
      String response = getElm327().getResponse();
      if (display != null)
        showValues(display);
    }
  }

  /**
   * get a String from the given string array
   * 
   * @param s
   *          - the source string array
   * @param from
   *          - the index from which to get parts
   * @param to
   *          - the index to which to get parts
   * @return - the resulting string
   */
  protected String getString(String[] s, int from, int to) {
    StringBuffer result = new StringBuffer();
    for (int i = from; i < to; i++) {
      String hex = s[i];
      if (!(hex.equals("FF") || hex.equals("00")))
        result.append((char) Integer.parseInt(hex, 16));
    }
    return result.toString();
  }

  /**
   * optionally log the response to the logWriter
   * 
   * @param pLogWriter
   * @param response
   * @param timeStamp
   */
  public void logWrite(PrintWriter pLogWriter, String response,
      Date timeStamp) {
    // if logging is enabled
    if (pLogWriter != null) {
      pLogWriter.println(
          timeStampIsoDateFormatter.format(timeStamp) + " " + response);
      pLogWriter.flush();
    }
  }

  /**
   * log Responses for the given logRoot and vehicleName
   * 
   * @param logRoot
   * @param vehicleName
   * @return - the LogFile
   * @throws FileNotFoundException
   */
  public File logResponses(File logRoot, String vehicleName)
      throws FileNotFoundException {
    Date now = new Date();
    String filename = vehicleName + "_" + logIsoDateFormatter.format(now)
        + ".log";
    logFile = new File(logRoot, filename);
    logWriter = new PrintWriter(logFile);
    return logFile;
  }

  /**
   * close me
   */
  public void close() {
    if (logWriter != null) {
      logWriter.close();
      logWriter = null;
      logFile = null;
    }
  }

  /**
   * handle Response
   * 
   * @param response
   * @param timeStamp
   */
  @Override
  public void handleStringResponse(String response, Date timeStamp) {
    if (response == null)
      return;
    log(" handling response " + response);
    // optionally log the response to a file
    logWrite(logWriter, response, timeStamp);
    // handle buffer overrun
    if (response.startsWith("BUFFER")) {
      getElm327().respondToBufferOverrun();
      this.bufferOverruns++;
      return;
    }
    List<PIDResponse> pidResponses = PIDResponse.fromString(getElm327(), response);
    for (PIDResponse pidResponse : pidResponses) {
      if (pidResponse.pid != null)
        handleResponse(pidResponse, timeStamp);
    }
  }

  public abstract void handleResponse(PIDResponse pidResponse, Date timeStamp);

  public abstract void showValues(final CANValueDisplay display);
}

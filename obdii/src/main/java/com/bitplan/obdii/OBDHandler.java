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
import com.bitplan.can4eve.Pid;
import com.bitplan.can4eve.VehicleGroup;
import com.bitplan.elm327.Connection;
import com.bitplan.elm327.ConnectionImpl;
import com.bitplan.elm327.Packet;
import com.bitplan.elm327.ResponseHandler;
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
    getElm327().getCon().setResponseHandler(this);
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
      Connection con=new ConnectionImpl();
      con.setInput(new FileInputStream(device));
      con.setOutput(new FileOutputStream(device));
      this.getElm327().setCon(con);
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
    Connection con=new ConnectionImpl();
    con.connect(elmSocket);
    getElm327().setCon(con);
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
    // this.getElm327().debug = pDebug;
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
   * check the given PID  
   * @param display
   * @param pidId
   * @param frameLimit
   * @throws Exception
   */
  public void checkPid(CANValueDisplay display, String pidId, long frameLimit)
      throws Exception {
    Pid pid=this.getElm327().getVehicleGroup().getPidById(pidId);
    if (pid==null)
      throw new IllegalArgumentException("unknown pid "+pidId);
    if (pid.getIsoTp()!=null) {
      this.readPid(display, pid);
      Thread.sleep(this.getElm327().getCon().getTimeout()*5);
      if (display != null)
        showValues(display);
    }
    else
      this.monitorPid(display, pidId, frameLimit);
    
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
    ELM327 lelm = getElm327();
    lelm.sendCommand("", ".*", true);
    lelm.sendCommand("AT L1", ".*");
    lelm.send("AT CRA " + pid);
    lelm.send("AT MA");
    for (long i = 0; i < frameLimit; i++) {
      // FIXME - Pseudo request - timeout handling ...
      lelm.getCon().getResponse(null);
      if (display != null)
        showValues(display);
    }
  }
  
  /**
   * read the given Pid
   * @param display
   * @param pid
   * @throws Exception 
   */
  public void readPid(CANValueDisplay display,Pid pid) throws Exception{
    if (pid.getIsoTp()==null) {
      throw new IllegalArgumentException("Pid "+pid.getName()+"("+pid.getPid()+") is not a ISO-TP frame pid it can not be read with readPid");
    }
    String isoPid=pid.getIsoTp();
    ELM327 lelm = this.getElm327();
    // stop current communication
    lelm.sendCommand("", ".*", true);
    // make sure length is available
    lelm.sendCommand("AT L1", ".*");
    lelm.sendCommand("AT H1", "OK");
    lelm.sendCommand("AT SP6", "OK");
    // TODO - do we want to filter?
    lelm.sendCommand("AT CRA " + pid.getPid(),"OK");
    lelm.sendCommand("AT FCSH"+isoPid,"OK");
    // FIXME - this is not true for all Pids
    lelm.sendCommand("AT FCSD300000","OK");
    lelm.sendCommand("AT FCSM1","OK");
    lelm.sendCommand("AT FCSH"+isoPid,"OK");
    lelm.sendCommand("AT SH"+isoPid,"OK");
    // FIXME - this is not true for all Pids - make configurable
    // special mode 21
    lelm.sendCommand("2101",".*");
    while (lelm.getCon().getResponse(null).isValid()) {
      lelm.getCon().pause(0, 200);
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
  public void logWrite(PrintWriter pLogWriter, Packet response) {
    // if logging is enabled
    if (pLogWriter != null) {
      Date ts=new Date(response.getTimeStamp());
      pLogWriter.println(
          timeStampIsoDateFormatter.format(ts) + " " + response.getData());
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
  public void handleResponse(Packet response) {
    if (response == null)
      return;
    log(" handling response " + response.asString());
    // optionally log the response to a file
    logWrite(logWriter, response);
    // handle buffer overrun
    if (response.getData().startsWith("BUFFER")) {
      getElm327().respondToBufferOverrun();
      this.bufferOverruns++;
      return;
    }
    List<PIDResponse> pidResponses = PIDResponse.fromResponse(getElm327(), response);
    for (PIDResponse pidResponse : pidResponses) {
      if (pidResponse.pid != null)
        handleResponse(pidResponse);
    }
  }

  public abstract void handleResponse(PIDResponse pidResponse);

  public abstract void showValues(final CANValueDisplay display);
}

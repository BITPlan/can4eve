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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wf on 03.06.17.
 */

public class ConnectionImpl extends Thread implements Connection {
  boolean handleResponses;
  boolean sendLineFeed = true;
  boolean receiveLineFeed = false;
  ResponseHandler responseHandler;
  String title = "con";

  InputStream input;
  OutputStream output;
  long timeOut = DEFAULT_TIMEOUT;
  Log log;
  boolean running = false;
  long pauses = 0;

  public static long DEFAULT_TIMEOUT = 250; // 250 millisecs
  final static int BUFFER_SIZE = 8192;

  // regular expression to check that an elm327 response is "complete"
  // while there is no line feed received
  final static Pattern snippetPattern = Pattern.compile(
      "(.*\\>)|STOPPED|BUFFER FULL|CAN ERROR|\\<DATA ERROR|OUT OF MEMORY",
      Pattern.MULTILINE | Pattern.DOTALL);

  LinkedBlockingQueue<Packet> responses = new LinkedBlockingQueue<Packet>();

  public boolean isSendLineFeed() {
    return sendLineFeed;
  }

  public void setSendLineFeed(boolean sendLineFeed) {
    this.sendLineFeed = sendLineFeed;
  }

  public boolean isReceiveLineFeed() {
    return receiveLineFeed;
  }

  public void setReceiveLineFeed(boolean receiveLineFeed) {
    this.receiveLineFeed = receiveLineFeed;
  }

  @Override
  public InputStream getInput() {
    return input;
  }

  @Override
  public void setInput(InputStream input) {
    this.input = input;
  }

  @Override
  public OutputStream getOutput() {
    return output;
  }

  @Override
  public void setOutput(OutputStream output) {
    this.output = output;
  }

  @Override
  public void run() {
    // prepare infinite loop
    Reader in = null;
    try {
      in = new InputStreamReader(this.getInput(), "UTF-8");
      BufferedReader reader = new BufferedReader(in);
      final char[] buffer = new char[BUFFER_SIZE];

      String line = "";
      running = true;
      if (this.isDebug()) {
        log(String.format("%s ready for reading", this.getTitle()));
      }
      while (running) {
        if (this.receiveLineFeed) {
          line = reader.readLine();
          if (line.length() > 0) {
            this.addResponseLine(line);
          }
        } else {
          // while initializing there may be no linefeeds yet
          // we have to read the stream char for char ...
          // is there data in the input stream?
          if (!reader.ready()) {
            // wait a very small of time (0,2 milliseconds) - to not overload
            // the CPU too much with
            // busy polling
            int microsecs = 200;
            pause(0, microsecs);
            pauses += microsecs;
          } else {
            int readCount = reader.read(buffer);
            if (readCount > 0) {
              StringBuffer strBuf = new StringBuffer();
              strBuf.append(buffer, 0, readCount);
              String snippet = strBuf.toString();
              if (isDebug()) {
                log(String.format("read %3d char '%s'", readCount, snippet));
              }
              addSnippet(snippet);
            }
          }
        }
      }
    } catch (Throwable th) {
      handle("run failed", th);
    }
  }

  String response = "";
  private WatchDog watchDog;
  private Restartable restarter;
  public static int WATCHDOG_TIMEOUT=2000; // how long do we wait if no communication is happening

  /**
   * we are not in receiveLineFeed Mode and might receive any kind of snippets
   * that might be just part of a reponse or span multiple responses - we have
   * to look at the snippet and split it up into responses
   * 
   * @param snippet
   */
  public void addSnippet(String snippet) {
    response += snippet;
    Matcher m = snippetPattern.matcher(response);
    boolean responseComplete = m.find();
    if (responseComplete) {
      if (isDebug()) {
        log(String.format("complete response is '%s' with %2d groups",
            response.replace("\r\n", "|CRLF|"), m.groupCount()));
      }
      synchronized (this) {
        addResponseLine(response);
        response = "";
      }
    }
  }

  /**
   * add the given string to the responses
   * 
   * @param line
   */
  public void addResponseLine(String line) {
    // ignore prompts
    if (line.trim().equals(">"))
      return;
    if (isDebug()) {
      log(String.format("response for %s is: '%s' (%3d)", title, line,line.length()));
    }
    Packet response = new PacketImpl();
    response.setData(line);
    response.updateTimeStamp();
    response.setResponse(response);
    responses.add(response);
    // tell the watchDog all is well
    if (watchDog!=null)
      watchDog.ping(this);
    if (this.handleResponses && this.responseHandler!=null) {
      this.responseHandler.handleResponse(response);
    }
    
  }

  /**
   * pause for the given number of milliseconds
   *
   * @param millis
   */
  public void pause(long millis, int nanos) {
    try {
      Thread.sleep(millis, nanos);
    } catch (InterruptedException e) {
      // ignore
    }
  }

  @Override
  public void halt() {
    running = false;
  }

  @Override
  public void close() throws IOException {
    halt();
    if (getInput() != null)
      getInput().close();
    if (getOutput() != null) {
      getOutput().close();
    }
  }

  @Override
  public void connect(Connection conn) throws IOException {
    throw new RuntimeException("not implemented");
  }

  @Override
  public void connect(Socket socket) throws IOException {
    setInput(socket.getInputStream());
    setOutput(socket.getOutputStream());
  }

  @Override
  public void connect(File device) throws IOException {
    setInput(new FileInputStream(device));
    setOutput(new FileOutputStream(device));
  }

  @Override
  public Packet output(String msg) throws IOException {
    Packet result = new PacketImpl();
    result.setData(msg);
    if (this.getOutput() != null) {
      if (msg != null) {
        if (isDebug()) {
          log(String.format("sending %3d chars: '%s' %s", msg.length(), msg,
              sendLineFeed ? "CRLF" : ""));
        }
        if (sendLineFeed)
          msg += "\r\n";
        // this.currentOutput = msg;
        getOutput().write(msg.getBytes());
        getOutput().flush();
        result.updateTimeStamp();
      }
    }
    return result;
  }

  @Override
  public Packet send(String msg) throws IOException {
    Packet request = output(msg);
    return getResponse(request);
  }

  @Override
  public long getTimeout() {
    return timeOut;
  }

  @Override
  public void setTimeout(long timeout) {
    this.timeOut = timeout;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public Packet getResponse(Packet request) {
    Packet response =null;

    try {
      response = responses.poll(timeOut, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      // ignore - handle like timeOut
    }
    if (response == null) {
      response = new PacketImpl();
      response.setResponse(response);
    } else {
      response.setValid(true);
    }
    if (this.isDebug()) {
      String rqdata="-";
      if (request!=null)
        rqdata=request.getData();
      String msg=String.format("%s: '%s'->'%s' %s" ,title,rqdata,response.getData(),response.isValid()?"!":"x");
      log(msg);
    }
    response.setRequest(request);
    if (request != null)
      request.setResponse(response);

    return response;
  }

  @Override
  public boolean isDebug() {
    return log != null;
  }

  @Override
  public void log(String msg) {
    if (log != null) {
      log.log(msg);
    }
  }

  @Override
  public void setLog(Log log) {
    this.log = log;
  }

  @Override
  public Log getLog() {
    return log;
  }

  @Override
  public void handle(String msg, Throwable th) {
    LogImpl.handle(log, msg, th);
  }

  @Override
  public ResponseHandler getResponseHandler() {
    return responseHandler;
  }

  @Override
  public void setResponseHandler(ResponseHandler handler) {
    responseHandler = handler;
  }

  @Override
  public boolean isHandleResponses() {
    return this.handleResponses;
  }

  @Override
  public void setHandleResponses(boolean handleResponses) {
    this.handleResponses = handleResponses;
  }

  @Override
  public void setWatchDog(WatchDog watchDog) {
    this.watchDog=watchDog;
  }

  @Override
  public int getWatchDogTimeOutMSecs() {
    return WATCHDOG_TIMEOUT;
  }

  @Override
  public void restart() throws Exception {
    if (restarter!=null)
      restarter.restart();
  }

  @Override
  public void setRestarter(Restartable restarter) {
    this.restarter=restarter;
  }

}

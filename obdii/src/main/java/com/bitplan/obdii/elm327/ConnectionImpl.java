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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.net.Socket;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import com.bitplan.obdii.Connection;
import com.bitplan.obdii.Response;
import com.bitplan.obdii.ResponseHandler;

/**
 * implementation of a connection between two streams
 * 
 * @author wf
 *
 */
public abstract class ConnectionImpl extends Thread implements Connection {
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.obdii");
  final static Pattern snippetComplete = Pattern.compile(
      "(.*\\>)|STOPPED|BUFFER FULL|CAN ERRROR",
      Pattern.MULTILINE | Pattern.DOTALL);

  protected boolean sendLineFeed = true;
  private boolean receiveLineFeed = false;
  public boolean debug = false;
  protected boolean echo = false;
  public String response = null;
  // default buffer sizes is 1024 responses
  public CircularFifoQueue<Response> responses = new CircularFifoQueue<Response>(
      1024);
  // default timeout
  long timeout = 250;
  int responseWaitusecs = 500;
  int sendCount = 0;
  ResponseHandler responseHandler;

  // private StreamBuffer ringBuffer;
  // private CircularByteBuffer ringBuffer;
  InputStream input;
  OutputStream output;
  private String currentOutput;
  boolean handleResponses = false;
  private boolean running;

  /**
   * create a connection
   */
  public ConnectionImpl() {
    // buffer all data in a ring Buffer
    // ringBuffer = new StreamBuffer();
    // ringBuffer =new CircularByteBuffer(CircularByteBuffer.INFINITE_SIZE);
    // ringBuffer.setMaxBufferElements(1024);
    // input = ringBuffer.getInputStream();
    // output = ringBuffer.getOutputStream();
    setResponseHandler(this);
  }

  /**
   * @return the timeout
   */
  public long getTimeout() {
    return timeout;
  }

  /**
   * @param timeout
   *          the timeout to set
   */
  public void setTimeout(long timeout) {
    this.timeout = timeout;
  }

  public ResponseHandler getResponseHandler() {
    return responseHandler;
  }

  public void setResponseHandler(ResponseHandler responseHandler) {
    this.responseHandler = responseHandler;
  }

  /**
   * @return the input
   */
  public InputStream getInput() {
    return input;
  }

  @Override
  public void setInput(InputStream input) {
    this.input = input;
  }

  /**
   * @return the output
   */
  public OutputStream getOutput() {
    return output;
  }

  @Override
  public void setOutput(OutputStream output) {
    this.output = output;
  }

  public boolean isHandleResponses() {
    return handleResponses;
  }

  public void setHandleResponses(boolean handleResponses) {
    this.handleResponses = handleResponses;
  }

  @Override
  public void connect(Connection conn) throws IOException {
    InputStream tmpInput = conn.getInput();
    conn.setInput(input);
    setInput(tmpInput);
    /*
     * Since we are using circular buffers we don't need this any more
     * 
     * OutputStream tmpOutput = conn.getOutput();
     * conn.setOutput(output);
     * setOutput(tmpOutput);
     * 
     * StreamConnector therehere = new StreamConnector(conn.getInput(),
     * getOutput());
     * StreamConnector herethere = new StreamConnector(getInput(),
     * conn.getOutput());
     * therehere.setDaemon(true);
     * therehere.start();
     * herethere.setDaemon(true);
     * herethere.start();
     */
  }

  @Override
  public void connect(Socket socket) throws IOException {
    setInput(socket.getInputStream());
    setOutput(socket.getOutputStream());
  }

  /**
   * run
   */
  public void run() {
    try {
      assert (getInput() != null);
      assert (getOutput() != null);
      final int bufferSize = 8192;
      final char[] buffer = new char[bufferSize];
      try {
        // prepare infinite loop
        Reader in = new InputStreamReader(this.getInput(), "UTF-8");
        BufferedReader reader = new BufferedReader(in);
        String line = "";
        boolean responseComplete = false;
        running = true;
        log("ready for reading");
        while (running) {
          // Linefeeds make life easier ...
          if (this.isReceiveLineFeed()) {
            line = reader.readLine();
            if (line!=null)
              log(" received line '" + line + "'");
            responseComplete = (line != null
                && !(line.equals("") || line.equals(">")));
          } else {
            // while initializing there may be no linefeeds yet
            // we have to read the stream char for char ...
            // is there data in the input stream?
            if (!reader.ready()) {
              // int available = this.getInput().available();
              // if there is no data
              // if (available <= 0) {
              // wait a very small of time (0,2 milliseconds) - to not overload
              // the CPU too much with
              // busy polling
              pause(0, 200);
            } else {
              // int readCount = in.read(buffer, 0, available);
              int readCount = reader.read(buffer);
              if (readCount < 1) {
                LOGGER.log(Level.WARNING, "readCount " + readCount);
              } else {
                StringBuffer strBuf = new StringBuffer();
                strBuf.append(buffer, 0, readCount);
                String snippet = strBuf.toString();
                log("received snippet " + snippet.replace("\r\n", "|CRLF|"));
                line += snippet;
                // for now we have to wait for the prompt character
              } // if readCount <1
            } // reader ready
            responseComplete = checkSnippetComplete(line, debug);
          } // if receiveLineFeed
          if (responseComplete) {
            Response currentResponse = new Response(currentOutput, line);
            responses.add(currentResponse);
            // remove CR/LF and Chevron
            String orgline = line;
            line = line.trim().replaceAll("(\\r|\\n|>)", "");
            log("received response " + responses.size() + ":'" + orgline
                + "' trimmed '" + line + "'");
            // TODO need clear responses or use cyclic history buffer for this
            if (responseHandler != null && handleResponses) {
              responseHandler.handleStringResponse(line, new Date());
            }
            synchronized (this) {
              response = line;
              // signal that a response is available
              this.notify();
              // reset line
              line = "";
            }
          } // if responseComplete
        } // while
      } catch (Exception e) {
        handle(e);
      }
    } finally {

    }
  }

  @Override
  public void halt() {
    running = false;
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
   * check that the given line is a complete snippet
   * 
   * @param line
   *          - the line to check
   * @return - whether the snippet is complete
   */
  public static boolean checkSnippetComplete(String line, boolean debug) {
    // or BUFFER FULL OR STOPPED ...
    Matcher m = snippetComplete.matcher(line);
    boolean responseComplete = m.find();
    if (responseComplete) {
      if (debug)
        LOGGER.log(Level.INFO,
            "response Complete with " + m.groupCount() + " groups");
    }
    return responseComplete;
  }

  /**
   * handle the given Throwable
   * 
   * @param th
   */
  protected void handle(Throwable th) {
    LOGGER.log(Level.WARNING, "issue with connection reading error="
        + th.getClass().getSimpleName() + " message='" + th.getMessage() + "'");
    StringWriter sw = new StringWriter();
    th.printStackTrace(new PrintWriter(sw));
    LOGGER.log(Level.INFO, sw.toString());
  }

  /**
   * handle the response
   */
  @Override
  public void handleStringResponse(String response, Date timeStamp) {
    if (echo) {
      try {
        output(response);
      } catch (IOException e) {
        handle(e);
      }
    }
  }

  @Override
  public void output(String msg) throws IOException {
    if (this.getOutput() != null) {
      if (msg != null) {
        if (sendLineFeed)
          msg += "\r\n";
        this.currentOutput = msg;
        getOutput().write(msg.getBytes());
        getOutput().flush();
      }
    }
  }

  @Override
  public String send(String msg) throws IOException {
    sendCount++;
    log(String.format("sending %4d '%s'", sendCount, msg));
    output(msg);
    String lResponse = this.getResponse();
    return lResponse;
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
  public String getResponse() {
    // pause(0, responseWaitusecs);
    long start = System.nanoTime();
    String result = null;
    try {
      synchronized (this) {
        if (response == null) {
          this.log("waiting");
          this.wait(timeout);
        }
        result = response;
        response = null;
      }
    } catch (InterruptedException e) {
      handle(e);
    }
    long stop = System.nanoTime();
    long timeMs = (stop - start) / 1000000;
    log("Received response '" + result + "' after " + timeMs + " ms");
    return result;
  }

  public boolean isReceiveLineFeed() {
    return receiveLineFeed;
  }

  public void setReceiveLineFeed(boolean receiveLineFeed) {
    this.receiveLineFeed = receiveLineFeed;
  }

}

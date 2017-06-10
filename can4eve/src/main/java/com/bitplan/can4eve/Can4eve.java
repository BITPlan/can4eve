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
package com.bitplan.can4eve;


import com.bitplan.elm327.ConnectionForwarder;
import com.bitplan.elm327.ELM327;
import com.bitplan.elm327.ELM327Impl;
import com.bitplan.elm327.LogImpl;
import com.bitplan.elm327.SerialImpl;

import org.kohsuke.args4j.Option;

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by wf on 05.06.17.
 */

public class Can4eve extends Main {

  @Option(name = "--port", aliases = {
    "--portnumber" }, usage = "port\nthe port to connect to")
  int portNumber = 7000;

  @Option(name = "-b", aliases = {
    "--baud" }, usage = "port\nthe baud rate for a serial connection")
  int baudRate = 115200*2;

  @Option(name= "-f", aliases={"--forward"}, usage="forward the local OBDII connection")
  boolean forward=false;

  @Option(name= "-dv", aliases={"--device"}, usage="the device for the local OBDII connection")
  String device;

  @Option(name= "-lf", aliases={"--logfile"}, usage="log to the logfile at the given path")
  String logFilePath=null;


  /**
   * constructor
   */
  public Can4eve() {
    super.name="can4eve";
  }

  /**
   * start forwarding
   * @param pDevice
   * @param pPortNumber
   * @param pBaudRate
   * @throws Exception if something goes wrong
   */
  public void forward(String pDevice, int pPortNumber, int pBaudRate) throws Exception {
    SerialImpl con = new SerialImpl();
    if (debug) {
      con.setLog(new LogImpl());
      if (this.logFilePath!=null) {
        con.getLog().setFile(new File(this.logFilePath),false);
      }
    }
    con.connect(pDevice, pBaudRate);
    con.start();
    ConnectionForwarder forwarder=new ConnectionForwarder();
    ELM327 elm = new ELM327Impl();
    elm.setCon(con);
    elm.initOBD2();
    // elm.initOBD2();
    forwarder.setLog(elm.getLog());
    forwarder.createServerSocket(pPortNumber);
    forwarder.startServer(elm.getCon());
    ServerSocket serverSocket = forwarder.getServerSocket();
    Socket clientSocket=new Socket("localhost",serverSocket.getLocalPort());
    forwarder.getServerThread().join();
  }

  @Override
  public void work() throws Exception {
    if (this.showVersion || this.debug)
      showVersion();
    if (this.showHelp) {
      showHelp();
    } else {
      if (forward) {
        forward(device,portNumber,baudRate);
      } else {
        usage("no operation mode selected");
      }
    }
  }

  /**
   * main routine
   *
   * @param args
   */
  public static void main(String[] args) {
    Can4eve can4eve = new Can4eve();
    int result = can4eve.maininstance(args);
    if (!testMode)
      System.exit(result);
  }


}

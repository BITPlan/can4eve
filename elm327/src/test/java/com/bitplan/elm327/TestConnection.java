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

import org.junit.Test;

import com.bitplan.elm327.Config.ConfigMode;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.*;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by wf on 03.06.17.
 */

/**
 * test different connection options
 * 
 * @author wf
 *
 */
public class TestConnection {
  static Connection testConnection;

  /**
   * get the test Connection
   * 
   * @return
   * @throws FileNotFoundException
   */
  public Connection getTestConnection() throws FileNotFoundException {
    if (testConnection == null) {
      Config config = Config.getInstance(ConfigMode.Test);
      SerialImpl con = new SerialImpl();
      con.setLog(new LogImpl());
      con.connect(config.serialDevice, config.baudRate);
      con.start();
      testConnection = con;
    }
    return testConnection;
  }

  @Test
  public void testConfig() throws FileNotFoundException {
    if (Config.getInstance(ConfigMode.Test) == null) {
      System.err.println(
          "Warning: Device configuration for tests missing - only tests with simulator will work!");
      System.err.println(
          "Please see http://can4eve.bitplan.com/index.php/JUnit_tests");
      System.err.println("for how to configure the test environment.");
      System.err.println();
      System.err.println("you might want to create a file "
          + Config.getConfigFile(ConfigMode.Test).getAbsolutePath());
      System.err.println("with json content like the following example");
      Config config = new Config();
      config.baudRate = 115200;
      config.serialDevice = "cu.usbserial-113010822821";
      System.err.println(config.asJson());
    }
  }

  @Test
  public void testConnection() throws IOException, InterruptedException {
    Config config = Config.getInstance(ConfigMode.Test);
    if (config != null) {
      Connection con = getTestConnection();
      Packet p = con.send("AT @1");
      assertNotNull("A packet should be returned", p);
      if (config.debug)
        System.out.println(String.format("%5d msecs", p.getResponseTime()));
      assertNotNull("The data of the packet should be set", p.getData());
      if (config.debug)
        System.out.println("data: '" + p.getData().replace("\r", "\r\n") + "' ("
            + p.getData().length() + ")");
    }
  }

  @Test
  public void testELM327() throws Exception {
    Config config = Config.getInstance(ConfigMode.Test);
    if (config != null) {
      ELM327 elm = new ELM327Impl();
      elm.setCon(getTestConnection());
      elm.initOBD2();
      if (config.debug) {
        System.out.println(String.format(
            "id: %s\ndescription:%s \ndevice id: %s\nhardware id: %s\nfirmware id:%s\nvoltage:%s\n",
            elm.getId(), elm.getDescription(), elm.getDeviceId(),
            elm.getHardwareId(), elm.getFirmwareId(), elm.getCarVoltage()));
      }
    }
  }

  @Test
  public void testServer() throws Exception {
    Config config = Config.getInstance(ConfigMode.Test);
    if (config != null) {
      ConnectionForwarder forwarder = new ConnectionForwarder();
      ELM327 elm = new ELM327Impl();
      elm.setCon(getTestConnection());
      forwarder.setLog(elm.getLog());
      forwarder.createServerSocket(ConnectionForwarder.DEFAULT_PORT);
      forwarder.startServer(elm.getCon());
      ServerSocket serverSocket = forwarder.getServerSocket();
      Socket clientSocket = new Socket("localhost",
          serverSocket.getLocalPort());
      elm.getCon().connect(clientSocket);
      elm.initOBD2();
      if (config.debug) {
        System.out.println(String.format(
            "id: %s\ndescription:%s \ndevice id: %s\nhardware id: %s\nfirmware id:%s\nvoltage:%s\n",
            elm.getId(), elm.getDescription(), elm.getDeviceId(),
            elm.getHardwareId(), elm.getFirmwareId(), elm.getCarVoltage()));
      }
    }
  }

  /**
   * get an ELM327 connection via WIFI
   * 
   * @param config
   * @return the ELM327 interface instance
   * @throws Exception
   */
  public ELM327 getWifi(Config config) throws Exception {
    // V-Link Wifi socket
    Socket clientSocket = new Socket(config.hostname, config.port);
    ELM327 elm = new ELM327Impl();
    Connection con = elm.getCon();
    con.setLog(new LogImpl());
    con.connect(clientSocket);
    con.start();
    elm.initOBD2(config.timeout);
    return elm;
  }

  /**
   * test connection via WIFI device
   */
  @Test
  public void testVLink() throws Exception {
    Config config = Config.getInstance(ConfigMode.Test);
    if (config != null && config.hostname != null) {
      ELM327 elm = getWifi(config);
      System.out.println(String.format(
          "id: %s\ndescription:%s \ndevice id: %s\nhardware id: %s\nfirmware id:%s\nvoltage:%s\n",
          elm.getId(), elm.getDescription(), elm.getDeviceId(),
          elm.getHardwareId(), elm.getFirmwareId(), elm.getCarVoltage()));
    }
  }
}

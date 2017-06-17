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

import java.io.IOException;

import static org.junit.Assert.*;

import java.net.ServerSocket;
import java.net.Socket;
/**
 * Created by wf on 03.06.17.
 */

public class TestConnection {
    static Connection testConnection;

    /**
     * get the test Connection
     * @return
     */
    public Connection getTestConnection() {
        if (testConnection == null) {
            SerialImpl con = new SerialImpl();
            con.setLog(new LogImpl());
            con.connect("cu.usbserial-113010822821", 115200 * 2);
            con.start();
            testConnection = con;
        }
        return testConnection;
    }
    
   

    @Test
    public void testConnection() throws IOException, InterruptedException {
        Connection con=getTestConnection();
        Packet p = con.send("AT @1");
        assertNotNull("A packet should be returned", p);
        System.out.println(String.format("%5d msecs", p.getResponseTime()));
        assertNotNull("The data of the packet should be set", p.getData());
        System.out.println("data: '" + p.getData().replace("\r", "\r\n") + "' (" + p.getData().length() + ")");
    }

    @Test
    public void testELM327() throws Exception {
        ELM327 elm = new ELM327Impl();
        elm.setCon(getTestConnection());
        elm.initOBD2();
        System.out.println(String.format("id: %s\ndescription:%s \ndevice id: %s\nhardware id: %s\nfirmware id:%s\nvoltage:%s\n",
                elm.getId(),elm.getDescription(),elm.getDeviceId(),elm.getHardwareId(),elm.getFirmwareId(),elm.getCarVoltage()));

    }

    @Test
    public void testServer() throws Exception {
        ConnectionForwarder forwarder=new ConnectionForwarder();
        ELM327 elm = new ELM327Impl();
        elm.setCon(getTestConnection());
        // elm.initOBD2();
        forwarder.setLog(elm.getLog());
        forwarder.createServerSocket(ConnectionForwarder.DEFAULT_PORT);
        forwarder.startServer(elm.getCon());
        ServerSocket serverSocket = forwarder.getServerSocket();
        Socket clientSocket=new Socket("localhost",serverSocket.getLocalPort());
        elm.getCon().connect(clientSocket);
        elm.initOBD2();
        System.out.println(String.format("id: %s\ndescription:%s \ndevice id: %s\nhardware id: %s\nfirmware id:%s\nvoltage:%s\n",
                elm.getId(),elm.getDescription(),elm.getDeviceId(),elm.getHardwareId(),elm.getFirmwareId(),elm.getCarVoltage()));

    }

    public ELM327 getWifi() throws Exception {
        // V-Link Wifi socket
        String ip="192.168.0.10";
        int port=35000;
        int timeout=40000;
        ip="2.0.0.45";
        port=7000;
        timeout=250;
        Socket clientSocket=new Socket(ip,port);
        ELM327 elm = new ELM327Impl();
        Connection con=elm.getCon();
        con.setLog(new LogImpl());
        con.connect(clientSocket);
        con.start();
        elm.initOBD2(timeout);
        return elm;
    }
    /**
     * test connection via WIFI device
     */
    @Test
    public void testVLink() throws Exception {
       ELM327 elm=getWifi();
        System.out.println(String.format("id: %s\ndescription:%s \ndevice id: %s\nhardware id: %s\nfirmware id:%s\nvoltage:%s\n",
          elm.getId(),elm.getDescription(),elm.getDeviceId(),elm.getHardwareId(),elm.getFirmwareId(),elm.getCarVoltage()));

    }
}

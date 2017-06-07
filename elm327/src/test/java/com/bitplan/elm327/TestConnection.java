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
        Connection con=new ConnectionImpl();
        con.setLog(new LogImpl());
        elm.setCon(con);
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

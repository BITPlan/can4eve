package com.bitplan.elm327;

import java.io.IOException;

/**
 * Created by wf on 03.06.17.
 *
 */

public class ELM327Impl implements ELM327 {
    public static long INIT_TIMEOUT = 150; // intialization operates with a faster timeout
    Connection con;

    public Connection getConnection() {
        return con;
    }

    public void setConnection(Connection con) {
        this.con = con;
    }

    boolean header;
    boolean length;

    // id as returned by AT I
    String id;

    // description as returned by AT @1
    String description;

    // device id as retuned by AT @2
    String deviceId;

    String carVoltage;
    String hardwareId;
    String firmwareId;


    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getDeviceId() {
        return deviceId;
    }

    public String getCarVoltage() {
        return carVoltage;
    }

    public String getHardwareId() {
        return hardwareId;
    }

    public String getFirmwareId() {
        return firmwareId;
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

    protected void showDebug(Packet p) {
        if (this.isDebug()) {
            String data = p.getRawData();
            int len=0;
            if (data!=null)
                len=data.length();
            String display = data;
            if (!con.isReceiveLineFeed()) {
                if (display!=null)
                    display = display.
                        replace("\r", "\r\n");
            }
            String msg = String.format("%3d msecs - data: '%s' (%3d)", p.getResponseTime(), display, len);
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
    }

    /**
     * sent the given message
     *
     * @param msg - the message to send
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
     * @param response
     * @param expectedResponse
     * @param allowNull
     * @throws Exception
     */
    private void checkResponse(String command, Packet response,
                               String expectedResponse, boolean allowNull) throws Exception {
        String data=response.getData();
        if (allowNull && data == null)
            return;
        if (data == null || (!data.matches(expectedResponse))) {
            throw new Exception("sendCommand '" + command + "' failed, expected '"
                    + expectedResponse + "' but got '" + response.getData() + "'");
        }
    }

    /**
     * check the given response
     * @param command
     * @param response
     * @param expectedResponse
     * @throws Exception
     */
    private void checkResponse(String command, Packet response,
                               String expectedResponse) throws Exception {
        checkResponse(command, response, expectedResponse, false);
    }


    @Override
    public void reinitCommunication(long timeOutMsecs) throws Exception {
        // keep the old timeout
        long timeout = con.getTimeout();
        // operate with a much lower timeout to quickly reinitialize
        con.setTimeout(timeOutMsecs);
        // assume the device has not been configured to return linefeeds (yet)
        con.setReceiveLineFeed(false);
        con.setSendLineFeed(true);
        // send a CR to stop current monitoring command like STM
        send("");
        // turn the ECHO off
        send("AT E0");
        // turn the Line feed on
        send("AT L1");
        con.setReceiveLineFeed(true);
        // restore old timeout value
        con.setTimeout(timeout);
    }

    /**
     * identify this device
     * set id, description
     * @throws Exception
     */
    public void identify() throws Exception {
        Packet r = sendCommand("AT I","ELM327 v.*");
        id=r.getData();
        r= send("AT @1");
        description=r.getData();
        r=send("AT @2");
        deviceId=r.getData();
        carVoltage = sendCommand("AT RV", ".*").getData(); // Car VoltageAT R
        if (description.startsWith("SCANTOOL")) {
            hardwareId = sendCommand("STDI", ".*").getData(); // Hardware ID string
            firmwareId = sendCommand("STI", ".*").getData(); // Firmware ID
        }
    }

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
        sendCommand("AT CAF0", "OK"); // switch off automatic formatting
        //elm327.setHandleResponses(true);
    }


    @Override
    public boolean isDebug() {
        return con.isDebug();
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
}

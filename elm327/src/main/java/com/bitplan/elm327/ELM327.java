package com.bitplan.elm327;

import java.io.IOException;

/**
 * communication to ELM327 devices
 * see e.g. https://www.sparkfun.com/datasheets/Widgets/ELM327_AT_Commands.pdf
 */
public interface ELM327 extends Debugable {
    public Connection getCon() ;
    public void setCon(Connection con);

    /**
     * reinitialize the communication to the ELM327 chip
     */
    public void reinitCommunication(long timeOutMsecs) throws Exception;

    public void identify() throws Exception;

    public void initOBD2() throws Exception;

    public void initOBD2(long timeOutMsecs) throws Exception;

    /**
     * get the ID of the Device as returned by the ATI command
     * @return - the device id e.g. ELM327 v1.3a
     */
    public String getId();

    /**
     * get the description of the Device as returned by the AT @1 command
     * @returns the description e.g SCANTOOL.NET LLC
     */
    public String getDescription();

    /**
     * get the id of the device as returned by the AT @2 command
     * @return the device id
     */
    public String getDeviceId();

    public String getCarVoltage();

    public String getHardwareId();

    public String getFirmwareId();

    public boolean isHeader();

    public void setHeader(boolean header);

    public boolean isLength();

    public void setLength(boolean length);
}

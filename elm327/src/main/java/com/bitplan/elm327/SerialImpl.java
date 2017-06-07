package com.bitplan.elm327;

/**
 * Created by wf on 03.06.17.
 */

import java.io.IOException;
import java.util.Enumeration;

import purejavacomm.CommPortIdentifier;
import purejavacomm.PortInUseException;
import purejavacomm.SerialPort;
import purejavacomm.UnsupportedCommOperationException;

import static purejavacomm.CommPortIdentifier.*;

/**
 * Connection via serial port
 * // http://www.drdobbs.com/jvm/java-communications-without-jni/240164940
 */
public class SerialImpl extends ConnectionImpl {
    SerialPort serialPort;

    /**
     * connect me to the given device with the given baud rate
     *
     * @param device - the device to connect to
     * @param baud   - the baud rate to use
     */
    public void connect(String device, int baud) {
        CommPortIdentifier serialId = getSerialPortIdentifier(device);
        if (serialId == null) {
            throw new RuntimeException("can't find device " + device);
        }
        try {
            serialPort = (SerialPort) serialId.open(this.getClass().getSimpleName(), (int) this.timeOut);
        } catch (PortInUseException e) {
            handle("device is in use", e);
            throw new RuntimeException("device " + device + " is in use");
        }
        int dataBits = SerialPort.DATABITS_8;
        int stopBits = SerialPort.STOPBITS_1;
        int parity = SerialPort.PARITY_NONE;
        try {
            serialPort.setSerialPortParams(baud, dataBits, stopBits, parity);
        } catch (UnsupportedCommOperationException e) {
            handle("can not set serial params", e);
            throw new RuntimeException("can not set serial params for device "+device);
        }
        try {
            this.setInput(serialPort.getInputStream());

            this.setOutput(serialPort.getOutputStream());
        } catch (IOException e) {
            handle("io exception",e);
            throw new RuntimeException("io problem for device "+device);
        }

    }

    /**
     * get the Serial Port identifier for the given device name
     *
     * @param device
     * @return the serial Port Identifier
     */
    public CommPortIdentifier getSerialPortIdentifier(String device) {
        CommPortIdentifier portid = null;
        Enumeration<CommPortIdentifier> portEnum = getPortIdentifiers();
        while (portEnum.hasMoreElements()) {
            portid = (CommPortIdentifier) portEnum.nextElement();
            if (this.isDebug()) {
                log("found serial port: " + portid.getName());
            }
            if (portid.getName().equals(device))
                return portid;
        }
        return portid;
    }
}
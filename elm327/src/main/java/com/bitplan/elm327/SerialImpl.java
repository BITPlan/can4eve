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

import static purejavacomm.CommPortIdentifier.getPortIdentifiers;

/**
 * Created by wf on 03.06.17.
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bitplan.elm327.util.OSCheck;
import com.bitplan.elm327.util.OSCheck.OSType;

import purejavacomm.CommPortIdentifier;
import purejavacomm.PortInUseException;
import purejavacomm.SerialPort;
import purejavacomm.UnsupportedCommOperationException;

/**
 * Connection via serial port
 * // http://www.drdobbs.com/jvm/java-communications-without-jni/240164940
 */
public class SerialImpl extends ConnectionImpl {
    SerialPort serialPort;
    static Map<String,SerialPort> openPorts=new HashMap<String,SerialPort>();

    /**
     * get the list of serial ports
     * @param filter - filter the relevant ones
     * @return - the list of serial ports
     */
    public List<String> getSerialPorts(boolean filter) {
      List<String> serialPorts=new ArrayList<String>();
      Enumeration<CommPortIdentifier> portEnum = getPortIdentifiers();
      OSType os = OSCheck.getOperatingSystemType();
      while (portEnum.hasMoreElements()) {
          CommPortIdentifier portid = (CommPortIdentifier) portEnum.nextElement();
          String portName=portid.getName();
          switch (os) {
          case MacOS:
            if (!portName.startsWith("cu"))
              continue;
            break;
          }
          serialPorts.add(portName);
      }
      return serialPorts;
    }
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
          String portName=serialId.getName();
          if (openPorts.containsKey(portName)) {
            SerialPort openPort = openPorts.get(portName);
            openPort.close();
            openPorts.remove(portName);
          }
          serialPort = (SerialPort) serialId.open(this.getClass().getSimpleName(), (int) this.timeOut);
          openPorts.put(portName,serialPort);
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
     * close this connection
     */
    public void close() {
      serialPort.close();
      openPorts.remove(serialPort);
    }
    
    /**
     * close all open serial ports
     */
    public static void closeAll() {
      for (SerialPort serialPort:openPorts.values()) {
        serialPort.close();
      }
      openPorts.clear();
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
    
    private static SerialImpl instance=null;
    /**
     * singleton for SerialImpl
     * @return the instance
     */
    public static SerialImpl getInstance() {
      if (instance==null) {
        instance=new SerialImpl();
      }
      return instance;
    }
}
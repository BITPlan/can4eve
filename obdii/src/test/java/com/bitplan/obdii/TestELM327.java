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
package com.bitplan.obdii;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;

import com.bitplan.can4eve.CANInfo;
import com.bitplan.can4eve.CANValue;
import com.bitplan.can4eve.CANValue.DoubleValue;
import com.bitplan.can4eve.Pid;
import com.bitplan.elm327.Config;
import com.bitplan.elm327.Connection;
import com.bitplan.elm327.LogImpl;
import com.bitplan.elm327.Packet;
import com.bitplan.obdii.elm327.ELM327;
import com.bitplan.obdii.elm327.ELM327SimulatorConnection;
import com.bitplan.obdii.elm327.ElmSimulator;
import com.bitplan.triplet.OBDTriplet;

/**
 * Test ELM327 communication
 * 
 * @author wf
 *
 */
public class TestELM327 extends TestOBDII {

  public static boolean debug = false;
  public static boolean simulated = true;
  // the vehicle under test

  public int SIMULATOR_TIMEOUT =50; // Simulator should be quick 2 msecs is feasible

  protected static Logger LOGGER = Logger.getLogger("com.bitplan.obdii");
  private Socket elmSocket;
  private OBDTriplet obdTriplet;
  private TripletDisplay display;

  /**
   * get the Socket for the testVehicle
   * 
   * @return
   * @throws UnknownHostException
   * @throws IOException
   */
  public Socket getTestVehicleSocket(Config config)
      throws UnknownHostException, IOException {
    Socket elmSocket = new Socket(config.getIp(), config.getPort());
    return elmSocket;
  }

  /**
   * get the simulation
   * 
   * @return the elm327 connected to the elm327 Simulator
   * @throws Exception
   */
  public ELM327 getSimulation() throws Exception {
    ELM327 elm327 = new ELM327(getVehicleGroup());
    ElmSimulator.verbose=debug;
    ElmSimulator elm327Simulator = ElmSimulator.getInstance();
    elm327Simulator.debug=debug;
    ServerSocket serverSocket = elm327Simulator.getServerSocket();
    Socket clientSocket=new Socket("localhost",serverSocket.getLocalPort());
    Connection con=elm327.getCon();
    con.connect(clientSocket);
    if (debug)
      con.setLog(new LogImpl());
    con.start();
    Thread.sleep(20); // 10 is not enough for jenkins on capri
    assertTrue(elm327.getCon().isAlive());
    ELM327SimulatorConnection elm327SimulatorConnection = elm327Simulator.getSimulatorConnection(clientSocket);
    assertNotNull(elm327SimulatorConnection);
    Connection simcon = elm327SimulatorConnection.getCon();
    assertTrue(simcon.isAlive());
    con.setTimeout(SIMULATOR_TIMEOUT);
    simcon.setTimeout(SIMULATOR_TIMEOUT);
    return elm327;
  }

  /**
   * prepare OBD Triplet
   * 
   * @param debug
   * @throws Exception
   */
  public void prepareOBDTriplet(boolean simulated, boolean debug)
      throws Exception {
    if (simulated) {
      obdTriplet = new OBDTriplet(getVehicleGroup());
      obdTriplet.setElm327(getSimulation());
      obdTriplet.getElm327().getCon().setResponseHandler(obdTriplet);
    } else {
      Config config=Config.getInstance();
      elmSocket = getTestVehicleSocket(config);
      obdTriplet = new OBDTriplet(getVehicleGroup(),elmSocket, debug);
    }
    display = new TripletDisplay();
    obdTriplet.showDisplay(display);
    //obdTriplet.getElm327().debug = debug;
    if (!simulated)
      obdTriplet.getElm327().getCon().start();
  }

  @Test
  public void testCircularFifoQueue() throws Exception {
    CircularFifoQueue<String> cob = new CircularFifoQueue<String>();
    cob.add("A");
    cob.add("B");
    cob.add("C");
    String content = "";
    for (String s : cob) {
      content += s;
    }
    assertEquals("ABC", content);
    cob = new CircularFifoQueue<String>(3);
    cob.add("A");
    cob.add("B");
    cob.add("C");
    cob.add("D");
    content = "";
    for (String s : cob) {
      content += s;
    }
    assertEquals("BCD", content);
    assertEquals(3, cob.size());
  }

  @Test
  public void testInit() throws Exception {
    // debug=true;
    ELM327 elm327 = getSimulation();
    long start = System.nanoTime();
    Packet response = elm327.send("ATI");
    assertEquals("ELM327 v1.3a", response.getData());
    response = elm327.send("AT @1");
    assertEquals("SCANTOOL.NET LLC", response.getData());
    response = elm327.send("STDI");
    assertEquals("OBDLink SX r4.2", response.getData());
    long stop = System.nanoTime();
    long msecs = (stop - start) / 1000000;
    elm327.log("" + msecs + " msescs");
    assertTrue(msecs < 100);
  }
  
  @Test
  public void testBatteryCapacity() throws Exception {
    this.prepareOBDTriplet(simulated, debug);
    obdTriplet.initOBD();
    // obdTriplet.setDebug(true);
    obdTriplet.readPid(display,byName("BatteryCapacity"));
    Thread.sleep(200);
    assertNotNull("the battery capacity should be set",obdTriplet.batteryCapacity.getValue());
    assertEquals(new Double(44.7),obdTriplet.batteryCapacity.getValue(),0.01);
  }
  
  @Test
  public void testOBDTriplet() throws Exception {
    //debug=true;
    //PIDResponse.debug=true;
    this.prepareOBDTriplet(simulated, debug);
    obdTriplet.initOBD();
    int frameLimit = 1;
    obdTriplet.readPid(display,byName("BatteryCapacity"));
    obdTriplet.monitorPid(display, byName("Range").getPid(), frameLimit);
    obdTriplet.monitorPid(display, byName("SOC").getPid(), frameLimit);
    obdTriplet.monitorPid(display, byName("Steering_Wheel").getPid(), frameLimit);
    obdTriplet.monitorPid(display, byName("Odometer_Speed").getPid(), frameLimit);
    obdTriplet.monitorPid(display, byName("VIN").getPid(), frameLimit * 3); // 3 should be enough but somehow on travis the test then fails
    // let's wait a bit for the results 
    Thread.sleep(500);   
    //display.waitClose();
    assertNotNull("the battery capacity should be set",obdTriplet.batteryCapacity.getValue());
    assertNotNull(obdTriplet.SOC);
    assertEquals(new Double(44.8),obdTriplet.batteryCapacity.getValue(),0.1);
    assertEquals(new Double(100.0), obdTriplet.SOC.getValue(), 0.1);
    assertEquals(new Integer(95), obdTriplet.range.getValue());
    assertEquals(new Integer(721), obdTriplet.odometer.getValue());
    assertEquals(new Double(-9.5),obdTriplet.steeringWheelPosition.getValue(),0.01);
    assertEquals(new Double(2.5),obdTriplet.steeringWheelMovement.getValue(),0.01);
    assertEquals("VF31NZKYZHU900769", obdTriplet.VIN.getValue());
    obdTriplet.close();
    //display.waitClose();
    display.close();
  }

  @Test
  public void testProtocols() throws Exception {
    ELM327 elm327 = getSimulation();
    Connection lcon = elm327.getCon();
    String prots[] = { "1", "SAE J1850 PWM", "2", "SAE J1850 VPW", "3",
        "ISO 9141-2", "4", "ISO 14230-4 (KWP 5BAUD)", "5",
        "ISO 14230-4 (KWP FAST)", "6", "ISO 15765-4 (CAN 11/500)", "7",
        "ISO 15765-4 (CAN 29/500)", "8", "ISO 15765-4 (CAN 11/250)", "9",
        "ISO 15765-4 (CAN 29/250)", "A", "SAE J1939 (CAN 29/250)" };
    for (int i = 0; i < prots.length; i += 2) {
      String code = prots[i];
      String prot = prots[i + 1];
      lcon.send("AT SP " + code);
      Packet request = lcon.output("AT DP");
      Packet response = lcon.getResponse(request);
      assertEquals(prot, response.getData());
    }
  }

  @Test
  public void testSocket() throws Exception {
    prepareOBDTriplet(simulated, debug);
    obdTriplet.initOBD();
    int loops = 1;
    int frameLimit = 3;
    ConsoleDisplay cdisplay = new ConsoleDisplay();
    List<CANValue<?>> canvalues = obdTriplet.getElm327().getCANValues();
    int max = 11;
    for (int i = 0; i < loops; i++) {
      int count = 0;
      for (CANValue<?> canvalue : canvalues) {
        obdTriplet.monitorPid(display,
            canvalue.canInfo.getPid().getPid().toString(), frameLimit);
        if (debug)
          obdTriplet.showValues(cdisplay);
        if (count++ > max)
          break;
      }
      if (debug)
        obdTriplet.showValues(cdisplay);
    }
  }

 

  @Test
  public void testOBDMain() throws Exception {
    // debug = true;
    getSimulation();
    OBDMain obdMain = new OBDMain();
    OBDMain.testMode = true;
    String debugArg = "-v";
    /*int frameLimit = 50000;
    String host="pilt.bitplan.com";
    int port=7000;*/
    String host="localhost";
    int port=35000;
    int frameLimit = 100;
    String limit = "--limit=" + frameLimit;
    if (debug)
      debugArg = "--debug";
    String args[] = { debugArg, "--host="+host,"--port="+port, "--display=Swing",
        limit };
    int exitCode = obdMain.maininstance(args);
    assertEquals(0,exitCode);
  }

  @Test
  public void testLogFile() throws Exception {
    getSimulation();
    OBDMain obdMain = new OBDMain();
    OBDMain.testMode = true;
    int frameLimit = 100;
    String limit = "--limit=" + frameLimit;
    File logRoot = new File("/tmp/Ion");
    logRoot.mkdirs();
    String args[] = { "--host=localhost", "--port="+ElmSimulator.DEFAULT_PORT, "--display=Swing", limit,
        "--log=" + logRoot.getAbsolutePath() };
    obdMain.maininstance(args);
  }

  @Ignore
  public void testOBDConsole() throws Exception {
    // debug = true;
    getSimulation();
    OBDMain obdMain = new OBDMain();
    OBDMain.testMode = true;
    String debugArg = "-v";
    if (debug)
      debugArg = "--debug";
    String args[] = { debugArg, "--host=localhost","--port="+ElmSimulator.DEFAULT_PORT, "--display=Console",
        "--limit=10", "--pid=346" };
    obdMain.maininstance(args);
  }

  @Test
  public void testSTM() throws Exception {
    // simulated=false;
    // Monitor.debug=true;
    // debug=true;
    prepareOBDTriplet(simulated, debug);
    obdTriplet.initOBD();
    File logRoot = new File("src/test/data");
    File logFile = null;
    if (!simulated) {
      Config config=Config.getInstance();
      logFile = obdTriplet.logResponses(logRoot, config.getVehicleName());
    }
    int frameLimit = 150;
    obdTriplet.STMMonitor(display, obdTriplet.getCANValues(), frameLimit);
    if (debug)
      obdTriplet.showValues(new ConsoleDisplay());
    obdTriplet.close();
    display.close();
    if (!simulated) {
      assertTrue(logFile.exists());
      List<String> logLines = FileUtils.readLines(logFile, "UTF-8");
      assertTrue(logLines.size() > frameLimit);
    }
  }

  @Test
  public void testTimeOut() throws Exception {
    // boolean simulated = false;
    prepareOBDTriplet(simulated, debug);
    ELM327 elm327 = obdTriplet.getElm327();
    String cmds[] = { "ATI", "AT@1", "ATSP6" };
    // binary search
    for (String cmd : cmds) {
      Packet response;
      long workingTimeOut = 300;
      Connection con=elm327.getCon();
      con.setTimeout(workingTimeOut);
      long diff = con.getTimeout();
      while (diff > 5) {
        elm327.log("timeout=" + con.getTimeout());
        response = con.send("ATI");
        if (response != null) {
          workingTimeOut = con.getTimeout();
          con.setTimeout(con.getTimeout() / 2);
        } else {
          con.setTimeout((workingTimeOut + con.getTimeout()) / 2);
        }
        diff = Math.abs(workingTimeOut - con.getTimeout());
      }
      if (debug)
        System.out.println("timeout=" + con.getTimeout() + " for command " + cmd);
      assertTrue(con.getTimeout() < 50);
      // TODO we might also need a minimum timeout
      // assertTrue("Timeout too low - simulator broken?",con.getTimeout() >4);   
    }
  }

  /**
   * Test PID handling from a CAN log
   * 
   * @throws Exception
   */
  @SuppressWarnings("unused")
  @Test
  public void testPIDs() throws Exception {
    final int slow = 0; 
    boolean withHistory = false;
    //final int slow=100; // msecs for slower motion
    //boolean withHistory = true;
    // debug=true;
    // prepareOBDTriplet(true);
    String[] fileNames = {
        // "Triplet_2017-04-17_151817.log",
        "Triplet_2017-04-17_104141.log", "Triplet_2017-04-15_192134.log",
        "Triplet_2017-04-15_132733.log", "Triplet_2017-04-14_191849.log",
        "capture_chg_1104.txt" };
    // final int[] max = { 1800000,122000,30000, 50000, 2000000 };
    final int[] max = { 72000, 180000, 122000, 30000, 50000, 200000 };

    for (String fileName : fileNames) {
      obdTriplet = new OBDTriplet(getVehicleGroup());
      obdTriplet.setWithHistory(withHistory);
      // obdTriplet.setDebug(true);
      display = new TripletDisplay();
      obdTriplet.showDisplay(display);
      if (slow > 0)
        Thread.sleep(2000);
      obdTriplet.getElm327().setHeader(true);
      obdTriplet.getElm327().setLength(true);
      File logCAN = new File("src/test/data/" + fileName+".zip");
      assertTrue("" + logCAN.getPath() + " should exist", logCAN.exists());
      LogReader logReader = new LogReader(logCAN, obdTriplet);
      final int updates = 300;
      logReader.read(logReader.new LogListener() {

        @Override
        public boolean onUpdate(String line, int len, int index, int count) {
          if (count % updates == 0) {
            obdTriplet.showValues(display);
            try {
              Thread.sleep(slow);
            } catch (InterruptedException e) {
              // ignore
            }
          }
          if (count % 3000 == 0) {
            if (debug)
              LOGGER.log(Level.INFO,
                  String.format("%6d (%6d): %s", count, len, line));
          }
          if (count > max[index]) {
            if (display != null)
              if (display.frame != null)
                display.frame.setVisible(false);
            return false;
          }
          return true;
        }

      });
    }

    if (debug)
      LOGGER.log(Level.INFO, "Done");
    display.waitClose();
  }

  @Test
  public void testCanValues() throws Exception {
    // debug = true;
    OBDTriplet lOBDTriplet = new OBDTriplet(getVehicleGroup());
    List<CANValue<?>> canValues = lOBDTriplet.getCANValues();
    assertEquals(84, canValues.size());
    String names = "";
    String delim = "";
    for (CANValue<?> canValue : canValues) {
      names += delim + canValue.canInfo.getTitle();
      delim = ",";
      assertTrue(canValue.isRead());
    }
    if (debug) {
      LOGGER.log(Level.INFO, names);
    }
    assertTrue(names.startsWith(
        "VIN,# of Cells,Battery Capacity,Key,total km,Trip Odo,Trip Rounds,Speed,RPM,RPM Speed,Range,SOC,Climate,Vent Dir,AC Amps,AC Volts,DC Amps,DC Volts,Motor temp,Charger temp,Shifter,Steering Position,Steering Movement,Accelerator,Break Pressed,Break Pedal,Blinker Left,Blinker Right,Door Open,Parking Light,Head Light,High Beam,Cell Temperature,Cell Voltage"
    ));
  }
  
  @Test
  public void testPidFromPid() throws Exception {
    OBDTriplet lOBDTriplet = new OBDTriplet(getVehicleGroup());
    List<CANValue<?>> canValues = lOBDTriplet.getCANValues();
    long start = System.nanoTime();
    for (int i = 0; i < 100; i++) {
      for (CANValue<?> canValue : canValues) {
        CANInfo canInfo = canValue.canInfo;
        assertNotNull(canInfo);
        assertNotNull("pid should not be null for " + canInfo.getTitle(),
            canInfo.getPid());
        String pidId=canInfo.getPid().getPid();
        Pid pid = lOBDTriplet.getElm327().getVehicleGroup().getPidById(pidId);
        assertEquals(pid.getPid(), canValue.canInfo.getPid().getPid());
      }
    }
    long done = System.nanoTime();
    long time = (done - start) / 1000000;
    assertTrue("time should be short but is " + time, time < 100);
  }

  @Test
  public void testGetPidList() throws Exception {
    // debug=true;
    OBDTriplet lOBDTriplet = new OBDTriplet(getVehicleGroup());
    List<CANValue<?>> canValues = lOBDTriplet.getCANValues();
    assertEquals(84, canValues.size());
    for (CANValue<?> canValue : canValues) {
      if (debug) {
        LOGGER.log(Level.INFO, canValue.canInfo.getTitle() + ":"
            + canValue.canInfo.getPid().getPid());
      }
    }
    List<Pid> pids = lOBDTriplet.getElm327().getVehicleGroup().getPids();
    assertEquals(51, pids.size());
    for (Pid pid : pids) {
      if (debug)
        LOGGER.log(Level.INFO, pid.getPid() + ":" + pid.getName());
    }
  }

  @Test
  public void testPidResponseRegexp() {
    // debug=true;
    String responses[] = {
        "101 1 04\n",
        "6D6 8 00 00 00 00 00 00 00 00\n",
        "231 8 00 00 00 00 00 00 00 00\n" + "6D5 8 00 00 00 00 00 00 00 00\n"
            + "101 1 04\n",
        "346 8 27 10 57 20 00 00 00 4D\n" + "308 8 00 03 E8 00 00 00 000^@0 "
            + "373 8 C0 C0 7F 42 0C 90 00 06 ",
        "236 8 0C 4E 10 00 80 00 00 4C " + "288 8 07 C4 29 9F 01 42 11 1C "
            + "6FA 8 02 37 36 39 00 00 00 00 " + "564 8 00 00 00 00 0" };
    // \\s([0-9])\\s(([0-9A-F]{2})\\s)+
    int expected[] = {1,1, 3, 3, 4 };
    int r = 0;
    for (String response : responses) {
      int hit = 0;
      r++;
      if (debug)
        System.out.println(String.format("%3d: %s", r, response));
      Matcher matcher = PIDResponse.PID_LINE_PATTERN.matcher(response);

      while (matcher.find()) {
        hit++;
        String pidline = matcher.group();
        if (debug)
          System.out.println(String.format("\t%3d:%s", hit, pidline));
        Matcher lmatcher = PIDResponse.PID_LINE_PATTERN.matcher(pidline);
        if (lmatcher.matches()) {
          assertEquals(4,lmatcher.groupCount());
          for (int i = 1; i <= lmatcher.groupCount(); i++) {
            String pid=lmatcher.group(1);
            assertEquals(3,pid.length());
            String lenStr=lmatcher.group(2);
            int len=Integer.parseInt(lenStr);
            String ds=lmatcher.group(3).trim();
            String[] d = ds.split(" ");
            // there are two broken pid strings
            if (!("308".equals(pid) || "564".equals(pid)))
              assertEquals("'"+ds+"'",len,d.length);
            if (debug)
              System.out
                  .println(String.format("\t\t%3d:%s", i, lmatcher.group(i)));
          }
        }
      }
      assertEquals(response,expected[r - 1],hit);
    }

  }
  
  @Test 
  public void testIntegrate() throws Exception {
    CANInfo tripRoundsInfo=getVehicleGroup().getCANInfoByName("TripRounds");
    DoubleValue tripRounds = new DoubleValue(tripRoundsInfo);
    // two round per minute values
    int rpm1=2000;
    int rpm2=4000;
    // two measurment dates - 1 second appart
    Date date1 =new Date(0);
    Date date2 =new Date(1000);
    tripRounds.integrate(rpm1, date1, rpm2, date2, 1/60000.0);
    assertEquals(50.0,tripRounds.getValueItem().getValue(),0.1);
  }

}

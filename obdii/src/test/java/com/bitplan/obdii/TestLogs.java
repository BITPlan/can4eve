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

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.bitplan.can4eve.CANValueHandler;
import com.bitplan.can4eve.VehicleGroup;
import com.bitplan.can4eve.gui.javafx.WaitableApp;
import com.bitplan.obdii.elm327.LogReader;
import com.bitplan.triplet.OBDTriplet;

/**
 * test the log files
 * 
 * @author wf
 *
 */
public class TestLogs {

  public class CANValueAnalyzer implements CANValueHandler {
    private File csvFile;
    private PrintWriter printWriter;
    private List<String> names;
    private Map<String, Integer> limitMap = new HashMap<String, Integer>();
    private Map<String, Integer> countMap = new HashMap<String, Integer>();
    final String DELIM = ";";

    /**
     * create a new value analyzer
     * 
     * @param csvFileName
     * @param names
     * @throws Exception
     */
    public CANValueAnalyzer(String csvFileName, String... names)
        throws Exception {
      csvFile = new File(csvFileName);
      printWriter = new PrintWriter(csvFile);
      this.names = Arrays.asList(names);
    }

    SimpleDateFormat isoDateFormatter = new SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss");
    private Integer[] limits;

    public void setLimits(Integer... limits) {
      if (limits.length != names.size())
        throw new IllegalArgumentException("limits.length!=names.size");
      
      this.limits=limits;
      this.resetCounters();
     
    }

    @Override
    public <T> void setValue(String name, T value, Date timeStamp) {
      if (value == null)
        return;
      if (this.names.contains(name)) {
        String format = "s";
        if (value instanceof Integer)
          format = "d";
        if (value instanceof Double)
          format = "f";
        boolean doPrint = true;
        Integer limit = limitMap.get(name);
        if (limit != null) {
          int count = countMap.get(name);
          countMap.put(name, count + 1);
          doPrint = count <= limit;
        }
        if (doPrint) {
          printWriter.println(String.format("%s%s%s%s%" + format,
              isoDateFormatter.format(timeStamp), DELIM, name, DELIM, value));
          printWriter.flush();
        }
      }
    }

    public void close() {
      printWriter.close();
    }

    public void resetCounters() {
      int i=0;
      for (String name : names) {
        limitMap.put(name, limits[i++]);
        countMap.put(name, 0);
      }
    }

  }

  public class KWAnalyzer implements CANValueHandler {
    SimpleDateFormat isoDateFormatter = new SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss");
    private Double dcamps;
    private Double dcvolts;
    private File csvFile;
    private PrintWriter printWriter;

    public KWAnalyzer(String csvFileName) throws Exception {
      csvFile = new File(csvFileName);
      printWriter = new PrintWriter(csvFile);
    }

    @Override
    public <T> void setValue(String name, T value, Date timeStamp) {
      if (value == null)
        return;
      if ("DCAmps".equals(name))
        dcamps = (Double) value;
      if ("DCVolts".equals(name))
        dcvolts = (Double) value;
      if ("Speed".equals(name)) {
        if (dcamps != null && dcvolts != null) {
          double kw = dcamps * dcvolts / -1000.0;
          printWriter.println(String.format("%s;%3d;%5.1f\n",
              isoDateFormatter.format(timeStamp), value, kw));
          printWriter.flush();
        }
      }
    }

    public void close() {
      printWriter.close();
    }

  }

  public static int limit = 5;

  @Test
  public void testLogs() throws Exception {
    WaitableApp.toolkitInit();
    Preferences prefs = Preferences.getInstance();
    if (prefs != null) {
      String logDirectoryName = prefs.logDirectory;
      if (!logDirectoryName.isEmpty()) {
        File logDirectory = new File(logDirectoryName);
        if (logDirectory.isDirectory()) {
          File[] logFiles = logDirectory.listFiles();
          OBDTriplet obdTriplet = new OBDTriplet(VehicleGroup.get("Triplet"));
          obdTriplet.getElm327().setHeader(true);
          obdTriplet.getElm327().setLength(true);
          CANValueAnalyzer analyzer = new CANValueAnalyzer("/tmp/canlog.csv",
              "Odometer", "BatteryCapacity");
          analyzer.setLimits(1,1);
          // KWAnalyzer analyzer = new KWAnalyzer("/tmp/kwlog.csv");
          obdTriplet.setCanValueHandler(analyzer);
          int count = 0;
          long sum = 0;
          for (File logFile : logFiles) {
            if (logFile.getName().endsWith(".log")) {
              sum += logFile.length() / 1024 / 1024;
              System.out.println(String.format("%3d: %5d m %7d k %s \n",
                  ++count, sum, logFile.length() / 1024, logFile.getName()));
              if (count >= limit)
                break;
              LogReader logReader = new LogReader(logFile);
              logReader.addReponseHandler(obdTriplet);
              logReader.read();
              analyzer.resetCounters();
            }
          } // for
          analyzer.close();
        }
      }
    }
  }

}

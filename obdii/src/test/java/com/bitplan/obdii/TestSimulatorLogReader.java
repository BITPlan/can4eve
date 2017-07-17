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
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Date;

import org.junit.Test;

import com.bitplan.elm327.Packet;
import com.bitplan.obdii.elm327.LogReader;
import com.bitplan.obdii.elm327.RandomAccessLogReader;

/**
 * test for RandomAccess Simulator files
 * @author wf
 *
 */
public class TestSimulatorLogReader {
  public static boolean debug=false;
  
  public static File getTestFile() {
    File logCAN = new File("src/test/data/Triplet_2017-04-17_104141.log.zip");
    return logCAN;
  }
  
  /**
   * get the logReader for the given fileName
   * @param fileName
   * @return
   * @throws Exception
   */
  RandomAccessLogReader getLogReader(String fileName) throws Exception {
    File logCAN = new File("src/test/data/" + fileName + ".zip");
    assertTrue("" + logCAN.getPath() + " should exist", logCAN.exists());
    RandomAccessLogReader logReader = new RandomAccessLogReader(logCAN);
    return logReader;
  }
  
  @Test
  public void testMoveTo() throws Exception {
    String fileName="Triplet_2017-04-17_104141.log";
    RandomAccessLogReader logReader = this.getLogReader(fileName);
    Date startDate = logReader.getStartDate();
    Date endDate = logReader.getEndDate();
    if (debug)
      System.out.println(logReader.getIsoRange(startDate, endDate));
    Date middle=new Date((startDate.getTime()+endDate.getTime())/2);
    logReader.open();
    logReader.moveTo(middle);
    Packet p = logReader.nextPacket();
    String middleDateIso = LogReader.logDateFormatter
        .format(p.getTime());
    assertEquals("2017-04-17 11:04:43.286",middleDateIso);
  }
 
  @Test
  public void testSimulatorFromElmLogFile() throws Exception {
    String[] fileNames = {
        // "Triplet_2017-04-17_151817.log",
        "Triplet_2017-04-17_104141.log", "Triplet_2017-04-15_192134.log",
        "Triplet_2017-04-15_132733.log", "Triplet_2017-04-14_191849.log",
        "capture_chg_1104.txt" };
    String startDates[] = { "2017-04-17 10:41:42.157",
        "2017-04-15 07:21:35.965", "2017-04-15 01:27:33.966",
        "2017-04-14 07:18:49.433", "2012-11-04 07:01:34.000" };
    int index = 0;
    for (String fileName : fileNames) {
      RandomAccessLogReader logReader = this.getLogReader(fileName);
      String startDate = LogReader.logDateFormatter
          .format(logReader.getStartDate());
      //System.out.println(startDate);
      assertEquals(startDates[index++], startDate);
      Packet packet = logReader.getPacket(1000);
      //System.out.println(packet.getData());
      String endDate=LogReader.logDateFormatter.format(logReader.getEndDate());
      // System.out.println(endDate);
    }
  }
}

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

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import com.bitplan.can4eve.VehicleGroup;
import com.bitplan.can4eve.gui.javafx.WaitableApp;
import com.bitplan.obdii.elm327.LogReader;
import com.bitplan.triplet.OBDTriplet;

/**
 * test the log files
 * @author wf
 *
 */
public class TestLogs {

  @Test
  public void testLogs() throws Exception {
    WaitableApp.toolkitInit();
    Preferences prefs = Preferences.getInstance();
    if (prefs!=null) {
      String logDirectoryName=prefs.logDirectory;
      if (logDirectoryName!=null) {
        File logDirectory=new File(logDirectoryName);
        assertTrue(logDirectory.isDirectory());
        File[] logFiles = logDirectory.listFiles();
        OBDTriplet obdTriplet = new OBDTriplet(VehicleGroup.get("triplet"));
        obdTriplet.getElm327().setHeader(true);
        obdTriplet.getElm327().setLength(true);
      
        int count=0;
        for (File logFile:logFiles) {
          if (logFile.getName().endsWith(".log")) {
            System.out.println(String.format("%3d:%s \n",++count,logFile.getName()));
            LogReader logReader = new LogReader(logFile);
            // logReader.addReponseHandler(obdTriplet);
            logReader.read();
          }
        }
      }
    }
  }

}

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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.bitplan.can4eve.CANInfo;
import com.bitplan.can4eve.Pid;

/**
 * test getting the Wiki Info
 * @author wf
 *
 */
public class TestWikiInfo extends TestOBDII {
  @Test
  public void testWikiInfo() throws Exception {
    List<CANInfo> allcaninfos=new ArrayList<CANInfo>();
    for (Pid pid : getVehicleGroup().getPids()) {
      String freq = "";
      if (pid.getFreq() > 0)
        freq = String.format("|frequency=%3d", pid.getFreq());
      String description = "";
      String delim = "";
      List<CANInfo> caninfos = pid.getCaninfos();
      allcaninfos.addAll(caninfos);
      if (caninfos.size()>2) {
        description="infos:";
      }
      for (int i=1;i<caninfos.size();i++) {
        CANInfo canInfo = caninfos.get(i);
          description = description + String.format("%s%s",caninfos.size()>2?"\n# ":"",canInfo.getDescription());
      }
      System.out.println(String.format(
          "{{PID|id=%s\n|name=%s%s\n|description=%s\n|examples=%s\n|storemode=property\n}}\n{{PID|id=%s|viewmode=masterdetail}}",
          pid.getPid(), pid.getName(), freq, description,pid.getExamples(),pid.getPid()));
    }
    for (CANInfo canInfo : allcaninfos) {
      String pid = "?";
      if (canInfo.getPid() != null)
        pid = canInfo.getPid().getPid();
      if (!canInfo.getName().startsWith(("Raw")))
        System.out.println(String.format(
            "{{CANInfo|name=%s|description=%s|unit=%s|pid=Cantriplet/PIDs/%s|storemode=subobject}}",
            canInfo.getTitle(), canInfo.getDescription(), canInfo.getUnit(), pid));
    }
  }
}

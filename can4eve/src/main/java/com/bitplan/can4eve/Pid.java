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
package com.bitplan.can4eve;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * see https://en.wikipedia.org/wiki/OBD-II_PIDs
 * @author wf
 *
 * OBD-II PIDs (On-board diagnostics Parameter IDs) are codes used to request data from a vehicle, 
 * used as a diagnostic tool.
 * 
 * SAE standard J/1979 defines many PIDs, but manufacturers also define many more PIDs specific to 
 * their vehicles. All light duty vehicles (i.e. less than 8,500 pounds) sold in North America since 1996, 
 * as well as medium duty vehicles (i.e. 8,500-14,000 pounds) beginning in 2005, and heavy duty vehicles 
 * (i.e. greater than 14,000 pounds) beginning in 2010,[1] are required to support OBD-II diagnostics, 
 * using a standardized data link connector, and a subset of the SAE J/1979 defined PIDs (or SAE J/1939 
 * as applicable for medium/heavy duty vehicles), primarily for state mandated emissions inspections.
 */
public class Pid {
  String name;     // name of the PID e.g. VIN
  String pid;      // hexadecimal code for the PID 
  Integer length;  // expected length of PID frame     
  int freq;        // frequency of transmission on bus per second
  String examples;
  String isoTp;    // if this is an ISO-TP frame based Pid 
  List<CANInfo> caninfos=new ArrayList<CANInfo>();

  /**
   * add the given CANinfo to this PID
   * @param caninfo
   */
  public void addCANInfo(CANInfo caninfo) {
    caninfos.add(caninfo);
  }
  
  public CANInfo getFirstInfo() {
    return caninfos.get(0);
  }

  public Integer getLength() {
    return length;
  }

  public void setLength(Integer length) {
    this.length = length;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPid() {
    return pid;
  }

  public void setPid(String pid) {
    this.pid = pid;
  }

  public int getFreq() {
    return freq;
  }

  public void setFreq(int freq) {
    this.freq = freq;
  }

  public String getExamples() {
    return examples;
  }

  public void setExamples(String examples) {
    this.examples = examples;
  }
  
  public String getIsoTp() {
    return isoTp;
  }
  public void setIsoTp(String isoTp) {
    this.isoTp = isoTp;
  }
  
  public List<CANInfo> getCaninfos() {
    return caninfos;
  }

  public void setCaninfos(List<CANInfo> caninfos) {
    this.caninfos = caninfos;
  }
  
  public String toString() {
    String pid=this.getPid();
    if (pid!=null)
      return pid;
    else 
      return null;
  }
}

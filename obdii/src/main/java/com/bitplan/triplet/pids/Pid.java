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
package com.bitplan.triplet.pids;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author wf
 *
 */
public enum Pid {

  Accelerator    ("210",50,"210 7 00 00 E9 80 80 00 00",
      CANInfo.Raw210,CANInfo.Accelerator),
  AmpsVolts      ("373",100,"373 8 C7 C6 7F 5E 0C C0 00 06",
      CANInfo.Raw375,CANInfo.DCAmps,CANInfo.DCVolts), 
  ACAmpsVolts    ("389",10,
      CANInfo.Raw389,CANInfo.ACAmps,CANInfo.ACVolts), 
  BreakPedal     ("208",50,"208 8 00 20 60 E3 C0 00 C0 00",
      CANInfo.Raw208,CANInfo.BreakPedal),
  BreakPressed   ("231",50,"states:\n# off: 231 8 00 00 00 00 00 00 00 00\n#on: 231 8 00 00 00 00 02 00 00 00",
      CANInfo.Raw231,CANInfo.BreakPressed),
  CellInfo1      ("6E1",25,"6E1 8 01 00 49 4B 01 71 01 72",
      CANInfo.Raw6E1,CANInfo.CellVoltage,CANInfo.CellTemperature),
  CellInfo2      ("6E2",25,"6E2 8 07 4B 4B 01 01 7F 01 80",
      CANInfo.Raw6E2,CANInfo.CellVoltage,CANInfo.CellTemperature),
  CellInfo3      ("6E3",25,"6E3 8 01 4A 48 00 01 7C 01 7C", 
      CANInfo.Raw6E3,CANInfo.CellVoltage,CANInfo.CellTemperature),
  CellInfo4      ("6E4",25,"6E4 8 07 00 00 00 01 5B 01 5B",
      CANInfo.Raw6E4,CANInfo.CellVoltage,CANInfo.CellTemperature),  
  ChargerTemp    ("286",10,"286 8 00 00 00 38 00 00 00 00",
      CANInfo.Raw286,CANInfo.ChargerTemp),
  Climate        ("3A4",10,"3A4 8 09 55 96 7C 6C 84 00 60",
      CANInfo.Raw3A4,CANInfo.Climate, CANInfo.VentDirection), 
  Key            ("101",10,"states:\n# off:101 1 00\n# on:101 1 04",
      CANInfo.Raw101,CANInfo.Key),
  Lights         ("424",25,"424 8 87 60 0C 00 45 CB 01 FF",
      CANInfo.Raw424,CANInfo.BlinkerLeft,CANInfo.BlinkerRight,CANInfo.DoorOpen,CANInfo.ParkingLight,CANInfo.HeadLight,CANInfo.HighBeam),
  MotorTemp_RPM  ("298",10,"298 8 5E 3A 43 39 3C 00 41 E0",
      CANInfo.Raw298,CANInfo.MotorTemp,CANInfo.RPM,CANInfo.RPMSpeed,CANInfo.TripOdo,CANInfo.TripRounds), 
  Odometer_Speed ("412",10,"412 8 FE 71 00 0B 97 00 21 12",
      CANInfo.Raw412,CANInfo.Odometer,CANInfo.Speed), 
  Range          ("346",50,"346 8 37 31 57 20 00 00 00 3B",
      CANInfo.Raw346,CANInfo.Range), 
  SOC            ("374",10,CANInfo.Raw374,CANInfo.SOC), 
  ShifterPosition("418",20,CANInfo.Raw418,CANInfo.ShifterPosition), 
  Steering_Wheel ("236",100,CANInfo.Raw236,CANInfo.SteeringWheelPosition,CANInfo.SteeringWheelMovement), 
  VIN            ("29A",10,"3 indices:\n#29A 8 00 56 46 33 31 4E 5A 4B\n#29A 8 01 59 5A 48 55 38 30 30\n#29A 8 02 37 36 39 FF FF FF FF",
      CANInfo.Raw29A,CANInfo.VIN,CANInfo.CellCount), 
  VIN2           ("6FA",10,CANInfo.Raw6FA,CANInfo.VIN2),
  PID1B6         ("1B6",CANInfo.Raw1B6),    // const 1B6 8 00 C0 FF C0 FF C0 FF 01
  // PID208         ("208",CANInfo.Raw208), // Brake pedal
                                                                      // https://github.com/plaes/i-miev-obd2
  // PID210         ("210",CANInfo.Raw210), // accelerator Pedal
  PID212         ("212",CANInfo.Raw212), 
  // PID231         ("231",CANInfo.Raw231), // Brake pedalswitch sensor
  // PID236("236",PidRawValue.class), // Steering wheel sensor
  PID285         ("285",CANInfo.Raw285), 
  // PID286         ("286",CANInfo.Raw286), // Charger temp?
  PID288         ("288",CANInfo.Raw288),
  // PID298("298",PidRawValue.class), 
  // PID29A("29A",10,CANInfo.Raw29A), // VIN
  PID308         ("308",CANInfo.Raw308),
  // PID346("346",PidRawValue.class), // autonomie & hand brake
  // PID373("373",PidRawValue.class), // Amps/Volts
  // PID374("374",PidRawValue.class), // SOC
  PID375         ("375",CANInfo.Raw375), 
  PID384         ("384",CANInfo.Raw384), // Climate/Heating effect
                                             // http://myimiev.com/forum/viewtopic.php?f=25&t=763&start=80
                                             // PTCAmpere
  // PID389("389",PidRawValue.class), // ACAmpsVolts
  PID38A         ("38A",CANInfo.Raw38A), 
  PID38D         ("38D",CANInfo.Raw38D), 
  PID39B         ("39B",CANInfo.Raw39B), 
  PID408         ("408",CANInfo.Raw408),
  // PID412("412",PidRawValue.class), // Speed / Odometer
  // PID418("418",PidRawValue.class), // shifter position
  // PID424(         "424",CANInfo.Raw424), // switches and stuff - like lights /blinker
  PID564(         "564",CANInfo.Raw564), 
  PID565(         "565",CANInfo.Raw565), 
  PID568(         "568",CANInfo.Raw568), 
  PID5A1(         "5A1",CANInfo.Raw5A1), 
  PID695(         "695",CANInfo.Raw695), 
  PID696(         "696",CANInfo.Raw696), 
  PID697(         "697",CANInfo.Raw697), 
  PID6D0(         "6D0",CANInfo.Raw6D0), 
  PID6D1(         "6D1",CANInfo.Raw6D1), 
  PID6D2(         "6D2",CANInfo.Raw6D2), 
  PID6D3(         "6D3",CANInfo.Raw6D3), 
  PID6D4(         "6D4",CANInfo.Raw6D4), 
  PID6D5(         "6D5",CANInfo.Raw6D5), 
  PID6D6(         "6D6",CANInfo.Raw6D6), 
  PID6DA(         "6DA",CANInfo.Raw6DA), 
  //PID6E1(         "6E1",CANInfo.Raw6E1), // cell voltages and temps
  //PID6E2(         "6E2",CANInfo.Raw6E2), 
  //PID6E3(         "6E3",CANInfo.Raw6E3), 
  //PID6E4(         "6E4",CANInfo.Raw6E4),
  // PID6FA("6FA",PidRawValue.class), // VIN 2
  PID738(         "738",CANInfo.Raw738), // remote request
  PID75A(         "75A",CANInfo.Raw75A), 
  PID75B(         "75B",CANInfo.Raw75B);
  private final String pid;
  // final Class<? extends CANValue<?>> type;
  private CANInfo[] infos;
  public int freq;
  public String examples;
  
  public CANInfo getFirstInfo() {
    return infos[0];
  }

  public CANInfo[] getInfos() {
    return infos;
  }

  /**
   * speedup for
   * 
   * @author wf
   */
  private static final class Lookup {
    // avoid cannot refer to the static enum field within an initializer
    // http://stackoverflow.com/a/26929580/1497139
    final static Map<String, Pid> pidByPid = new HashMap<String, Pid>();
  }

  private Pid(String pid,CANInfo ...infos) {
    this(pid,-1,"",infos);
  }
  
  /**
   * create the given pid  
   * @param pid
   * @param freq - the frequence fps
   * @param infos
   */
  private Pid(String pid,int freq,CANInfo ...infos) {
    this(pid,freq,"",infos);
  }
  
  /**
   * create the given pid  
   * @param pid
   * @param freq - the frequence fps
   * @param examples
   * @param infos
   */
  private Pid(String pid,int freq,String examples,CANInfo ...infos) {
    this.pid=pid;
    // this.type=null;
    this.freq=freq;
    this.examples=examples;
    Lookup.pidByPid.put(pid.toUpperCase(), this);
    this.infos=infos;
    for (CANInfo canInfo:infos) {
      canInfo.addPid(this);
    }
  }

  public boolean equalsName(String otherName) {
    // (otherName == null) check is not needed because name.equals(null) returns
    // false
    return pid.equals(otherName);
  }

  /**
   * get the given Pid from the given pid
   * 
   * @param pPid
   * @return - the pid
   */
  public static Pid fromPid(String pPid) {
    Pid result = Lookup.pidByPid.get(pPid.toUpperCase());
    return result;
  }

  public String getPid() {
    return pid;
  }

  public String toString() {
    return this.pid;
  }
}

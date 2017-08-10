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

import java.util.ArrayList;
import java.util.List;

import com.bitplan.can4eve.CANValue;
import com.bitplan.can4eve.CANValue.BooleanValue;
import com.bitplan.can4eve.CANValue.DoubleValue;
import com.bitplan.can4eve.CANValue.IntegerValue;
import com.bitplan.can4eve.CANValue.StringValue;
import com.bitplan.triplet.ClimateValue;
import com.bitplan.triplet.ShifterPositionValue;
import com.bitplan.triplet.VINValue;

/**
 * possible Information on the CANBus
 * 
 * @author wf
 *
 */
public enum CANInfo {
  Accelerator("Accelerator","Accelerator Pedal","%4.0f %%","%",60,DoubleValue.class), //
  ACAmps("AC Amps","Charging Power Amps","%7.1f A ","Ampere",10,DoubleValue.class), //
  ACVolts("AC Volts","Charging Current Volts","%5.1f V","Volt",10,DoubleValue.class), //
  BreakPedal("Break Pedal","Break Pedal Position","%4.0f %%","%",60,DoubleValue.class), //
  BreakPressed("Break Pressed","Break Pressed","%s",60,StringValue.class), //
  CellCount("# of Cells","# of battery cells","%2d",1,IntegerValue.class),
  CellTemperature("Cell Temperature","Cell Temperature","%2d °C","Celsius",4,DoubleValue.class,66), //
  CellVoltage("Cell Voltage","Cell Voltage","5.2f V","Volt",4,DoubleValue.class,88), //
  ChargerTemp("Charger temp", "Charger temperature °C", "%2d °C","Celsius",2, IntegerValue.class), //
  Climate("Climate","Climate and Heating settings","%s",5,ClimateValue.class), //
  VentDirection("Vent Dir","Ventilator direction","%s",5,StringValue.class), //
  DCAmps("DC Amps","Battery Power Amps","%7.1f A ","Ampere",30,DoubleValue.class), //
  DCVolts("DC Volts","Battery Current Volts","%5.1f V","Volt",30,DoubleValue.class), //
  Key("Key","Key state","%s",1,StringValue.class), ///
  MotorTemp("Motor temp", "Motor temperature °C", "%2d °C","Celsius",2, IntegerValue.class), //
  Odometer("total km", "total km", "%6d km","km", 4,IntegerValue.class), //
  Range("Range", "Range km", "%3d km", "km",1,IntegerValue.class), //
  
  BlinkerLeft("Blinker Left","Left Blinker","%s",30,BooleanValue.class), //
  BlinkerRight("Blinker Right","Right Blinker","%s",30,BooleanValue.class), //
  DoorOpen("Door Open","Door Open","%s",30,BooleanValue.class), //
  ParkingLight("Parking Light","Parking Light","%s",4,BooleanValue.class), //
  HeadLight("Head Light","Head Light","%s",4,BooleanValue.class), //
  HighBeam("High Beam","High Beam","%s",4,BooleanValue.class), //
  Raw101("Raw101","Raw Value 101","%s",1,StringValue.class), //
  Raw1B6("Raw1B6","Raw Value 1B6","%s",1,StringValue.class), //
  Raw208("Raw208","Raw Value 208","%s",1,StringValue.class), //
  Raw210("Raw210","Raw Value 210","%s",1,StringValue.class), //
  Raw212("Raw212","Raw Value 212","%s",1,StringValue.class), //
  Raw231("Raw231","Raw Value 231","%s",1,StringValue.class), //
  Raw236("Raw236","Raw Value 236","%s",1,StringValue.class), //
  Raw285("Raw285","Raw Value 285","%s",1,StringValue.class), //
  Raw286("Raw286","Raw Value 286","%s",1,StringValue.class), //
  Raw288("Raw288","Raw Value 288","%s",1,StringValue.class), //
  Raw298("Raw298","Raw Value 298","%s",1,StringValue.class), //
  Raw29A("Raw29A","Raw Value 29A","%s",1,StringValue.class), //
  Raw308("Raw308","Raw Value 308","%s",1,StringValue.class), //
  Raw346("Raw346","Raw Value 346","%s",1,StringValue.class), //
  Raw373("Raw373","Raw Value 373","%s",1,StringValue.class), //
  Raw374("Raw374","Raw Value 374","%s",1,StringValue.class), //
  Raw375("Raw375","Raw Value 375","%s",1,StringValue.class), //
  Raw384("Raw384","Raw Value 384","%s",1,StringValue.class), //
  Raw389("Raw389","Raw Value 389","%s",1,StringValue.class), //
  Raw38A("Raw38A","Raw Value 38A","%s",1,StringValue.class), //
  Raw38D("Raw38D","Raw Value 38D","%s",1,StringValue.class), //
  Raw39B("Raw39B","Raw Value 39B","%s",1,StringValue.class), //
  Raw3A4("Raw3A4","Raw Value 3A4","%s",1,StringValue.class), //
  Raw408("Raw408","Raw Value 408","%s",1,StringValue.class), //
  Raw412("Raw412","Raw Value 412","%s",1,StringValue.class), //
  Raw418("Raw418","Raw Value 418","%s",1,StringValue.class), //
  Raw424("Raw424","Raw Value 424","%s",1,StringValue.class), //
  Raw564("Raw564","Raw Value 564","%s",1,StringValue.class), //
  Raw565("Raw565","Raw Value 565","%s",1,StringValue.class), //
  Raw568("Raw568","Raw Value 568","%s",1,StringValue.class), //
  Raw5A1("Raw5A1","Raw Value 5A1","%s",1,StringValue.class), //
  Raw695("Raw695","Raw Value 695","%s",1,StringValue.class), //
  Raw696("Raw696","Raw Value 696","%s",1,StringValue.class), //
  Raw697("Raw697","Raw Value 697","%s",1,StringValue.class), //
  Raw6D0("Raw6D0","Raw Value 6D0","%s",1,StringValue.class), //
  Raw6D1("Raw6D1","Raw Value 6D1","%s",1,StringValue.class), //
  Raw6D2("Raw6D2","Raw Value 6D2","%s",1,StringValue.class), //
  Raw6D3("Raw6D3","Raw Value 6D3","%s",1,StringValue.class), //
  Raw6D4("Raw6D4","Raw Value 6D4","%s",1,StringValue.class), //
  Raw6D5("Raw6D5","Raw Value 6D5","%s",1,StringValue.class), //
  Raw6D6("Raw6D6","Raw Value 6D6","%s",1,StringValue.class), //
  Raw6DA("Raw6DA","Raw Value 6DA","%s",1,StringValue.class), //
  Raw6E1("Raw6E1","Raw Value 6E1","%s",1,StringValue.class), //
  Raw6E2("Raw6E2","Raw Value 6E2","%s",1,StringValue.class), //
  Raw6E3("Raw6E3","Raw Value 6E3","%s",1,StringValue.class), //
  Raw6E4("Raw6E4","Raw Value 6E4","%s",1,StringValue.class), //
  Raw6FA("Raw6FA","Raw Value 6FA","%s",1,StringValue.class), //
  Raw738("Raw738","Raw Value 738","%s",1,StringValue.class), //
  Raw75A("Raw75A","Raw Value 75A","%s",1,StringValue.class), //
  Raw75B("Raw75B","Raw Value 75B","%s",1,StringValue.class), //
  RPM("RPM", "Rounds per minute", "%4d rpm","rpm",20, IntegerValue.class), //
  RPMSpeed("RPM Speed", "Speed from RPM", "%6.3f m/r","km/h",20, DoubleValue.class), //  
  SOC("SOC", "State of Charging %", "%4.1f %%","%",4, DoubleValue.class), //
  TripOdo("Trip Odo","Trip km","%7.3f km","km",4,DoubleValue.class), //
  TripRounds("Trip Rounds","Trip rounds","%8.0f","rounds",60,DoubleValue.class), //
  Speed("Speed", "Speed km/h", "%3d km/h", "km/h",20,IntegerValue.class), //
  SteeringWheelPosition("Steering Position","Steering Wheel position","%7.1f deg","degree",60,DoubleValue.class), //
  SteeringWheelMovement("Steering Movement","Steering Wheel movement","%7.1f mov","?",60,DoubleValue.class), //
  ShifterPosition("Shifter","Shifter position","%s","P/R/N/D",60,ShifterPositionValue.class), //
  VIN("VIN","Vehicle Identification number","%20s",0,VINValue.class), //
  VIN2("VIN2","Vehicle Identification number 2","%20s",0,VINValue.class); // 

  Class<? extends CANValue<?>> type;
  public String title;
  public String description;
  public Pid pid;
  private List<Pid> pids=new ArrayList<Pid>();
  public String format;
  public int historyValuesPerMinute;
  public int maxIndex=0;
  String unit;

  /**
   * create a CANInfo for the given type
   * 
   * @param title
   * @param description
   * @param format
   * @param historyValuesPerMinute
   * @param type
   * @param maxIndex
   */
  private CANInfo(String title, String description, String format,String unit,
      int historyValuesPerMinute, Class<? extends CANValue<?>> type, int maxIndex) {
    this.title = title;
    this.description = description;
    this.unit=unit;
    this.format = format;
    this.historyValuesPerMinute = historyValuesPerMinute;
    this.type = type;
    this.setMaxIndex(maxIndex);
  }
  
  /**
   * constructor without unit
   * @param title
   * @param description
   * @param format
   * @param historyValuesPerMinute
   * @param type
   */
  private CANInfo(String title, String description, String format,
      int historyValuesPerMinute, Class<? extends CANValue<?>> type) {
    this(title,description,format,"",historyValuesPerMinute,type,0);
  }
  /**
   * create a CANInfo for the given type
   * 
   * @param title
   * @param description
   * @param format
   * @param unit
   * @param historyValuesPerMinute
   * @param type
   */
  private CANInfo(String title, String description, String format,String unit,
      int historyValuesPerMinute, Class<? extends CANValue<?>> type) {
    this(title,description,format,unit,historyValuesPerMinute,type,0);
  }

  public Class<? extends CANValue<?>> getType() {
    return type;
  }

  public void setType(Class<? extends CANValue<?>> type) {
    this.type = type;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Pid getPid() {
    return pid;
  }

  public void setPid(Pid pid) {
    this.pid = pid;
  }

  public int getMaxIndex() {
    return maxIndex;
  }

  public void setMaxIndex(int maxIndex) {
    this.maxIndex = maxIndex;
  }

  public void addPid(Pid pid) {
    if (this.pid==null)
      setPid(pid);
    getPids().add(pid);
  }

  public List<Pid> getPids() {
    return pids;
  }

  public void setPids(List<Pid> pids) {
    this.pids = pids;
  }

}

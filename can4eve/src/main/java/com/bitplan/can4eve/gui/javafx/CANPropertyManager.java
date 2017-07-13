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
package com.bitplan.can4eve.gui.javafx;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.bitplan.can4eve.CANValue;
import com.bitplan.can4eve.VehicleGroup;
import com.bitplan.can4eve.CANValue.BooleanValue;
import com.bitplan.can4eve.CANValue.DoubleValue;
import com.bitplan.can4eve.CANValue.IntegerValue;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * manager for the properties/CANInfos of a vehicleGroup
 * 
 * @author wf
 *
 */
public class CANPropertyManager {
  private Map<String, CANProperty> canProperties = new HashMap<String, CANProperty>();
  VehicleGroup vehicleGroup;

  public Map<String, CANProperty> getCanProperties() {
    return canProperties;
  }

  public void setCanProperties(Map<String, CANProperty> canProperties) {
    this.canProperties = canProperties;
  }

  /**
   * create this propertyManager
   * 
   * @param vehicleGroup
   */
  public CANPropertyManager(VehicleGroup vehicleGroup) {
    this.vehicleGroup = vehicleGroup;
  }

  /**
   * add the given Value
   * 
   * @param doubleValue
   * @return - the value
   */
  public DoubleValue addValue(DoubleValue doubleValue) {
    this.addCanProperty(doubleValue, new SimpleDoubleProperty());
    return doubleValue;
  }

  /**
   * add the given Value
   * 
   * @param integerValue
   * @return - the value
   */
  public IntegerValue addValue(IntegerValue integerValue) {
    this.addCanProperty(integerValue, new SimpleIntegerProperty());
    return integerValue;
  }

  /**
   * add the given Value
   * 
   * @param booleanValue
   * @return - the value
   */
  public BooleanValue addValue(BooleanValue booleanValue) {
    this.addCanProperty(booleanValue, new SimpleBooleanProperty());
    return booleanValue;
  }

  /**
   * add a CAN Property
   * 
   * @param canValue
   *          - the can Value
   * @param property
   *          - the Property
   */
  protected <CT extends CANValue<T>, T> void addCanProperty(CT canValue,
      Property<T> property) {
    CANProperty<CT, T> canProperty = new CANProperty<CT, T>(canValue, property);
    getCanProperties().put(canValue.canInfo.getName(), canProperty);
  }

  /**
   * add a double Value property
   * 
   * @param canValue
   * @param property
   */
  private void addCanProperty(DoubleValue canValue,
      SimpleDoubleProperty property) {
    CANProperty<DoubleValue, Double> canProperty = new CANProperty<DoubleValue, Double>(
        canValue, property);
    getCanProperties().put(canValue.canInfo.getName(), canProperty);
  }

  /**
   * add an integer Value property
   * 
   * @param canValue
   * @param property
   */
  private void addCanProperty(IntegerValue canValue,
      SimpleIntegerProperty property) {
    CANProperty<IntegerValue, Integer> canProperty = new CANProperty<IntegerValue, Integer>(
        canValue, property);
    getCanProperties().put(canValue.canInfo.getName(), canProperty);
  }

  /**
   * add a boolean Value property
   * 
   * @param canValue
   * @param property
   */
  private void addCanProperty(BooleanValue canValue,
      SimpleBooleanProperty property) {
    CANProperty<BooleanValue, Boolean> canProperty = new CANProperty<BooleanValue, Boolean>(
        canValue, property);
    getCanProperties().put(canValue.canInfo.getName(), canProperty);
  }
  
  @SuppressWarnings("unchecked")
  public void setValue(String name, Double value, Date timeStamp) {
    getCanProperties().get(name).setValue(value, timeStamp);
  }
  
  @SuppressWarnings("unchecked")
  public void setValue(final String name, final Integer value, final Date timeStamp) {
    getCanProperties().get(name).setValue(value, timeStamp);
  }
}

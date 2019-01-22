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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bitplan.can4eve.CANData;
import com.bitplan.can4eve.CANInfo;
import com.bitplan.can4eve.CANValue;
import com.bitplan.can4eve.CANValue.BooleanValue;
import com.bitplan.can4eve.CANValue.DoubleValue;
import com.bitplan.can4eve.CANValue.IntegerValue;
import com.bitplan.can4eve.CANValue.StringValue;
import com.bitplan.can4eve.CANValueHandler;
import com.bitplan.can4eve.VehicleGroup;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * manager for the properties/CANInfos of a vehicleGroup
 * 
 * @author wf
 *
 */
public class CANPropertyManager implements CANValueHandler {
  @SuppressWarnings("rawtypes")
  private Map<String, CANProperty> canProperties = new HashMap<String, CANProperty>();
  VehicleGroup vehicleGroup;

  @SuppressWarnings("rawtypes")
  public Map<String, CANProperty> getCanProperties() {
    return canProperties;
  }

  @SuppressWarnings("rawtypes")
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
   * get the list of Properties for the given canInfoNames
   * 
   * @param canInfoNames
   * @return the list
   * @throws Exception
   */
  @SuppressWarnings("rawtypes")
  public Map<String, CANProperty> getCANProperties(String... canInfoNames)
      throws Exception {
    Map<String, CANProperty> properties = new HashMap<String, CANProperty>();
    for (String canInfoName : canInfoNames) {
      if (!this.canProperties.containsKey(canInfoName)) {
        addValue(canInfoName);
      }
      properties.put(canInfoName, canProperties.get(canInfoName));
    }
    return properties;
  }

  /**
   * add Value by Name
   * 
   * @param canInfoName
   * @return the added Value
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public <T> CANProperty addValue(final String canInfoName) {
    CANInfo canInfo = vehicleGroup.getCANInfoByName(canInfoName);
    String type = canInfo.getType();
    Class<CANValue<?>> clazz = null;
    CANValue<T> canValue = null;
    if (type == null)
      throw new RuntimeException(
          String.format("invalid CANInfo configuration %s - type not specified",
              canInfoName));
    if (type.equals("StringValue")) {
      StringValue stringValue = new StringValue(canInfo);
      addValue(stringValue);
    } else if (type.equals("DoubleValue")) {
      DoubleValue doubleValue = new DoubleValue(canInfo);
      addValue(doubleValue);
    } else if (type.equals("IntegerValue")) {
      IntegerValue integerValue = new IntegerValue(canInfo);
      addValue(integerValue);
    } else if (type.equals("BooleanValue")) {
      BooleanValue booleanValue = new BooleanValue(canInfo);
      addValue(booleanValue);
    } else {
      // handle generic vehicle specific CANValue types
      // the type is taken from the json specification
      try {
        clazz = (Class<CANValue<?>>) Class.forName(type);
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(
            String.format("invalid CANInfo configuration %s - invalid type %s",
                canInfoName, type));
      }
      try {
        Constructor<CANValue<?>> constructor = clazz
            .getDeclaredConstructor(CANInfo.class);
        canValue = (CANValue<T>) constructor.newInstance(canInfo);
        addValue(canValue);
      } catch (Throwable th) {
        throw new RuntimeException(String.format(
            "invalid CANInfo configuration %s - can not instantiate type %s - error %s",
            canInfoName, type, th.getMessage()));
      }
    }
    CANProperty result = this.get(canInfoName);
    if (result == null)
      throw new RuntimeException(String.format(
          "invalid CANInfo configuration %s could not add to CANProperties",
          canInfoName));
    return result;
  }

  /**
   * add the given string Value
   * @param stringValue
   * @return - the value
   */
  public StringValue addValue(StringValue stringValue) {
    this.addCanProperty(stringValue, new SimpleStringProperty());
    return stringValue;
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
   * add the given canValue
   * 
   * @param canValue
   * @return it
   */
  public <T> CANValue<T> addValue(CANValue<T> canValue) {
    this.addCanProperty(canValue, new SimpleObjectProperty<T>());
    return canValue;
  }

  /**
   * add a CAN Property
   * 
   * @param canValue
   *          - the can Value
   * @param property
   *          - the Property
   */
  public <CT extends CANValue<T>, T> void addCanProperty(CT canValue,
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

  /**
   * add the given property
   * 
   * @param canValue
   * @param property
   */
  public <T> void addCanProperty(CANValue<T> canValue,
      SimpleObjectProperty<T> property) {
    CANProperty<CANValue<T>, T> canProperty = new CANProperty<CANValue<T>, T>(
        canValue, property);
    getCanProperties().put(canValue.canInfo.getName(), canProperty);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> void setValue(String name, T value, Date timeStamp) {
    getCanProperties().get(name).setValue(value, timeStamp);
  }

  /**
   * get the given CANProperty byName
   * 
   * @param CANInfoName
   * @return the CANProperty
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public <CT extends CANValue<T>, T> CANProperty<CT, T> get(
      String CANInfoName) {
    CANProperty result = getCanProperties().get(CANInfoName);
    return result;
  }

  /**
   * get the CANValues
   * 
   * @return the list of CANValues
   */
  @SuppressWarnings("rawtypes")
  public List<CANValue<?>> getCANValues() {
    List<CANValue<?>> canValues = new ArrayList<CANValue<?>>();
    for (CANProperty canProperty : this.canProperties.values()) {
      canValues.add(canProperty.getCanValue());
    }
    return canValues;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public <T> CANData<T> getValue(String name) {
    CANProperty property = get(name);
    if (property == null)
      throw new RuntimeException("invalid canInfoName " + name);
    return property;
  }

}

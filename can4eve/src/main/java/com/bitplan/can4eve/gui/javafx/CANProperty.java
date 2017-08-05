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

import com.bitplan.can4eve.CANData;
import com.bitplan.can4eve.CANInfo;
import com.bitplan.can4eve.CANValue;
import com.bitplan.can4eve.CANValue.DoubleValue;
import com.bitplan.can4eve.CANValue.IntegerValue;
import com.bitplan.can4eve.CANValueItem;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * generic JavaFX CANProperty
 * @author wf
 *
 * @param <CT>
 * @param <T>
 */
public class CANProperty<CT extends CANValue<T>,T> implements CANData<T>{
  CT canValue;
  private Property<T>property;
  private IntegerProperty updateCountProperty=new SimpleIntegerProperty();
  private ObservableList<CANValueItem<T>> propertyList;
  private Property<T>max;
  private Property<T>avg;
  
  public CT getCanValue() {
    return canValue;
  }

  public Property<T> getProperty() {
    return property;
  }

  public void setProperty(Property<T> property) {
    this.property = property;
  }

  public Property<T> getMax() {
    return max;
  }

  public void setMax(Property<T> max) {
    this.max = max;
  }

  public Property<T> getAvg() {
    return avg;
  }

  public void setAvg(Property<T> avg) {
    this.avg = avg;
  }
  
  public IntegerProperty getUpdateCountProperty() {
    return updateCountProperty;
  }

  /**
   * initialize me with the given CANValue and property
   * @param canValue
   * @param property
   */
  public void init(CT canValue, Property<T> property){
    this.canValue=canValue;
    this.setProperty(property);
    this.propertyList=FXCollections.observableList(canValue.getValueItems());
  }

  /**
   * construct me
   * @param canValue
   * @param property
   */
  public CANProperty(CT canValue, Property<T> property) {
    init(canValue,property);
  }
  
  /**
   * construct a CANProperty
   * @param canValue
   * @param property
   */
  @SuppressWarnings({ "unchecked"})
  public CANProperty(DoubleValue canValue, SimpleDoubleProperty property) {
    init((CT)canValue,(Property<T>)property);
    this.setAvg((Property<T>) new SimpleDoubleProperty());
    this.setMax((Property<T>) new SimpleDoubleProperty());
  }
  
  /**
   * construct me for an IntegerValue
   * @param canValue
   * @param property
   */
  @SuppressWarnings({ "unchecked"})
  public CANProperty(IntegerValue canValue,
      SimpleIntegerProperty property) {
    init((CT)canValue,(Property<T>)property);
    this.setAvg((Property<T>) new SimpleIntegerProperty());
    this.setMax((Property<T>) new SimpleIntegerProperty());
  }
  
  /**
   * set the value for CANValue and property
   * @param value - the value to set
   * @param timeStamp
   */
  public void setValue(T value, Date timeStamp) {
    canValue.setValue(value, timeStamp);
    Platform.runLater(()->setValue(value));
  }
  
  /**
   * set the value for CANValue and property
   * @param index - the index of the value
   * @param value - the value to set
   * @param timeStamp
   */
  public void setValue(int index,T value, Date timeStamp) {
    Platform.runLater(()->doSetValue(index,value,timeStamp));
  }
  
  /**
   * set the value at the given index (needs to be run on JavaFX thread!)
   * @param index
   * @param value
   */
  private void doSetValue(int index, T value, Date timeStamp) {
    canValue.setValue(index,value, timeStamp);
    setMinMax(value);
    int triggerIndex=canValue.getCANInfo().getMaxIndex()-1;
    // FIXME check
    triggerIndex=0;
    if (index==triggerIndex) {
      this.updateCountProperty.setValue(this.updateCountProperty.getValue()+1);
    }
  }

  /**
   * set the value (needs to be run on JavaFX thread!)
   * @param value
   */
  private void setValue(T value) {  
    property.setValue(value);
    this.updateCountProperty.setValue(canValue.getUpdateCount());
    setMinMax(value);
  }
  
  /**
   * sets the minimum and maximum Value as well as the update count
   * @param value
   */
  private void setMinMax(T value) {
    if (canValue instanceof DoubleValue) {
      DoubleValue dv=(DoubleValue) canValue;
      if (dv.getMax()!=null)
        getMax().setValue((T) dv.getMax());
      if (dv.getAvg()!=null)
        getAvg().setValue((T) dv.getAvg());
    }
    if (canValue instanceof IntegerValue) {
      IntegerValue iv=(IntegerValue) canValue;
      if (iv.getMax()!=null)
        getMax().setValue((T) iv.getMax());
      if (iv.getAvg()!=null)
        getAvg().setValue((T) iv.getAvg());
    }
  }

  /**
   * get the name of this property
   * @return
   */
  public String getName() {
    String name=canValue.canInfo.getName();
    return name;
  }

  @Override
  public T getValue() {
    return this.getCanValue().getValue();
  }

  @Override
  public boolean isAvailable() {
    if (this.canValue==null)
      return false;
    return this.getCanValue().isAvailable();
  }

  @Override
  public CANInfo getCANInfo() {
    return this.getCanValue().getCANInfo();
  }

  @Override
  public Date getTimeStamp() {
    return this.getCanValue().getTimeStamp();
  }

  @Override
  public int getUpdateCount() {
    return this.getCanValue().getUpdateCount();
  }
}
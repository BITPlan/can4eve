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

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import com.bitplan.csv.CSVUtil;


/**
 * base class for all PidValues
 * 
 * @author wf
 *
 */
public abstract class CANValue<ValueType> {
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.can4eve");
  protected static boolean debug = false;
  protected static List<CANValue<?>> canValues;

  /**
   * a value to be stored
   * @author wf
   *
   * @param <ValueType>
   */
  public static class ValueItem<ValueType> {
    boolean available = false;
    ValueType value;
    Date timeStamp;
    
    public boolean isAvailable() {
      return available;
    }
    public void setAvailable(boolean available) {
      this.available = available;
    }
    public ValueType getValue() {
      return value;
    }
    public void setValue(ValueType value) {
      this.value = value;
    }
    public Date getTimeStamp() {
      return timeStamp;
    }
    public void setTimeStamp(Date timeStamp) {
      this.timeStamp = timeStamp;
    }
  }

  public CANInfo canInfo;

  ValueItem<ValueType> valueItem = new ValueItem<ValueType>();
  private ValueItem<ValueType>[] valueItems; // for indexed CANValues

  private CircularFifoQueue<ValueItem<ValueType>> history;
  private Date previousHistoryTimeStamp;
  long historyMSecs;
  Date startTime;
  private boolean display = false;
  private boolean read = false;
  private int updateCount;
  private Class<?> clazz;

  /**
   * create a canValue for the given CANInfo
   * 
   * @param canInfo
   * @param clazz
   */
  public CANValue(CANInfo canInfo, Class<?> clazz) {
    if (canInfo != null)
      init(canInfo);
    this.clazz = clazz;
  }

  /**
   * initialize my canInfo
   * 
   * @param canInfo
   */
  public void init(CANInfo canInfo) {
    this.canInfo = canInfo;
    if (canInfo.getHistoryValuesPerMinute() > 0)
      this.historyMSecs = 60000 / canInfo.getHistoryValuesPerMinute();
    else
      this.historyMSecs = Long.MAX_VALUE;
    // create a history buffer for 5 hours
    setHistory(new CircularFifoQueue<ValueItem<ValueType>>(
        canInfo.getHistoryValuesPerMinute() * 300 + 1));
  }

  /**
   * activate this CANValue
   */
  public void activate() {
    setRead(true);
    setDisplay(true);
  }

  /**
   * assign value and timeStamp to the given item
   * 
   * @param item
   * @param value
   * @param timeStamp
   * @return
   */
  public ValueItem<ValueType> assign(ValueItem<ValueType> item, ValueType value,
      Date timeStamp) {
    item.value = value;
    item.available = value != null;
    item.timeStamp = timeStamp;
    return item;
  }

  /**
   * set the value at the given index for the given timeStamp
   * 
   * @param value
   * @param timeStamp
   */
  @SuppressWarnings("unchecked")
  public void setValue(int index, ValueType value, Date timeStamp) {
    if (this.startTime == null)
      startTime = timeStamp;
    ValueItem<ValueType> currentItem = null;
    if (index < 0) {
      currentItem = assign(this.valueItem, value, timeStamp);
    } else {
      if (getValueItems() == null) {
        setValueItems(new ValueItem[canInfo.getMaxIndex()]);
        for (int i = 0; i < getValueItems().length; i++) {
          getValueItems()[i] = new ValueItem<ValueType>();
        }
      }
      try {
        currentItem = assign(getValueItems()[index], value, timeStamp);
      } catch (ArrayStoreException ase) {
        throw new RuntimeException(
            "setValue fails due to ArrayStore Exception");
      }
    }
    try {
      if (previousHistoryTimeStamp == null) {
        addToHistory(currentItem);
      } else {
        long msecsAgo = timeStamp.getTime()
            - previousHistoryTimeStamp.getTime();
        if (msecsAgo >= this.historyMSecs) {
          addToHistory(currentItem);
        }
      }
    } catch (Exception e) {
      ErrorHandler.handle(e);
    }
    updateCount++;
    log();
  }

  /**
   * add a valueItem to the history
   * @param currentItem
   */
  private void addToHistory(ValueItem<ValueType> currentItem) {
    if (currentItem.available) {
      ValueItem<ValueType> historyItem = new ValueItem<ValueType>();
      historyItem.available = true;
      historyItem.timeStamp = currentItem.timeStamp;
      historyItem.value = currentItem.value;
      getHistory().add(historyItem);
      previousHistoryTimeStamp = currentItem.timeStamp;
    }
  }

  /**
   * get the value Item
   * @param i
   * @return the item
   */
  public String get(int i) {
    if (getValueItems() != null) {
      ValueType value = getValueItems()[i].value;
      return asString(value);
    } else {
      return "?";
    }
  }

  /**
   * set the Value for the given timestamp
   * 
   * @param value
   * @param timeStamp
   */
  public void setValue(ValueType value, Date timeStamp) {
    setValue(-1, value, timeStamp);
  }

  public ValueItem<ValueType> getValueItem() {
    return valueItem;
  }

  public void setValueItem(ValueItem<ValueType> valueItem) {
    this.valueItem = valueItem;
  }

  public boolean isDisplay() {
    return display;
  }

  public void setDisplay(boolean display) {
    this.display = display;
  }

  protected void log() {
    if (debug) {
      LOGGER.log(Level.INFO, this.canInfo.getTitle() + ":" + asString());
    }
  }

  public ValueType getValue() {
    return valueItem.value;
  }

  public ValueItem<ValueType>[] getValueItems() {
    return valueItems;
  }

  public void setValueItems(ValueItem<ValueType>[] valueItems) {
    this.valueItems = valueItems;
  }

  public CircularFifoQueue<ValueItem<ValueType>> getHistory() {
    return history;
  }

  public void setHistory(CircularFifoQueue<ValueItem<ValueType>> history) {
    this.history = history;
  }

  public boolean isRead() {
    return read;
  }

  public void setRead(boolean read) {
    this.read = read;
  }

  public int getUpdateCount() {
    return updateCount;
  }

  public void setUpdateCount(int updateCount) {
    this.updateCount = updateCount;
  }

  /**
   * convert me to a string
   * 
   * @return
   */
  public String asString(ValueType value) {
    String result = "?";
    try {
      result = String.format(canInfo.getFormat(), value);
    } catch (Throwable th) {
      ErrorHandler.handle(th, this.canInfo.getTitle()+"='"+value+"'");
    }
    return result;
  }

  /**
   * convert me to a string
   * 
   * @return
   */
  public String asString() {
    if (this.valueItem.available)
      return asString(getValue());
    else
      return "-";
  }
  
  public String asCSV() {
    if (this.valueItem.available) {
      String csv=CSVUtil.csv(this.canInfo.title, asString());
      return csv;
    }
    else
      return "";
  }

  public static class IntegerValue extends CANValue<Integer> {
    public IntegerValue(CANInfo canInfo) {
      super(canInfo, Integer.class);
    }
  }

  public static class DoubleValue extends CANValue<Double> {
    public DoubleValue(CANInfo canInfo) {
      super(canInfo, Double.class);
    }

    /**
     * calc the numerical integral and add it
     * 
     * @param value
     * @param timeStamp
     * @param newValue
     * @param newtimeStamp
     * @param factor
     *          for millisecs
     * 
     *          e.g. if RPM is integrated the 1/60000.0
     */
    public void integrate(Integer value, Date timeStamp, int newValue,
        Date newTimeStamp, Double factor) {
      // width of integral
      long msecs = newTimeStamp.getTime() - timeStamp.getTime();
      double average = (newValue + value) / 2;
      double area = average * msecs * factor;
      if (!this.valueItem.available)
        this.setValue(0.0, timeStamp);
      double newIntegral = this.valueItem.value + area;
      this.setValue(newIntegral, newTimeStamp);
    }
  }

  public static class BooleanValue extends CANValue<Boolean> {

    private String trueSymbol;
    private String falseSymbol;

    public BooleanValue(CANInfo canInfo, String trueSymbol,
        String falseSymbol) {
      super(canInfo, Boolean.class);
      this.trueSymbol = trueSymbol;
      this.falseSymbol = falseSymbol;
    }

    public String asString() {
      String result = "?";
      if (valueItem.available) {
        if (this.valueItem.value)
          result = trueSymbol;
        else
          result = falseSymbol;
      }
      return result;
    }
  }

  public static class StringValue extends CANValue<String> {

    public StringValue(CANInfo canInfo) {
      super(canInfo, String.class);
    }

  }
 
  /**
   * Raw Value
   * 
   * @author wf
   *
   */
  public static class CANRawValue extends CANValue<String> {

    public CANRawValue(CANInfo canInfo) {
      super(canInfo, String.class);
    }

    /**
     * set the raw Value based on an array of hex string elements
     * 
     * @param string
     * @param timeStamp
     */
    public void setRawValue(String line, Date timeStamp) {
      super.setValue(line, timeStamp);
    }
  }

}
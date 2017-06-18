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
package com.bitplan.elm327;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wf on 03.06.17.
 */

public class PacketImpl implements Packet {
    Date time;
    long timeStamp;
    String data;
    boolean valid;

    Packet request;
    Packet response;

    static SimpleDateFormat isoDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    
    public PacketImpl() {
        this.updateTimeStamp();
        valid=false;
    }

    /**
     * construct me
     * @param data
     * @param time - the time of the data
     */
    public PacketImpl(String data, Date time) {
      this.time=time;
      this.data=data;
    }

    @Override
    public long getTimeStamp() {
        return timeStamp;
    }
    
    @Override
    public Date getTime() {
      return time;
    }

    @Override
    public Packet getRequest() {
        return request;
    }

    @Override
    public void setRequest(Packet request) {
        this.request = request;
    }

    @Override
    public Packet getResponse() {
        return response;
    }

    @Override
    public void setResponse(Packet response) {
        this.response = response;
    }

    @Override
    public boolean isTimeOut() {
        return data == null;
    }

    public boolean isValid() {
      return valid;
    }

    public void setValid(boolean valid) {
      this.valid = valid;
    }

    @Override
    public String getRawData() {
        return data;
    }

    @Override
    public String getData() {
        if (data==null)
            return null;
        return data.replace(">","").trim();
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public void updateTimeStamp() {
        timeStamp = System.nanoTime();
        time=new Date();
    }

    @Override
    public long getResponseTime() {
        long result = (getResponse().getTimeStamp() - getRequest().getTimeStamp()) / 1000000;
        return result;
    }
    
    /**
     * return me as a string
     * @return
     */
    public String asString() {
      String ts=isoDateFormatter.format(time);
      String result="null";
      if (response!=null)
        result=String.format("%s (%s): %s",ts,getData(),response.getData());
      return result;
    }

}

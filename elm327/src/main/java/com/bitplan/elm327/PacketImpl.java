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


/**
 * Created by wf on 03.06.17.
 */

public class PacketImpl implements Packet {
    long timeStamp;
    String data;

    Packet request;
    Packet response;

    public PacketImpl() {
        this.updateTimeStamp();
    }

    @Override
    public long getTimeStamp() {
        return timeStamp;
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
    }

    @Override
    public long getResponseTime() {
        long result = (getResponse().getTimeStamp() - getRequest().getTimeStamp()) / 1000000;
        return result;
    }
}

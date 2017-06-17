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
 * a transmission to or from the ELM327 device
 * it carries the data and the timestamp when the packet was
 * sent/received and a flag whether a timeout occured
 *
 * Created by wf on 03.06.17.
 *
 */

public interface Packet {
    // when the packet was sent/received
    long getTimeStamp();
    // time in millisecs for response
    long getResponseTime();

    // true if this is an empty package created by a timeout
    boolean isTimeOut();
    // true if this is a package which was received as a response
    boolean isValid();
    void setValid(boolean valid);

    // The raw data for this packet e.g. including echo an caret prompt
    String getRawData();
    String getData();

    void setData(String data);

    // set the timeStamp to the current time
    void updateTimeStamp();

    public Packet getRequest();
    public void setRequest(Packet p);
    public Packet getResponse();
    public void setResponse(Packet p);
    String asString();

}

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
}

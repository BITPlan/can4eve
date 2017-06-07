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

package com.bitplan.elm327;

/**
 * Created by wf on 03.06.17.
 */

public interface Debugable {
    /**
     * is debugging switched on (e.g. by setting a Log?)
     * @return true if debugging is on
     */
    public boolean isDebug();

    /**
     * log the given message
     * @param msg - the message to log
     */
    public void log(String msg);

    /**
     * set the Log to be used for debugging
     * @param log - the log to be used
     */
    public void setLog(Log log);

    /**
     * get the log of me
     * @return my Log
     */
    public Log getLog();

    /**
     * handle the given Throwable
     * @param msg - the message to output with this Throwable
     * @param th - the Throwable to handle
     */
    public void handle(String msg,Throwable th);
}

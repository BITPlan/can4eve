package com.bitplan.elm327;

/**
 * Created by wf on 03.06.17.
 */
import java.io.File;
import java.io.IOException;

public interface Log {
    // log the given message
    public void log(String msg);

    /**
     * handle the given Throwable
     * @param msg - the message to output with this Throwable
     * @param th - the Throwable to handle
     */
    public void handle(String msg,Throwable th);

    /**
     * set a logFile
     * @param logFile - the file to log to
     * @param noConsole - true if the console handler should be switched off
     */
    public void setFile(File logFile, boolean noConsole) throws IOException;
}

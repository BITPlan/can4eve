package com.bitplan.elm327;


/**
 * Created by wf on 03.06.17.
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.io.File;

/**
 * Logging and exceptiion handling
 */
public class LogImpl implements Log {
    protected static Logger LOGGER = Logger.getLogger("com.bitplan.elm327");

    @Override
    public void log(String msg) {
        LOGGER.log(Level.INFO,msg);
    }

    @Override
    public void handle(String msg, Throwable th) {
        LogImpl.handle(this,msg,th);
    }

    /**
     * set the logfile
     * @param logFile
     * @param noConsole
     */
    public void setFile(File logFile, boolean noConsole) throws IOException {
        FileHandler fh = new FileHandler(logFile.getAbsolutePath());
        LOGGER.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
        if (noConsole) {
            LOGGER.setUseParentHandlers(false);
        }
    }

    /**
     * handler for Throwables
     * @param log
     * @param msg
     * @param th
     */
    public static void handle(Log log,String msg, Throwable th) {
        if (log!=null) {
            log.log(msg
                    + th.getClass().getSimpleName() + " message='" + th.getMessage() + "'");
            StringWriter sw = new StringWriter();
            th.printStackTrace(new PrintWriter(sw));
            log.log(sw.toString());
        }
    }
}

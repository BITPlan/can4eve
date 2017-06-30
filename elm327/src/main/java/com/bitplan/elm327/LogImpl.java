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
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.io.File;

/**
 * Logging and exception handling
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

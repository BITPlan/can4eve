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

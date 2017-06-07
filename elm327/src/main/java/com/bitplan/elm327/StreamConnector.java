package com.bitplan.elm327;
/**
 * Copyright 2017 Wolfgang Fahl https://github.com/WolfgangFahl/
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * <p>
 * You may obtain a copy of the License at
 * <p>
 * http:www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * <p>
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * connect the given input stream to the given output stream
 */
public class StreamConnector extends Thread {
    Log log;
    public final static int BUFFER_SIZE = 4096;
    protected boolean running = false;
    String title;

    /**

     * Input stream to read from.
     */
    private InputStream is = null;

    /**
     * Output stream to write to.
     */
    private OutputStream os = null;

    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Specify the streams that this object will connect in the {@link #run()}
     * method.
     *
     * @param is
     *          the InputStream to read from.
     * @param os
     *          the OutputStream to write to.
     */
    public StreamConnector(InputStream is, OutputStream os) {

        this.is = is;
        this.os = os;

    }

    /**
     * Connect the InputStream and OutputStream objects specified in the
     * {@link #StreamConnector(InputStream, OutputStream)} constructor.
     */
    public void run() {
        assert (is != null);
        assert (os != null);

        // If the InputStream or outputstream is null, don't do anything
        if ((is == null) || (os == null))
            return;

        // Connect the streams for ever
        running = true;
        while (running) {
            try {
                int bytesRead = 0;
                byte[] buf = new byte[BUFFER_SIZE];
                while ((bytesRead = is.read(buf)) != -1) {
                    // if one of the streams is vanished
                    if (is==null)
                        break;
                    if (os==null)
                        break;
                    if (bytesRead > 0) {
                        os.write(buf, 0, bytesRead);
                        os.flush();
                    }
                    if (log!=null) {
                        log.log("piped " + bytesRead + " bytes " + title);
                        if (bytesRead<80) {
                            String str = new String(buf,0,bytesRead, "UTF-8");
                            log.log(str);
                        }
                    }
                    yield();
                }
            } catch (IOException e) {
                if (running) {
                    if (log!=null)
                        log.handle("StreamConnector " + title + " issue ", e);
                    // Stream closed might happen e.g. if USB connection is disconnected
                    if ("Stream Closed".equals(e.getMessage())) {
                        halt();
                    }
                }
            }
        } // while
        // we purposely left the while loop so we are not running any more
        running=false;
    }

    /**
     * stop this thread
     */
    public void halt() {
        synchronized (this) {
            is = null;
            os = null;
            running = false;
        }
    }

}
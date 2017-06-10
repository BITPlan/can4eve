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
package com.bitplan.obdii;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * connect the given input stream to the given output stream
 */
public class StreamConnector extends Thread {
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.obdii");
  public final static int BUFFER_SIZE = 4096;
  protected boolean running = false;

  /**
   * Input stream to read from.
   */
  private InputStream is = null;

  /**
   * Output stream to write to.
   */
  private OutputStream os = null;

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
    assert(is!=null);
    assert(os!=null);
    
    // If the InputStream or outputstream is null, don't do anything
    if ((is == null) || (os==null))
      return;

    // Connect the streams for ever
    running = true;
    while (running) {
      try {
        int bytesRead = 0;
        byte[] buf = new byte[BUFFER_SIZE];
        while ((bytesRead = is.read(buf)) != -1) {
          LOGGER.log(Level.INFO,"piping "+bytesRead+" bytes");
          if (os != null && bytesRead > 0) {
            os.write(buf, 0, bytesRead);
            os.flush();
          }
          yield();
        }
      } catch (IOException e) {
        LOGGER.log(Level.WARNING, "StreamConnector issue " + e.getMessage());
      }
    }
  }
  
  /**
   * stop this thread
   */
  public void halt() {
    running=false;
  }

}
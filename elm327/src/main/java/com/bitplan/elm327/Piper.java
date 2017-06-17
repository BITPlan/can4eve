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

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by wf on 04.06.17.
 */

public class Piper {
  InputStream fromIs;
  OutputStream fromOs;
  InputStream toIs;
  OutputStream toOs;

  StreamConnector toFrom = null;
  StreamConnector fromTo = null;
  Log log;

  public Log getLog() {
    return log;
  }

  public void setLog(Log log) {
    this.log = log;
  }

  /**
   * pipe
   *
   * @param fromIs
   * @param fromOs
   * @param toIs
   * @param toOs
   */
  public Piper(InputStream fromIs, OutputStream fromOs, InputStream toIs, OutputStream toOs) {
    this.fromIs = fromIs;
    this.fromOs = fromOs;
    this.toIs = toIs;
    this.toOs = toOs;
  }

  /**
   * start the pipe
   */
  public void pipe() {
    // connect me with a different connection
    // e.g. if i am currently doing input/output to an usb device
    // and the connection is a network socket then
    // all the data received via usb on the input shall go to the network socket
    fromTo = new StreamConnector(fromIs, toOs);
    toFrom = new StreamConnector(toIs, fromOs);
    fromTo.setTitle("from->to");
    toFrom.setTitle("to->from");
    fromTo.setLog(log);
    toFrom.setLog(log);
    toFrom.start();
    fromTo.start();
  }

  /**
   * halt the piping
   */
  public void halt() {
    if (toFrom != null) {
      toFrom.halt();
    }
    if (fromTo != null) {
      fromTo.halt();
    }
  }
}

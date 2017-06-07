package com.bitplan.elm327;

import java.io.IOException;
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

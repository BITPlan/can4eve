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
package com.bitplan.obdii.elm327;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bitplan.can4eve.ErrorHandler;
import com.bitplan.elm327.Packet;

/**
 * implements a LogPlayer
 * 
 * @author wf
 *
 */
public class LogPlayerImpl implements LogPlayer {
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.obdii.elm327");

  public static boolean debug =false;
  boolean open = false;

  private File elmLogFile;
  private RandomAccessLogReader logReader;
  Date logReaderStartDate;
  Date logReaderEndDate;
  List<LogPlayerListener> listeners = new ArrayList<LogPlayerListener>();

  @Override
  public File getLogFile() {
    return elmLogFile;
  }

  @Override
  public void setLogFile(File file) {
    this.elmLogFile = file;
  }

  @Override
  public Date getStartDate() {
    if (logReaderStartDate != null)
      return logReaderStartDate;
    return null;
  }

  @Override
  public Date getEndDate() {
    if (logReaderEndDate != null)
      return logReaderEndDate;
    return null;
  }

  @Override
  public void addListener(LogPlayerListener listener) {
    listeners.add(listener);
  }

  @Override
  public void moveTo(Date date) throws Exception {
    if (debug) {
      SimpleDateFormat moveDateFormatter = new SimpleDateFormat(
          "yyyy-MM-dd hh:mm:ss ");
      LOGGER.log(Level.INFO,
          "monitor moveTo " + moveDateFormatter.format(date));
    }
    // were are called from a different thread - synchronize with the running
    // thread
    synchronized (this) {
      logReader.moveTo(date);
    }
  }

  @Override
  public String getSample() {
    try {
      Packet p = null;
      synchronized (this) {
        p = logReader.nextPacket();
      }
      if (p != null) {
        for (LogPlayerListener listener : this.listeners) {
          listener.onProgress(p.getTime());
        }
        return p.getData();
      }
    } catch (Exception e) {
      ErrorHandler.handle(e);
    }
    return null;
  }

  static LogPlayer instance;

  public static LogPlayer getInstance() {
    if (instance == null) {
      instance = new LogPlayerImpl();
    }
    return instance;
  }

  @Override
  public void open() {
    try {
      logReader = new RandomAccessLogReader(elmLogFile);
      this.logReaderStartDate = logReader.getStartDate();
      this.logReaderEndDate = logReader.getEndDate();
      logReader.open();
      open = true;
      for (LogPlayerListener listener : this.listeners) {
        listener.onOpen();
      }
    } catch (Exception e) {
      ErrorHandler.handle(e);
    }
  }

  @Override
  public boolean isOpen() {
    return open;
  }

  public void start() {
    for (LogPlayerListener listener : this.listeners) {
      listener.onStart();
    }
  }

  @Override
  public void close() throws Exception {
    if (logReader != null) {
      logReader.close();
      logReader=null;
      open = false;
      for (LogPlayerListener listener : this.listeners) {
        listener.onClose();
      }
    }
  }

}

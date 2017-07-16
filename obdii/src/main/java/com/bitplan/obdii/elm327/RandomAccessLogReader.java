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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.io.IOUtils;

import com.bitplan.elm327.Packet;

/**
 * random access log file reader
 * 
 * @author wf
 *
 */
public class RandomAccessLogReader {
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.obdii.elm327");
  private ZipFile zipFile;

  File elmLogFile;

  private Date startDate;
  private Date endDate;

  private RandomAccessFile raf;
  private PacketSeek low;
  private PacketSeek high;

  /**
   * create me based on a (potentially zipped) file
   * 
   * @param file
   * @throws Exception
   */
  public RandomAccessLogReader(File logFile) throws Exception {
    if (logFile.getName().endsWith(".zip")) {
      File unzipped = new File(logFile.getParentFile(), "unzipped");
      zipFile = new ZipFile(logFile);
      Enumeration<? extends ZipEntry> entries = zipFile.entries();
      if (entries.hasMoreElements()) {
        ZipEntry entry = entries.nextElement();
        if (!entry.isDirectory()) {
          if (!unzipped.isDirectory()) {
            unzipped.mkdir();
          }
          elmLogFile = new File(unzipped, entry.getName());
          InputStream in = zipFile.getInputStream(entry);
          OutputStream out = new FileOutputStream(elmLogFile);
          IOUtils.copy(in, out);
          IOUtils.closeQuietly(in);
          out.close();
        }
      }
    } else {
      elmLogFile = logFile;
    }
  }

  /**
   * get the Packet for the given start Position
   * 
   * @param start
   * @return
   * @throws Exception
   */
  public Packet getPacket(long start) throws Exception {
    open();
    PacketSeek ps = getPacket(this.raf, start);
    close();
    if (ps!=null)
      return ps.packet;
    return null;
  }

  /**
   * open me
   * 
   * @throws FileNotFoundException
   */
  public void open() throws FileNotFoundException {
    raf = new RandomAccessFile(elmLogFile, "r");
  }

  /**
   * close me
   * 
   * @throws IOException
   */
  public void close() throws IOException {
    raf.close();
  }

  public class PacketSeek {
    long pos;
    Packet packet;
  }
  
  /**
   * get a line from the Random Access file
   * 
   * @param raf
   *          - the random access file
   * @param start
   * @return
   * @throws Exception
   */
  public PacketSeek getPacket(RandomAccessFile raf, long start) throws Exception {
    // we want an even start if 2 bytes per char
    // long pos = (start % 2 == 0) ? start : start - 1;
    long pos = start;
    if (pos == 0) {
      return nextPacket(raf);
    }
    byte b;
    int count = 0;
    // seek backward
    do {
      pos -= 1;
      raf.seek(pos);
      checkCountLimit("char", count++, true);
    } while (pos > 0 && raf.readByte() != 0x0a);
    // pos = (pos <= 0) ? 0 : pos + 2;
    // raf.seek(pos);
    return nextPacket(raf);
  } // getPacket

  /**
   * check the count limit
   * 
   * @param count
   * @kind
   * @throws Exception
   */
  private boolean checkCountLimit(String kind, int count, boolean dothrow)
      throws Exception {
    final int limit = 1000;
    boolean overlimit = count > limit;
    if (overlimit) {
      if (dothrow)
        throw new Exception("ELM log file " + this.elmLogFile.getName()
            + " not seekable for lines gave up after " + limit + " " + kind
            + " seeks");
    }
    return overlimit;
  }

  /**
   * get the next Packet
   * 
   * @return
   * @throws Exception
   */
  public Packet nextPacket() throws Exception {
    PacketSeek ps=nextPacket(raf);
    if (ps.packet!=null)
      return ps.packet;
    return null;
  }

  /**
   * get the next Packet from the RandomAccessFile
   * 
   * @param raf
   * @return - the Packet
   * @throws Exception
   */
  public PacketSeek nextPacket(RandomAccessFile raf) throws Exception {
    String line = null;
    int count = 0;
    long pos=raf.getFilePointer();
    while ((line = raf.readLine()) != null) {
      Packet p = LogReader.lineAsPacket(line);
      if (p != null) {
        PacketSeek packetSeek=new PacketSeek();
        packetSeek.packet=p;
        packetSeek.pos=pos;
        return packetSeek;
      }
      pos=raf.getFilePointer();
      if (checkCountLimit("line", count++, false))
        return null;
    } // while
    return null;
  } // nextPacket

  /**
   * binary seek a packet
   * @param raf - the random access file
   * @param high 
   * @param low 
   * @param from - from where to seek
   * @param l - to where to seek
   * @return - the seek result
   * @throws Exception
   */
  public PacketSeek binarySeek(RandomAccessFile raf, PacketSeek low, PacketSeek high, long from, long to) throws Exception {
    if (to-from<200)
      return low;
    if (high!=null) {
      return high;
    }
    from=(from+to)/2;
    PacketSeek newlow=this.getPacket(raf,from);
    if (newlow==null)
      return low;
    return binarySeek(raf,newlow,high,from,to);
  }

  /**
   * get the start Date of the logFile
   * 
   * @return
   * @throws Exception
   */
  public Date getStartDate() throws Exception {
    if (startDate == null) {
      RandomAccessFile raf = new RandomAccessFile(elmLogFile, "r");
      Packet p = this.getPacket(0);
      raf.close();
      startDate = p.getTime();
    }
    return startDate;
  }

  /**
   * get the end Date of elmLogFile
   * 
   * @return
   * @throws Exception
   */
  public Date getEndDate() throws Exception {
    if (endDate == null) {
      RandomAccessFile raf = new RandomAccessFile(elmLogFile, "r");
      low = this.getPacket(raf,0);
      high = this.getPacket(raf,raf.length());
      high = this.binarySeek(raf, low, high,0,raf.length());
      endDate = high.packet.getTime();
      raf.close();
    }
    return endDate;
  }
  
  /**
   * get the given isoRange
   * @param from
   * @param to
   * @return the isoRange
   */
  public String getIsoRange(Date from, Date to) {
    String fromDateIso = LogReader.logDateFormatter
        .format(from);
    String toDateIso = LogReader.logDateFormatter
        .format(to);
    return fromDateIso+" - "+toDateIso;
  }

  /**
   * move to the given timestamp
   * 
   * @param timeStamp
   * @throws Exception
   */
  public void moveTo(Date timeStamp) throws Exception {
    if (endDate == null || startDate == null) {
      throw new RuntimeException(
          "moveTo called with getting start and end Date");
    }
    if (timeStamp.before(startDate)) {
      timeStamp = startDate;
    }
    if (timeStamp.after(endDate)) {
      timeStamp = endDate;
    }
    dateSeek(timeStamp, low, high,0);
  }

  /**
   * seek the given timestamp in the range
   * 
   * @param timeStamp
   * @param low
   * @param from
   * @param high
   * @param to
   * @throws Exception
   */
  private void dateSeek(Date timeStamp, PacketSeek low,PacketSeek high,
      int steps) throws Exception {
    if (steps > 30) {
      LOGGER.log(Level.SEVERE,
          "dateSeek recursion not ended after 30 steps - forcing end of recursion");
      return;
    }
    double diffMSecs=(high.packet.getTime().getTime()-low.packet.getTime().getTime())/1000;
    if (diffMSecs<0.5)
      return;
    LOGGER.log(Level.INFO, String.format("%3d: %5.1f secs %s",steps,diffMSecs,this.getIsoRange(low.packet.getTime(),high.packet.getTime())));
    if (high.packet.getTime().after(timeStamp) && low.packet.getTime().before(timeStamp)) {
      long middle = (low.pos+high.pos)/ 2;
      PacketSeek middleP = getPacket(raf, middle);
      if (middleP.packet.getTime().after(timeStamp)) {
        dateSeek(timeStamp, low, middleP, ++steps);
      } else {
        dateSeek(timeStamp, middleP,  high,++steps);
      }
    }
  }
}
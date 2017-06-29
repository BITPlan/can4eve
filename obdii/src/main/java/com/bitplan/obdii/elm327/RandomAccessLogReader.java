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
  private ZipFile zipFile;

  File elmLogFile;

  private Date startDate;
  private Date endDate;

  private RandomAccessFile raf;
  
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
    Packet p = this.getPacket(this.raf, start);
    close();
    return p;
  }
 
  public void open() throws FileNotFoundException {
     raf= new RandomAccessFile(elmLogFile, "r");
  }
  public void close() throws IOException {
    raf.close();
  }
  /**
   * get a line from the Random Access file
   * @param raf - the random access file
   * @param start
   * @return
   * @throws Exception 
   */
  public Packet getPacket(RandomAccessFile raf,long start) throws Exception {
    // we want an even start if 2 bytes per char
    // long pos = (start % 2 == 0) ? start : start - 1;
    long pos=start;
    if (pos == 0) {
      return nextPacket(raf);
    }
    byte b;
    int count = 0;
    // seek backward
    do {
      pos -= 1;
      raf.seek(pos);
      checkCountLimit("char",count++,true);
    } while (pos > 0 && raf.readByte() != 0x0a);
    //pos = (pos <= 0) ? 0 : pos + 2;
    //raf.seek(pos);
    return nextPacket(raf);
  } // getPacket

  /**
   * check the count limit
   * @param count
   * @kind
   * @throws Exception
   */
  private boolean checkCountLimit(String kind,int count, boolean dothrow) throws Exception {
    final int limit=1000;
    boolean overlimit=count>limit;
    if (overlimit) {
      if (dothrow)
        throw new Exception("ELM log file "+this.elmLogFile.getName()+" not seekable for lines gave up after "+limit+" "+kind+" seeks"); 
    }
    return overlimit;
  }
  
  /**
   * get the next Packet
   * @return
   * @throws Exception
   */
  public Packet nextPacket() throws Exception {
    return nextPacket(raf);
  }

  /**
   * get the next Packet from the RandomAccessFile
   * @param raf
   * @return - the Packet
   * @throws Exception 
   */
  public Packet nextPacket(RandomAccessFile raf) throws Exception {
    String line = null;
    int count=0;
    while ((line = raf.readLine()) != null) {
      Packet p = LogReader.lineAsPacket(line);
      if (p != null)
        return p;
      if (checkCountLimit("line",count++,false)) 
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
  public Packet binarySeek(RandomAccessFile raf, Packet low, Packet high, long from, long to) throws Exception {
    if (to-from<200)
      return low;
    if (high!=null) {
      return high;
    }
    from=(from+to)/2;
    Packet newlow=this.getPacket(raf,from);
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
      Packet p=this.getPacket(0);
      raf.close();
      startDate = p.getTime();
    }
    return startDate;
  }

  /**
   * get the end Date of elmLogFile
   * @return
   * @throws Exception
   */
  public Date getEndDate() throws Exception {
    if (endDate==null) {
      RandomAccessFile raf = new RandomAccessFile(elmLogFile, "r");
      Packet low=this.getPacket(0);
      Packet high=this.getPacket(raf.length());
      Packet p = this.binarySeek(raf, low,high,0, raf.length());
      endDate=p.getTime();
      raf.close();
    }
    return endDate;
  }
}
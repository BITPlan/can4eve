package com.bitplan.android.usbserial;

import com.felhr.usbserial.UsbSerialDevice;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Created by wf on 04.06.17.
 */

public class UsbInputStream extends InputStream {
  UsbSerialDevice device;
  // https://stackoverflow.com/questions/4332264/wrapping-a-bytebuffer-with-an-inputstream

  ByteBuffer buf=ByteBuffer.allocate(8192);

  public UsbInputStream(UsbSerialDevice device) {
    this.device=device;
  }

  @Override
  public int available() {
    if (!buf.hasRemaining()) {
      return -1;
    }
    return buf.remaining();
  }

  @Override
  public int read() throws IOException {
    if (!buf.hasRemaining()) {
      return -1;
    }
    return buf.get() & 0xFF;
  }

  @Override
  public int read(byte[] bytes, int off, int len)
    throws IOException {
    if (!buf.hasRemaining()) {
      return -1;
    }

    len = Math.min(len, buf.remaining());
    buf.get(bytes, off, len);
    return len;
  }


  /**
   * callback receiving of bytes
   * @param bytes
   */
  public void received(byte[] bytes) {
    buf.put(bytes);
  }
}

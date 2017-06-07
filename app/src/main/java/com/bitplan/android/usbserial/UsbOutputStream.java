package com.bitplan.android.usbserial;

import com.felhr.usbserial.UsbSerialDevice;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by wf on 04.06.17.
 */

public class UsbOutputStream extends OutputStream {
  UsbSerialDevice device;

  public UsbOutputStream(UsbSerialDevice device) {
    this.device=device;
  }
  @Override
  public void write(int b) throws IOException {
    byte[] bytes = {(byte)b};
    write(bytes);
  }

  @Override
  public void write(byte[] bytes) throws IOException {
    device.write(bytes);
  }
}

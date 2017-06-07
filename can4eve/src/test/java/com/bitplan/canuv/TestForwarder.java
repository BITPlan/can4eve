package com.bitplan.canuv;

import com.bitplan.caneve.Can4eve;

import org.junit.Test;


/**
 * Created by wf on 05.06.17.
 */

public class TestForwarder {
  @Test
  public void testCanUvForwarding() {
    Can4eve can4eve = new Can4eve();
    String args[] = {"-f", "-d", "--device", "cu.usbserial-113010822821"};
    can4eve.maininstance(args);
  }
}



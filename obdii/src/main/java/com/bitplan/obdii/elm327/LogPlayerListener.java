package com.bitplan.obdii.elm327;

import java.util.Date;

/**
 * listener for LogPlayer
 * @author wf
 *
 */
public interface LogPlayerListener {
  public void onOpen();
  public void onProgress(Date currentDate);
}

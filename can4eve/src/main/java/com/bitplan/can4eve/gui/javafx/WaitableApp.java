package com.bitplan.can4eve.gui.javafx;

import com.bitplan.can4eve.gui.Display;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * Waitable Application that does not need launch
 * 
 * @author wf
 *
 */
public abstract class WaitableApp extends Application implements Display {
  protected Stage stage;

  @SuppressWarnings("restriction")

  public static void toolkitInit() {
    com.sun.javafx.application.PlatformImpl.startup(() -> {
    });
  }

  @Override
  public void start(Stage stage) {
    this.stage = stage;
  }

  /**
   * wait for close
   * 
   * @throws InterruptedException
   */
  public void waitStatus(boolean open) {
    int sleep = 1000 / 50; // human eye reaction time
    try {
      if (open)
        while ((stage == null) || (!stage.isShowing())) {
          Thread.sleep(sleep);
        }
      else
        while (stage != null && stage.isShowing()) {
          Thread.sleep(sleep);
        }
    } catch (InterruptedException e) {
      com.bitplan.can4eve.ErrorHandler.handle(e);
    }
  }

  public void waitOpen() {
    waitStatus(true);
  }

  public void waitClose() {
    waitStatus(false);
  }

  /**
   * show me
   */
  public void show() {

    Platform.runLater(() -> {
      try {
        this.start(new Stage());
      } catch (Exception e) {
        com.bitplan.can4eve.ErrorHandler.handle(e);
      }
    });
  }

  /**
   * close this display
   */
  public void close() {
    if (stage != null)
      Platform.runLater(() -> stage.close());
  }

}

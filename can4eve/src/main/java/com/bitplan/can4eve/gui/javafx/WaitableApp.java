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
package com.bitplan.can4eve.gui.javafx;

import com.bitplan.can4eve.gui.Display;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Waitable Application that does not need launch
 * 
 * @author wf
 *
 */
public abstract class WaitableApp extends Application implements Display {
  protected Stage stage;
  static boolean toolkitStarted;

  /**
   * allow startup without launch
   */
  @SuppressWarnings("restriction")
  public static void toolkitInit() {
    if (!toolkitStarted) {
      toolkitStarted = true;
      // do not exit on close of last window
      // https://stackoverflow.com/a/10217157/1497139
      Platform.setImplicitExit(false);
      /// https://stackoverflow.com/a/38883432/1497139
      // http://www.programcreek.com/java-api-examples/index.php?api=com.sun.javafx.application.PlatformImpl
      com.sun.javafx.application.PlatformImpl.startup(() -> {
      });
    }
  }

  /**
   * get SceneBounds
   * @param screenPercent
   * @return
   */
  public Rectangle2D getSceneBounds(int screenPercent, int xDiv, int yDiv) {
    Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
    double screenWidth = primScreenBounds.getWidth();
    double screenHeight = primScreenBounds.getHeight();
    double sceneWidth = screenWidth * screenPercent / 100.0;
    double sceneHeight = screenHeight * screenPercent / 100.0;
    double x=(primScreenBounds.getWidth() - sceneWidth) / xDiv;
    double y=(primScreenBounds.getHeight() - sceneHeight) / yDiv;
    Rectangle2D sceneBounds = new Rectangle2D(x,y,sceneWidth,sceneHeight);
    return sceneBounds;
  }

  @Override
  public void start(Stage stage) {
    this.stage = stage;
  }

  public Stage getStage() {
    return stage;
  }

  public void setStage(Stage stage) {
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
    if (stage != null)
      return;
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
    this.waitClose();
  }

}

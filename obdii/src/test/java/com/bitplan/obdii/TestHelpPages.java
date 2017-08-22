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
package com.bitplan.obdii;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.bitplan.can4eve.util.TaskLaunch;
import com.bitplan.javafx.WaitableApp;
import com.bitplan.javafx.XYTabPane;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * tests which create screenshots for http://can4eve.bitplan.com/index.php/Help
 * as a side effect
 * 
 * @author wf
 *
 */
public class TestHelpPages {
  boolean debug = true;

  private OBDMain obdMain;

  /**
   * start the OBDMain software
   * 
   * @return nothing
   */
  public Void startOBDMain() {
    OBDMain.testMode = true;
    obdMain = new OBDMain();
    String[] args = {};
    obdMain.maininstance(args);
    return null;
  }

  /**
   * create a snapShot
   * @param stage
   * @param title
   */
  public void snapShot(Stage stage, String title)  {
    //PauseTransition pause = new PauseTransition(Duration.millis(500));
    //pause.setOnFinished(event -> {
      try {
        File snapShotDir = File.createTempFile("can4eve", "snapShots")
            .getParentFile();
        File snapShot = new File(snapShotDir, title + ".png");
        if (debug)
          System.out.println(snapShot.getAbsolutePath());
        WaitableApp.saveAsPng(stage, snapShot);
      } catch (IOException e) {
        ErrorHandler.handle(e);
      }
     
    //});
    //pause.play();
  }
  
  public void loopTabs(JFXTripletDisplay jfxDisplay) {
    XYTabPane xyTabPane = jfxDisplay.getXyTabPane();
    ObservableList<Tab> vtabs = xyTabPane.getvTabPane().getTabs();
    for (Tab vtab : vtabs) {
      xyTabPane.getvTabPane().getSelectionModel().select(vtab);
      TabPane hTabPane = xyTabPane.getSelectedTabPane();
      ObservableList<Tab> htabs = hTabPane.getTabs();

      if (vtab.getTooltip() != null) {
        String vTitle = vtab.getTooltip().getText();
        for (Tab htab : htabs) {
          hTabPane.getSelectionModel().select(htab);
          String title = htab.getTooltip().getText();
          System.out.println(vTitle + "_" + title);
          snapShot(jfxDisplay.getStage(),vTitle+"_"+title);
        }
      }
    }

  }

  @Test
  public void testTabs() throws Exception {
    WaitableApp.toolkitInit();
    TaskLaunch.start(() -> startOBDMain());
    while (obdMain == null || obdMain.canValueDisplay == null)
      Thread.sleep(10);
    JFXTripletDisplay jfxDisplay = (JFXTripletDisplay) obdMain.canValueDisplay;
    jfxDisplay.waitOpen();
    Thread.sleep(2000);
    Platform.runLater(()->loopTabs(jfxDisplay));
    Thread.sleep(5000);
  }
}

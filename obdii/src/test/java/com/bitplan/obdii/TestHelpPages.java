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

import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import org.junit.Test;

import com.bitplan.can4eve.gui.javafx.WaitableApp;
import com.bitplan.can4eve.util.TaskLaunch;

/**
 * tests which create screenshots for http://can4eve.bitplan.com/index.php/Help
 * as a side effect
 * 
 * @author wf
 *
 */
public class TestHelpPages {

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
   * 
   * @param stage
   * @param title
   * @throws Exception
   */
  public void snapShot(Stage stage,String title) throws Exception {
    File snapShotDir=File.createTempFile("can4eve", "snapShots").getParentFile();
    File snapShot=new File(snapShotDir,title+".png");
    System.out.println(snapShot.getAbsolutePath());
    Platform.runLater(()->WaitableApp.saveAsPng(stage, snapShot));
  }

  @Test
  public void testMenu() throws Exception {
    WaitableApp.toolkitInit();
    TaskLaunch.start(() -> startOBDMain());
    while (obdMain == null || obdMain.canValueDisplay == null)
      Thread.sleep(10);
    JFXTripletDisplay jfxDisplay = (JFXTripletDisplay) obdMain.canValueDisplay;
    while (jfxDisplay.getMenuBar() == null || jfxDisplay.getActiveTabPane()==null)
      Thread.sleep(20);
    Thread.sleep(1000);
    TabPane tabPane = jfxDisplay.getActiveTabPane();
    for (int tabIndex = 0; tabIndex < tabPane.getTabs()
        .size(); tabIndex++) {
      tabPane.getSelectionModel().select(tabIndex);
      Tab tab = tabPane.getSelectionModel().getSelectedItem();
      snapShot(jfxDisplay.getStage(),tab.getText());
    }
  }
}

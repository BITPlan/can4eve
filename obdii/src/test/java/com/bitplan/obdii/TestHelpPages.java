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

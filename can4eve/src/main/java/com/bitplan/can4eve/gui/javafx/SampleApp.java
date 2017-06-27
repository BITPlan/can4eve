package com.bitplan.can4eve.gui.javafx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

/**
 * create a sample App
 * @author wf
 *
 */
public class SampleApp extends Application {
  private String title;
  private Region region;

  @SuppressWarnings("restriction")
  
  public static void toolkitInit() {
    com.sun.javafx.application.PlatformImpl.startup(() -> {
    });
  }
  
  /**
   * construct the given Sample App
   * @param title
   * @param region
   */
  public SampleApp(String title, Region region) {
    
    this.title = title;
    this.region = region;
  }

  @Override
  public void start(Stage stage) {
    stage.setTitle(title);
    Scene scene = new Scene(region, 800, 600);
    stage.setScene(scene);
    stage.show();
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
}

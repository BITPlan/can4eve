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

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import com.bitplan.can4eve.gui.Display;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.stage.Screen;
import javafx.stage.Stage;
import sun.awt.image.IntegerComponentRaster;

/**
 * Waitable Application that does not need launch
 * 
 * @author wf
 *
 */
public abstract class WaitableApp extends Application implements Display {
  protected Stage stage;
  static boolean toolkitStarted;
  File screenShot=null;

  public static double getScreenWidth() {
    return Screen.getPrimary().getVisualBounds().getWidth();
  }

  public static double getScreenHeight() {
    return Screen.getPrimary().getVisualBounds().getHeight();
  }
  
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
    double sceneWidth = getScreenWidth() * screenPercent / 100.0;
    double sceneHeight = getScreenHeight() * screenPercent / 100.0;
    double x=(getScreenWidth() - sceneWidth) / xDiv;
    double y=(getScreenHeight() - sceneHeight) / yDiv;
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
  
  @Override
  public void browse(String url) {
    this.getHostServices().showDocument(url);
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
  
  /**
   * save me as a Png File
   * @param file
   * @return 
   */
  public static void saveAsPng(Stage stage,File file) {
    WritableImage image = stage.getScene().snapshot(null);
    try {
        ImageIO.write(fromFXImage(image, null), "png", file);
    } catch (IOException e) {
        // TODO: handle exception here
    }
  }
  
  /**
   * Snapshots the specified JavaFX {@link Image} object and stores a
   * copy of its pixels into a {@link BufferedImage} object, creating
   * a new object if needed.
   * The method will only convert a JavaFX {@code Image} that is readable
   * as per the conditions on the
   * {@link Image#getPixelReader() Image.getPixelReader()}
   * method.
   * If the {@code Image} is not readable, as determined by its
   * {@code getPixelReader()} method, then this method will return null.
   * If the {@code Image} is a writable, or other dynamic image, then
   * the {@code BufferedImage} will only be set to the current state of
   * the pixels in the image as determined by its {@link PixelReader}.
   * Further changes to the pixels of the {@code Image} will not be
   * reflected in the returned {@code BufferedImage}.
   * <p>
   * The optional {@code BufferedImage} parameter may be reused to store
   * the copy of the pixels.
   * A new {@code BufferedImage} will be created if the supplied object
   * is null, is too small or of a type which the image pixels cannot
   * be easily converted into.
   * 
   * @param img the JavaFX {@code Image} to be converted
   * @param bimg an optional {@code BufferedImage} object that may be
   *        used to store the returned pixel data
   * @return a {@code BufferedImage} containing a snapshot of the JavaFX
   *         {@code Image}, or null if the {@code Image} is not readable.
   * @since JavaFX 2.2
   */
  public static BufferedImage fromFXImage(Image img, BufferedImage bimg) {
      PixelReader pr = img.getPixelReader();
      if (pr == null) {
          return null;
      }
      int iw = (int) img.getWidth();
      int ih = (int) img.getHeight();
      if (bimg != null) {
          int type = bimg.getType();
          int bw = bimg.getWidth();
          int bh = bimg.getHeight();
          if (bw < iw || bh < ih ||
              (type != BufferedImage.TYPE_INT_ARGB &&
               type != BufferedImage.TYPE_INT_ARGB_PRE))
          {
              bimg = null;
          } else if (iw < bw || ih < bh) {
              Graphics2D g2d = bimg.createGraphics();
              g2d.setComposite(AlphaComposite.Clear);
              g2d.fillRect(0, 0, bw, bh);
              g2d.dispose();
          }
      }
      if (bimg == null) {
          bimg = new BufferedImage(iw, ih, BufferedImage.TYPE_INT_ARGB_PRE);
      }
      IntegerComponentRaster icr = (IntegerComponentRaster) bimg.getRaster();
      int offset = icr.getDataOffset(0);
      int scan = icr.getScanlineStride();
      int data[] = icr.getDataStorage();
      WritablePixelFormat<IntBuffer> pf = (bimg.isAlphaPremultiplied() ?
                                           PixelFormat.getIntArgbPreInstance() :
                                           PixelFormat.getIntArgbInstance());
      pr.getPixels(0, 0, iw, ih, pf, data, offset, scan);
      return bimg;
  }
  

}

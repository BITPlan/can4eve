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
package com.bitplan.obdii.javafx;

import java.net.URL;

import javafx.beans.property.DoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 * a button with two Image States
 * 
 * @author wf
 *
 */
public class ImageButton extends Button {
  ImageView iv;
  
  public DoubleProperty imageWidthProperty() {
    return iv.fitWidthProperty();
  }
  
  public DoubleProperty imageHeightProperty() {
    return iv.fitHeightProperty();
  }
  
  /**
   * construct an ImageButton with to Images
   * 
   * @param pushed
   * @param unpushed
   */
  public ImageButton(final Image pushed, final Image unpushed) {
    iv=new ImageView(unpushed);
    //this.getChildren().add(iv);
    super.setGraphic(iv);
    super.setStyle("-fx-background-color: rgba(255, 255, 255, 0);");

    setOnMousePressed(new EventHandler<MouseEvent>() {
      public void handle(MouseEvent evt) {
        iv.setImage(pushed);
      }
    });
    
    setOnMouseReleased(new EventHandler<MouseEvent>() {
      public void handle(MouseEvent evt) {
        iv.setImage(unpushed);
      }
    });

  }
  
  /**
   * get an ImageButton for the given Icons
   * @param pushedIcon
   * @param unpushedIcon
   */
  public ImageButton(final String pushedIcon, final String unpushedIcon) {
    this(getImageIcon(pushedIcon),getImageIcon(unpushedIcon));
  }
  
  /**
   * get an ImageIcon for the given iconName
   * @param iconName
   * @return
   */
  public static Image getImageIcon(String iconName) {
    URL resource = ImageButton.class.getResource("/icons/" + iconName);
    if (resource == null) {
      throw new RuntimeException("icon " + iconName + " missing");
    }
    String imgUrl = resource.toExternalForm();
    Image img = new Image(imgUrl);
    return img;
  }
  
}
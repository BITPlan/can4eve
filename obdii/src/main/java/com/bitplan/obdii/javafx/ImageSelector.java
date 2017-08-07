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

import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

/**
 * select an image from a list of images
 * @author wf
 *
 */
public class ImageSelector extends GridPane {

  private ChoiceBox<String> choice;
  List<ImageView> imageViews=new ArrayList<ImageView>();
  
  /**
   * get the choice
   * @return
   */
  public ChoiceBox<String> getChoice() {
    return choice;
  }
  
  /**
   * create an Image Selector for the given selection and images 
   * @param selections
   * @param pictures
   */
  public ImageSelector(String[] selections, String[] pictures) {
    choice=new ChoiceBox<String>(
        FXCollections.observableArrayList(
             selections));
    BorderPane imageFrame = new BorderPane();
    for (String picture:pictures) {
      String imgUrl = getClass().getResource("/pictures/"+picture).toExternalForm();
      Image img=new Image(imgUrl);
      ImageView imageView=new ImageView(img);
      imageView.setUserData(imgUrl);
      imageViews.add(imageView);
    }
    choice.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
      @Override
      public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
        ImageView imageView = imageViews.get(newValue.intValue());
        imageFrame.setCenter(imageView);
      }
    });
    choice.getSelectionModel().select(0);
    add(choice, 0, 1);
    add(imageFrame, 0,2);
  }

}

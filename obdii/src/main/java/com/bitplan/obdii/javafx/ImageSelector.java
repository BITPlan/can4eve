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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class ImageSelector<T> extends GridPane {

  ChoiceBox<T> choice;
  List<ImageView> imageViews=new ArrayList<ImageView>();
  Map<T,ImageView> imageViewMap=new HashMap<T,ImageView>();
  T[] selections;
  String[] pictures;
  String title;
  
  /**
   * get the choice
   * @return
   */
  public ChoiceBox<T> getChoice() {
    return choice;
  }
  
  public T[] getSelections() {
    return selections;
  }

  public void setSelections(T[] selections) {
    this.selections = selections;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * create an Image Selector for the given selection and images 
   * @param title 
   * @param selections
   * @param pictures
   */
  public ImageSelector(String title, T[] selections, String[] pictures) {
    this.title=title;
    this.setSelections(selections);
    this.pictures=pictures;
    choice=new ChoiceBox<T>(
        FXCollections.observableArrayList(
             selections));
    BorderPane imageFrame = new BorderPane();
    int index=0;
    for (String picture:pictures) {
      URL resource = getClass().getResource("/pictures/"+picture);
      if (resource==null) {
        throw new RuntimeException("picture "+picture+" missing");
      }
      String imgUrl=resource.toExternalForm();
      Image img=new Image(imgUrl);
      ImageView imageView=new ImageView(img);
      imageView.setUserData(imgUrl);
      imageViews.add(imageView);
      imageViewMap.put(selections[index++], imageView);
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
  
  public T getSelection() {
    return choice.getSelectionModel().getSelectedItem();
  }
  
  public ImageView getImageView(T t) {
    return imageViewMap.get(t);
  }

}

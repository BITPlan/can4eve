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

import java.util.Date;

import com.bitplan.obdii.elm327.LogPlayer;
import com.bitplan.obdii.elm327.LogPlayerListener;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.util.Duration;

/**
 * Pane for Simulator information
 * 
 * @author wf
 *
 */
public class SimulatorPane extends ConstrainedGridPane
    implements LogPlayerListener {

  private Slider slider;
  private TextField fileField;
  private Label playTime;
  private LogPlayer logPlayer;
  private Duration duration;
  private Duration elapsed;
  private boolean humanSliderMovement = false;
  private boolean computerChange = false;

  /**
   * construct me
   * 
   * @param logPlayer
   * @param e
   * @param d
   */
  public SimulatorPane(LogPlayer logPlayer) {
    this.logPlayer = logPlayer;
    logPlayer.addListener(this);
    fileField = new TextField();
    fileField.setEditable(false);
    slider = new Slider();
    playTime = new Label();
    this.add(fileField, 0, 0);
    this.add(slider, 1, 0);
    this.add(playTime, 2, 0);
    super.fixColumnSizes(5, 25, 60, 15);
    super.fixRowSizes(0, 100);
    slider.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
      @Override
      public void changed(
              ObservableValue<? extends Boolean> observableValue,
              Boolean wasChanging,
              Boolean changing) {
            if (changing) {
              if (!computerChange) {
                humanSliderMovement=true;
              }
            } else {
              if (humanSliderMovement) {
                onSliderNewHumanValue();
                humanSliderMovement=false;
              }
            }
          }
    });
  } // SimulatorPane 
  
  /**
   * we have got a new SliderNewHumanValue
   */
  protected void onSliderNewHumanValue() {
    long newTime=(long) (this.logPlayer.getStartDate().getTime()+slider.getValue());
    logPlayer.moveTo(new Date(newTime));
  }

  /**
   * get the textFileFeed
   * 
   * @return
   */
  public TextField getFileField() {
    return fileField;
  }

  /**
   * format the times
   * 
   * @param elapsed
   * @param duration
   * @return
   */
  public static String formatTime(Duration elapsed, Duration duration) {
    int intElapsed = (int) Math.floor(elapsed.toSeconds());
    int elapsedHours = intElapsed / (60 * 60);
    if (elapsedHours > 0) {
      intElapsed -= elapsedHours * 60 * 60;
    }
    int elapsedMinutes = intElapsed / 60;
    int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
        - elapsedMinutes * 60;

    if (duration.greaterThan(Duration.ZERO)) {
      int intDuration = (int) Math.floor(duration.toSeconds());
      int durationHours = intDuration / (60 * 60);
      if (durationHours > 0) {
        intDuration -= durationHours * 60 * 60;
      }
      int durationMinutes = intDuration / 60;
      int durationSeconds = intDuration - durationHours * 60 * 60
          - durationMinutes * 60;
      if (durationHours > 0) {
        return String.format("%d:%02d:%02d/%d:%02d:%02d", elapsedHours,
            elapsedMinutes, elapsedSeconds, durationHours, durationMinutes,
            durationSeconds);
      } else {
        return String.format("%02d:%02d/%02d:%02d", elapsedMinutes,
            elapsedSeconds, durationMinutes, durationSeconds);
      }
    } else {
      if (elapsedHours > 0) {
        return String.format("%d:%02d:%02d", elapsedHours, elapsedMinutes,
            elapsedSeconds);
      } else {
        return String.format("%02d:%02d", elapsedMinutes, elapsedSeconds);
      }
    }
  }

  @Override
  public void onOpen() {
    elapsed = Duration.ZERO;
    duration = new Duration(
        logPlayer.getEndDate().getTime() - logPlayer.getStartDate().getTime());
    Platform.runLater(() -> updateElapsed());
  }

  @Override
  public void onProgress(Date currentDate) {
    elapsed = new Duration(
        currentDate.getTime() - logPlayer.getStartDate().getTime());
    Platform.runLater(() -> updateElapsed());
  }

  /**
   * update the Elapsed Valuedd
   */
  private void updateElapsed() {
    if (!humanSliderMovement) {
      computerChange = true;
      slider.setMax(duration.toSeconds());
      slider.setValue(elapsed.toSeconds());
      computerChange = false;
    }
    String durationText = formatTime(elapsed, duration);
    this.playTime.setText(durationText);
  }

}

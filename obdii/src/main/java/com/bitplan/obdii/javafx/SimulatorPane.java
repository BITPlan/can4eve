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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import com.bitplan.javafx.ConstrainedGridPane;
import com.bitplan.obdii.ErrorHandler;
import com.bitplan.obdii.elm327.LogPlayer;
import com.bitplan.obdii.elm327.LogPlayerListener;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
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
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.obdii.javafx");
  public static boolean debug = false;
  private Slider slider;
  private TextField fileField;
  private Label playTime;
  private Button playButton;
  private LogPlayer logPlayer;
  private Duration duration;
  private Duration elapsed;
  private boolean humanSliderMovement = false;
  private boolean computerChange = false;
  private GlyphFont fontAwesome;
  private Glyph pause;
  private Glyph play;
  private boolean started = false;
  private MonitorControl monitorControl;

  public Duration getDuration() {
    return duration;
  }

  /**
   * construct me
   * 
   * @param logPlayer
   * @param javaFXDisplay
   * @param e
   * @param d
   */
  public SimulatorPane(LogPlayer logPlayer, MonitorControl monitorControl) {
    this.monitorControl = monitorControl;
    this.logPlayer = logPlayer;
    logPlayer.addListener(this);
    fileField = new TextField();
    fileField.setEditable(false);
    slider = new Slider();
    playTime = new Label();
    fontAwesome = GlyphFontRegistry.font("FontAwesome");
    pause = fontAwesome.create(FontAwesome.Glyph.PAUSE);
    play = fontAwesome.create(FontAwesome.Glyph.PLAY);
    playButton = new Button("", play);
    this.add(fileField, 0, 0);
    this.add(playButton, 1, 0);
    this.add(slider, 2, 0);
    this.add(playTime, 3, 0);
    super.fixColumnSizes(5, 25, 5, 55, 15);
    super.fixRowSizes(0, 100);
    slider.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
      @Override
      public void changed(ObservableValue<? extends Boolean> observableValue,
          Boolean wasChanging, Boolean changing) {
        if (changing) {
          if (!computerChange) {
            humanSliderMovement = true;
          }
        } else {
          if (humanSliderMovement) {
            onSliderNewHumanValue();
            humanSliderMovement = false;
          }
        }
      }
    });
    playButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        play();
      }
    });
  } // SimulatorPane

  /**
   * we have got a new SliderNewHumanValue
   */
  protected void onSliderNewHumanValue() {
    long newTime = (long) (this.logPlayer.getStartDate().getTime()
        + slider.getValue() * 1000);
    try {
      if (debug)
        LOGGER.log(Level.INFO, "new time set");
      logPlayer.moveTo(new Date(newTime));
    } catch (Exception e) {
      // FIXME GUI error handling via interface?
      ErrorHandler.handle(e);
    }
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
   * get the time string for a given number of seconds
   * https://stackoverflow.com/a/6118983/1497139
   * 
   * @param totalSecs
   * @return the timestring
   */
  public String getTimeString(double totalSecsDouble) {
    long totalSecs = (long) Math.floor(totalSecsDouble);
    long days = totalSecs / 86400;
    long hours = totalSecs / 3600;
    long minutes = (totalSecs % 3600) / 60;
    long seconds = totalSecs % 60;
    // TODO i18n
    if (days > 0)
      return String.format("%2d d %02d:%02d:%02d", days, hours, minutes,
          seconds);
    else if (hours > 0)
      return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    else
      return String.format("%02d:%02d", minutes, seconds);
  }

  /**
   * format the times
   * 
   * @param elapsed
   * @param duration
   * @return
   */
  public String formatTime(Duration elapsed, Duration duration) {
    if (duration.greaterThan(Duration.ZERO)) {
      return getTimeString(elapsed.toSeconds()) + "/"
          + getTimeString(duration.toSeconds());
    } else {
      return "";
    }
  }

  @Override
  public void onOpen() {
    elapsed = Duration.ZERO;
    duration = new Duration(
        logPlayer.getEndDate().getTime() - logPlayer.getStartDate().getTime());
    Platform.runLater(() -> {
      setTickMarks();
      getFileField().setText(logPlayer.getLogFile().getName());
      updateElapsed();
    });
  }

  private void setTickMarks() {
    double secs = duration.toSeconds();
    // up to 24 minutes
    if (secs<24*60) {
      // max 24 ticks 1  minute each
      slider.setMajorTickUnit(60);
    } else if (secs<24*300) {
      // max 24 ticks 5 minutes each
      slider.setMajorTickUnit(300);
      slider.setMinorTickCount(5);
    } else{
      // normally we'll end up with less than 10 tick 1 hour each
      // but could be a full day 24 ticks
      slider.setMajorTickUnit(3600);
      slider.setMinorTickCount(4);
    }
    slider.setShowTickMarks(true);
    
  }

  @Override
  public void onStart() {
    Platform.runLater(() -> {
      playButton.setDisable(false);
      playButton.setGraphic(pause);
    });
  }

  @Override
  public void onClose() {
    if (started)
      monitorControl.stopMonitoring();
    monitorControl.closeSimulation();
  }

  /**
   * playButton has bin pressed
   */
  protected void play() {
    if (this.monitorControl != null) {
      if (!started) {
        playButton.setDisable(true);
        monitorControl.startMonitoring(false);
        started = true;
      } else {
        monitorControl.stopMonitoring();
        started = false;
        Platform.runLater(() -> {
          playButton.setGraphic(play);
        });
      }
    }

  }

  @Override
  public void onProgress(Date currentDate) {
    elapsed = new Duration(
        currentDate.getTime() - logPlayer.getStartDate().getTime());
    Platform.runLater(() -> updateElapsed());
  }

  /**
   * set the elapsed time
   * 
   * @param seconds
   */
  public void setElapsed(double seconds) {
    elapsed = new Duration(seconds * 1000);
    updateElapsed();
  }

  /**
   * update the Elapsed Value
   */
  private void updateElapsed() {
    if (!humanSliderMovement) {
      computerChange = true;
      slider.setMax(duration.toSeconds());
      slider.setValue(elapsed.toSeconds());
      computerChange = false;
    }
    String durationText = formatTime(elapsed, duration);
    if (debug)
      LOGGER.log(Level.INFO, durationText);
    this.playTime.setText(durationText);
  }

}

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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.bitplan.can4eve.CANValue;

/**
 * Swing based Display
 * @author wf
 *
 */
public class TripletDisplay extends SwingDisplay implements CANValueDisplay, ActionListener {
  protected LabelField fpsField;

  @Override
  public void actionPerformed(ActionEvent e) {
    Object source = e.getSource();
    if (source instanceof JButton) {
      JButton button = (JButton)source;
      if ("stop".equals(button.getName())) {
        System.exit(1);
      }
    }
  }
  
  /**
   * create the TripletDisplay
   */
  public TripletDisplay() {
    super("CanTriplet");
    super.addButton("stop",this);
    fpsField=super.addField("fps", "%4.0f", 3, 6);
  }
  
  /**
   * update the given pid Field
   */
  public void updateCanValueField(CANValue<?> canValue) {
    if (canValue.canInfo.getMaxIndex()==0) {
      String title=canValue.canInfo.getTitle();
      super.updateField(title, canValue.asString(),canValue.getUpdateCount());
    } else {
      // TODO - how to display?
      //for (int i=0;i<canValue.canInfo.maxIndex;i++) {
      //  super.updateField(canValue.canInfo.title+String.format("#%2d",i), canValue.get(i),canValue.getUpdateCount());
      //}
    }
  }
  
  /**
   * add the given canValue
   * @param canValue
   */
  public void addCANValueField(CANValue<?> canValue) {
    if (canValue.canInfo.getMaxIndex()==0) {
      addField(canValue.canInfo.getTitle(),"%s",10,40);
    } else {
      //for (int i=1;i<=canValue.canInfo.maxIndex;i++) {
        // addField(canValue.canInfo.getTitle()+String.format("#%2d",i),"%s",10,40);
      //}
    }
  }

  @Override
  public void addCanValueFields(Collection<CANValue<?>> canValues) {
    for (CANValue<?> canValue:canValues) {
      if (canValue.isDisplay())
        addCANValueField(canValue);
    }
  }

  public void close() {
    this.frame.setVisible(false);
  }
  
  /**
   * get the title of the active Panel
   * @return - the activePanelTitle
   */
  public String getActivePanelTitle() {
    int selectedIndex = tabbedPane.getSelectedIndex();
    String title=tabbedPane.getTitleAt(selectedIndex);
    return title;
  }

  /**
   * update the historyPanel
   * @param panel
   */
  public void updateHistory(JPanel panel) {
    historyPanel.updatePanel(panel);
  }
  
  /**
   * update the cellTemperaturePanel
   * @param panel
   */
  public void updateCellTemperature(JPanel panel) {
    this.cellTemperaturPanel.updatePanel(panel);
  }
  
  /**
   * update the cellVoltagePanel
   * @param panel
   */
  public void updateCellVoltage(JPanel panel) {
    this.cellVoltagePanel.updatePanel(panel);
  }

}

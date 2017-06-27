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
package com.bitplan.can4eve;

import org.junit.Test;

import com.bitplan.can4eve.gui.swing.GaugePanel;
import com.bitplan.can4eve.gui.swing.PanelFrame;

import eu.hansolo.steelseries.gauges.Radial;

/**
 * test the Gauges 
 * @author wf
 *
 */
public class TestGauges {
 
  @Test
  public void testRadial() throws InterruptedException {
    final PanelFrame gaugeDemo = new PanelFrame(true);
    final Radial gauge = new Radial();
    int b=30;
    gauge.setBounds(0, 0, gaugeDemo.getWidth()-b, gaugeDemo.getHeight()-b);
    gauge.setTitle("SOC");
    gauge.setUnitString("%");
    final GaugePanel gaugePanel = new GaugePanel();
    gaugePanel.add(gauge);
    gaugeDemo.show(gaugePanel.getComponents());
    gaugeDemo.waitOpen();
    
    double value = 55.0;
    while (gaugeDemo.frame.isVisible()) {
      Thread.sleep(50);
      gauge.setValue(value);
      value = value - 0.1;
      if (value < 50)
        gaugeDemo.frame.setVisible(false);
    }
  }
}

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

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.junit.Test;

import com.bitplan.can4eve.gui.swing.GaugePanel;

import eu.hansolo.steelseries.gauges.Radial;

/**
 * test the Gauges 
 * @author wf
 *
 */
public class TestGauges {
  int W=600;
  int H=600;
  public class GaugeDemo {
    private JFrame frame;
    private JPanel panel;

    @SuppressWarnings("serial")
    public void createAndShowUI(GaugePanel gaugePanel) {
      frame = new JFrame();
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setLocationByPlatform(true);

      panel = new JPanel() {
        @Override
        public Dimension getPreferredSize() {
          return new Dimension(W, H);
        }
      };
      for (JComponent component : gaugePanel.getComponents()) {
        panel.add(component);
      }
      frame.add(panel);
      frame.pack();
      frame.setVisible(true);
    }

  }

  @Test
  public void testRadial() throws InterruptedException {
    final GaugeDemo gaugeDemo = new GaugeDemo();
    final Radial gauge = new Radial();
    gauge.setBounds(0, 0, W, H);
    gauge.setTitle("SOC");
    gauge.setUnitString("%");
    final GaugePanel gaugePanel = new GaugePanel();
    gaugePanel.add(gauge);
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        gaugeDemo.createAndShowUI(gaugePanel);
      }
    });
    while (gaugeDemo.frame == null || !gaugeDemo.frame.isVisible())
      Thread.sleep(10);
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

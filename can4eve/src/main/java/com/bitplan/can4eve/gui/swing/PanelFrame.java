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
package com.bitplan.can4eve.gui.swing;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Frame to show specified components
 * 
 * @author wf
 *
 */
public class PanelFrame {
  public JFrame frame;
  private int width;
  private int height;
  private JPanel panel;

  /**
   * create a new Panel Frame
   */
  public PanelFrame(boolean square) {
    // get the screen size as a java dimension
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    // get 2/3 of the height, and 2/3 of the width
    height = screenSize.height * 2 / 3;
    width = screenSize.width * 2 / 3;
    if (square) {
      int b = Math.min(height, width);
      width = b;
      height = b;
    }
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  @SuppressWarnings("serial")
  public void createAndShowUI(List<JComponent> components) {
    frame = new JFrame();

    // set the jframe height and width
    frame.setPreferredSize(new Dimension(width, height));

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLocationByPlatform(true);

    panel = new JPanel() {
      @Override
      public Dimension getPreferredSize() {
        return new Dimension(getWidth(), getHeight());
      }
    };
    for (JComponent component : components) {
      panel.add(component);
    }
    frame.add(panel);
    frame.pack();
    frame.setVisible(true);
  }

  public void show(JComponent component) {
    List<JComponent> components=new ArrayList<JComponent>();
    components.add(component);
    show(components);
  }
  
  /**
   * show the components
   * 
   * @param components
   */
  public void show(List<JComponent> components) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        createAndShowUI(components);
      }
    });
  }

  public void waitOpen() {
    while (frame == null || !frame.isVisible())
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
      }
  }

}

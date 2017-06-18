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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

/**
 * simple Swing Display
 * 
 * @author wf
 *
 */
public class SwingDisplay implements Display {
  public static boolean debug = false;
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.obdii");
  String title;
  JFrame frame;
  private List<JPanel> dataPanels=new ArrayList<JPanel>();
  private JPanel buttonPanel;
  MigLayout layout;
  Map<String, SwingLabelField> fieldMap = new HashMap<String, SwingLabelField>();
  JPanel mainPanel;
  ContainerPanel historyPanel;
  ContainerPanel cellTemperaturPanel;
  ContainerPanel cellVoltagePanel;
  protected JTabbedPane tabbedPane;
  
  /**
   * a container for a panel to be exchanged
   * @author wf
   *
   */
  public class ContainerPanel {
    String title;
    JPanel containerPanel;
    JPanel contentPanel;
    
    /**
     * update the content of this Panel
     * @param panel
     */
    public void updatePanel(JPanel panel) {
      tabbedPane.setSelectedComponent(containerPanel);
      containerPanel.remove(contentPanel);
      contentPanel = panel;
      containerPanel.add(panel);
      containerPanel.validate();
      containerPanel.repaint();
      containerPanel.setVisible(true);
    }
  }

  /**
   * add a data Panel
   * @return
   */
  public JScrollPane addDataPanel() {
    layout = new MigLayout("wrap 3");
    JPanel dataPanel = new JPanel(layout);
    JScrollPane scrollPane = new JScrollPane(dataPanel,
        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    dataPanels.add(dataPanel);
    return scrollPane;
  }
  
  /**
   * display the display
   * 
   * @param title
   */
  public SwingDisplay(String title) {
    this.title = title;
    tabbedPane=new JTabbedPane();
   
    mainPanel = new JPanel(new MigLayout("wrap 2"));
    buttonPanel=new JPanel();
    JScrollPane scrollPane = addDataPanel();
    mainPanel.add(buttonPanel,"span 2");
    mainPanel.add(scrollPane);
    addTab("data"+dataPanels.size(), mainPanel);
    this.historyPanel=addContainerPanel("history");
    this.cellTemperaturPanel=addContainerPanel("cellTemperature");
    this.cellVoltagePanel=addContainerPanel("cellVoltage");
  }

  /**
   * add a ContainerPanel with the given titel
   * @param title
   * @return the ContainerPanel
   */
  private ContainerPanel addContainerPanel(String title) {
    ContainerPanel cPanel=new ContainerPanel();
    cPanel.title=title;
    cPanel.contentPanel=new JPanel();
    cPanel.containerPanel=new JPanel();
    cPanel.containerPanel.add(cPanel.contentPanel);
    addTab(title, cPanel.containerPanel);
    return cPanel;
  }

  /**
   * label field
   * 
   * @author wf
   *
   */
  public static class SwingLabelField implements LabelField {
    String title;
    JLabel label;
    JLabel countLabel;
    JTextField field;
    String format;
    int labelSize;
    int fieldSize;
    int countLabelSize = 6;
    int updateCount = 0;

    public int getUpdateCount() {
      return updateCount;
    }

    public void setUpdateCount(int updateCount) {
      this.updateCount = updateCount;
    }

    /**
     * a label field with the given title
     * 
     * @param title
     * @param labelSize
     */
    public SwingLabelField(String title, String format, int labelSize,
        int fieldSize) {
      label = new JLabel(title);
      field = new JTextField("", fieldSize);
      countLabel = new JLabel();
      this.labelSize = labelSize;
      this.fieldSize = fieldSize;
      this.title = title;
      this.format = format;
    }

    /**
     * update my value
     * 
     * @param value
     */
    @Override
    public void updateValue(Object value, int updateCount) {
      if (this.updateCount != updateCount) {
        this.setUpdateCount(updateCount);
        String newValue = "?";
        if (value != null) {
          if (debug)
            LOGGER.log(Level.INFO, "updating " + title + " to " + value);
          newValue = String.format(format, value);
        }
        field.setText(newValue);
        countLabel.setText(String.format("%6d", this.getUpdateCount()));
      }
    }
  }

  /**
   * update the display field with the given title with the given value
   * 
   * @param title
   * @param value
   */
  public void updateDisplayField(String title, Object value, int updateCount) {
    SwingLabelField lf = fieldMap.get(title);
    if (lf != null)
      lf.updateValue(value, updateCount);
    else {
      if (debug)
        LOGGER.log(Level.WARNING, "undefined field " + title);
    }
  }

  /**
   * update the given Field
   * 
   * @param title
   * @param value
   * @param updateCount
   */
  public void updateField(final String title, final Object value,
      final int updateCount) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        updateDisplayField(title, value, updateCount);
      }
    });
  }

  /**
   * get a MigLayout width string for the given size
   * 
   * @param size
   * @return the layout string
   */
  public String migWidth(int size) {
    // http://www.miglayout.com/QuickStart.pdf
    String width = String.format("width %3d!", size * 10);
    return width;
  }

  /**
   * add the given label field
   * 
   * @param lf
   */
  public void add(SwingLabelField lf) {
    if (debug)
      LOGGER.log(Level.INFO, "Adding field " + lf.title);
    fieldMap.put(lf.title, lf);
    JPanel dataPanel = dataPanels.get(dataPanels.size()-1);
    if (dataPanel.getComponents().length>27) {
      this.addDataPanel();
      dataPanel = dataPanels.get(dataPanels.size()-1);
      addTab("data"+dataPanels.size(), dataPanel);
    }
    dataPanel.add(lf.label, migWidth(lf.labelSize));
    dataPanel.add(lf.field, migWidth(lf.fieldSize));
    dataPanel.add(lf.countLabel, migWidth(lf.countLabelSize));
  }

  /**
   * add the panel with the given title to the tabbed pane
   * @param title
   * @param panel
   */
  private void addTab(String title, JPanel panel) {
    tabbedPane.add(title,panel);
  }

  /**
   * the display
   */
  public void displayFrame() {
    frame = new JFrame(title);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.getContentPane().add(tabbedPane);
    frame.pack();
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameDim=(Dimension) dim.clone();
    frameDim.height=480;
    frame.setMaximumSize(frameDim);
    frame.setPreferredSize(frameDim);
    frame.setLocation(dim.width / 2 - frame.getSize().width / 2,
        dim.height / 2 - frame.getSize().height / 2);
    frame.setVisible(true);
  }

  /**
   * show the display
   */
  public void show() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        displayFrame();
      }
    });
  }

  @Override
  public SwingLabelField addField(String title, String format, int labelSize,
      int fieldSize) {
    SwingLabelField labelField = new SwingLabelField(title, format, labelSize,
        fieldSize);
    add(labelField);
    return labelField;
  }

  /**
   * wait for close
   * 
   * @throws InterruptedException
   */
  public void waitClose() throws InterruptedException {
    while (frame != null && frame.isVisible()) {
      Thread.sleep(50);
    }
  }

  /**
   * add a button with the given name
   * @param name
   * @param tripletDisplay 
   * @return
   */
  public JButton addButton(String name, ActionListener actionListener) {
    JButton button=new JButton(name);
    button.setName(name);
    button.addActionListener(actionListener);
    buttonPanel.add(button);
    return button;
  }
}

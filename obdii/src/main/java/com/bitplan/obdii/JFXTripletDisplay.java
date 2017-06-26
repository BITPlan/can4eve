package com.bitplan.obdii;

import javax.swing.JPanel;

import com.bitplan.can4eve.CANValue;
import com.bitplan.can4eve.CANValue.DoubleValue;
import com.bitplan.can4eve.SoftwareVersion;
import com.bitplan.can4eve.gui.App;

import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.scene.control.Tab;

/**
 * a Java FX based display for triplet vehicles
 * 
 * @author wf
 *
 */
public class JFXTripletDisplay extends JavaFXDisplay {

  /**
   * construct me
   * 
   * @param app
   * @param softwareVersion
   */
  public JFXTripletDisplay(App app, SoftwareVersion softwareVersion) {
    super(app, softwareVersion);
  }

  /**
   * update the given tab with the given panel
   * 
   * @param tab
   * @param panel
   */
  public void updateTab(Tab tab, JPanel panel) {
    if (panel != null) {
      final SwingNode swingNode = new SwingNode();
      swingNode.setContent(panel);
      tab.setContent(swingNode);
    }
  }

  /**
   * special handling for Cell Temperature and Cell Voltage
   */
  @Override
  public void updateCanValueField(CANValue<?> canValue) {
    String title = canValue.canInfo.getTitle();
    if (title.toLowerCase().startsWith("cell")) {
      // TODO - use some kind of id to clearly identify plotable stuff
      // Cell Temp / Cell Voltage e.g. might not work in i18n
      Tab activeTab = super.getActiveTab();
      String activePanelTitle = activeTab.getText();
      if (title.startsWith(activePanelTitle)) {
        if (canValue.getUpdateCount() % canValue.canInfo.getMaxIndex() == 0) {
          if ("Cell Temp".equals(activePanelTitle)) {
            DoubleValue cellTemperature = (DoubleValue) canValue;
            final CANCellStatePlot cellStatePlot = new CANCellStatePlot(
                "cellTemperature", "cell", "Temperature", cellTemperature, 1.0);
            Platform
                .runLater(() -> updateTab(activeTab, cellStatePlot.getPanel()));
          }
          if ("Cell Voltage".equals(activePanelTitle)) {
            DoubleValue cellVoltage = (DoubleValue) canValue;
            final CANCellStatePlot cellStatePlot = new CANCellStatePlot(
                "cellVoltage", "cell", "Voltage", cellVoltage, 0.01);
            Platform
                .runLater(() -> updateTab(activeTab, cellStatePlot.getPanel()));
          }
        }
      }
    } else {
      super.updateCanValueField(canValue);
    }
  }

}

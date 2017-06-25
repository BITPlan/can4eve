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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

/**
 * A generic multiple platform panel with gauges
 * @author wf
 *
 */
public class GaugePanel {
  
  public GaugePanel() {
    
  }
  
  private List<JComponent> components=new ArrayList<JComponent>();
  
  public List<JComponent> getComponents() {
    return components;
  }

  public void setComponents(List<JComponent> components) {
    this.components = components;
  }

  /**
   * add the given component
   * @param component
   */
  public void add(JComponent component) {
    getComponents().add(component);
  }
}

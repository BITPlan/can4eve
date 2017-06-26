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
package com.bitplan.can4eve.gui;

import java.util.ArrayList;
import java.util.List;

/**
 * a platform independent menu
 * @author wf
 *
 */
public class Menu {
  String id;
  String title;
  String shortCut;
  List<MenuItem> menuItems=new ArrayList<MenuItem>();
  List<Menu> subMenus=new ArrayList<Menu>();
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getTitle() {
    return title;
  }
  public void setTitle(String title) {
    this.title = title;
  }
  public String getShortCut() {
    return shortCut;
  }
  public void setShortCut(String shortCut) {
    this.shortCut = shortCut;
  }
  public List<MenuItem> getMenuItems() {
    return menuItems;
  }
  public void setMenuItems(List<MenuItem> menuItems) {
    this.menuItems = menuItems;
  }
  public List<Menu> getSubMenus() {
    return subMenus;
  }
  public void setSubMenus(List<Menu> subMenus) {
    this.subMenus = subMenus;
  }
}

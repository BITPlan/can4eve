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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * platform independent app description
 * 
 * @author wf
 *
 */
public class App {
  String name;
  String home;
  String feedback;
  String help;
  private List<Group> groups = new ArrayList<Group>();
  private List<ExceptionHelp> exceptionHelps=new ArrayList<ExceptionHelp>();
  private Map<String,ExceptionHelp> exceptionHelpByName=new HashMap<String,ExceptionHelp>();
  private Map<String,Group> groupById=new HashMap<String,Group>();
  private Menu mainMenu;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getHome() {
    return home;
  }

  public void setHome(String home) {
    this.home = home;
  }

  public String getFeedback() {
    return feedback;
  }

  public void setFeedback(String feedback) {
    this.feedback = feedback;
  }

  public String getHelp() {
    return help;
  }

  public void setHelp(String help) {
    this.help = help;
  }

  public static void setInstance(App instance) {
    App.instance = instance;
  }

  public Menu getMainMenu() {
    return mainMenu;
  }

  public void setMainMenu(Menu mainMenu) {
    this.mainMenu = mainMenu;
  }

  public List<Group> getGroups() {
    return groups;
  }
  public void setGroups(List<Group> groups) {
    this.groups = groups;
  }

  /**
   * reinitializatin of data structures
   */
  public void reinit() {
    for (Group group:this.getGroups()) {
      group.reinit();
      if (group.getId()!=null)
        this.groupById.put(group.getId(), group);
    }
    for (ExceptionHelp help:this.exceptionHelps) {
      help.reinit();
      this.exceptionHelpByName.put(help.getException(), help);
    }
  }
  
  /**
   * get the group by the given id
   * @param groupId
   * @return the group
   */
  public Group getGroupById(String groupId) {
    Group group=this.groupById.get(groupId);
    return group;
  }

  
  /**
   * get the form by the given id
   * @param id
   * @return the form by id
   */
  public Form getFormById(String groupId,String formId) {
    Group group=getGroupById(groupId);
    Form form=group.getFormById(formId);
    return form;
  }
  
  /**
   * get the exception help by the given name
   * @param exception
   * @return - the exception help
   */
  public ExceptionHelp getExceptionHelpByName(String exception) {
    return this.exceptionHelpByName.get(exception);
  }

  /**
   * get the App from the given Json Stream
   * 
   * @param jsonStream
   * @return
   * @throws Exception
   */
  public static App fromJsonStream(InputStream jsonStream) throws Exception {
    Gson gson = new Gson();
    App app = gson.fromJson(new InputStreamReader(jsonStream), App.class);
    app.reinit();
    return app;
  }

  public String asJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    // new GraphAdapterBuilder().addType(Pid.class).registerOn(gsonBuilder);
    Gson gson = gsonBuilder.setPrettyPrinting().create();
    String json = gson.toJson(this);
    return json;
  }
  
  public List<ExceptionHelp> getExceptionHelps() {
    return exceptionHelps;
  }

  public void setExceptionHelps(List<ExceptionHelp> exceptionHelps) {
    this.exceptionHelps = exceptionHelps;
  }

  private static App instance;


  /**
   * get the Application instance as configured
   * @return - the application
   * @throws Exception
   */
  public static App getInstance() throws Exception {
    if (instance == null) {
      // FIXME make configurable
      String path = "com/bitplan/can4eve/gui/CanTriplet.json";
      InputStream jsonStream = App.class.getClassLoader()
          .getResourceAsStream(path);
      if (jsonStream == null) {
        throw new Exception(
            String.format("Could not load App  from classpath " + path));
      }
      instance = App.fromJsonStream(jsonStream);
    }
    return instance;
  }

  
}

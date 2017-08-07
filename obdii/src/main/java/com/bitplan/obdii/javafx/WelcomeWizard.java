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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

import com.bitplan.obdii.I18n;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

/**
 * the Welcome wizard
 * 
 * @author wf
 *
 */
public class WelcomeWizard extends Wizard {
  public static final String resourcePath = "/com/bitplan/can4eve/gui/";
  List<WizardPane> pages = new ArrayList<WizardPane>();

  /**
   * construct the welcome Wizard
   * 
   * @param title
   * @param pageNames
   * @throws Exception
   */
  public WelcomeWizard(String i18nTitle) {
    setTitle(I18n.get(i18nTitle));
  }

  /**
   * https://stackoverflow.com/a/45540425/1497139
   * 
   * @param pageNames
   * @throws Exception
   */
  public void setPages(String... pageNames) throws Exception {
    for (String pageName : pageNames) {
      Parent root = FXMLLoader
          .load(getClass().getResource(resourcePath + pageName + ".fxml"));
      WizardPane page = new WizardPane();
      page.setHeaderText(I18n.get(pageName));
      page.setContent(root);
      this.pages.add(page);
    }
  }

  public void addPage(WizardPane page) {
    this.pages.add(page);
  }

  public void prepare() {
    setFlow(new LinearFlow(pages));
  }

  public void display() {
    showAndWait().ifPresent(result -> {
      if (result == ButtonType.FINISH) {
        System.out.println("Wizard finished, settings: " + getSettings());
      }
    });
  }

  public void close() {
    Field field;
    try {
      field = Wizard.class.getDeclaredField("dialog");
      field.setAccessible(true);
      @SuppressWarnings("rawtypes")
      Dialog dlg=(Dialog) field.get(this);
      dlg.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}

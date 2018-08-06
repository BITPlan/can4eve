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
package com.bitplan.obdii.javafx.presenter;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import com.bitplan.i18n.I18n;
import com.bitplan.i18n.Translator;
import com.bitplan.javafx.BasePresenter;
import com.bitplan.javafx.GenericDialog;
import com.bitplan.obdii.Can4EveI18n;
import com.bitplan.obdii.Preferences;
import com.bitplan.obdii.Preferences.LangChoice;

/**
 * present the preferences
 * @author wf
 *
 */
public class PreferencesPresenter extends BasePresenter<Preferences>{

  @Override
  public void updateView(Preferences model) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Preferences updateModel() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void show(Preferences preferences) {
    GenericDialog preferencesDialog = new GenericDialog(getStage(),
        getApp().getFormById(Can4EveI18n.PREFERENCES_GROUP, Can4EveI18n.PREFERENCES_FORM));
    Optional<Map<String, Object>> result = preferencesDialog
        .show(preferences.asMap());
    if (result.isPresent()) {
      LangChoice lang = preferences.getLanguage();
      preferences.fromMap(result.get());
      try {
        preferences.save();
      } catch (IOException e) {
        this.getExceptionHandler().handleException(e);
      }
      if (!lang.equals(preferences.getLanguage())) {
        Translator.initialize("can4eve", preferences.getLanguage().name());
        GenericDialog.showAlert(getStage(), I18n.get(Can4EveI18n.LANGUAGE_CHANGED_TITLE),
            I18n.get(Can4EveI18n.LANGUAGE_CHANGED), I18n.get(Can4EveI18n.NEWLANGUAGE_RESTART));
      }
    }
    
  }

}

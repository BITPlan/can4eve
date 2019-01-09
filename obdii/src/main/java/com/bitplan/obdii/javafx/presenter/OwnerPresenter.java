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

import com.bitplan.can4eve.Owner;
import com.bitplan.can4eve.Vehicle;
import com.bitplan.gui.Form;
import com.bitplan.javafx.BasePresenter;
import com.bitplan.javafx.GenericDialog;
import com.bitplan.obdii.Can4EveI18n;

/**
 * present any owner information
 * @author wf
 *
 */
public class OwnerPresenter extends BasePresenter<Owner>{
  private Form ownerForm;
  private Owner owner;
  
  @Override
  public void updateView() {   
  }

  @Override
  public Owner updateModel() {
    return null;
  }
  
  /**
   * initialize me
   */
  @Override
  public void postInit() {
    ownerForm = super.getApp().getFormById(Can4EveI18n.PREFERENCES_GROUP, Can4EveI18n.OWNER_FORM);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    
  }

  @Override
  public void show(Owner owner) {
    GenericDialog vehicleDialog = new GenericDialog(getStage(), ownerForm);
    Optional<Map<String, Object>> result = vehicleDialog.show(owner.asMap());
    if (result.isPresent()) {
      owner.fromMap(result.get());
      try {
        owner.save();
      } catch (IOException e) {
        super.getExceptionHandler().handleException(e);
      }
    }
  }

}

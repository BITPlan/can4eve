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

import com.bitplan.can4eve.CANData;
import com.bitplan.can4eve.Vehicle;
import com.bitplan.error.ExceptionHandler;
import com.bitplan.gui.Form;
import com.bitplan.i18n.I18n;
import com.bitplan.javafx.BasePresenter;
import com.bitplan.javafx.GenericDialog;
import com.bitplan.javafx.ImageSelector;
import com.bitplan.obdii.Can4EveI18n;
import com.bitplan.triplet.VINValue;

import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

/**
 * vehicle specific Presenter
 * @author wf
 */

public class VehiclePresenter extends BasePresenter<Vehicle> {
  @FXML
  private
  ProgressBar progressBar;
  @FXML
  TextField vin;
  @FXML
  TextField model;
  @FXML
  TextField vehicleYear;
  @FXML
  TextField cellCount;
  @FXML
  TextField vehicleManufacturer;
  @FXML
  
  ImageView vehicleImage;
  String carSelections[] = { "Citroën C-Zero", "Mitsubishi i-Miev",
      "Mitsubishi Outlander PHEV", "Peugeot Ion" };
  String carPictures[] = { "c-zero.jpg", "i-miev.jpg", "outlanderphev.jpg",
      "ion.jpg" };
  private ImageSelector<String> carSelector = new ImageSelector<String>("vehicle",
      carSelections, carPictures);
  
  private Form vehicleForm;
  private Vehicle vehicle;

  public ImageSelector<String> getCarSelector() {
    return carSelector;
  }

  public void setCarSelector(ImageSelector<String> carSelector) {
    this.carSelector = carSelector;
  }

  public ProgressBar getProgressBar() {
    return progressBar;
  }

  public void setProgressBar(ProgressBar progressBar) {
    this.progressBar = progressBar;
  }

  /**
   * initialize me
   */
  @Override
  public void postInit() {
    vehicleForm = super.getApp().getFormById(Can4EveI18n.PREFERENCES_GROUP, Can4EveI18n.VEHICLE_FORM);
  }
  
  @Override
  public void initialize(URL location, ResourceBundle resources) {
  }

  /**
   * update this view with the given vehicle Data
   * 
   * @param vehicle
   */
  @Override
  public void updateView() {
    model.setText(vehicle.getModel());
    ImageView imageView = getCarSelector().getImageView(vehicle.getModel());
    vehicleImage.setImage(imageView.getImage());
    vin.setText(vehicle.getVIN());
    vehicleYear.setText("" + vehicle.getYear());
  }

  @SuppressWarnings("rawtypes")
  public void showVehicleInfo(Vehicle pVehicle, Map<String, CANData> vehicleInfo,
      ExceptionHandler exceptionHandler) {
    this.vehicle=pVehicle;
    CANData<VINValue> vinInfo = vehicleInfo.get("VIN");
    VINValue VIN = vinInfo.getValue();
    if (VIN == null) {
      exceptionHandler.handleException(new Exception(I18n.get(Can4EveI18n.VEHICLE_VIN_PROBLEM)));
    } else {
      vehicle.setVIN(VIN.vin);
      vehicle.setYear(VIN.year);
      updateView();
      // TODO - manufacturer/factory and cellCount are missing in Vehicle
      vehicleManufacturer.setText(VIN.manufacturer + "/" + VIN.factory);
      cellCount.setText(""+VIN.cellCount);
    }
  }

  /**
   * show the vehicle Dialog
   * 
   * @throws Exception
   */
  @Override
  public void show(Vehicle vehicle) {
    GenericDialog vehicleDialog = new GenericDialog(getStage(), vehicleForm);
    Optional<Map<String, Object>> result = vehicleDialog.show(vehicle.asMap());
    if (result.isPresent()) {
      vehicle.fromMap(result.get());
      try {
        vehicle.save();
      } catch (IOException e) {
        super.getExceptionHandler().handleException(e);
      }
    }
  }

  @Override
  public Vehicle updateModel() {
    if (vehicle == null) {
      vehicle = Vehicle.getInstance();
    }
    vehicle.setModel(getCarSelector().getSelection());
    if ("Mitsubishi Outlander PHEV".equals(vehicle.getModel())) {
      vehicle.setGroup("OutlanderPHEV");
    } else {
      vehicle.setGroup("Triplet");
    }
    return vehicle;
  }
}

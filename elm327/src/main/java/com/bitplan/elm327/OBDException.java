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
package com.bitplan.elm327;

/**
 * wrapper for OBD Exceptions
 * @author wf
 *
 */
public class OBDException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = -2319797397505818150L;
  private Packet response;

  public Packet getResponse() {
    return response;
  }

  public void setResponse(Packet response) {
    this.response = response;
  }

  /**
   * create an OBDException
   * @param msg
   * @param response
   */
  public OBDException(String msg, Packet response) {
    super(msg);
    this.setResponse(response);
  }

  /**
   * an OBD Exception without a specific response package
   * @param msg
   */
  public OBDException(String msg) {
    this(msg,null);
  }

}

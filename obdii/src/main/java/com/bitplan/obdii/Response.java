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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Response 
 * @author wf
 *
 */
public class Response {

  private Date timeStamp;
  private String command;
  private String response;
  
  static SimpleDateFormat isoDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
  /**
   * create a Response with the given line input
   * @param line
   * @param line2 
   */
  public Response(String command, String response) {
    timeStamp=new Date();
    this.command=command;
    this.response=response;
  }
  
  /**
   * return me as a string
   * @return
   */
  public String asString() {
    String ts=isoDateFormatter.format(timeStamp);
    String result=String.format("%s (%s): %s",ts,command.trim(),response);
    return result;
  }

}

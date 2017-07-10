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
package com.bitplan.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Translation utility
 * 
 * @author wf
 *
 */
public class Translator {
  public static String BUNDLE_NAME = "can4eve";
  public static Locale[] SUPPORTED_LOCALES = { Locale.ENGLISH, Locale.GERMAN };

  private static ResourceBundle resourceBundle;
  private static MessageFormat formatter;

  /**
   * translate the given message with the given params
   * @param messageName
   * @param params
   * @return - the translated text
   */
  public static String translate(String messageName, Object[] params) {
    formatter.applyPattern(resourceBundle.getString(messageName));
    return formatter.format(params);
  }

  /**
   * translate the given message with the given parameters
   * @param messageName
   * @param param
   * @return - the translated message
   */
  public static String translate(String messageName, Object param) {
    formatter.applyPattern(resourceBundle.getString(messageName));
    return formatter.format(new Object[] { param });
  }

  /**
   * tranlsate the given message
   * @param messageName
   * @return - the translated message
   */
  public static String translate(String messageName) {
    return resourceBundle.getString(messageName);
  }

  /**
   * get the current locale from the resource Bundle
   * @return - the locale
   */
  public static Locale getCurrentLocale() {
    if (resourceBundle==null)
      return null;
    return resourceBundle.getLocale();
  }

  /**
   * initialize me
   * @return the resource Bundle for the given localName
   */
  public static ResourceBundle initialize(String localeName) {
    Locale locale = new Locale(localeName);
    return loadBundle(locale);
  }

  /**
   * load the bundle
   * @param locale
   * @return - the resource bundle for the given locale
   */
  public static ResourceBundle loadBundle(Locale locale) {
    resourceBundle = ResourceBundle.getBundle("i18n/"+BUNDLE_NAME, locale);
    formatter = new MessageFormat("");
    formatter.setLocale(locale);
    return resourceBundle;
  }
}

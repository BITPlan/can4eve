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

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

import org.junit.Test;

import com.bitplan.can4eve.gui.swing.Translator;

/**
 * 
 * @author wf
 *
 */
public class TestI18n {
  static boolean show=false;

  /**
   * check the translations
   * 
   * @throws Exception
   * @throws IllegalArgumentException
   */
  @Test
  public void testi18nFieldsTranslated() throws Exception {
  
    String locales[] = { "en", "de" };
    int errors=0;
    for (String locale : locales) {
      Translator.initialize(locale);
      if (show)
        System.out.println("# locale "+locale);
      for (Field field : I18n.class.getFields()) {
        String text = (String) field.get(null);
        String translated;
        try {
          translated = Translator.translate(text);
        } catch (Throwable th) {
          translated = "";
          if (show)
            System.out.println(text + "=" + translated);
          errors++;
        }
      }
    }
    assertEquals(0,errors);
  }
  
  @Test
  public void testPropertiesAreFields() throws Exception {
    boolean show=true;
    String locales[] = { "en", "de" };
    List<String> fieldList = new ArrayList<String>();
    for (Field field : I18n.class.getFields()) {
      fieldList.add(field.getName());
    }
    int errors=0;
    for (String locale : locales) {
      ResourceBundle bundle = Translator.initialize(locale);
      Enumeration<String> keys = bundle.getKeys();
      while (keys.hasMoreElements()) {
        String key=keys.nextElement();
        if (!fieldList.contains(key.toUpperCase())) {
          errors++;
          if (show)
            System.out.println("  public static final String "+key.toUpperCase()+"=\""+key+"\"; //"+bundle.getString(key));
        }
      }
    }
    assertEquals(0,errors);
  }
}

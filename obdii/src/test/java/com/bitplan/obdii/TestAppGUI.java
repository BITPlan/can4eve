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
import static org.junit.Assert.assertNotNull;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.bitplan.can4eve.gui.App;
import com.bitplan.can4eve.gui.Group;
import com.bitplan.can4eve.json.JsonManager;
import com.bitplan.can4eve.json.JsonManagerImpl;
import com.bitplan.obdii.Preferences.LangChoice;

/**
 * test the descriptive application gui
 * @author wf
 *
 */
public class TestAppGUI {
  @Test
  public void testAppGUI() throws Exception {
    App app=App.getInstance();
    assertNotNull(app);
    assertEquals(4,app.getMainMenu().getSubMenus().size());
    assertEquals(2,app.getGroups().size());
    int [] expected={3,9};
    int i=0;
    for (Group group:app.getGroups()) {
      assertEquals(expected[i++],group.getForms().size());
    }
  }
  
  @Test
  public void testJoin() {
    String langs=StringUtils.join(LangChoice.values(),",");
    assertEquals("en,de,notSet",langs);
  }
  
  @Test
  public void testPreferences() {
    Preferences pref=new Preferences();
    pref.debug=true;
    pref.setLanguage(LangChoice.de);
    String json=pref.asJson();
    //System.out.println(json);
    assertEquals("{\n" + 
        "  \"language\": \"de\",\n" + 
        "  \"debug\": true\n" + 
        "}" ,json);
    JsonManager<Preferences> jmPreferences=new JsonManagerImpl<Preferences>(Preferences.class);
    Preferences pref2=jmPreferences.fromJson(json);
    assertNotNull(pref2);
    assertEquals(pref2.debug,pref.debug);
    assertEquals(pref2.getLanguage(),pref.getLanguage());
    Preferences pref3=new Preferences();
    pref3.fromMap(pref2.asMap());
    assertEquals(pref3.debug,pref.debug);
    assertEquals(pref3.getLanguage(),pref.getLanguage());
  }
}

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
package com.bitplan.can4eve;

import static org.junit.Assert.*;

import org.junit.Test;

import com.bitplan.i18n.Translator;

/**
 * test the Translator
 * @author wf
 *
 */
public class TestTranslator {

  @Test
  public void testTranslator() {
    Translator.initialize("de");
    assertEquals("Hilfe",Translator.translate("helpMenu"));
  }
}

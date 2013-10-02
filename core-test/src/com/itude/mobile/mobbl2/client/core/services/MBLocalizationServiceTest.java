/*
 * (C) Copyright ItudeMobile.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.itude.mobile.mobbl2.client.core.services;

import android.test.ApplicationTestCase;

import com.itude.mobile.android.util.DataUtil;
import com.itude.mobile.mobbl2.client.core.MBApplicationCore;

public class MBLocalizationServiceTest extends ApplicationTestCase<MBApplicationCore>
{

  public MBLocalizationServiceTest()
  {
    super(MBApplicationCore.class);
  }

  @Override
  protected void setUp() throws Exception
  {
    createApplication();
    DataUtil.getInstance().setContext(getContext());

    MBMetadataService.setConfigName("config/config.xml");
  }

  public void testService()
  {
    MBLocalizationService instance = MBLocalizationService.getInstance();
    assertNotNull(instance);
  }

  public void testText()
  {
    String text = MBLocalizationService.getInstance().getTextForKey("normal");
    assertEquals("Normaal", text);
  }

  public void testTextWithArguments()
  {
    String text = MBLocalizationService.getInstance().getText("substitution", new Integer(2));
    assertEquals("Er zijn 2 variabelen", text);

    text = MBLocalizationService.getInstance().getText("substitution");
    assertNotNull(text);
  }

}

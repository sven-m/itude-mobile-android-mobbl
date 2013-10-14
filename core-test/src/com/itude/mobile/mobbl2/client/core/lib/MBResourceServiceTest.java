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
package com.itude.mobile.mobbl2.client.core.lib;

import android.graphics.drawable.Drawable;
import android.test.ApplicationTestCase;

import com.itude.mobile.android.util.AssetUtil;
import com.itude.mobile.mobbl2.client.core.MBApplicationCore;
import com.itude.mobile.mobbl2.client.core.services.MBResourceService;
import com.itude.mobile.mobbl2.client.core.services.exceptions.MBResourceNotDefinedException;

public class MBResourceServiceTest extends ApplicationTestCase<MBApplicationCore>
{
  private MBResourceService service = null;

  public MBResourceServiceTest()
  {
    super(MBApplicationCore.class);
  }

  @Override
  protected void setUp() throws Exception
  {
    AssetUtil.getInstance().setContext(getContext());
    service = MBResourceService.getInstance();
  }

  public void testGetResourceByID()
  {

  }

  public void testGetImageByID()
  {
    String resourceID = "nonexistent";
    Drawable drawable = null;
    try
    {
      drawable = service.getImageByID(resourceID);
    }
    catch (MBResourceNotDefinedException e)
    {
    }

    assertNull(drawable);

    resourceID = "whitelabel-splashscreen";
    try
    {
      drawable = service.getImageByID(resourceID);
    }
    catch (MBResourceNotDefinedException e)
    {
      fail("Resource could not be found");
    }

    assertNotNull(drawable);
  }

  @Override
  protected void tearDown() throws Exception
  {
    service = null;
  }

}

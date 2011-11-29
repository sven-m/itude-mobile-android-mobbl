package com.itude.mobile.mobbl2.client.core.lib;

import android.graphics.drawable.Drawable;
import android.test.ApplicationTestCase;

import com.itude.mobile.mobbl2.client.core.MBApplicationCore;
import com.itude.mobile.mobbl2.client.core.services.MBResourceService;
import com.itude.mobile.mobbl2.client.core.services.exceptions.MBResourceNotDefinedException;
import com.itude.mobile.mobbl2.client.core.util.AssetUtil;

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

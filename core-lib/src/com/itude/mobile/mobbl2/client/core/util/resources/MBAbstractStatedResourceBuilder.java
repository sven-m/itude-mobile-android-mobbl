package com.itude.mobile.mobbl2.client.core.util.resources;

import java.util.Map;

import android.graphics.drawable.Drawable;

import com.itude.mobile.mobbl2.client.core.configuration.resources.MBItemDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.resources.MBResourceConfiguration;
import com.itude.mobile.mobbl2.client.core.configuration.resources.MBResourceDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.resources.MBStatedResourceDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.resources.exceptions.MBInvalidItemException;
import com.itude.mobile.mobbl2.client.core.services.MBResourceService;

public abstract class MBAbstractStatedResourceBuilder
{
  private MBResourceConfiguration _config;

  public MBAbstractStatedResourceBuilder(MBResourceConfiguration config)
  {
    _config = config;
  }

  public abstract <T> T build(MBStatedResourceDefinition def);

  protected void validateItemInStatedResource(String itemResource) throws MBInvalidItemException
  {
    MBResourceDefinition targetResourceDef = getConfig().getResourceWithID(itemResource);

    if (targetResourceDef instanceof MBStatedResourceDefinition)
    {
      throw new MBInvalidItemException("A stated resource can not have a stated item");
    }
  }

  protected Drawable getImageByID(String resourceId)
  {
    return MBResourceService.getInstance().getImageByID(resourceId);
  }

  protected Map<String, MBItemDefinition> getItems(MBStatedResourceDefinition def)
  {
    Map<String, MBItemDefinition> items = def.getItems();

    if (items.size() == 0)
    {
      return null;
    }

    return items;
  }

  public MBResourceConfiguration getConfig()
  {
    return _config;
  }
}

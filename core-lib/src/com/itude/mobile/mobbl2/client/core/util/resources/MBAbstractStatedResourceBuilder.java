package com.itude.mobile.mobbl2.client.core.util.resources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.itude.mobile.mobbl2.client.core.configuration.resources.MBItemDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.resources.MBResourceConfiguration;
import com.itude.mobile.mobbl2.client.core.configuration.resources.MBResourceDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.resources.MBStatedResourceDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.resources.exceptions.MBInvalidItemException;
import com.itude.mobile.mobbl2.client.core.services.MBResourceService;
import com.itude.mobile.mobbl2.client.core.util.Constants;

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
    List<MBItemDefinition> items = def.getItems();

    if (items == null || items.isEmpty())
    {
      return null;
    }

    HashMap<String, MBItemDefinition> itemMap = new HashMap<String, MBItemDefinition>(items.size());

    for (MBItemDefinition item : items)
    {
      if (itemMap.containsKey(item.getState()))
      {
        Log.w(Constants.APPLICATION_NAME,
              "State overridden: multiple item definitions with the same state for resource " + def.getResourceId());
      }

      itemMap.put(item.getState(), item);
    }

    return itemMap;
  }

  public MBResourceConfiguration getConfig()
  {
    return _config;
  }
}

package com.itude.mobile.mobbl2.client.core.util.resources;

import android.graphics.drawable.Drawable;

import com.itude.mobile.mobbl2.client.core.configuration.resources.exceptions.MBInvalidItemException;
import com.itude.mobile.mobbl2.client.core.view.MBResource;

public abstract class MBAbstractStatedResourceBuilder implements MBResourceBuilder.Builder<Drawable>
{
  protected void validateItemInStatedResource(MBResource resource) throws MBInvalidItemException
  {
  }
}

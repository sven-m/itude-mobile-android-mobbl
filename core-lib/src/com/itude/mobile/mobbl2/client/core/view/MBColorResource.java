package com.itude.mobile.mobbl2.client.core.view;

import com.itude.mobile.mobbl2.client.core.configuration.resources.MBResourceDefinition;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;

public class MBColorResource extends MBResource
{
  private String _color;

  public MBColorResource(MBResourceDefinition definition, MBDocument document, MBComponentContainer parent)
  {
    super(definition, document, parent);

    _color = definition.getColor();
  }

  public String getColor()
  {
    return _color;
  }

  public void setColor(String color)
  {
    _color = color;
  }

}

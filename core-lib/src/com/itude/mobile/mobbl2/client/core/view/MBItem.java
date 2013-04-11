package com.itude.mobile.mobbl2.client.core.view;

import com.itude.mobile.mobbl2.client.core.configuration.resources.MBItemDefinition;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;

public class MBItem extends MBComponent
{
  private String _resource;
  private String _state;
  private String _align;

  public MBItem(MBItemDefinition definition, MBDocument document, MBComponentContainer parent)
  {
    super(definition, document, parent);

    _resource = definition.getResource();
    _state = definition.getState();
    _align = definition.getAlign();
  }

  public String getResource()
  {
    return _resource;
  }

  public void setResource(String resource)
  {
    _resource = resource;
  }

  public String getState()
  {
    return _state;
  }

  public void setState(String state)
  {
    _state = state;
  }

  public String getAlign()
  {
    return _align;
  }

  public void setAlign(String align)
  {
    _align = align;
  }

}

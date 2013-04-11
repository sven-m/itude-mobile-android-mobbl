package com.itude.mobile.mobbl2.client.core.view;

import com.itude.mobile.mobbl2.client.core.configuration.resources.MBResourceDefinition;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;

public class MBResource extends MBComponent
{
  private String _id;
  private String _url;
  private String _align;

  public MBResource(MBResourceDefinition definition, MBDocument document, MBComponentContainer parent)
  {
    super(definition, null, null);

    _id = definition.getResourceId();
    _url = definition.getUrl();
    _align = definition.getAlign();
  }

  public String getId()
  {
    return _id;
  }

  public void setId(String id)
  {
    _id = id;
  }

  public String getUrl()
  {
    return _url;
  }

  public void setUrl(String url)
  {
    _url = url;
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

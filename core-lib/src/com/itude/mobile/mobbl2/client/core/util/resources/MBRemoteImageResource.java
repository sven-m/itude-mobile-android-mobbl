package com.itude.mobile.mobbl2.client.core.util.resources;

import com.itude.mobile.mobbl2.client.core.view.MBImageResource;

public class MBRemoteImageResource extends MBImageResource
{

  public MBRemoteImageResource(String url)
  {
    super(null, null, null);

    setUrl(url);
  }

}

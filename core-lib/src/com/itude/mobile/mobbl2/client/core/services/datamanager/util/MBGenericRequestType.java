package com.itude.mobile.mobbl2.client.core.services.datamanager.util;

import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.services.MBDataManagerService;

/***
 * Class which lists all names of MBGenericRequests.
 * 
 */
public class MBGenericRequestType
{
  private String _name;

  public MBGenericRequestType()
  {
  }

  public MBGenericRequestType(String name)
  {
    _name = name;
  }

  /**
   * Loads a MBGenericRequest for this RequestType.
   */
  public MBDocument load()
  {
    MBDocument gen = MBDataManagerService.getInstance().loadDocument("MBGenericRequest");
    gen.setValue(_name, "Request[0]/@name");
    return gen;
  }
}

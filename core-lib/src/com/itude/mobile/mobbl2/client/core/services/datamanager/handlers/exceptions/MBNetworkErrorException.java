package com.itude.mobile.mobbl2.client.core.services.datamanager.handlers.exceptions;

import com.itude.mobile.mobbl2.client.core.MBException;
import com.itude.mobile.mobbl2.client.core.services.MBLocalizationService;

public class MBNetworkErrorException extends MBException
{

  /**
   * 
   */
  private static final long serialVersionUID = 3091172841776637109L;

  public MBNetworkErrorException(String msg)
  {
    super(MBLocalizationService.getInstance().getTextForKey(msg));
  }

}

package com.itude.mobile.mobbl2.client.core.services.datamanager.handlers.exceptions;

import com.itude.mobile.mobbl2.client.core.MBException;
import com.itude.mobile.mobbl2.client.core.services.MBLocalizationService;

public class MBServerErrorException extends MBException
{

  /**
   * 
   */
  private static final long serialVersionUID = 2100678925497392898L;

  public MBServerErrorException(String msg)
  {
    super(MBLocalizationService.getInstance().getTextForKey(msg));
  }

}

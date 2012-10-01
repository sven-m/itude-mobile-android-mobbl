package com.itude.mobile.mobbl2.client.core.services.datamanager.handlers.mobbl1;

import com.itude.mobile.mobbl2.client.core.model.MBDocumentFactory;

public class MBMobbl1XmlServerDataHandler extends MBMobbl1ServerDataHandlerBase
{
  public MBMobbl1XmlServerDataHandler()
  {
    setDataHandlerType(MBDocumentFactory.PARSER_XML);
  }
}

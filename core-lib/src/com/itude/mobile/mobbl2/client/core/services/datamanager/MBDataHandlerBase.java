package com.itude.mobile.mobbl2.client.core.services.datamanager;

import android.util.Log;

import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.util.Constants;

public class MBDataHandlerBase implements MBDataHandler
{

  public MBDocument loadDocument(String documentName)
  {
    Log.w(Constants.APPLICATION_NAME, "MBDataHandlerBase: No loadDocument implementation for "+documentName);
    return null;
  }

  public MBDocument loadDocument(String documentName, MBDocument args)
  {
    Log.w(Constants.APPLICATION_NAME, "MBDataHandlerBase: No loadDocument implementation for "+documentName);
    return null;
  }

  public void storeDocument(MBDocument document)
  {
    Log.w(Constants.APPLICATION_NAME, "MBDataHandlerBase: No storeDocument implementation for "+document.getDefinition().getName());
  }

}

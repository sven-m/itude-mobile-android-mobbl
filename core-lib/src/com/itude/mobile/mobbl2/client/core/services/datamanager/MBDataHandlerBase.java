package com.itude.mobile.mobbl2.client.core.services.datamanager;

import android.util.Log;

import com.itude.mobile.mobbl2.client.core.model.MBDocument;

public class MBDataHandlerBase implements MBDataHandler
{

  public MBDocument loadDocument(String documentName)
  {
    Log.w("MOBBL", "MBDataHandlerBase: No loadDocument implementation for "+documentName);
    return null;
  }

  public MBDocument loadDocument(String documentName, MBDocument args)
  {
    Log.w("MOBBL", "MBDataHandlerBase: No loadDocument implementation for "+documentName);
    return null;
  }

  public void storeDocument(MBDocument document)
  {
    Log.w("MOBBL", "MBDataHandlerBase: No storeDocument implementation for "+document.getDefinition().getName());
  }

}

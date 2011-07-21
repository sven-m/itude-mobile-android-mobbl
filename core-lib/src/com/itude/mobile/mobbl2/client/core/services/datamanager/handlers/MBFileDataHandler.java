package com.itude.mobile.mobbl2.client.core.services.datamanager.handlers;

import android.util.Log;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDocumentDefinition;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.model.MBDocumentFactory;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;
import com.itude.mobile.mobbl2.client.core.services.datamanager.MBDataHandlerBase;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.DataUtil;
import com.itude.mobile.mobbl2.client.core.util.FileUtil;

public class MBFileDataHandler extends MBDataHandlerBase
{

  @Override
  public MBDocument loadDocument(String documentName)
  {

    Log.d(Constants.APPLICATION_NAME, "MBFileDataHandler.loadDocument: " + documentName);
    String fileName = determineFileName(documentName);
    MBDocumentDefinition docDef = MBMetadataService.getInstance().getDefinitionForDocumentName(documentName);
    byte[] data = DataUtil.getInstance().readFromAssetOrFile(fileName);

    if (data == null) return null;
    else return MBDocumentFactory.getInstance().getDocumentWithData(data, MBDocumentFactory.PARSER_XML, docDef);
  }

  @Override
  public void storeDocument(MBDocument document)
  {

    if (document != null)
    {
      String fileName = determineFileName(document.getName());
      StringBuffer sb = new StringBuffer(4096);
      String xml = document.asXmlWithLevel(sb, 0).toString();

      Log.d(Constants.APPLICATION_NAME, "Writing document " + document.getName() + " to " + fileName);

      try
      {
        // TODO: parameterize character encoding.
        FileUtil.getInstance().writeToFile(xml.getBytes(), fileName, "UTF-8");
      }
      catch (Exception e)
      {
        Log.w(Constants.APPLICATION_NAME, "MBFileDataHandler.storeDocument: Error writing document " + document.getName() + " to " + fileName, e);
      }
    }
  }

  private String determineFileName(String documentName)
  {
    return "documents/" + documentName + ".xml";

  }

  @Override
  public MBDocument loadDocument(String documentName, MBDocument args)
  {
    return loadDocument(documentName);
  }

}

package com.itude.mobile.mobbl2.client.core.services.datamanager.handlers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDialogDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDocumentDefinition;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.model.MBElement;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;
import com.itude.mobile.mobbl2.client.core.services.datamanager.MBDataHandlerBase;

public class MBMetadataDataHandler extends MBDataHandlerBase
{

  public static final String      DIALOGS_DOCUMENT = "MBDialogs";
  private Map<String, MBDocument> _cache           = new ConcurrentHashMap<String, MBDocument>();

  @Override
  public MBDocument loadDocument(String documentName)
  {
    if (!_cache.containsKey(documentName)) _cache.put(documentName, loadFreshDocument(documentName));
    return _cache.get(documentName);
  }

  @Override
  public MBDocument loadFreshDocument(String documentName)
  {
    if (documentName.equals(DIALOGS_DOCUMENT)) return loadDialogs();
    else return null;
  }

  private MBDocument loadDialogs()
  {
    MBDocumentDefinition docDef = MBMetadataService.getInstance().getDefinitionForDocumentName(DIALOGS_DOCUMENT);
    MBDocument doc = new MBDocument(docDef);
    MBMetadataService service = MBMetadataService.getInstance();
    for (MBDialogDefinition def : service.getDialogs())
      if (def.isShowAsDocument())
      {
        MBElement element = new MBElement(docDef.getElementWithPath("/Dialog"));
        element.setAttributeValue(def.getName(), "name");
        element.setAttributeValue(def.getMode(), "mode");
        element.setAttributeValue(def.getIcon(), "icon");
        element.setAttributeValue(def.getShowAs(), "showAs");
        element.setAttributeValue(def.getTitle(), "title");
        doc.addElement(element);
      }
    return doc;

  }
}

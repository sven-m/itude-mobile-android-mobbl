package com.itude.mobile.mobbl2.client.core.util.resources;

import java.util.List;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBConfigurationDefinition;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.model.MBDocumentFactory;
import com.itude.mobile.mobbl2.client.core.model.MBElement;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;
import com.itude.mobile.mobbl2.client.core.services.MBResourceService;
import com.itude.mobile.mobbl2.client.core.util.resources.MBBundleBuilder.Builder;
import com.itude.mobile.mobbl2.client.core.view.MBBundle;

public class MBLanguageBundleBuilder implements Builder
{

  @Override
  public MBBundle buildBundle(MBBundle bundle)
  {
    List<byte[]> dataSources = MBResourceService.getInstance().getResourceByURL(bundle);

    bundle.clear();

    for (byte[] dataSource : dataSources)
    {
      MBDocument bundleDoc = MBDocumentFactory.getInstance()
          .getDocumentWithData(dataSource, MBDocumentFactory.PARSER_XML,
                               MBMetadataService.getInstance().getDefinitionForDocumentName(MBConfigurationDefinition.DOC_SYSTEM_LANGUAGE));

      for (MBElement text : (List<MBElement>) bundleDoc.getValueForPath("/Text"))
      {
        bundle.putText(text.getValueForAttribute("key"), text.getValueForAttribute("value"));
      }
    }

    return bundle;
  }

}

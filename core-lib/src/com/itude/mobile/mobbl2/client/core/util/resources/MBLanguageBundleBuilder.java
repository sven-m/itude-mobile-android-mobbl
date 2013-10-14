/*
 * (C) Copyright ItudeMobile.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

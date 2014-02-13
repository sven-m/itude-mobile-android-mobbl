/*
 * (C) Copyright Itude Mobile B.V., The Netherlands
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
package com.itude.mobile.mobbl.core.configuration.endpoints;

import java.util.Map;

import com.itude.mobile.mobbl.core.configuration.MBConfigurationParser;
import com.itude.mobile.mobbl.core.configuration.MBDefinition;
import com.itude.mobile.mobbl.core.services.MBResultListenerDefinition;

/**
 * {@link MBConfigurationParser} Class to parse a service configuration file
 *
 */
public class MBEndpointsConfigurationParser extends MBConfigurationParser
{

  @Override
  public MBDefinition parseData(byte[] data, String documentName)
  {
    MBEndpointsConfiguration config = (MBEndpointsConfiguration) super.parseData(data, documentName);
    config.linkGlobalListeners();

    return config;
  }

  @Override
  public boolean processElement(String elementName, Map<String, String> attributeDict)
  {

    if (elementName.equals("EndPoints"))
    {
      MBEndpointsConfiguration confDef = new MBEndpointsConfiguration();
      getStack().push(confDef);
    }
    else if (elementName.equals("EndPoint"))
    {
      MBEndPointDefinition endpointDef = new MBEndPointDefinition();
      endpointDef.setDocumentIn(attributeDict.get("documentIn"));
      endpointDef.setDocumentOut(attributeDict.get("documentOut"));
      endpointDef.setEndPointUri(attributeDict.get("endPoint"));
      endpointDef.setCacheable(Boolean.parseBoolean(attributeDict.get("cacheable")));

      if (attributeDict.containsKey("ttl"))
      {
        endpointDef.setTtl(Integer.parseInt(attributeDict.get("ttl")));
      }
      if (attributeDict.containsKey("timeout"))
      {
        endpointDef.setTimeout(Integer.parseInt(attributeDict.get("timeout")));
      }
      else
      {
        endpointDef.setTimeout(300);
      }

      getStack().peek().addEndPoint(endpointDef);
      getStack().push(endpointDef);

    }
    else if (elementName.equals("ResultListener"))
    {
      MBResultListenerDefinition listenerDef = new MBResultListenerDefinition();
      listenerDef.setName(attributeDict.get("name"));
      listenerDef.setMatchExpression(attributeDict.get("matchExpression"));

      getStack().peek().addResultListener(listenerDef);
      getStack().push(listenerDef);
    }
    else
    {
      return false;
    }

    return true;
  }

  @Override
  public void didProcessElement(String elementName)
  {
    if (!elementName.equals("EndPoints"))
    {
      getStack().pop();
    }
  }

  @Override
  public boolean isConcreteElement(String element)
  {
    return element.equals("EndPoints") || element.equals("EndPoint") || element.equals("ResultListener");
  }

  @Override
  public boolean isIgnoredElement(String element)
  {
    return element.equals("ResultListeners");
  }

}

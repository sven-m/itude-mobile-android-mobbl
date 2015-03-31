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
package com.itude.mobile.mobbl.core.services;

import com.itude.mobile.android.util.DataUtil;
import com.itude.mobile.mobbl.core.configuration.endpoints.MBEndPointDefinition;
import com.itude.mobile.mobbl.core.configuration.endpoints.MBEndpointsConfiguration;
import com.itude.mobile.mobbl.core.configuration.endpoints.MBEndpointsConfigurationParser;
import com.itude.mobile.mobbl.core.configuration.mvc.MBActionDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBAlertDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBConfigurationDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBDialogDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBDocumentDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBDomainDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBMvcConfigurationParser;
import com.itude.mobile.mobbl.core.configuration.mvc.MBOutcomeDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBPageDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBPageStackDefinition;
import com.itude.mobile.mobbl.core.controller.MBOutcome;
import com.itude.mobile.mobbl.core.services.exceptions.MBActionNotDefinedException;
import com.itude.mobile.mobbl.core.services.exceptions.MBAlertNotDefinedException;
import com.itude.mobile.mobbl.core.services.exceptions.MBDialogNotDefinedException;
import com.itude.mobile.mobbl.core.services.exceptions.MBDocumentNotDefinedException;
import com.itude.mobile.mobbl.core.services.exceptions.MBDomainNotDefinedException;
import com.itude.mobile.mobbl.core.services.exceptions.MBPageNotDefinedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Service to handle meta data
 *
 */
public final class MBMetadataService
{
  private final MBConfigurationDefinition _cfg;
  private final MBEndpointsConfiguration  _endpointConfiguration;

  private static MBMetadataService        _instance;
  private static String                   _configName       = "config.xml";
  private static String                   _endpointsName    = "endpoints.xml";

  private MBDialogDefinition              _homeDialog       = null;

  private MBMetadataService()
  {
    MBMvcConfigurationParser mvcParser = new MBMvcConfigurationParser();
      
    // Configuration definition
    _cfg = (MBConfigurationDefinition) mvcParser.parseData(DataUtil.getInstance().readFromAssetOrFile(_configName), _configName);

    // Endpoint configuration
    MBEndpointsConfigurationParser endpointParser = new MBEndpointsConfigurationParser();
    byte[] data = DataUtil.getInstance().readFromAssetOrFile(_endpointsName);
    _endpointConfiguration = data != null ? (MBEndpointsConfiguration) endpointParser.parseData(data, _endpointsName) : null;
  }

  public static MBMetadataService getInstance()
  {
    if (_instance == null)
    {
      _instance = new MBMetadataService();
    }

    return _instance;
  }

  public static void setConfigName(String name)
  {
    _configName = name;
    _instance = null;
  }

  public static void setEndpointsName(String name)
  {
    _endpointsName = name;
    _instance = null;
  }

  public static String getEndpointsName()
  {
    return _endpointsName;
  }

  public MBEndPointDefinition getEndpointForDocumentName(String name)
  {
    return _endpointConfiguration.getEndPointForDocumentName(name);
  }

  public MBDomainDefinition getDefinitionForDomainName(String domainName)
  {
    return getDefinitionForDomainName(domainName, true);
  }

  public MBDomainDefinition getDefinitionForDomainName(String domainName, boolean doThrow)
  {
    MBDomainDefinition domDef = _cfg.getDefinitionForDomainName(domainName);
    if (domDef == null && doThrow)
    {
      String message = "Domain with name " + domainName + " not defined";
      throw new MBDomainNotDefinedException(message);
    }

    return domDef;
  }

  public MBPageDefinition getDefinitionForPageName(String pageName)
  {
    return getDefinitionForPageName(pageName, true);
  }

  public MBPageDefinition getDefinitionForPageName(String pageName, boolean doThrow)
  {
    MBPageDefinition pageDef = _cfg.getDefinitionForPageName(pageName);
    if (pageDef == null && doThrow)
    {
      String message = "Page with name " + pageName + " not defined";
      throw new MBPageNotDefinedException(message);
    }

    return pageDef;
  }

  public MBActionDefinition getDefinitionForActionName(String actionName)
  {
    return getDefinitionForActionName(actionName, true);
  }

  public MBActionDefinition getDefinitionForActionName(String actionName, boolean doThrow)
  {
    MBActionDefinition actionDef = _cfg.getDefinitionForActionName(actionName);
    if (actionDef == null && doThrow)
    {
      String message = "Action with name " + actionName + " not defined";
      throw new MBActionNotDefinedException(message);
    }

    return actionDef;
  }

  public MBDocumentDefinition getDefinitionForDocumentName(String documentName)
  {
    return getDefinitionForDocumentName(documentName, true);
  }

  public MBDocumentDefinition getDefinitionForDocumentName(String documentName, boolean doThrow)
  {
    MBDocumentDefinition docDef = _cfg.getDefinitionForDocumentName(documentName);
    if (docDef == null && doThrow)
    {
      String message = "Document with name " + documentName + " not defined";
      throw new MBDocumentNotDefinedException(message);
    }

    return docDef;
  }

  public MBDialogDefinition getDefinitionForDialogName(String dialogName)
  {
    return getDefinitionForDialogName(dialogName, true);
  }

  public MBDialogDefinition getDefinitionForDialogName(String dialogName, boolean doThrow)
  {
    MBDialogDefinition dialogDef = _cfg.getDefinitionForDialogName(dialogName);
    if (dialogDef == null && doThrow)
    {
      String message = "Dialog with name " + dialogName + " not defined";
      throw new MBDialogNotDefinedException(message);
    }

    return dialogDef;
  }

  public MBPageStackDefinition getDefinitionForPageStackName(String pageStack)
  {
    return getDefinitionForPageStackName(pageStack, true);
  }

  public MBPageStackDefinition getDefinitionForPageStackName(String pageStack, boolean doThrow)
  {
    MBPageStackDefinition dialogDef = _cfg.getDefinitionForPageStackName(pageStack);
    if (dialogDef == null && doThrow)
    {
      String message = "Pagestack with name " + pageStack + " not defined";
      throw new MBDialogNotDefinedException(message);
    }

    return dialogDef;
  }

  public MBDialogDefinition getHomeDialogDefinition()
  {
    if (_homeDialog == null)
    {
      _homeDialog = _cfg.getHomeDialogDefinition();
    }

    return _homeDialog;
  }

  public void setHomeDialogDefinition(MBDialogDefinition dialogDef)
  {
    _homeDialog = dialogDef;
  }

  public List<MBDialogDefinition> getDialogs()
  {
    return new ArrayList<MBDialogDefinition>(_cfg.getDialogs().values());
  }

  public List<MBOutcomeDefinition> getOutcomeDefinitionsForOrigin(MBOutcome.Origin origin, String outcomeName)
  {
    return getOutcomeDefinitionsForOrigin(origin, outcomeName, true);
  }

  public List<MBOutcomeDefinition> getOutcomeDefinitionsForOrigin(MBOutcome.Origin origin, String outcomeName, boolean doThrow)
  {
    List<MBOutcomeDefinition> outcomeDefs = _cfg.getOutcomeDefinitionsForOrigin(origin, outcomeName);
    if (outcomeDefs.size() == 0 && doThrow)
    {
      String message = "Outcome with originName=" + origin + " outcomeName=" + outcomeName + " not defined";
      throw new MBActionNotDefinedException(message);
    }
    return outcomeDefs;
  }

  public MBAlertDefinition getDefinitionForAlertName(String alertName)
  {
    return getDefinitionForAlertName(alertName, true);
  }

  public MBAlertDefinition getDefinitionForAlertName(String alertName, boolean doThrow)
  {
    MBAlertDefinition alertDef = _cfg.getDefinitionForAlertName(alertName);
    if (alertDef == null && doThrow)
    {
      String message = "Alert with name " + alertDef + " not defined";
      throw new MBAlertNotDefinedException(message);
    }
    return alertDef;
  }

  public MBConfigurationDefinition getConfiguration()
  {
    return _cfg;
  }

}

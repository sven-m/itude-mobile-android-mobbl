package com.itude.mobile.mobbl2.client.core.services;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.itude.mobile.mobbl2.client.core.configuration.endpoints.MBEndPointDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.endpoints.MBEndpointsConfiguration;
import com.itude.mobile.mobbl2.client.core.configuration.endpoints.MBEndpointsConfigurationParser;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBActionDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBConfigurationDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDialogDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDocumentDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDomainDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBMvcConfigurationParser;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBOutcomeDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBPageDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBToolDefinition;
import com.itude.mobile.mobbl2.client.core.services.exceptions.MBActionNotDefinedException;
import com.itude.mobile.mobbl2.client.core.services.exceptions.MBDialogNotDefinedException;
import com.itude.mobile.mobbl2.client.core.services.exceptions.MBDocumentNotDefinedException;
import com.itude.mobile.mobbl2.client.core.services.exceptions.MBDomainNotDefinedException;
import com.itude.mobile.mobbl2.client.core.services.exceptions.MBPageNotDefinedException;
import com.itude.mobile.mobbl2.client.core.services.exceptions.MBToolNotDefinedException;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.DataUtil;
import com.itude.mobile.mobbl2.client.core.util.MBDevice;

public final class MBMetadataService
{
  private final MBConfigurationDefinition _cfg;
  private final MBEndpointsConfiguration  _endpointConfiguration;

  private static MBMetadataService        _instance;
  private static String                   _configName       = "config.xml";
  private static String                   _phoneConfigName  = null;
  private static String                   _tabletConfigName = null;
  private static String                   _endpointsName    = "endpoints.xml";

  private MBMetadataService()
  {
    MBMvcConfigurationParser mvcParser = new MBMvcConfigurationParser();
    if (_phoneConfigName != null && (MBDevice.getInstance().isPhone() || MBDevice.getInstance().isPhoneV14())) _configName = _phoneConfigName;
    else if (_tabletConfigName != null && MBDevice.getInstance().isTablet()) _configName = _tabletConfigName;

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

  public static void setPhoneConfigName(String name)
  {
    _phoneConfigName = name;
    _instance = null;
  }

  public static void setTabletConfigName(String name)
  {
    _tabletConfigName = name;
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

  /**
   * A dialog can either be part of a DialogGroup or exist on its own. In some cases it is
   * desirable to get the name of the top dialog. This could either be just the dialog name,
   * or it can be the name of its parent; which is a DialogGroup.
   * 
   * @param dialogName
   * @return the dialog name of the Dialog or its parent, the DialogGroup (if there is one)
   */
  public MBDialogDefinition getTopDialogDefinitionForDialogName(String dialogName)
  {
    MBDialogDefinition def = getDefinitionForDialogName(dialogName);
    if (def.getParent() != null) return getDefinitionForDialogName(def.getParent());
    return def;
  }

  public MBDialogDefinition getHomeDialogDefinition()
  {
    return _cfg.getHomeDialogDefinition();
  }

  public List<MBDialogDefinition> getDialogs()
  {
    return new ArrayList<MBDialogDefinition>(_cfg.getDialogs().values());
  }

  //For now do not raise an exception if an outcome is not defined
  public List<MBOutcomeDefinition> getOutcomeDefinitionsForOrigin(String originName)
  {
    ArrayList<MBOutcomeDefinition> list = (ArrayList<MBOutcomeDefinition>) _cfg.getOutcomeDefinitionsForOrigin(originName);
    if (list == null || list.size() <= 0)
    {
      Log.w(Constants.APPLICATION_NAME, "WARNING No outcomes defined for origin " + originName + " ");
    }

    return list;
  }

  public List<MBOutcomeDefinition> getOutcomeDefinitionsForOrigin(String originName, String outcomeName)
  {
    return getOutcomeDefinitionsForOrigin(originName, outcomeName, true);
  }

  public List<MBOutcomeDefinition> getOutcomeDefinitionsForOrigin(String originName, String outcomeName, boolean doThrow)
  {
    ArrayList<MBOutcomeDefinition> outcomeDefs = (ArrayList<MBOutcomeDefinition>) _cfg.getOutcomeDefinitionsForOrigin(originName,
                                                                                                                      outcomeName);
    if (outcomeDefs.size() == 0 && doThrow)
    {
      String message = "Outcome with originName=" + originName + " outcomeName=" + outcomeName + " not defined";
      throw new MBActionNotDefinedException(message);
    }
    return outcomeDefs;
  }

  public List<MBToolDefinition> getTools()
  {
    return new ArrayList<MBToolDefinition>(_cfg.getTools().values());
  }

  public List<MBToolDefinition> getToolDefinitionsForType(String type)
  {
    List<MBToolDefinition> toolDefs = _cfg.getToolDefinitionsForType(type);

    if (toolDefs.size() == 0)
    {
      String message = "Tool with type=" + type + " not defined";
      throw new MBToolNotDefinedException(message);
    }
    return toolDefs;
  }

  public MBConfigurationDefinition getConfiguration()
  {
    return _cfg;
  }

}

package com.itude.mobile.mobbl2.client.core.configuration.mvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.MBIncludableDefinition;
import com.itude.mobile.mobbl2.client.core.util.Constants;

public class MBConfigurationDefinition extends MBDefinition implements MBIncludableDefinition
{

  private static final String                     ORIGIN_WILDCARD                   = "*";
  public static final String                      DOC_SYSTEM_EMPTY                  = "MBEmpty";
  public static final String                      DOC_SYSTEM_LANGUAGE               = "MBBundle";
  public static final String                      DOC_SYSTEM_EXCEPTION              = "MBException";
  public static final String                      PATH_SYSTEM_EXCEPTION_NAME        = "/Exception[0]/@name";
  public static final String                      PATH_SYSTEM_EXCEPTION_DESCRIPTION = "/Exception[0]/@description";
  public static final String                      PATH_SYSTEM_EXCEPTION_ORIGIN      = "/Exception[0]/@origin";
  public static final String                      PATH_SYSTEM_EXCEPTION_OUTCOME     = "/Exception[0]/@outcome";
  public static final String                      PATH_SYSTEM_EXCEPTION_PATH        = "/Exception[0]/@path";

  public static final String                      DOC_SYSTEM_PROPERTIES             = "MBApplicationProperties";
  public static final String                      DOC_SYSTEM_DEVICE                 = "MBDevice";

  private final Map<String, MBDomainDefinition>   _domainTypes;
  private final Map<String, MBDocumentDefinition> _documentTypes;
  private final Map<String, MBActionDefinition>   _actionTypes;
  private final List<MBOutcomeDefinition>         _outcomeTypes;
  private final Map<String, MBPageDefinition>     _pageTypes;
  private final Map<String, MBDialogDefinition>   _dialogs;
  private MBDialogDefinition                      _homeDialog;
  private final Map<String, MBToolDefinition>     _tools;
  private final Map<String, MBAlertDefinition>    _alerts;

  public MBConfigurationDefinition()
  {
    _domainTypes = new HashMap<String, MBDomainDefinition>();
    _documentTypes = new HashMap<String, MBDocumentDefinition>();
    _actionTypes = new HashMap<String, MBActionDefinition>();
    _outcomeTypes = new ArrayList<MBOutcomeDefinition>();
    _dialogs = Collections.synchronizedMap(new LinkedHashMap<String, MBDialogDefinition>());
    _pageTypes = new HashMap<String, MBPageDefinition>();
    _tools = new LinkedHashMap<String, MBToolDefinition>();
    _alerts = new HashMap<String, MBAlertDefinition>();
  }

  @Override
  public void addAll(MBIncludableDefinition otherDefinition)
  {
    MBConfigurationDefinition otherConfig = null;

    if (otherDefinition instanceof MBConfigurationDefinition)
    {
      otherConfig = (MBConfigurationDefinition) otherDefinition;
    }

    if (_homeDialog == null)
    {
      _homeDialog = otherConfig.getHomeDialogDefinition();
    }

    for (MBDocumentDefinition documentDef : otherConfig.getDocuments().values())
    {
      addDocument(documentDef);
    }
    for (MBDomainDefinition domainDef : otherConfig.getDomains().values())
    {
      addDomain(domainDef);
    }
    for (MBActionDefinition actionDef : otherConfig.getActions().values())
    {
      addAction(actionDef);
    }
    for (MBOutcomeDefinition outcomeDef : otherConfig.getOutcomes())
    {
      addOutcome(outcomeDef);
    }
    for (MBDialogDefinition dialogDef : otherConfig.getDialogs().values())
    {
      addDialog(dialogDef);
    }
    for (MBPageDefinition pageDef : otherConfig.getPages().values())
    {
      addPage(pageDef);
    }
    for (MBToolDefinition toolDef : otherConfig.getTools().values())
    {
      addTool(toolDef);
    }
    for (MBAlertDefinition alertDef : otherConfig.getAlerts().values())
    {
      addAlert(alertDef);
    }
  }

  @Override
  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    StringUtil.appendIndentString(appendToMe, level).append("<Configuration>\n").append(StringUtil.getIndentStringWithLevel(level + 2))
        .append("<Model>\n").append(StringUtil.getIndentStringWithLevel(level + 4)).append("<Domains>\n");
    for (MBDomainDefinition domain : _domainTypes.values())
    {
      domain.asXmlWithLevel(appendToMe, level + 6);
    }
    StringUtil.appendIndentString(appendToMe, level + 4).append("</Domains>\n").append(StringUtil.getIndentStringWithLevel(level + 4))
        .append("<Documents>\n");
    for (MBDocumentDefinition document : _documentTypes.values())
    {
      document.asXmlWithLevel(appendToMe, level + 6);
    }
    appendToMe.append(StringUtil.getIndentStringWithLevel(level + 4)).append("</Documents>\n")
        .append(StringUtil.getIndentStringWithLevel(level + 2)).append("</Model>\n").append(StringUtil.getIndentStringWithLevel(level + 2))
        .append("<Controller>\n").append(StringUtil.getIndentStringWithLevel(level + 4)).append("<Actions>\n");
    for (MBActionDefinition acion : _actionTypes.values())
    {
      acion.asXmlWithLevel(appendToMe, level + 6);
    }
    appendToMe.append(StringUtil.getIndentStringWithLevel(level + 4)).append("</Actions>\n")
        .append(StringUtil.getIndentStringWithLevel(level + 4)).append("<Wiring>\n");
    for (MBOutcomeDefinition outcome : _outcomeTypes)
    {
      outcome.asXmlWithLevel(appendToMe, level + 6);
    }
    appendToMe.append(StringUtil.getIndentStringWithLevel(level + 4)).append("</Wiring>\n")
        .append(StringUtil.getIndentStringWithLevel(level + 2)).append("</Controller>\n")
        .append(StringUtil.getIndentStringWithLevel(level + 2)).append("<View>\n").append(StringUtil.getIndentStringWithLevel(level + 4))
        .append("<Dialogs>\n");
    for (MBDialogDefinition dialog : _dialogs.values())
    {
      dialog.asXmlWithLevel(appendToMe, level + 6);
    }
    appendToMe.append(StringUtil.getIndentStringWithLevel(level + 4)).append("</Dialogs>\n")
        .append(StringUtil.getIndentStringWithLevel(level + 4)).append("<Toolbar>\n");
    for (MBToolDefinition tool : _tools.values())
    {
      tool.asXmlWithLevel(appendToMe, level + 6);
    }
    appendToMe.append(StringUtil.getIndentStringWithLevel(level + 4)).append("</Toolbar>\n");

    // Append pages
    for (MBPageDefinition page : _pageTypes.values())
    {
      page.asXmlWithLevel(appendToMe, level + 4);
    }

    // Append alerts
    appendToMe.append(StringUtil.getIndentStringWithLevel(level + 4)).append("<Alerts>\n");
    for (MBAlertDefinition alert : _alerts.values())
    {
      alert.asXmlWithLevel(appendToMe, level + 6);
    }
    appendToMe.append(StringUtil.getIndentStringWithLevel(level + 4)).append("</Alerts>\n");

    // Close configuration
    appendToMe.append(StringUtil.getIndentStringWithLevel(level + 2)).append("</View>\n")
        .append(StringUtil.getIndentStringWithLevel(level)).append("</Configuration>\n");

    return appendToMe;
  }

  @Override
  public void addChildElement(MBActionDefinition child)
  {
    addAction(child);
  }

  @Override
  public void addChildElement(MBDialogDefinition child)
  {
    addDialog(child);
  }

  @Override
  public void addChildElement(MBAlertDefinition child)
  {
    addAlert(child);
  }

  @Override
  public void addChildElement(MBDocumentDefinition child)
  {
    addDocument(child);
  }

  @Override
  public void addChildElement(MBDomainDefinition child)
  {
    addDomain(child);
  }

  @Override
  public void addChildElement(MBOutcomeDefinition child)
  {
    addOutcome(child);
  }

  @Override
  public void addChildElement(MBPageDefinition child)
  {
    addPage(child);
  }

  @Override
  public void addChildElement(MBToolDefinition child)
  {
    addTool(child);
  }

  public void addDomain(MBDomainDefinition domain)
  {
    if (_domainTypes.containsKey(domain.getName()))
    {
      Log.w(Constants.APPLICATION_NAME, "Domain definition overridden: multiple definitions for domain with name " + domain.getName());
    }
    _domainTypes.put(domain.getName(), domain);
  }

  public void addDocument(MBDocumentDefinition document)
  {
    if (_documentTypes.containsKey(document.getName()))
    {
      Log.w(Constants.APPLICATION_NAME, "Document definition overridden: multiple definitions for document with name " + document.getName());
    }
    _documentTypes.put(document.getName(), document);
  }

  public void addAction(MBActionDefinition action)
  {
    if (_actionTypes.containsKey(action.getName()))
    {
      Log.w(Constants.APPLICATION_NAME, "Action definition overridden: multiple definitions for action with name " + action.getName());
    }
    _actionTypes.put(action.getName(), action);
  }

  public void addOutcome(MBOutcomeDefinition outcome)
  {
    _outcomeTypes.add(outcome);
  }

  public void addPage(MBPageDefinition page)
  {
    if (_pageTypes.containsKey(page.getName()))
    {
      Log.w(Constants.APPLICATION_NAME, "Page definition overridden: multiple definitions for page with name " + page.getName());
    }
    _pageTypes.put(page.getName(), page);
  }

  public void addDialog(MBDialogDefinition dialog)
  {
    if (_dialogs.containsKey(dialog.getName()))
    {
      Log.w(Constants.APPLICATION_NAME, "Dialog definition overridden: multiple definitions for action with name " + dialog.getName());
    }

    if (_homeDialog == null)
    {
      _homeDialog = dialog;
    }
    _dialogs.put(dialog.getName(), dialog);

    createImplicitOutcomeForDialog(dialog);
  }

  private void createImplicitOutcomeForDialog(MBDialogDefinition dialog)
  {
    List<MBOutcomeDefinition> def = getOutcomeDefinitionsForOrigin(ORIGIN_WILDCARD, dialog.getName());
    if (def.isEmpty())
    {
      MBOutcomeDefinition definition = new MBOutcomeDefinition();
      definition.setAction("none");
      definition.setDialog(dialog.getName());
      definition.setName(dialog.getName());
      definition.setNoBackgroundProcessing(true);
      definition.setOrigin(ORIGIN_WILDCARD);
      addOutcome(definition);
    }
  }

  public void addTool(MBToolDefinition tool)
  {
    if (_tools.containsKey(tool.getName()))
    {
      Log.w(Constants.APPLICATION_NAME, "Tool definition overridden: multiple definitions for tool with name " + tool.getName());
    }

    _tools.put(tool.getName(), tool);
  }

  public void addAlert(MBAlertDefinition alert)
  {
    if (_alerts.containsKey(alert.getName()))
    {
      Log.w(Constants.APPLICATION_NAME, "Alert definition overridden: multiple definitions for alert with name " + alert.getName());
    }

    _alerts.put(alert.getName(), alert);
  }

  public MBDomainDefinition getDefinitionForDomainName(String domainName)
  {
    return _domainTypes.get(domainName);
  }

  public MBPageDefinition getDefinitionForPageName(String name)
  {
    return _pageTypes.get(name);
  }

  public MBActionDefinition getDefinitionForActionName(String actionName)
  {
    return _actionTypes.get(actionName);
  }

  public MBDocumentDefinition getDefinitionForDocumentName(String documentName)
  {
    return _documentTypes.get(documentName);
  }

  public MBDialogDefinition getDefinitionForDialogName(String dialogName)
  {
    return _dialogs.get(dialogName);
  }

  public List<MBOutcomeDefinition> getOutcomeDefinitionsForOrigin(String originName)
  {
    List<MBOutcomeDefinition> result = new ArrayList<MBOutcomeDefinition>();

    for (MBOutcomeDefinition outcomeDef : _outcomeTypes)
    {
      if (outcomeDef.getOrigin().equals(originName) || outcomeDef.getOrigin().equals(ORIGIN_WILDCARD))
      {
        result.add(outcomeDef);
      }
    }

    return result;
  }

  public List<MBOutcomeDefinition> getOutcomeDefinitionsForOrigin(String originName, String outcomeName)
  {
    List<MBOutcomeDefinition> result = new ArrayList<MBOutcomeDefinition>();

    // First look for specific matches
    for (MBOutcomeDefinition outcomeDef : _outcomeTypes)
    {
      if (outcomeDef.getOrigin().equals(originName) && outcomeDef.getName().equals(outcomeName))
      {
        result.add(outcomeDef);
      }
    }

    // If there are no specific matches; and there are wildcard matches (outcomeName matches and origin='*') then add these:
    if (result.size() <= 0)
    {
      for (MBOutcomeDefinition outcomeDef : _outcomeTypes)
      {
        if (outcomeDef.getOrigin().equals(ORIGIN_WILDCARD) && outcomeDef.getName().equals(outcomeName))
        {
          result.add(outcomeDef);
        }
      }
    }

    return result;
  }

  public Map<String, MBDialogDefinition> getDialogs()
  {
    return _dialogs;
  }

  public Map<String, MBDomainDefinition> getDomains()
  {
    return _domainTypes;
  }

  public Map<String, MBActionDefinition> getActions()
  {
    return _actionTypes;
  }

  public List<MBOutcomeDefinition> getOutcomes()
  {
    return _outcomeTypes;
  }

  public Map<String, MBDocumentDefinition> getDocuments()
  {
    return _documentTypes;
  }

  public Map<String, MBPageDefinition> getPages()
  {
    return _pageTypes;
  }

  public MBDialogDefinition getHomeDialogDefinition()
  {
    return _homeDialog;
  }

  public Map<String, MBToolDefinition> getTools()
  {
    return _tools;
  }

  public List<MBToolDefinition> getToolDefinitionsForType(String type)
  {
    ArrayList<MBToolDefinition> result = new ArrayList<MBToolDefinition>();

    for (MBToolDefinition toolDef : _tools.values())
    {
      if (type.equals(toolDef.getType()))
      {
        result.add(toolDef);
      }
    }
    return result;
  }

  public Map<String, MBAlertDefinition> getAlerts()
  {
    return _alerts;
  }

  public MBAlertDefinition getDefinitionForAlertName(String name)
  {
    return _alerts.get(name);
  }

}

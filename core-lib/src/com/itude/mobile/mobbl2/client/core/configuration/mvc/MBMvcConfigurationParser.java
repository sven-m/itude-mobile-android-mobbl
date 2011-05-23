package com.itude.mobile.mobbl2.client.core.configuration.mvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.itude.mobile.mobbl2.client.core.configuration.MBConfigurationParser;
import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBPageDefinition.MBPageType;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.exceptions.MBFileNotFoundException;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.exceptions.MBInvalidPageTypeException;
import com.itude.mobile.mobbl2.client.core.services.MBDataManagerService;
import com.itude.mobile.mobbl2.client.core.util.DataUtil;

public class MBMvcConfigurationParser extends MBConfigurationParser
{
  private List<String>              _configAttributes;
  private List<String>              _documentAttributes;
  private List<String>              _elementAttributes;
  private List<String>              _attributeAttributes;
  private List<String>              _actionAttributes;
  private List<String>              _outcomeAttributes;
  private List<String>              _dialogAttributes;
  private List<String>              _dialogGroupAttributes;
  private List<String>              _pageAttributes;
  private List<String>              _panelAttributes;
  private List<String>              _forEachAttributes;
  private List<String>              _fieldAttributes;
  private List<String>              _domainAttributes;
  private List<String>              _domainValidatorAttributes;
  private List<String>              _variableAttributes;
  private MBConfigurationDefinition _rootConfig;

  @Override
  public MBDefinition parseData(byte[] data, String documentName)
  {
    if (_configAttributes == null)
    {
      _configAttributes = new ArrayList<String>();
      _configAttributes.add("xmlns");
    }
    if (_documentAttributes == null)
    {
      _documentAttributes = new ArrayList<String>();
      _documentAttributes.add("xmlns");
      _documentAttributes.add("name");
      _documentAttributes.add("dataManager");
      _documentAttributes.add("autoCreate");
    }
    if (_elementAttributes == null)
    {
      _elementAttributes = new ArrayList<String>();
      _elementAttributes.add("xmlns");
      _elementAttributes.add("name");
      _elementAttributes.add("minOccurs");
      _elementAttributes.add("maxOccurs");
    }
    if (_attributeAttributes == null)
    {
      _attributeAttributes = new ArrayList<String>();
      _attributeAttributes.add("xmlns");
      _attributeAttributes.add("name");
      _attributeAttributes.add("type");
      _attributeAttributes.add("required");
      _attributeAttributes.add("defaultValue");
    }
    if (_actionAttributes == null)
    {
      _actionAttributes = new ArrayList<String>();
      _actionAttributes.add("xmlns");
      _actionAttributes.add("name");
      _actionAttributes.add("className");
    }
    if (_outcomeAttributes == null)
    {
      _outcomeAttributes = new ArrayList<String>();
      _outcomeAttributes.add("xmlns");
      _outcomeAttributes.add("origin");
      _outcomeAttributes.add("name");
      _outcomeAttributes.add("action");
      _outcomeAttributes.add("dialog");
      _outcomeAttributes.add("displayMode");
      _outcomeAttributes.add("persist");
      _outcomeAttributes.add("transferDocument");
      _outcomeAttributes.add("preCondition");
      _outcomeAttributes.add("noBackgroundProcessing");
    }
    if (_dialogAttributes == null)
    {
      _dialogAttributes = new ArrayList<String>();
      _dialogAttributes.add("xmlns");
      _dialogAttributes.add("name");
      _dialogAttributes.add("title");
      _dialogAttributes.add("mode");
      _dialogAttributes.add("icon");
    }
    if (_dialogGroupAttributes == null)
    {
      _dialogGroupAttributes = new ArrayList<String>();
      _dialogGroupAttributes.add("xmlns");
      _dialogGroupAttributes.add("name");
      _dialogGroupAttributes.add("title");
      _dialogGroupAttributes.add("mode");
      _dialogGroupAttributes.add("icon");
    }
    if (_pageAttributes == null)
    {
      _pageAttributes = new ArrayList<String>();
      _pageAttributes.add("xmlns");
      _pageAttributes.add("name");
      _pageAttributes.add("type");
      _pageAttributes.add("document");
      _pageAttributes.add("title");
      _pageAttributes.add("titlePath");
      _pageAttributes.add("width");
      _pageAttributes.add("height");
      _pageAttributes.add("preCondition");
      _pageAttributes.add("style");
      _pageAttributes.add("orientationPermissions");
    }
    if (_panelAttributes == null)
    {
      _panelAttributes = new ArrayList<String>();
      _panelAttributes.add("xmlns");
      _panelAttributes.add("name");
      _panelAttributes.add("type");
      _panelAttributes.add("style");
      _panelAttributes.add("title");
      _panelAttributes.add("titlePath");
      _panelAttributes.add("width");
      _panelAttributes.add("height");
      _panelAttributes.add("preCondition");
      _panelAttributes.add("outcome");
      _panelAttributes.add("path");
      _panelAttributes.add("mode");
      _panelAttributes.add("permissions");
    }
    if (_forEachAttributes == null)
    {
      _forEachAttributes = new ArrayList<String>();
      _forEachAttributes.add("xmlns");
      _forEachAttributes.add("name");
      _forEachAttributes.add("value");
      _forEachAttributes.add("suppressRowComponent");
      _forEachAttributes.add("preCondition");
    }
    if (_variableAttributes == null)
    {
      _variableAttributes = new ArrayList<String>();
      _variableAttributes.add("xmlns");
      _variableAttributes.add("name");
      _variableAttributes.add("expression");
    }
    if (_fieldAttributes == null)
    {
      _fieldAttributes = new ArrayList<String>();
      _fieldAttributes.add("xmlns");
      _fieldAttributes.add("name");
      _fieldAttributes.add("label");
      _fieldAttributes.add("path");
      _fieldAttributes.add("type");
      _fieldAttributes.add("dataType");
      _fieldAttributes.add("required");
      _fieldAttributes.add("outcome");
      _fieldAttributes.add("style");
      _fieldAttributes.add("width");
      _fieldAttributes.add("height");
      _fieldAttributes.add("formatMask");
      _fieldAttributes.add("alignment");
      _fieldAttributes.add("valueIfNil");
      _fieldAttributes.add("hidden");
      _fieldAttributes.add("preCondition");
      _fieldAttributes.add("custom1");
      _fieldAttributes.add("custom2");
      _fieldAttributes.add("custom3");
    }
    if (_domainAttributes == null)
    {
      _domainAttributes = new ArrayList<String>();
      _domainAttributes.add("xmlns");
      _domainAttributes.add("name");
      _domainAttributes.add("type");
      _domainAttributes.add("maxLength");
    }
    if (_domainValidatorAttributes == null)
    {
      _domainValidatorAttributes = new ArrayList<String>();
      _domainValidatorAttributes.add("xmlns");
      _domainValidatorAttributes.add("name");
      _domainValidatorAttributes.add("title");
      _domainValidatorAttributes.add("value");
      _domainValidatorAttributes.add("lowerBound");
      _domainValidatorAttributes.add("upperBound");
    }

    MBConfigurationDefinition conf = (MBConfigurationDefinition) super.parseData(data, documentName);

    if (conf.getDefinitionForDocumentName(MBConfigurationDefinition.DOC_SYSTEM_EXCEPTION) == null)
    {
      addSystemDocuments(conf);
    }

    return conf;
  }

  private void addAttribute(MBElementDefinition elementDef, String name, String type)
  {
    MBAttributeDefinition attributeDef = new MBAttributeDefinition();
    attributeDef.setName(name);
    attributeDef.setType(type);
    elementDef.addAttribute(attributeDef);
  }

  @Override
  public boolean processElement(String elementName, Map<String, String> attributeDict)
  {

    if (elementName.equals("Configuration"))
    {
      checkAttributesForElement(elementName, attributeDict, _configAttributes);

      MBConfigurationDefinition confDef = new MBConfigurationDefinition();
      getStack().add(confDef);
      _rootConfig = confDef;
    }
    else if (elementName.equals("Include"))
    {
      String name = attributeDict.get("name");
      MBMvcConfigurationParser parser = new MBMvcConfigurationParser();
      byte[] data = DataUtil.getInstance().readFromAssetOrFile(name);
      if (data == null)
      {
        throw new MBFileNotFoundException(name);
      }
      MBConfigurationDefinition include = (MBConfigurationDefinition) parser.parseData(data, name);
      _rootConfig.addAll(include);
    }
    else if (elementName.equals("Document"))
    {
      checkAttributesForElement(elementName, attributeDict, _documentAttributes);

      MBDocumentDefinition docDef = new MBDocumentDefinition();
      docDef.setName(attributeDict.get("name"));
      docDef.setDataManager(attributeDict.get("dataManager"));
      docDef.setAutoCreate(Boolean.parseBoolean(attributeDict.get("autoCreate")));

      notifyProcessed(docDef);
    }
    else if (elementName.equals("Element"))
    {
      checkAttributesForElement(elementName, attributeDict, _elementAttributes);

      MBElementDefinition elementDef = new MBElementDefinition();
      elementDef.setName(attributeDict.get("name"));
      if (attributeDict.containsKey("minOccurs"))
      {
        elementDef.setMinOccurs(Integer.parseInt(attributeDict.get("minOccurs")));
      }
      if (attributeDict.containsKey("maxOccurs"))
      {
        elementDef.setMaxOccurs(Integer.parseInt(attributeDict.get("maxOccurs")));
      }

      notifyProcessed(elementDef);
    }
    else if (elementName.equals("Attribute"))
    {
      checkAttributesForElement(elementName, attributeDict, _attributeAttributes);

      MBAttributeDefinition attributeDef = new MBAttributeDefinition();
      attributeDef.setName(attributeDict.get("name"));
      attributeDef.setType(attributeDict.get("type"));
      attributeDef.setDefaultValue(attributeDict.get("defaultValue"));
      attributeDef.setRequired(Boolean.parseBoolean(attributeDict.get("required")));

      notifyProcessed(attributeDef);
    }
    else if (elementName.equals("Action"))
    {
      checkAttributesForElement(elementName, attributeDict, _actionAttributes);

      MBActionDefinition actionDef = new MBActionDefinition();
      actionDef.setName(attributeDict.get("name"));
      actionDef.setClassName(attributeDict.get("className"));

      notifyProcessed(actionDef);
    }
    else if (elementName.equals("Outcome"))
    {
      checkAttributesForElement(elementName, attributeDict, _outcomeAttributes);

      MBOutcomeDefinition outcomeDef = new MBOutcomeDefinition();
      outcomeDef.setOrigin(attributeDict.get("origin"));
      outcomeDef.setName(attributeDict.get("name"));
      outcomeDef.setAction(attributeDict.get("action"));
      outcomeDef.setDialog(attributeDict.get("dialog"));
      outcomeDef.setDisplayMode(attributeDict.get("displayMode"));
      outcomeDef.setPreCondition(attributeDict.get("preCondition"));
      outcomeDef.setPersist(Boolean.parseBoolean(attributeDict.get("persist")));
      outcomeDef.setTransferDocument(Boolean.parseBoolean(attributeDict.get("transferDocument")));
      outcomeDef.setNoBackgroundProcessing(Boolean.parseBoolean(attributeDict.get("noBackgroundProcessing")));

      notifyProcessed(outcomeDef);
    }
    else if (elementName.equals("Dialog"))
    {
      checkAttributesForElement(elementName, attributeDict, _dialogAttributes);

      MBDialogDefinition dialogDef = new MBDialogDefinition();
      dialogDef.setName(attributeDict.get("name"));
      dialogDef.setTitle(attributeDict.get("title"));
      dialogDef.setMode(attributeDict.get("mode"));
      dialogDef.setIcon(attributeDict.get("icon"));

      // On tablets, we can have a split view, which is defined as a DialogGroup in xml
      MBDefinition lastDef = getStack().peek();
      if (lastDef instanceof MBDialogGroupDefinition) dialogDef.setParent(lastDef.getName());

      notifyProcessed(dialogDef);
    }
    else if (elementName.equals("DialogGroup"))
    {
      checkAttributesForElement(elementName, attributeDict, _dialogGroupAttributes);

      MBDialogGroupDefinition dialogDef = new MBDialogGroupDefinition();
      dialogDef.setName(attributeDict.get("name"));
      dialogDef.setTitle(attributeDict.get("title"));
      dialogDef.setMode(attributeDict.get("mode"));
      dialogDef.setIcon(attributeDict.get("icon"));

      notifyProcessed(dialogDef);
    }
    else if (elementName.equals("Page"))
    {
      checkAttributesForElement(elementName, attributeDict, _pageAttributes);

      MBPageDefinition pageDef = new MBPageDefinition();
      pageDef.setName(attributeDict.get("name"));
      pageDef.setDocumentName(attributeDict.get("document"));
      pageDef.setTitle(attributeDict.get("title"));
      pageDef.setTitlePath(attributeDict.get("titlePath"));
      if (attributeDict.containsKey("width"))
      {
        pageDef.setWidth(Integer.parseInt(attributeDict.get("width")));
      }
      if (attributeDict.containsKey("height"))
      {
        pageDef.setHeight(Integer.parseInt(attributeDict.get("height")));
      }
      pageDef.setPreCondition(attributeDict.get("preCondition"));
      pageDef.setStyle(attributeDict.get("style"));

      String type = attributeDict.get("type");
      if (type != null)
      {
        if (type.equals("normal"))
        {
          pageDef.setPageType(MBPageType.MBPageTypesNormal);
        }
        else if (type.equals("popup"))
        {
          pageDef.setPageType(MBPageType.MBPageTypesPopup);
        }
        else if (type.equals("error"))
        {
          pageDef.setPageType(MBPageType.MBPageTypesErrorPage);
        }
        else
        {
          throw new MBInvalidPageTypeException(type);
        }
      }
      pageDef.setOrientationPermissions(attributeDict.get("orientationPermissions"));

      notifyProcessed(pageDef);
    }
    else if (elementName.equals("Panel"))
    {
      checkAttributesForElement(elementName, attributeDict, _panelAttributes);

      MBPanelDefinition panelDef = new MBPanelDefinition();
      panelDef.setType(attributeDict.get("type"));
      panelDef.setName(attributeDict.get("name"));
      panelDef.setStyle(attributeDict.get("style"));
      panelDef.setTitle(attributeDict.get("title"));
      panelDef.setTitlePath(attributeDict.get("titlePath"));
      if (attributeDict.containsKey("width"))
      {
        panelDef.setWidth(Integer.parseInt(attributeDict.get("width")));
      }
      if (attributeDict.containsKey("height"))
      {
        panelDef.setHeight(Integer.parseInt(attributeDict.get("height")));
      }
      panelDef.setPreCondition(attributeDict.get("preCondition"));
      panelDef.setOutcomeName(attributeDict.get("outcome"));
      panelDef.setPath(attributeDict.get("path"));
      panelDef.setMode(attributeDict.get("mode"));
      panelDef.setPermissions(attributeDict.get("permissions"));

      notifyProcessed(panelDef);
    }
    else if (elementName.equals("ForEach"))
    {
      checkAttributesForElement(elementName, attributeDict, _forEachAttributes);

      MBForEachDefinition forEachDef = new MBForEachDefinition();
      forEachDef.setValue(attributeDict.get("value"));
      forEachDef.setSuppressRowComponent(Boolean.parseBoolean(attributeDict.get("suppressRowComponent")));
      forEachDef.setPreCondition(attributeDict.get("preCondition"));

      notifyProcessed(forEachDef);
    }
    else if (elementName.equals("Variable"))
    {
      checkAttributesForElement(elementName, attributeDict, _variableAttributes);

      MBVariableDefinition variableDef = new MBVariableDefinition();
      variableDef.setName(attributeDict.get("name"));
      variableDef.setExpression(attributeDict.get("expression"));

      notifyProcessed(variableDef);
    }
    else if (elementName.equals("Field"))
    {
      checkAttributesForElement(elementName, attributeDict, _fieldAttributes);

      MBFieldDefinition fieldDef = new MBFieldDefinition();
      fieldDef.setName(attributeDict.get("name"));
      fieldDef.setLabel(attributeDict.get("label"));
      fieldDef.setPath(attributeDict.get("path"));
      fieldDef.setDisplayType(attributeDict.get("type"));
      fieldDef.setDataType(attributeDict.get("dataType"));
      fieldDef.setStyle(attributeDict.get("style"));
      fieldDef.setRequired(attributeDict.get("required"));
      fieldDef.setOutcomeName(attributeDict.get("outcome"));
      fieldDef.setWidth(attributeDict.get("width"));
      fieldDef.setHeight(attributeDict.get("height"));
      fieldDef.setFormatMask(attributeDict.get("formatMask"));
      fieldDef.setAlignment(attributeDict.get("alignment"));
      fieldDef.setValueIfNil(attributeDict.get("valueIfNil"));
      fieldDef.setHidden(attributeDict.get("hidden"));
      fieldDef.setPreCondition(attributeDict.get("preCondition"));
      fieldDef.setCustom1(attributeDict.get("custom1"));
      fieldDef.setCustom2(attributeDict.get("custom2"));
      fieldDef.setCustom3(attributeDict.get("custom3"));

      notifyProcessed(fieldDef);
    }
    else if (elementName.equals("Domain"))
    {
      checkAttributesForElement(elementName, attributeDict, _domainAttributes);

      MBDomainDefinition domainDef = new MBDomainDefinition();
      domainDef.setName(attributeDict.get("name"));
      domainDef.setType(attributeDict.get("type"));
      if (attributeDict.containsKey("maxLength"))
      {
        domainDef.setMaxLength(new BigDecimal(attributeDict.get("maxLength")));
      }

      notifyProcessed(domainDef);
    }
    else if (elementName.equals("DomainValidator"))
    {
      checkAttributesForElement(elementName, attributeDict, _domainValidatorAttributes);

      MBDomainValidatorDefinition validatorDef = new MBDomainValidatorDefinition();
      validatorDef.setName(attributeDict.get("name"));
      validatorDef.setTitle(attributeDict.get("title"));
      validatorDef.setValue(attributeDict.get("value"));
      if (attributeDict.containsKey("lowerBound"))
      {
        validatorDef.setLowerBound(new BigDecimal(attributeDict.get("lowerBound")));
      }
      if (attributeDict.containsKey("upperBound"))
      {
        validatorDef.setUpperBound(new BigDecimal(attributeDict.get("upperBound")));
      }

      notifyProcessed(validatorDef);
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
    if (elementName.equals("Field"))
    {
      MBFieldDefinition fieldDef = (MBFieldDefinition) getStack().get(getStack().size() - 1);
      fieldDef.setText(getCharacters());
    }
    else if ("DialogGroup".equals(elementName))
    {
      // On tablets, we can have a split view in a tab. In XML they are defined as two dialogs in a dialogGroup.
      // This means that the dialogs are automatically added to a dialogGroup. 
      // That is why we need to make sure that the dialogs are also kept locally, like on the phone, because the local references are used to address the Dialogs
      // Thats why we copy them here after the group has been added.
      MBDefinition configDef = getStack().elementAt(getStack().size() - 2);
      MBDialogGroupDefinition groupDef = (MBDialogGroupDefinition) getStack().peek();
      for (MBDialogDefinition dialogDef : groupDef.getChildren())
        configDef.addChildElement(dialogDef);
    }

    if (!elementName.equals("Configuration") && !elementName.equals("Include"))
    {
      getStack().remove(getStack().size() - 1);
    }

  }

  @Override
  public boolean isConcreteElement(String element)
  {
    return element.equals("Configuration") || element.equals("Include") || element.equals("Document") || element.equals("Element")
           || element.equals("Attribute") || element.equals("Action") || element.equals("Outcome") || element.equals("Page")
           || element.equals("Dialog") || element.equals("DialogGroup") || element.equals("ForEach") || element.equals("Variable")
           || element.equals("Panel") || element.equals("Field") || element.equals("Domain") || element.equals("DomainValidator");
  }

  @Override
  public boolean isIgnoredElement(String element)
  {
    return element.equals("Model") || element.equals("Dialogs") || element.equals("Domains") || element.equals("Documents")
           || element.equals("Controller") || element.equals("Actions") || element.equals("Wiring") || element.equals("View");
  }

  private void addExceptionDocument(MBConfigurationDefinition conf)
  {
    MBDocumentDefinition docDef = new MBDocumentDefinition();
    docDef.setName(MBConfigurationDefinition.DOC_SYSTEM_EXCEPTION);
    docDef.setDataManager(MBDataManagerService.DATA_HANDLER_MEMORY);
    docDef.setAutoCreate(true);

    MBElementDefinition elementDef = new MBElementDefinition();
    elementDef.setName("Exception");
    elementDef.setMinOccurs(1);

    docDef.addElement(elementDef);
    addAttribute(elementDef, "name", "string");
    addAttribute(elementDef, "description", "string");
    addAttribute(elementDef, "origin", "string");
    addAttribute(elementDef, "outcome", "string");
    addAttribute(elementDef, "path", "string");

    MBElementDefinition stackLine = new MBElementDefinition();
    stackLine.setName("Stackline");
    stackLine.setMinOccurs(0);
    addAttribute(stackLine, "line", "string");
    elementDef.addElement(stackLine);

    conf.addDocument(docDef);
  }

  private void addEmptyDocument(MBConfigurationDefinition conf)
  {
    MBDocumentDefinition docDef = new MBDocumentDefinition();
    docDef.setName(MBConfigurationDefinition.DOC_SYSTEM_EMPTY);
    docDef.setDataManager(MBDataManagerService.DATA_HANDLER_MEMORY);
    docDef.setAutoCreate(true);

    MBElementDefinition elementDef = new MBElementDefinition();
    elementDef.setName("Empty");
    elementDef.setMinOccurs(1);

    docDef.addElement(elementDef);
    conf.addDocument(docDef);
  }

  private void addLanguageDocument(MBConfigurationDefinition conf)
  {
    MBDocumentDefinition docDef = new MBDocumentDefinition();
    docDef.setName(MBConfigurationDefinition.DOC_SYSTEM_LANGUAGE);
    docDef.setDataManager(MBDataManagerService.DATA_HANDLER_MEMORY);
    docDef.setAutoCreate(true);

    MBElementDefinition elementDef = new MBElementDefinition();
    elementDef.setName("Text");
    addAttribute(elementDef, "key", "string");
    addAttribute(elementDef, "value", "string");
    elementDef.setMinOccurs(0);

    docDef.addElement(elementDef);
    conf.addDocument(docDef);
  }

  private void addPropertiesDocument(MBConfigurationDefinition conf)
  {
    MBDocumentDefinition docDef = new MBDocumentDefinition();
    docDef.setName(MBConfigurationDefinition.DOC_SYSTEM_PROPERTIES);
    docDef.setDataManager(MBDataManagerService.DATA_HANDLER_SYSTEM);
    docDef.setAutoCreate(true);

    MBElementDefinition elementDef = new MBElementDefinition();
    elementDef.setMinOccurs(1);
    elementDef.setName("System");

    MBElementDefinition propDef = new MBElementDefinition();
    propDef.setMinOccurs(0);
    propDef.setName("Property");
    addAttribute(propDef, "name", "string");
    addAttribute(propDef, "value", "string");
    elementDef.addElement(propDef);
    docDef.addElement(elementDef);

    MBElementDefinition elementDef2 = new MBElementDefinition();
    elementDef2.setMinOccurs(1);
    elementDef2.setName("Application");

    MBElementDefinition propDef2 = new MBElementDefinition();
    propDef2.setMinOccurs(0);
    propDef2.setName("Property");
    addAttribute(propDef2, "name", "string");
    addAttribute(propDef2, "value", "string");
    elementDef2.addElement(propDef2);

    docDef.addElement(elementDef2);
    conf.addDocument(docDef);
  }

  private void addSystemDocuments(MBConfigurationDefinition conf)
  {
    addExceptionDocument(conf);
    addEmptyDocument(conf);
    addPropertiesDocument(conf);
    addLanguageDocument(conf);
  }

  public List<String> getConfigAttributes()
  {
    return _configAttributes;
  }

  public void setConfigAttributes(List<String> configAttributes)
  {
    _configAttributes = configAttributes;
  }

  public List<String> getDocumentAttributes()
  {
    return _documentAttributes;
  }

  public void setDocumentAttributes(List<String> documentAttributes)
  {
    _documentAttributes = documentAttributes;
  }

  public List<String> getElementAttributes()
  {
    return _elementAttributes;
  }

  public void setElementAttributes(List<String> elementAttributes)
  {
    _elementAttributes = elementAttributes;
  }

  public List<String> getAttributeAttributes()
  {
    return _attributeAttributes;
  }

  public void setAttributeAttributes(List<String> attributeAttributes)
  {
    _attributeAttributes = attributeAttributes;
  }

  public List<String> getActionAttributes()
  {
    return _actionAttributes;
  }

  public void setActionAttributes(List<String> actionAttributes)
  {
    _actionAttributes = actionAttributes;
  }

  public List<String> getOutcomeAttributes()
  {
    return _outcomeAttributes;
  }

  public void setOutcomeAttributes(List<String> outcomeAttributes)
  {
    _outcomeAttributes = outcomeAttributes;
  }

  public List<String> getDialogAttributes()
  {
    return _dialogAttributes;
  }

  public void setDialogAttributes(List<String> dialogAttributes)
  {
    _dialogAttributes = dialogAttributes;
  }

  public List<String> getPageAttributes()
  {
    return _pageAttributes;
  }

  public void setPageAttributes(List<String> pageAttributes)
  {
    _pageAttributes = pageAttributes;
  }

  public List<String> getPanelAttributes()
  {
    return _panelAttributes;
  }

  public void setPanelAttributes(List<String> panelAttributes)
  {
    _panelAttributes = panelAttributes;
  }

  public List<String> getForEachAttributes()
  {
    return _forEachAttributes;
  }

  public void setForEachAttributes(List<String> forEachAttributes)
  {
    _forEachAttributes = forEachAttributes;
  }

  public List<String> getVariableAttributes()
  {
    return _variableAttributes;
  }

  public void setVariableAttributes(List<String> variableAttributes)
  {
    _variableAttributes = variableAttributes;
  }

  public List<String> getFieldAttributes()
  {
    return _fieldAttributes;
  }

  public void setFieldAttributes(List<String> fieldAttributes)
  {
    _fieldAttributes = fieldAttributes;
  }

  public List<String> getDomainAttributes()
  {
    return _domainAttributes;
  }

  public void setDomainAttributes(List<String> domainAttributes)
  {
    _domainAttributes = domainAttributes;
  }

  public List<String> getDomainValidatorAttributes()
  {
    return _domainValidatorAttributes;
  }

  public void setDomainValidatorAttributes(List<String> domainValidatorAttributes)
  {
    _domainValidatorAttributes = domainValidatorAttributes;
  }

}

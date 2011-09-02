package com.itude.mobile.mobbl2.client.core.configuration.mvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;
import com.itude.mobile.mobbl2.client.core.util.StringUtilities;
import com.itude.mobile.mobbl2.client.core.view.MBConditionalDefinition;

public class MBForEachDefinition extends MBConditionalDefinition
{
  private String                            _value;
  private List<MBDefinition>                _children;
  private Map<String, MBVariableDefinition> _variables;
  private boolean                           _suppressRowComponent;

  public MBForEachDefinition()
  {
    _children = new ArrayList<MBDefinition>();
    _variables = new HashMap<String, MBVariableDefinition>();
  }

  @Override
  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    StringUtilities.appendIndentString(appendToMe, level).//
    append("<MBForEach ").//
    append(getAttributeAsXml("value", _value)).//
    append(getAttributeAsXml("suppressRowComponent", _suppressRowComponent ? "TRUE" : "FALSE")).//
    append(">\n");

    for (MBVariableDefinition var : _variables.values())
      var.asXmlWithLevel(appendToMe, level + 2);

    List<MBDefinition> children = getChildren();

    for (int i = 0; i < children.size(); i++)
    {
      MBDefinition def = (MBDefinition) children.get(i);
      def.asXmlWithLevel(appendToMe, level + 2);
    }

    return StringUtilities.appendIndentString(appendToMe, level).append("</MBForEach>\n");
  }

  @Override
  public void addChildElement(MBDefinition definition)
  {
    addChild(definition);
  }

  public void addChild(MBDefinition child)
  {
    _children.add(child);
  }

  public void addVariable(MBVariableDefinition variable)
  {
    _variables.put(variable.getName(), variable);
  }

  public MBVariableDefinition getVariable(String name)
  {
    return _variables.get(name);
  }

  public String getValue()
  {
    return _value;
  }

  public void setValue(String value)
  {
    _value = value;
  }

  public <T extends MBDefinition> List<MBDefinition> getChildren()
  {
    return _children;
  }

  public void setChildren(List<MBDefinition> children)
  {
    _children = children;
  }

  public Map<String, MBVariableDefinition> getVariables()
  {
    return _variables;
  }

  public void setVariables(Map<String, MBVariableDefinition> variables)
  {
    _variables = variables;
  }

  public boolean getSuppressRowComponent()
  {
    return _suppressRowComponent;
  }

  public void setSuppressRowComponent(boolean suppressRowComponent)
  {
    _suppressRowComponent = suppressRowComponent;
  }

}

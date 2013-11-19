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
package com.itude.mobile.mobbl2.client.core.configuration.mvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl2.client.core.configuration.MBConditionalDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;

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
    StringUtil.appendIndentString(appendToMe, level).//
        append("<MBForEach ").//
        append(getAttributeAsXml("value", _value)).//
        append(getAttributeAsXml("suppressRowComponent", _suppressRowComponent)).//
        append(">\n");

    for (MBVariableDefinition var : _variables.values())
      var.asXmlWithLevel(appendToMe, level + 2);

    List<MBDefinition> children = getChildren();

    for (int i = 0; i < children.size(); i++)
    {
      MBDefinition def = children.get(i);
      def.asXmlWithLevel(appendToMe, level + 2);
    }

    return StringUtil.appendIndentString(appendToMe, level).append("</MBForEach>\n");
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

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
package com.itude.mobile.mobbl2.client.core.view;

import android.view.ViewGroup;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBForEachDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBVariableDefinition;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilderFactory;

public class MBForEachItem extends MBComponentContainer
{
  private int _index;

  public MBForEachItem(MBDefinition definition, MBDocument document, MBComponentContainer parent)
  {
    super(definition, document, parent);
  }

  public int getIndex()
  {
    return _index;
  }

  public void setIndex(int index)
  {
    _index = index;
  }

  @Override
  public String getComponentDataPath()
  {
    MBForEachDefinition def = (MBForEachDefinition) getDefinition();
    String path = def.getValue() + "[" + _index + "]";
    return substituteExpressions(path);
  }

  @Override
  public ViewGroup buildView()
  {
    return MBViewBuilderFactory.getInstance().getForEachItemViewBuilder().buildForEachItemView(this);
  }

  @Override
  public String evaluateExpression(String variableName)
  {
    MBForEachDefinition eachDef = (MBForEachDefinition) getParent().getDefinition();
    MBVariableDefinition varDef = eachDef.getVariable(variableName);
    if (varDef == null) return getParent().evaluateExpression(variableName);

    if ("currentPath()".equals(varDef.getExpression()) || "currentpath()".equals(varDef.getExpression())) return getAbsoluteDataPath();
    if ("rootPath()".equals(varDef.getExpression()) || "rootpath()".equals(varDef.getExpression())) return getPage().getRootPath();

    String value;
    if (varDef.getExpression().startsWith("/") || varDef.getExpression().indexOf(":") > -1)
    {
      value = varDef.getExpression();
    }
    else
    {
      String componentPath = substituteExpressions(getComponentDataPath());

      //in config file, when the expression of a variable does not contain ":" or a prefix "/", componentPath needs to be reset. 
      if (!componentPath.startsWith("/") && componentPath.indexOf(":") == -1)
      {
        componentPath = getPage().getAbsoluteDataPath() + "/" + componentPath;
      }
      value = componentPath + "/" + varDef.getExpression();
    }

    String returnValue = null;
    if (getDocument() != null)
    {
      returnValue = (String) getDocument().getValueForPath(value);
    }
    return returnValue;
  }

  @Override
  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    StringUtil.appendIndentString(appendToMe, level).append("<MBForEachItem index=").append(_index).append(">\n");
    childrenAsXmlWithLevel(appendToMe, level + 2);
    return StringUtil.appendIndentString(appendToMe, level).append("</MBForEachItem>\n");
  }

  @Override
  public String toString()
  {
    StringBuffer rt = new StringBuffer();
    return asXmlWithLevel(rt, 0).toString();
  }

}

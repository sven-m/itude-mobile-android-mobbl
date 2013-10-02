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
package com.itude.mobile.mobbl2.client.core.view;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBForEachDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBVariableDefinition;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;

public class MBVariable extends MBComponentContainer
{
  private String _expression;
  private String _name;

  public MBVariable(MBDefinition definition, MBDocument document, MBComponentContainer parent)
  {
    super(definition, document, parent);

    MBVariableDefinition varDef = (MBVariableDefinition) getDefinition();

    setName(substituteExpressions(varDef.getName()));
    setExpression(substituteExpressions(varDef.getExpression()));

    MBForEachDefinition eachDef = (MBForEachDefinition) getParent().getParent().getDefinition();
    eachDef.addVariable(varDef);

  }

  public void setName(String name)
  {
    this._name = name;
  }

  @Override
  public String getName()
  {
    return _name;
  }

  public void setExpression(String expression)
  {
    this._expression = expression;
  }

  public String getExpression()
  {
    return _expression;
  }

  @Override
  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    StringUtil.appendIndentString(appendToMe, level).append("<MBVariable ").append(attributeAsXml("name", _name)).append(" ")
        .append(attributeAsXml("expression", _expression)).append(">\n");

    childrenAsXmlWithLevel(appendToMe, level + 2);

    return StringUtil.appendIndentString(appendToMe, level).append("</MBVariable>\n");
  }

  @Override
  public String toString()
  {
    StringBuffer rt = new StringBuffer();
    return asXmlWithLevel(rt, 0).toString();
  }

}

package com.itude.mobile.mobbl2.client.core.view;

import android.view.ViewGroup;

import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBForEachDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBVariableDefinition;
import com.itude.mobile.mobbl2.client.core.controller.MBViewManager.MBViewState;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.util.StringUtilities;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilderFactory;

public class MBRow extends MBComponentContainer
{
  private int _index;

  public MBRow(MBDefinition definition, MBDocument document, MBComponentContainer parent)
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
  public ViewGroup buildViewWithMaxBounds(MBViewState viewState)
  {
    return MBViewBuilderFactory.getInstance().getRowViewBuilder().buildRowView(this, viewState);
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
    StringUtilities.appendIndentString(appendToMe, level).append("<MBRow index=").append(_index).append(">\n");
    childrenAsXmlWithLevel(appendToMe, level + 2);
    return StringUtilities.appendIndentString(appendToMe, level).append("</MBRow>\n");
  }

  @Override
  public String toString()
  {
    StringBuffer rt = new StringBuffer();
    return asXmlWithLevel(rt, 0).toString();
  }

}

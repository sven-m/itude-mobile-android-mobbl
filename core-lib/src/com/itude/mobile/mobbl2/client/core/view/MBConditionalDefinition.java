package com.itude.mobile.mobbl2.client.core.view;

import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;
import com.itude.mobile.mobbl2.client.core.controller.exceptions.MBExpressionNotBooleanException;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;

public class MBConditionalDefinition extends MBDefinition
{
  private String _preCondition;

  public String getPreCondition()
  {
    return _preCondition;
  }

  public void setPreCondition(String preCondition)
  {
    _preCondition = preCondition;
  }

  @Override
  public boolean isPreConditionValid(MBDocument document, String currentPath)
  {
    if (_preCondition == null) return true;

    String result = document.evaluateExpression(_preCondition, currentPath);

    result = result.toUpperCase();
    if ("1".equals(result) || "YES".equals(result) || "TRUE".equals(result)) return true;
    if ("0".equals(result) || "NO".equals(result) || "FALSE".equals(result)) return false;

    String msg = "Expression preCondition=" + _preCondition + " is not boolean (" + result + ")";
    throw new MBExpressionNotBooleanException(msg);
  }

}

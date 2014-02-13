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
package com.itude.mobile.mobbl.core.configuration;

import android.util.Log;

import com.itude.mobile.mobbl.core.configuration.mvc.MBConfigurationDefinition;
import com.itude.mobile.mobbl.core.controller.exceptions.MBExpressionNotBooleanException;
import com.itude.mobile.mobbl.core.model.MBDocument;
import com.itude.mobile.mobbl.core.services.MBDataManagerService;
import com.itude.mobile.mobbl.core.util.Constants;
import com.itude.mobile.mobbl.core.util.MBParseUtil;

/**
 * {@link MBDefinition} Class for a condition
 *
 */
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

  public boolean isPreConditionValid()
  {
    if (getPreCondition() == null)
    {
      return true;
    }

    MBDocument doc = MBDataManagerService.getInstance().loadDocument(MBConfigurationDefinition.DOC_SYSTEM_EMPTY);

    String result = doc.evaluateExpression(getPreCondition());
    Boolean bool = MBParseUtil.strictBooleanValue(result);

    if (bool != null)
    {
      return bool;
    }
    String msg = "Expression of definition with name=" + getName() + " precondition=" + getPreCondition() + " is not boolean (result="
                 + result + ")";

    throw new MBExpressionNotBooleanException(msg);
  }

  @Override
  public boolean isPreConditionValid(MBDocument document, String currentPath)
  {
    if (_preCondition == null)
    {
      return true;
    }

    String result = null;
    try
    {
      result = document.evaluateExpression(_preCondition, currentPath);
    }
    catch (NullPointerException npe)
    {
      Log.e(Constants.APPLICATION_NAME, "Error validating precondition: " + _preCondition + " for type " + getClass().getSimpleName()
                                        + " with name " + getName(), npe);
      return false;
    }

    Boolean parsed = MBParseUtil.strictBooleanValue(result);
    if (parsed != null) return parsed;

    String msg = "Expression preCondition=" + _preCondition + " is not boolean (" + result + ")";
    throw new MBExpressionNotBooleanException(msg);
  }

}

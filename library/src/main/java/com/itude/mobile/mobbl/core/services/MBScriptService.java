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
package com.itude.mobile.mobbl.core.services;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

import com.itude.mobile.android.util.log.MBLog;
import com.itude.mobile.mobbl.core.services.exceptions.MBScriptErrorException;
import com.itude.mobile.mobbl.core.util.Constants;

/**
 * Service class for evaluating Javascript expressions
 * 
 */
public final class MBScriptService
{
  private static final String        ERROR_MARKER         = "SCRIPT_ERROR: ";

  private static MBScriptService     _instance            = null;

  // map needs synchronization because it is not read-only in the evaluate code
  private static Map<String, String> COMPUTED_EXPRESSIONS = Collections.synchronizedMap(new HashMap<String, String>());
  static
  {
    COMPUTED_EXPRESSIONS.put("false", Constants.C_FALSE);
    COMPUTED_EXPRESSIONS.put("!false", Constants.C_TRUE);
    COMPUTED_EXPRESSIONS.put("true", Constants.C_TRUE);
    COMPUTED_EXPRESSIONS.put("!true", Constants.C_FALSE);
    COMPUTED_EXPRESSIONS.put("('EUR'=='EUR')", Constants.C_TRUE);
    COMPUTED_EXPRESSIONS.put("!('EUR'=='EUR')", Constants.C_FALSE);
    COMPUTED_EXPRESSIONS.put("('USD'=='EUR')", Constants.C_FALSE);
    COMPUTED_EXPRESSIONS.put("!('USD'=='EUR')", Constants.C_TRUE);
    COMPUTED_EXPRESSIONS.put("'EUR'=='EUR'", Constants.C_TRUE);
    COMPUTED_EXPRESSIONS.put("1!=10", Constants.C_TRUE);
    COMPUTED_EXPRESSIONS.put("10!=10", Constants.C_FALSE);
    COMPUTED_EXPRESSIONS.put("1==2", Constants.C_FALSE);
    COMPUTED_EXPRESSIONS.put("1!=2", Constants.C_TRUE);
    COMPUTED_EXPRESSIONS.put("10==2", Constants.C_FALSE);
    COMPUTED_EXPRESSIONS.put("10!=2", Constants.C_TRUE);
  }

  private MBScriptService()
  {

  }

  public static MBScriptService getInstance()
  {
    if (_instance == null)
    {
      // 2 threads may enter this if
      synchronized (MBScriptService.class)
      {
        // but one of them temporarily blocks on the sync block
        // the other one will create the new instance, so we need
        // to check again if the instance is null.
        if (_instance == null)
        {
          _instance = new MBScriptService();
        }
      }
    }

    return _instance;
  }

  public String evaluate(String expression)
  {
    String result = COMPUTED_EXPRESSIONS.get(expression);
    if (result != null) return result;
    MBLog.d(Constants.APPLICATION_NAME, "expression for ScriptService=" + expression);

    String stub = "function x(){ var TRUE=true; var FALSE=false; try { return " + //
                  expression + //
                  "; } catch(e) { return '" + ERROR_MARKER + "'+e; } } x(); ";
    result = "";

    Context jsContext = ContextFactory.getGlobal().enterContext();
    jsContext.setOptimizationLevel(-1);

    try
    {
      Object jsResult = jsContext.evaluateString(jsContext.initStandardObjects(), stub, "evaluate:", 1, null);
      if (jsResult == null) result = null;
      else if (jsResult instanceof Boolean)
      {
        result = Boolean.toString((Boolean) jsResult);
      }
      else result = jsResult.toString();

      if (result != null && result.startsWith(ERROR_MARKER))
      {
        String message = "Error evaluating expression <" + expression + ">: " + result.substring(ERROR_MARKER.length());
        throw new MBScriptErrorException(message);
      }
      else
      {
        // most expressions are extremely common, like "1==2" or "1!=2"
        // prevent instantiation of an expensive JS interpreter for future cases
        COMPUTED_EXPRESSIONS.put(expression, result);
      }

    }
    finally
    {
      Context.exit();
    }

    return result;
  }

}

package com.itude.mobile.mobbl2.client.core.services;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

import android.util.Log;

import com.itude.mobile.mobbl2.client.core.services.exceptions.MBScriptErrorException;
import com.itude.mobile.mobbl2.client.core.util.Constants;

public final class MBScriptService
{
  private static final String ERROR_MARKER = "SCRIPT_ERROR: ";
  
  private static MBScriptService _instance = null;
  
  // map needs synchronization because it is not read-only in the evaluate code
  private static Map<String, String> COMPUTED_EXPRESSIONS = Collections.synchronizedMap(new HashMap<String, String>());
  static {
    COMPUTED_EXPRESSIONS.put("false", "false");
    COMPUTED_EXPRESSIONS.put("!false", "true");
    COMPUTED_EXPRESSIONS.put("true", "true");
    COMPUTED_EXPRESSIONS.put("!true", "false");
    COMPUTED_EXPRESSIONS.put("('EUR'=='EUR')", "true");
    COMPUTED_EXPRESSIONS.put("!('EUR'=='EUR')", "false");
    COMPUTED_EXPRESSIONS.put("('USD'=='EUR')", "false");
    COMPUTED_EXPRESSIONS.put("!('USD'=='EUR')", "true");
    COMPUTED_EXPRESSIONS.put("'EUR'=='EUR'", "true");
    COMPUTED_EXPRESSIONS.put("1!=10", "true");
    COMPUTED_EXPRESSIONS.put("10!=10", "false");
    COMPUTED_EXPRESSIONS.put("1==2", "false");
    COMPUTED_EXPRESSIONS.put("1!=2", "true");
    COMPUTED_EXPRESSIONS.put("10==2", "false");
    COMPUTED_EXPRESSIONS.put("10!=2", "true");
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
    if (result != null)
      return result;
    Log.d(Constants.APPLICATION_NAME, "expression for ScriptService=" + expression);
    
    String stub = "function x(){ try { return " + expression + "; } catch(e) { return '" + ERROR_MARKER + "'+e; } } x(); ";
    result = "";

    Context jsContext = ContextFactory.getGlobal().enterContext();
    jsContext.setOptimizationLevel(-1);

    try
    {
      Object jsResult = jsContext.evaluateString(jsContext.initStandardObjects(), stub, "evaluate:", 1, null);

      if (jsResult instanceof Boolean)
      {
        result = Boolean.toString((Boolean) jsResult);
      }

      if (result.startsWith(ERROR_MARKER))
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

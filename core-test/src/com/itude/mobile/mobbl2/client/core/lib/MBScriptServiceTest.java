package com.itude.mobile.mobbl2.client.core.lib;

import android.test.ApplicationTestCase;

import com.itude.mobile.mobbl2.client.core.MBApplicationCore;
import com.itude.mobile.mobbl2.client.core.services.MBScriptService;

public class MBScriptServiceTest extends ApplicationTestCase<MBApplicationCore>
{

  private String trueExpression;
  private String falseExpression;

  public MBScriptServiceTest()
  {
    super(MBApplicationCore.class);
  }

  @Override
  protected void setUp() throws Exception
  {
    trueExpression = "'1'=='1'";
    falseExpression = "'0'=='1'";
  }

  public void testExpressionEvaluation()
  {
    MBScriptService service = MBScriptService.getInstance();
    assertNotNull(service);

    assertEquals("true", service.evaluate(trueExpression));
    assertEquals("false", service.evaluate(falseExpression));
  }
}

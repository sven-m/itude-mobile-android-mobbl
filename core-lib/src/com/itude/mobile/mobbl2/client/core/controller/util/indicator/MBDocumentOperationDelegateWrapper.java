package com.itude.mobile.mobbl2.client.core.controller.util.indicator;

import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.services.datamanager.handlers.MBDocumentOperationDelegate;
import com.itude.mobile.mobbl2.client.core.util.AssertUtil;

public class MBDocumentOperationDelegateWrapper implements MBDocumentOperationDelegate
{
  final MBDocumentOperationDelegate _actualDelegate;
  final MBIndicator                 _indicator;

  public MBDocumentOperationDelegateWrapper(MBDocumentOperationDelegate actualDelegate, MBIndicator indicator)
  {
    AssertUtil.notNull("actualDelegate", actualDelegate);
    AssertUtil.notNull("indicator", indicator);
    _actualDelegate = actualDelegate;
    _indicator = indicator;
  }

  @Override
  public void processResult(MBDocument document)
  {

    try
    {
      _actualDelegate.processResult(document);
    }
    finally
    {
      _indicator.release();
    }

  }

  @Override
  public void processException(Exception e)
  {
    try
    {
      _actualDelegate.processException(e);
    }
    finally
    {
      _indicator.release();
    }
  }

}

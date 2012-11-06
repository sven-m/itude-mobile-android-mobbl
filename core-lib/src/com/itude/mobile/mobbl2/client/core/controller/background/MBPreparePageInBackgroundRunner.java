package com.itude.mobile.mobbl2.client.core.controller.background;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBPageDefinition;
import com.itude.mobile.mobbl2.client.core.controller.MBOutcome;
import com.itude.mobile.mobbl2.client.core.controller.util.indicator.MBIndicator;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;

public class MBPreparePageInBackgroundRunner extends MBApplicationControllerBackgroundRunner
{
  final MBIndicator _indicator;
  MBOutcome         _outcome            = null;
  String            _selectPageInDialog = null;
  String            _pageName           = null;
  boolean           _backStackEnabled   = true;

  public MBPreparePageInBackgroundRunner(MBIndicator indicator)
  {
    _indicator = indicator;

  }

  public void setOutcome(MBOutcome mbOutcome)
  {
    _outcome = mbOutcome;
  }

  public void setSelectPageInDialog(String selectPageInDialog)
  {
    _selectPageInDialog = selectPageInDialog;
  }

  public void setPageName(String name)
  {
    _pageName = name;
  }

  public void setBackStackEnabled(boolean value)
  {
    _backStackEnabled = value;
  }

  @Override
  protected Object[] doInBackground(Object[]... params)
  {
    try
    {
      Object[] result = getController().preparePage(_outcome, _pageName, _selectPageInDialog, _backStackEnabled);
      return result;
    }
    finally
    {
      _indicator.release();
    }
  }

  @Override
  protected void onPostExecute(Object[] result)
  {
    MBOutcome outcome = (MBOutcome) result[0];
    MBPageDefinition pageDefinition = (MBPageDefinition) result[1];
    MBDocument document = (MBDocument) result[2];
    String selectPageInDialog = (String) result[3];
    boolean backStackEnabled = (Boolean) result[4];
    getController().showResultingPage(outcome, pageDefinition, document, selectPageInDialog, backStackEnabled);
  }

}

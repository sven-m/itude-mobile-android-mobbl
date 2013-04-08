package com.itude.mobile.mobbl2.client.core.controller.background;

import android.util.Log;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBPageDefinition;
import com.itude.mobile.mobbl2.client.core.controller.MBOutcome;
import com.itude.mobile.mobbl2.client.core.controller.util.indicator.MBIndicator;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.util.Constants;

public class MBPreparePageInBackgroundRunner extends MBApplicationControllerBackgroundRunner
{
  private final MBIndicator _indicator;
  private MBOutcome         _outcome            = null;
  private String            _selectPageInDialog = null;
  private String            _pageName           = null;
  private boolean           _backStackEnabled   = true;

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
    if (result != null)
    {
      MBOutcome outcome = (MBOutcome) result[0];
      MBPageDefinition pageDefinition = (MBPageDefinition) result[1];
      MBDocument document = (MBDocument) result[2];
      String selectPageInDialog = (String) result[3];
      boolean backStackEnabled = (Boolean) result[4];
      getController().showResultingPage(outcome, pageDefinition, document, selectPageInDialog, backStackEnabled);
    }
    else
    {
      Log.d(Constants.APPLICATION_NAME, "Not showing page, since (presumably) an exception occurred while building it");
    }
  }

}

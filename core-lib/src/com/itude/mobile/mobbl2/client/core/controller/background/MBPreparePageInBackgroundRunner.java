package com.itude.mobile.mobbl2.client.core.controller.background;

import com.itude.mobile.android.util.AssertUtil;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBPageDefinition;
import com.itude.mobile.mobbl2.client.core.controller.MBOutcome;
import com.itude.mobile.mobbl2.client.core.controller.background.MBPreparePageInBackgroundRunner.PageBuildResult;
import com.itude.mobile.mobbl2.client.core.controller.util.indicator.MBIndicator;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;

public class MBPreparePageInBackgroundRunner extends MBApplicationControllerBackgroundRunner<PageBuildResult>
{
  public static interface Callback
  {
    public void onPagePrepared(PageBuildResult result);
  }

  public static class PageBuildResult
  {
    public final MBOutcome        outcome;
    public final MBPageDefinition pageDef;
    public final MBDocument       document;
    public final boolean          backstackEnabled;

    public PageBuildResult(MBOutcome outcome, MBPageDefinition pageDef, MBDocument document, boolean backstackEnabled)
    {
      this.outcome = outcome;
      this.pageDef = pageDef;
      this.document = document;
      this.backstackEnabled = backstackEnabled;

    }
  }

  private final MBIndicator _indicator;
  private MBOutcome         _outcome          = null;
  private String            _pageName         = null;
  private boolean           _backStackEnabled = true;
  private final Callback    _callback;

  public MBPreparePageInBackgroundRunner(MBIndicator indicator, Callback callback)
  {
    AssertUtil.notNull("indicator", indicator);
    AssertUtil.notNull("callback", callback);
    _indicator = indicator;
    _callback = callback;

  }

  public void setOutcome(MBOutcome mbOutcome)
  {
    _outcome = mbOutcome;
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
  protected PageBuildResult doInBackground(Object[]... params)
  {
    try
    {
      PageBuildResult result = getController().preparePage(_outcome, _pageName, _backStackEnabled);
      _callback.onPagePrepared(result);

      return result;
    }
    finally
    {
      _indicator.release();
    }
  }

}

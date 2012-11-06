package com.itude.mobile.mobbl2.client.core.controller.util.indicator;

import android.app.Activity;
import android.app.ProgressDialog;

import com.itude.mobile.mobbl2.client.core.services.MBLocalizationService;

public final class MBActivityIndicator extends MBCountingIndicator
{

  private ProgressDialog _dialog = null;

  MBActivityIndicator()
  {

  }

  @Override
  protected void show(final Activity activity)
  {

    _dialog = ProgressDialog.show(activity, MBLocalizationService.getInstance().getTextForKey("title_loading"), MBLocalizationService
        .getInstance().getTextForKey("msg_loading"), true, false);
  }

  @Override
  protected void dismiss(final Activity activity)
  {

    _dialog.dismiss();
    _dialog = null;
  }

}

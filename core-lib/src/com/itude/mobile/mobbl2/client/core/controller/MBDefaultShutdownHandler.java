package com.itude.mobile.mobbl2.client.core.controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.itude.mobile.mobbl2.client.core.services.MBLocalizationService;
import com.itude.mobile.mobbl2.client.core.util.helper.MBSecurityHelper;

public class MBDefaultShutdownHandler extends MBShutdownHandler
{

  @Override
  public void onShutdown()
  {
    String message = MBLocalizationService.getInstance().getTextForKey("close app message");
    String positive = MBLocalizationService.getInstance().getTextForKey("close app positive button");
    String negative = MBLocalizationService.getInstance().getTextForKey("close app negative button");
    new AlertDialog.Builder(getActivity()).setMessage(message).setPositiveButton(positive, new OnClickListener()
    {

      @Override
      public void onClick(DialogInterface dialog, int which)
      {
        MBSecurityHelper.getInstance().logOutIfCheckNotSelected();
        finish();
      }
    }).setNegativeButton(negative, new OnClickListener()
    {

      @Override
      public void onClick(DialogInterface dialog, int which)
      {
        dialog.dismiss();
      }
    }).show();
  }

}

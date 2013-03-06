package com.itude.mobile.mobbl2.client.core.view.builders;

import android.app.AlertDialog;

import com.itude.mobile.mobbl2.client.core.controller.MBViewManager;

public class MBAlertViewBuilder
{

  public AlertDialog createAlertView()
  {

    // TODO: implement tha magic here!
    AlertDialog alertDialog = new AlertDialog.Builder(MBViewManager.getInstance()).create();

    // Setting Dialog Title
    alertDialog.setTitle("Alert Dialog");

    // Setting Dialog Message
    alertDialog.setMessage("TODO: Implement me correctly");

    // Showing Alert Message
    return alertDialog;
  }
}

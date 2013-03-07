package com.itude.mobile.mobbl2.client.core.view.builders;

import android.app.AlertDialog;

import com.itude.mobile.mobbl2.client.core.controller.MBViewManager;
import com.itude.mobile.mobbl2.client.core.view.MBAlert;

public class MBAlertViewBuilder
{

  public AlertDialog buildAlertDialog(MBAlert alert)
  {

    // TODO: implement tha magic here!
    AlertDialog alertDialog = new AlertDialog.Builder(MBViewManager.getInstance()).create();

    // Setting Dialog Title
    alertDialog.setTitle(alert.getTitle());

    // TODO: Setting Dialog Message (is a child)
    alertDialog.setMessage("TODO: Implement me correctly");

    // TODO: Buttons (is a child)

    // Showing Alert Message
    return alertDialog;
  }
}

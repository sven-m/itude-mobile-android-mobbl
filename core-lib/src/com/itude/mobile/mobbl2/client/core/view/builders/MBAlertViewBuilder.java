package com.itude.mobile.mobbl2.client.core.view.builders;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.controller.MBOutcome;
import com.itude.mobile.mobbl2.client.core.controller.MBViewManager;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.view.MBAlert;
import com.itude.mobile.mobbl2.client.core.view.MBComponent;
import com.itude.mobile.mobbl2.client.core.view.MBField;

public class MBAlertViewBuilder
{

  private static final String C_FIELD_BUTTON_STYLE_NEGATIVE = "NEGATIVE"; // iOS: Cancel Button
  private static final String C_FIELD_BUTTON_STYLE_POSITIVE = "POSITIVE"; // iOS: Other Button
  private static final String C_FIELD_BUTTON_STYLE_OTHER    = "OTHER";   // iOS: Other Button

  public AlertDialog buildAlertDialog(MBAlert alert)
  {

    AlertDialog.Builder builder = new AlertDialog.Builder(MBViewManager.getInstance());
    builder.setTitle(alert.getTitle());

    // Build all the children
    for (MBComponent child : alert.getChildren())
    {
      if (child instanceof MBField)
      {
        MBField field = (MBField) child;
        String text = field.getLabel();
        if (Constants.C_FIELD_TEXT.equals(field.getType()))
        {
          builder.setMessage(text);
        }

        else if (Constants.C_FIELD_BUTTON.equals(field.getType()))
        {
          if (C_FIELD_BUTTON_STYLE_NEGATIVE.equals(field.getStyle()))
          {
            builder.setNegativeButton(text, createOnClickListener(field.getOutcomeName()));
          }
          else if (C_FIELD_BUTTON_STYLE_POSITIVE.equals(field.getStyle()))
          {
            builder.setPositiveButton(text, createOnClickListener(field.getOutcomeName()));
          }
          else if (C_FIELD_BUTTON_STYLE_OTHER.equals(field.getStyle()))
          {
            builder.setNeutralButton(text, createOnClickListener(field.getOutcomeName()));
          }
        }

      }
    }

    return builder.create();
  }

  private DialogInterface.OnClickListener createOnClickListener(final String outcomeName)
  {

    if (outcomeName != null)
    {
      return new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface dialog, int id)
        {
          MBOutcome oc = new MBOutcome();
          oc.setOutcomeName(outcomeName);
          MBApplicationController.getInstance().handleOutcome(oc);
        }
      };
    }

    return null;
  }

}

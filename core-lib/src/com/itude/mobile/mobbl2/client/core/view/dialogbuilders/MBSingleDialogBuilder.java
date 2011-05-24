package com.itude.mobile.mobbl2.client.core.view.dialogbuilders;

import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

/**
 * @author Coen Houtman
 * 
 * A dialog for displaying one fragment. Typically, this type of dialog is used when a Dialog
 * is not part of a DialogGroup. Normally, this will be used for the smartphone configurations,
 * but it is flexible.
 * 
 * This implementation is not orientation dependent.
 * 
 * Result after successful build:
 * 
 * RelativeLayout
 * | FragmentLayout |
 */
public class MBSingleDialogBuilder extends MBDialogBuilder
{
  @Override
  public ViewGroup build()
  {
    RelativeLayout mainContainer = buildContainer();

    if (getSortedDialogIds() != null && !getSortedDialogIds().isEmpty())
    {
      FrameLayout fragmentContainer = new FrameLayout(mainContainer.getContext());
      fragmentContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
      fragmentContainer.setId(getSortedDialogIds().get(0));

      mainContainer.addView(fragmentContainer);
    }

    return mainContainer;
  }
}

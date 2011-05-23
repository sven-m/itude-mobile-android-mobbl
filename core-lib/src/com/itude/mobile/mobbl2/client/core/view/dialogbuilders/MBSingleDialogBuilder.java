package com.itude.mobile.mobbl2.client.core.view.dialogbuilders;

import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

public class MBSingleDialogBuilder extends MBDialogBuilder
{

  @Override
  public ViewGroup buildDialog()
  {
    RelativeLayout mainContainer = buildContainer();

    FrameLayout fragmentContainer = new FrameLayout(mainContainer.getContext());
    fragmentContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    fragmentContainer.setId(getSortedDialogIds().get(0));

    mainContainer.addView(fragmentContainer);

    return mainContainer;
  }

}

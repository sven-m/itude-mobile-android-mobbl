package com.itude.mobile.mobbl2.client.core.view.dialogbuilders;

import java.util.List;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.MBScreenUtilities;

public class MBSplitDialogBuilder extends MBDialogBuilder
{
  @Override
  public ViewGroup buildDialog()
  {
    RelativeLayout mainContainer = buildContainer();

    List<Integer> sortedDialogIds = getSortedDialogIds();
    if (sortedDialogIds == null)
    {
      Log.w(Constants.APPLICATION_NAME, "No child dialogs to build");
      return mainContainer;
    }

    for (int i = 0; i < sortedDialogIds.size(); i++)
    {
      FrameLayout fragmentContainer = new FrameLayout(MBApplicationController.getInstance().getBaseContext());
      fragmentContainer.setId(sortedDialogIds.get(i));

      RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(MBScreenUtilities.getWidthPixelsForPercentage(20),
          RelativeLayout.LayoutParams.MATCH_PARENT);
      // position fragment containers next to each other
      if (i == 0)
      {
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
      }
      else if (i > 0)
      {
        layoutParams.addRule(RelativeLayout.RIGHT_OF, sortedDialogIds.get(i - 1));
      }

      if (i == sortedDialogIds.size() - 1) layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

      fragmentContainer.setLayoutParams(layoutParams);
      mainContainer.addView(fragmentContainer);

    }

    return mainContainer;
  }

}

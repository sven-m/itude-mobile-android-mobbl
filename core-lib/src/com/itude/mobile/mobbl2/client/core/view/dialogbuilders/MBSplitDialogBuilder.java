package com.itude.mobile.mobbl2.client.core.view.dialogbuilders;

import java.util.List;

import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.util.MBScreenUtilities;

/**
 * @author Coen Houtman
 * 
 * This builder places multiple dialogs next to each other. Each dialog has a width of 20 percent of the screen width.
 * The last dialog is stretched to fill the void on the right, if there is any.
 * 
 * NOTE: When there is only one child defined, the #MBSingleDialogBuilder is invoked instead.
 * 
 * Result after successful build:
 * 
 * RelativeLayout
 * | FrameLayout | FrameLayout | FrameLayout | ... |
 *
 */
public class MBSplitDialogBuilder extends MBDialogBuilder
{
  @Override
  public ViewGroup build()
  {
    List<Integer> sortedDialogIds = getSortedDialogIds();

    // if there is a DialogGroup with one child, just build a single dialog
    if ((sortedDialogIds == null) || (sortedDialogIds.size() <= 1)) return MBDialogBuilderFactory.getInstance().getSingleDialogBuilder()
        .build();
    else
    {
      RelativeLayout mainContainer = buildContainer();

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

}

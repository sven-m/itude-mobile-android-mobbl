package com.itude.mobile.mobbl.core.view.builders.dialog;

import java.util.List;

import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.itude.mobile.android.util.ScreenUtil;
import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl.core.controller.MBApplicationController;
import com.itude.mobile.mobbl.core.controller.MBDialogController;
import com.itude.mobile.mobbl.core.controller.util.MBBasicViewController;
import com.itude.mobile.mobbl.core.view.builders.MBDialogContentBuilder;
import com.itude.mobile.mobbl.core.view.builders.MBStyleHandler;

public class SplitDialogBuilder extends MBDialogContentBuilder.Builder
{

  private static final int SPLIT_MARGIN                   = 0;
  private static final int LEFT_FRAGMENT_WIDTH_PERCENTAGE = 33;

  @Override
  public ViewGroup buildDialog(List<Integer> sortedDialogIds)
  {
    ViewGroup container = buildContainer();
    MBStyleHandler styleHandler = getStyleHandler();

    for (int i = 0; i < sortedDialogIds.size(); i++)
    {
      FrameLayout fragmentContainer = new FrameLayout(MBApplicationController.getInstance().getBaseContext());
      fragmentContainer.setId(sortedDialogIds.get(i));

      RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
          ScreenUtil.getWidthPixelsForPercentage(MBApplicationController.getInstance().getBaseContext(), LEFT_FRAGMENT_WIDTH_PERCENTAGE),
          RelativeLayout.LayoutParams.MATCH_PARENT);

      // position fragment containers next to each other
      if (i == 0)
      {
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
      }
      else if (i > 0)
      {
        layoutParams.addRule(RelativeLayout.RIGHT_OF, sortedDialogIds.get(i - 1));
        layoutParams.setMargins(SPLIT_MARGIN, 0, 0, 0);
      }

      if (i == sortedDialogIds.size() - 1) layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

      fragmentContainer.setLayoutParams(layoutParams);
      styleHandler.styleBackground(fragmentContainer);
      styleHandler.styleFragmentPadding(fragmentContainer, i);
      container.addView(fragmentContainer);
    }

    return container;
  }

  @Override
  public void configurationChanged(Configuration newConfig, MBDialogController dialog)
  {
    List<MBBasicViewController> fragments = dialog.getAllFragments();
    for (int i = 0; i < fragments.size() - 1; ++i)
    {
      Fragment fragment = fragments.get(i);
      // if the fragment didn't load correctly (e.g. a network
      // error occurred), we don't want to crash the app
      if (fragment != null)
      {
        FrameLayout fragmentContainer = (FrameLayout) fragment.getView().getParent();
        fragmentContainer.getLayoutParams().width = ScreenUtil.getWidthPixelsForPercentage(dialog.getBaseContext(), LEFT_FRAGMENT_WIDTH_PERCENTAGE);
      }
    }
  }

}

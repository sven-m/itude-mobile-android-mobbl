package com.itude.mobile.mobbl.core.view.builders.dialog;

import java.util.List;

import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout.LayoutParams;

import com.itude.mobile.mobbl.core.controller.MBApplicationController;
import com.itude.mobile.mobbl.core.view.builders.MBDialogContentBuilder;
import com.itude.mobile.mobbl.core.view.builders.MBStyleHandler;

public class SingleDialogBuilder extends MBDialogContentBuilder.Builder
{

  @Override
  public ViewGroup buildDialog(List<Integer> sortedDialogIds)
  {
    if (sortedDialogIds != null && !sortedDialogIds.isEmpty())
    {
      FrameLayout fragmentContainer = new FrameLayout(MBApplicationController.getInstance().getBaseContext());
      fragmentContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
      fragmentContainer.setId(sortedDialogIds.get(0));

      MBStyleHandler styleHandler = getStyleHandler();
      styleHandler.styleFragmentPadding(fragmentContainer, 0);
      styleHandler.styleBackground(fragmentContainer);

      return fragmentContainer;
    }

    return buildContainer();
  }

}

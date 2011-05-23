package com.itude.mobile.mobbl2.client.core.view.dialogbuilders;

import java.util.List;

import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;

public abstract class MBDialogBuilder
{
  public abstract ViewGroup buildDialog();

  private List<Integer> _sortedDialogIds;

  protected RelativeLayout buildContainer()
  {
    RelativeLayout mainContainer = new RelativeLayout(MBApplicationController.getInstance().getBaseContext());
    mainContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

    return mainContainer;
  }

  protected List<Integer> getSortedDialogIds()
  {
    return _sortedDialogIds;
  }

  public void setSortedDialogIds(List<Integer> sortedDialogIds)
  {
    _sortedDialogIds = sortedDialogIds;
  }
}

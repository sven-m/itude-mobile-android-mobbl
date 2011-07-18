package com.itude.mobile.mobbl2.client.core.view.builders;

import java.util.List;

import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.util.MBScreenUtilities;

/**
 * @author Coen Houtman
 *
 * Base class for all DialogBuilders
 */
public class MBDialogViewBuilder
{
  public enum MBDialogType {
    Single, Split
  }

  private static final int SPLIT_MARGIN = 2;

  /**
   * A list of integers which are ids for the views to be built. In case of a single Dialog,
   * the list will contain one item. In the case of a DialogGroup, the list contains ids for each child
   * Dialog in the order in which they are defined in the config.
   */
  private List<Integer>    _sortedDialogIds;

  /**
   * Method to build the view group(s) necessary for the type of dialog.
   * There is a number possibilities for the implementation, which depend on the type of dialog:
   *   1. only implement the build method. That view is then used for both portrait as landscape
   *   2. implement all three build methods, where in the build method is determined when to build portrait
   *      and when to build landscape
   * @param dialogType 
   * @return
   */
  public ViewGroup buildDialog(MBDialogType dialogType, List<Integer> sortedDialogIds)
  {
    _sortedDialogIds = sortedDialogIds;

    RelativeLayout container = buildContainer();

    if (dialogType == MBDialogType.Single)
    {
      buildSingleDialog(container);
    }
    else if (dialogType == MBDialogType.Split)
    {
      buildSplitDialog(container);
    }

    return container;
  }

  protected ViewGroup buildSingleDialog(RelativeLayout container)
  {
    if (_sortedDialogIds != null && !_sortedDialogIds.isEmpty())
    {
      FrameLayout fragmentContainer = new FrameLayout(container.getContext());
      fragmentContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
      fragmentContainer.setId(_sortedDialogIds.get(0));

      container.addView(fragmentContainer);
    }

    return container;
  }

  protected ViewGroup buildSplitDialog(RelativeLayout container)
  {
    for (int i = 0; i < _sortedDialogIds.size(); i++)
    {
      FrameLayout fragmentContainer = new FrameLayout(MBApplicationController.getInstance().getBaseContext());
      fragmentContainer.setId(_sortedDialogIds.get(i));

      RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(MBScreenUtilities.getWidthPixelsForPercentage(20),
          RelativeLayout.LayoutParams.MATCH_PARENT);

      // position fragment containers next to each other
      if (i == 0)
      {
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
      }
      else if (i > 0)
      {
        layoutParams.addRule(RelativeLayout.RIGHT_OF, _sortedDialogIds.get(i - 1));
        layoutParams.setMargins(SPLIT_MARGIN, 0, 0, 0);
      }

      if (i == _sortedDialogIds.size() - 1) layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

      fragmentContainer.setLayoutParams(layoutParams);
      getStyleHandler().styleBackground(fragmentContainer);
      container.addView(fragmentContainer);
    }

    return container;
  }

  /**
   * Build the container in which to place the fragments. A RelativeLayout should provide
   * enough flexibility to build any possible view. The view ids can be retrieved from the {@link #__sortedDialogIds}.
   * @return
   */
  protected RelativeLayout buildContainer()
  {
    RelativeLayout mainContainer = new RelativeLayout(MBApplicationController.getInstance().getBaseContext());
    mainContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

    return mainContainer;
  }

  public MBStyleHandler getStyleHandler()
  {
    return MBViewBuilderFactory.getInstance().getStyleHandler();
  }
}

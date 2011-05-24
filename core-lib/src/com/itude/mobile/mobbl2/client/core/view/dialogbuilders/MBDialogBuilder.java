package com.itude.mobile.mobbl2.client.core.view.dialogbuilders;

import java.util.List;

import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;

/**
 * @author Coen Houtman
 *
 * Base class for all DialogBuilders
 */
public abstract class MBDialogBuilder
{
  /**
   * A list of integers which are ids for the views to be built. In case of a single Dialog,
   * the list will contain one item. In the case of a DialogGroup, the list contains ids for each child
   * Dialog in the order in which they are defined in the config.
   */
  private List<Integer> _sortedDialogIds;

  /**
   * Method to build the view group(s) necessary for the type of dialog.
   * There is a number possibilities for the implementation, which depend on the type of dialog:
   *   1. only implement the build method. That view is then used for both portrait as landscape
   *   2. implement all three build methods, where in the build method is determined when to build portrait
   *      and when to build landscape
   * @return
   */
  public abstract ViewGroup build();

  protected ViewGroup buildPortrait()
  {
    return null;
  }

  protected ViewGroup buildLandscape()
  {
    return null;
  }

  /**
   * Build the container in which to place the fragments. A RelativeLayout should provide
   * enough flexibility to build any possible view. The view ids can be retrieved from the {@link #_sortedDialogIds}.
   * @return
   */
  protected RelativeLayout buildContainer()
  {
    RelativeLayout mainContainer = new RelativeLayout(MBApplicationController.getInstance().getBaseContext());
    mainContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

    return mainContainer;
  }

  ////////////////////////

  protected List<Integer> getSortedDialogIds()
  {
    return _sortedDialogIds;
  }

  public void setSortedDialogIds(List<Integer> sortedDialogIds)
  {
    _sortedDialogIds = sortedDialogIds;
  }
}

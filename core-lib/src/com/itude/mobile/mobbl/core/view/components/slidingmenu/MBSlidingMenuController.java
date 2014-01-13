/*
 * (C) Copyright Itude Mobile B.V., The Netherlands
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.itude.mobile.mobbl.core.view.components.slidingmenu;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;

import com.itude.mobile.android.util.ViewUtilities;
import com.itude.mobile.mobbl.core.controller.MBDialogController;
import com.itude.mobile.mobbl.core.controller.MBViewManager;
import com.itude.mobile.mobbl.core.view.builders.MBStyleHandler;
import com.itude.mobile.mobbl.core.view.builders.MBViewBuilderFactory;
import com.itude.mobile.widget.slidingmenu.SlidingMenu;

public class MBSlidingMenuController
{

  private SlidingMenu            _slidingMenu    = null;
  private TopViewPadding         _topViewPadding = null;
  private final FragmentActivity _activity;

  protected SlidingMenu getSlidingMenu()
  {
    return _slidingMenu;
  }

  public MBSlidingMenuController(FragmentActivity activity)
  {
    _activity = activity;
    build();
  }

  public void build()
  {
    _slidingMenu = new SlidingMenu(_activity.getBaseContext());

    // https://mobiledev.itude.com/jira/browse/MOBBL-633
    TopViewPadding topViewPadding = getTopViewPadding();
    if (topViewPadding != null)
    {
      _slidingMenu.setPadding(topViewPadding.getLeft(), topViewPadding.getTop(), topViewPadding.getRight(), topViewPadding.getBottom());
    }

    MBStyleHandler styleHandler = MBViewBuilderFactory.getInstance().getStyleHandler();
    styleHandler.styleSlidingMenu(_slidingMenu);

    MBDialogController menu = MBViewManager.getInstance().getMenuDialog();
    if (menu != null)
    {
      ViewUtilities.detachView(menu.getMainContainer());
      menu.activateWithoutSwitching();
      menu.removeOnBackStackChangedListenerOfCurrentDialog();
      _slidingMenu.setMenu(menu.getMainContainer());
    }

    _slidingMenu.attachToActivity(_activity, SlidingMenu.SLIDING_WINDOW);

  }

  public void remove()
  {
    final SlidingMenu slidingMenu = getSlidingMenu();

    if (slidingMenu == null)
    {
      return;
    }

    View content = slidingMenu.getContent();

    // https://mobiledev.itude.com/jira/browse/MOBBL-633
    if (_topViewPadding == null)
    {
      _topViewPadding = new TopViewPadding(slidingMenu);
    }

    ViewUtilities.detachView(content);

    ViewGroup decorView = (ViewGroup) MBViewManager.getInstance().getWindow().getDecorView();

    decorView.removeView(slidingMenu);

    decorView.addView(content);
  }

  public void rebuild()
  {
    remove();
    build();
  }

  public void toggle()
  {
    getSlidingMenu().toggle(true);
  }

  public void hide()
  {
    getSlidingMenu().showContent(true);
  }

  protected TopViewPadding getTopViewPadding()
  {
    return _topViewPadding;
  }

  private static final class TopViewPadding
  {
    private final int _left;
    private final int _top;
    private final int _right;
    private final int _bottom;

    private TopViewPadding(View view)
    {
      this(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
    }

    private TopViewPadding(int left, int top, int right, int bottom)
    {
      _left = left;
      _top = top;
      _right = right;
      _bottom = bottom;
    }

    public int getLeft()
    {
      return _left;
    }

    public int getTop()
    {
      return _top;
    }

    public int getRight()
    {
      return _right;
    }

    public int getBottom()
    {
      return _bottom;
    }
  }

}

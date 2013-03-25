package com.itude.mobile.mobbl2.client.core.view.components.slidingmenu;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;

import com.itude.mobile.android.util.ViewUtilities;
import com.itude.mobile.mobbl2.client.core.controller.MBDialogController;
import com.itude.mobile.mobbl2.client.core.controller.MBViewManager;
import com.itude.mobile.mobbl2.client.core.view.builders.MBStyleHandler;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilderFactory;
import com.itude.mobile.widget.slidingmenu.SlidingMenu;

public class MBSlidingMenuController
{

  private SlidingMenu    _slidingMenu    = null;
  private TopViewPadding _topViewPadding = null;

  protected SlidingMenu getSlidingMenu()
  {
    return _slidingMenu;
  }

  public MBSlidingMenuController(FragmentActivity activity)
  {
    _slidingMenu = new SlidingMenu(activity.getBaseContext());

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
      menu.activateWithoutSwitching();
      _slidingMenu.setMenu(menu.getMainContainer());
    }

    /*
        MBPageDefinition pageDefinition = MBMetadataService.getInstance().getDefinitionForPageName("PAGE-sliding-menu");
        MBDataManagerService.getInstance().loadDocument(pageDefinition.getDocumentName());

        MBPage page = MBApplicationFactory.getInstance().getPageConstructor()
            .createPage(pageDefinition, null, null, MBViewState.MBViewStatePlain);
        page.setController(MBApplicationController.getInstance());
        page.setDialogName(null);

        MBBasicViewController fragment = page.getViewController();
        fragment.setArguments(new Bundle());
        fragment.setDialogController(MBViewManager.getInstance().getActiveDialog());

        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(fragment, "SLIDING");

        ft.commitAllowingStateLoss();

        _slidingMenu.setMenu(fragment.getView()); */
    _slidingMenu.attachToActivity(activity, SlidingMenu.SLIDING_WINDOW);
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

  public void toggle()
  {

    getSlidingMenu().toggle(true);
  }

}

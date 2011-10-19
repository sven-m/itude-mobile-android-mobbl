package com.itude.mobile.mobbl2.client.core.view.builders;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.controller.MBViewManager;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.view.MBPage;
import com.itude.mobile.mobbl2.client.core.view.components.MBHeader;

public class MBPageViewBuilder extends MBViewBuilder
{

  public ViewGroup buildPageView(MBPage page, MBViewManager.MBViewState viewState)
  {
    Context context = MBApplicationController.getInstance().getBaseContext();
    MBStyleHandler styleHandler = getStyleHandler();

    LinearLayout main = new LinearLayout(context);
    main.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    main.setOrientation(LinearLayout.VERTICAL);

    LinearLayout view = new LinearLayout(context);
    view.setOrientation(LinearLayout.VERTICAL);
    view.setScrollContainer(true);
    view.setFadingEdgeLength(0);
    view.setVerticalFadingEdgeEnabled(false);

    if (page.getTitle() != null)
    {
      LinearLayout headerContainer = new LinearLayout(view.getContext());
      headerContainer.setOrientation(LinearLayout.VERTICAL);
      headerContainer.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

      MBHeader header = new MBHeader(headerContainer.getContext());
      header.setTag(Constants.C_PAGE_CONTENT_HEADER_VIEW);
      header.getTitleView().setText(page.getTitle());
      styleHandler.stylePageHeader(header);
      styleHandler.stylePageHeaderTitle(header.getTitleView());
      headerContainer.addView(header);

      main.addView(headerContainer);
    }

    buildChildren(page.getChildren(), view, viewState);

    styleHandler.applyStyle(page, view, viewState);

    // Add linearlayout to scrollview
    ScrollView scrollView = new ScrollView(context);
    scrollView.setTag(Constants.C_PAGE_CONTENT_VIEW);
    scrollView.setFadingEdgeLength(0);
    scrollView.setVerticalFadingEdgeEnabled(false);
    styleHandler.styleMainScrollbarView(page, scrollView);
    scrollView.addView(view);

    main.addView(scrollView);

    main.setFadingEdgeLength(0);
    main.setVerticalFadingEdgeEnabled(false);
    // End of page content

    return main;
  }

  public ViewGroup buildPageViewWithoutContent(MBPage page, MBViewManager.MBViewState viewState)
  {
    Context context = MBApplicationController.getInstance().getBaseContext();
    MBStyleHandler styleHandler = getStyleHandler();

    LinearLayout main = new LinearLayout(context);
    main.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    main.setOrientation(LinearLayout.VERTICAL);

    if (page.getTitle() != null)
    {
      LinearLayout headerContainer = new LinearLayout(context);
      headerContainer.setOrientation(LinearLayout.VERTICAL);
      headerContainer.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

      MBHeader header = new MBHeader(headerContainer.getContext());
      header.setTag(Constants.C_PAGE_CONTENT_HEADER_VIEW);
      header.getTitleView().setText(page.getTitle());
      styleHandler.stylePageHeader(header);
      styleHandler.stylePageHeaderTitle(header.getTitleView());
      headerContainer.addView(header);

      main.addView(headerContainer);
    }

    // Add linearlayout to scrollview
    ScrollView scrollView = new ScrollView(context);
    scrollView.setTag(Constants.C_PAGE_CONTENT_VIEW);
    scrollView.setFadingEdgeLength(0);
    scrollView.setVerticalFadingEdgeEnabled(false);
    styleHandler.styleMainScrollbarView(page, scrollView);

    main.addView(scrollView);

    main.setFadingEdgeLength(0);
    main.setVerticalFadingEdgeEnabled(false);
    // End of page content

    return main;
  }

}

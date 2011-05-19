package com.itude.mobile.mobbl2.client.core.view.builders;

import android.content.Context;
import android.graphics.Color;
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

    LinearLayout main = new LinearLayout(context);
    main.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    main.setOrientation(LinearLayout.VERTICAL);

    LinearLayout view = new LinearLayout(context);
    view.setOrientation(LinearLayout.VERTICAL);
    view.setScrollContainer(true);
    view.setFadingEdgeLength(0);
    view.setVerticalFadingEdgeEnabled(false);
    //    view.setBackgroundColor(0xFFEEEEEE);

    if (page.getTitle() != null)
    {
      LinearLayout headerContainer = new LinearLayout(view.getContext());
      headerContainer.setOrientation(LinearLayout.VERTICAL);
      headerContainer.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

      /*LinearLayout header = new LinearLayout(view.getContext());
      header.setOrientation(LinearLayout.VERTICAL);

      getStyleHandler().stylePageHeader(header);
      header.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
      headerContainer.addView(header);

      TextView title = new TextView(header.getContext());
      title.setText(page.getTitle());
      getStyleHandler().stylePageHeaderTitle(title);
      */
      MBHeader header = new MBHeader(headerContainer.getContext());
      header.setTag(Constants.C_PAGE_CONTENT_HEADER_VIEW);
      header.getTitleView().setText(page.getTitle());
      getStyleHandler().stylePageHeader(header);
      getStyleHandler().stylePageHeaderTitle(header.getTitleView());
      headerContainer.addView(header);

      main.addView(headerContainer);

      LinearLayout divider = new LinearLayout(view.getContext());
      divider.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 1));
      divider.setBackgroundColor(Color.DKGRAY);

      main.addView(divider);

    }

    buildChildren(page.getChildren(), view, viewState);

    getStyleHandler().applyStyle(page, view, viewState);

    // Add linearlayout to scrollview
    ScrollView scrollView = new ScrollView(context);
    scrollView.setTag(Constants.C_PAGE_CONTENT_VIEW);
    scrollView.setFadingEdgeLength(0);
    scrollView.setVerticalFadingEdgeEnabled(false);
    getStyleHandler().styleMainScrollbarView(scrollView);
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

    LinearLayout main = new LinearLayout(context);
    main.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    main.setOrientation(LinearLayout.VERTICAL);

    if (page.getTitle() != null)
    {
      LinearLayout headerContainer = new LinearLayout(context);
      //      headerContainer.setTag(Constants.C_PAGE_CONTENT_HEADER_VIEW);
      headerContainer.setOrientation(LinearLayout.VERTICAL);
      headerContainer.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

      /*LinearLayout header = new LinearLayout(context);
      header.setOrientation(LinearLayout.VERTICAL);

      getStyleHandler().stylePageHeader(header);
      header.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
      headerContainer.addView(header);

      TextView title = new TextView(header.getContext());
      title.setText(page.getTitle());
      getStyleHandler().stylePageHeaderTitle(title);

      header.addView(title);*/

      MBHeader header = new MBHeader(headerContainer.getContext());
      header.setTag(Constants.C_PAGE_CONTENT_HEADER_VIEW);
      header.getTitleView().setText(page.getTitle());
      getStyleHandler().stylePageHeader(header);
      getStyleHandler().stylePageHeaderTitle(header.getTitleView());
      headerContainer.addView(header);

      main.addView(headerContainer);

      LinearLayout divider = new LinearLayout(context);
      divider.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 1));
      divider.setBackgroundColor(Color.DKGRAY);

      main.addView(divider);

    }

    // Add linearlayout to scrollview
    ScrollView scrollView = new ScrollView(context);
    scrollView.setTag(Constants.C_PAGE_CONTENT_VIEW);
    scrollView.setFadingEdgeLength(0);
    scrollView.setVerticalFadingEdgeEnabled(false);
    getStyleHandler().styleMainScrollbarView(scrollView);

    main.addView(scrollView);

    main.setFadingEdgeLength(0);
    main.setVerticalFadingEdgeEnabled(false);
    // End of page content

    return main;
  }

}

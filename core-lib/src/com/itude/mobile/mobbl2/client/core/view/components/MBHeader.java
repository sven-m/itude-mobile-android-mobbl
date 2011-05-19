package com.itude.mobile.mobbl2.client.core.view.components;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MBHeader extends RelativeLayout
{
  private TextView _titleView = null;

  public MBHeader(Context context)
  {
    super(context);

    setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
    RelativeLayout.LayoutParams titleViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
        RelativeLayout.LayoutParams.WRAP_CONTENT);
    titleViewParams.addRule(RelativeLayout.CENTER_VERTICAL);
    setTitleView(new TextView(context));
    _titleView.setLayoutParams(titleViewParams);
    addView(getTitleView());
  }

  public void addViewToRight(View view)
  {
    RelativeLayout.LayoutParams rightLayout = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    rightLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
    view.setLayoutParams(rightLayout);
    addView(view);
  }

  /**
   * This method can be used to determine your own positioning of childviews. 
   * If you don't specify any positioning parameters the view will be positioned at the top left corner of the MBHeader 
   * Of course MBHeader.addView could be used too, this is merely a convenience method
   * @param view to be added to the MBHeader
   */
  public void addViewToHeader(View view)
  {
    addView(view);
  }

  public boolean removeViewFromHeader(String tag)
  {
    if (findViewWithTag(tag) != null)
    {
      removeView(findViewWithTag(tag));
      return true;
    }

    return false;
  }

  public boolean removeViewFromRight(View view)
  {
    if (this == view.getParent())
    {
      removeView(view);
      return true;
    }

    return false;
  }

  public void setTitleView(TextView title)
  {
    _titleView = title;
  }

  public TextView getTitleView()
  {
    return _titleView;
  }

  public void setTitleText(String titleText)
  {
    if (_titleView != null)
    {
      _titleView.setText(titleText);
    }
  }

}

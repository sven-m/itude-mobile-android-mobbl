/*
 * (C) Copyright ItudeMobile.
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

    setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    RelativeLayout.LayoutParams titleViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
        RelativeLayout.LayoutParams.WRAP_CONTENT);
    titleViewParams.addRule(RelativeLayout.CENTER_VERTICAL);
    _titleView = new TextView(context);
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

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
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.itude.mobile.mobbl2.client.core.view.builders.MBStyleHandler;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilderFactory;

public class MBDrawablePageIndicatorBar extends MBAbstractPageIndicator
{

  public MBDrawablePageIndicatorBar(Context context)
  {
    super(context);
  }

  @Override
  protected ViewGroup setupIndicatorContainer()
  {
    LinearLayout container = new LinearLayout(this.getContext());
    container.setOrientation(LinearLayout.HORIZONTAL);
    container.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    removeAllViews();

    return container;
  }

  @Override
  protected View setupActiveIndicatorView()
  {
    MBStyleHandler styleHandler = MBViewBuilderFactory.getInstance().getStyleHandler();

    ImageView activeIndicatorView = new ImageView(getContext());
    activeIndicatorView.setBackgroundDrawable(styleHandler.getDrawablePageIndicatorDrawable());

    styleHandler.styleDrawablePageIndicatorBarActiveIndicatorView(activeIndicatorView);

    activeIndicatorView.setSelected(true);

    return activeIndicatorView;
  }

  @Override
  protected View setupInactiveIndicatorView()
  {
    MBStyleHandler styleHandler = MBViewBuilderFactory.getInstance().getStyleHandler();

    ImageView inactiveIndicatorView = new ImageView(getContext());
    inactiveIndicatorView.setBackgroundDrawable(styleHandler.getDrawablePageIndicatorDrawable());

    styleHandler.styleDrawablePageIndicatorBarActiveIndicatorView(inactiveIndicatorView);

    inactiveIndicatorView.setSelected(false);

    return inactiveIndicatorView;
  }

  @Override
  public void setActiveIndicator(int activatingIndicator)
  {

    ImageView currentIndicator = (ImageView) getIndicatorList().get(getActiveIndicatorIndex());
    currentIndicator.setSelected(false);

    setActiveIndicatorIndex(activatingIndicator);

    currentIndicator = (ImageView) getIndicatorList().get(activatingIndicator);
    currentIndicator.setSelected(true);

    getIndicatorContainer().invalidate();

  }
}

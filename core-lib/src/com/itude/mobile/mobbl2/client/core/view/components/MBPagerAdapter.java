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
package com.itude.mobile.mobbl2.client.core.view.components;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class MBPagerAdapter<T extends View> extends PagerAdapter
{
  private final List<T> views;

  public MBPagerAdapter()
  {
    views = new ArrayList<T>();
  }

  public void addView(T v)
  {
    views.add(v);
  }

  public T getView(int index)
  {
    return views.get(index);
  }

  @Override
  public void destroyItem(View view, int arg1, Object object)
  {
    ((ViewPager) view).removeView((T) object);
  }

  @Override
  public void finishUpdate(View view)
  {
  }

  @Override
  public int getCount()
  {
    return views.size();
  }

  @Override
  public Object instantiateItem(View view, int position)
  {
    View myView = views.get(position);
    ((ViewPager) view).addView(myView);
    return myView;
  }

  @Override
  public boolean isViewFromObject(View view, Object object)
  {
    return view == ((T) object);
  }

  @Override
  public void restoreState(Parcelable arg0, ClassLoader arg1)
  {
  }

  @Override
  public Parcelable saveState()
  {
    return null;
  }

  @Override
  public void startUpdate(View view)
  {
  }
}

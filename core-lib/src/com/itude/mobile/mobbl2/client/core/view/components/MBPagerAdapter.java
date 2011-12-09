package com.itude.mobile.mobbl2.client.core.view.components;

import java.util.ArrayList;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class MBPagerAdapter<T extends View> extends PagerAdapter
  {
    private ArrayList<T> views;

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

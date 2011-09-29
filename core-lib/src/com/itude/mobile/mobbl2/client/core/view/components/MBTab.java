package com.itude.mobile.mobbl2.client.core.view.components;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itude.mobile.mobbl2.client.core.util.MBScreenUtilities;
import com.itude.mobile.mobbl2.client.core.util.UniqueIntegerGenerator;
import com.itude.mobile.mobbl2.client.core.view.builders.MBStyleHandler;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilderFactory;
import com.itude.mobile.mobbl2.client.core.view.listeners.MBTabListenerI;

public class MBTab extends RelativeLayout implements OnClickListener
{
  private int            _tabId;
  private MBTabBar       _tabBar     = null;
  private ImageView      _icon       = null;
  private TextView       _textView   = null;
  private View           _activeView = null;
  private MBTabListenerI _listener   = null;

  private View           _leftSpacer;
  private View           _rightSpacer;
  private LinearLayout   _content;

  private int[]          _oldPadding = null;

  public MBTab(Context context)
  {
    super(context);

    setFocusable(true);
    setClickable(true);
    setOnClickListener(this);

    MBStyleHandler styleHandler = MBViewBuilderFactory.getInstance().getStyleHandler();
    styleHandler.styleTab(this);

    setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, MBScreenUtilities.convertDimensionPixelsToPixels(56)));

    RelativeLayout.LayoutParams leftSpacerParams = new RelativeLayout.LayoutParams(0, 0);
    leftSpacerParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

    _leftSpacer = new View(context);
    _leftSpacer.setId(UniqueIntegerGenerator.getId());
    _leftSpacer.setLayoutParams(leftSpacerParams);

    RelativeLayout.LayoutParams contentParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    contentParams.addRule(RelativeLayout.RIGHT_OF, _leftSpacer.getId());
    contentParams.addRule(RelativeLayout.CENTER_VERTICAL);

    _content = new LinearLayout(context);
    _content.setId(UniqueIntegerGenerator.getId());
    _content.setLayoutParams(contentParams);
    _content.setOrientation(LinearLayout.HORIZONTAL);
    _content.setGravity(Gravity.CENTER_VERTICAL);

    _icon = new ImageView(context);
    _icon.setId(UniqueIntegerGenerator.getId());

    _textView = new TextView(context);
    _textView.setId(UniqueIntegerGenerator.getId());
    _textView.setSingleLine();
    _textView.setTextSize(18);

    styleHandler.styleTabText(_textView);

    _content.addView(_icon);
    _content.addView(_textView);

    RelativeLayout.LayoutParams rightSpacerParams = new RelativeLayout.LayoutParams(0, 0);
    rightSpacerParams.addRule(RelativeLayout.RIGHT_OF, _content.getId());

    _rightSpacer = new View(context);
    _rightSpacer.setLayoutParams(rightSpacerParams);

    addView(_leftSpacer);
    addView(_content);
    addView(_rightSpacer);
  }

  public void select()
  {
    if (_tabBar == null)
    {
      throw new IllegalStateException("There must be a relation with the tab bar this tab is placed in");
    }

    _tabBar.selectTab(this);
  }

  void doSelect()
  {
    if (isSelected())
    {
      reselect();
      return;
    }

    setSelected(true);

    if (_listener != null)
    {
      _listener.onTabSelected(this);
    }

    if (_activeView != null)
    {
      changeActiveView();
    }
  }

  void unselect()
  {
    setSelected(false);

    if (_listener != null)
    {
      _listener.onTabUnselected(this);
    }

    if (_activeView != null)
    {
      changeActiveView();
    }
  }

  void reselect()
  {
    if (_listener != null)
    {
      _listener.onTabReselected(this);
    }
  }

  private void changeActiveView()
  {
    _content.removeAllViews();
    if (isSelected())
    {
      _oldPadding = new int[]{getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom()};
      setPadding(0, 0, 0, 0);
      _content.addView(_activeView);
    }
    else
    {
      if (_oldPadding != null)
      {
        setPadding(_oldPadding[0], _oldPadding[1], _oldPadding[2], _oldPadding[3]);
      }
      _content.addView(_icon);
      _content.addView(_textView);
    }
  }

  public MBTab setIcon(Drawable drawable)
  {
    _icon.setImageDrawable(drawable);

    return this;
  }

  public MBTab setText(CharSequence text)
  {
    _textView.setText(text);

    return this;
  }

  public MBTab setActiveView(View view)
  {
    _activeView = view;

    return this;
  }

  public MBTab setListener(MBTabListenerI listener)
  {
    _listener = listener;

    return this;
  }

  public MBTab setTabId(int tabId)
  {
    _tabId = tabId;
    return this;
  }

  public int getTabId()
  {
    return _tabId;
  }

  void setTabBar(MBTabBar tabBar)
  {
    _tabBar = tabBar;
  }

  public MBTab setLeftPadding(int pixels)
  {
    _leftSpacer.getLayoutParams().width = pixels;
    return this;
  }

  public MBTab setRightPadding(int pixels)
  {
    _leftSpacer.getLayoutParams().width = pixels;
    return this;
  }

  @Override
  public void onClick(View view)
  {
    select();
  }
}

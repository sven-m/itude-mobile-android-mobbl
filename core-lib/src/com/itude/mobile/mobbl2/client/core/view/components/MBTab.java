package com.itude.mobile.mobbl2.client.core.view.components;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itude.mobile.mobbl2.client.core.util.MBScreenUtilities;
import com.itude.mobile.mobbl2.client.core.util.UniqueIntegerGenerator;
import com.itude.mobile.mobbl2.client.core.view.listeners.MBTabListenerI;

public class MBTab extends RelativeLayout
{
  private ImageView      _icon          = null;
  private TextView       _textView      = null;
  private MBTabListenerI _listener      = null;
  private boolean        _customViewSet = false;

  public MBTab(Context context)
  {
    super(context);

    setFocusable(true);
    setClickable(true);

    int tabDrawableId = getResources().getIdentifier("tab_indicator_holo", "drawable", "android");
    setBackgroundResource(tabDrawableId);

    setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, MBScreenUtilities.convertDimensionPixelsToPixels(56)));

    RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    imageParams.addRule(RelativeLayout.CENTER_VERTICAL);

    _icon = new ImageView(context);
    _icon.setId(UniqueIntegerGenerator.getId());
    _icon.setLayoutParams(imageParams);
    _icon.setVisibility(View.GONE);

    RelativeLayout.LayoutParams textViewParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    textViewParams.addRule(RelativeLayout.RIGHT_OF, _icon.getId());
    textViewParams.addRule(RelativeLayout.CENTER_VERTICAL);

    _textView = new TextView(context);
    _textView.setSingleLine();
    _textView.setPadding(0, 0, MBScreenUtilities.SIXTEEN, 0);
    _textView.setLayoutParams(textViewParams);
    _textView.setTextSize(18);

    addView(_icon);
    addView(_textView);
  }

  public void select()
  {
    setSelected(true);

    if (_listener != null)
    {
      _listener.onTabSelected(this);
    }
  }

  public void unselect()
  {
    setSelected(false);

    if (_listener != null)
    {
      _listener.onTabUnselected(this);
    }
  }

  public void reselect()
  {
    if (_listener != null)
    {
      _listener.onTabReselected(this);
    }
  }

  public MBTab setIcon(Drawable drawable)
  {
    _icon.setImageDrawable(drawable);
    _icon.setVisibility(View.VISIBLE);

    return this;
  }

  public MBTab setText(CharSequence text)
  {
    if (_customViewSet) throw new IllegalStateException("Unable to set text after custom view is set");

    _textView.setText(text);
    return this;
  }

  public MBTab setView(View view)
  {
    _customViewSet = true;

    _textView.setVisibility(View.GONE);
    _icon.setVisibility(View.GONE);

    RelativeLayout.LayoutParams viewParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    viewParams.addRule(RelativeLayout.RIGHT_OF, _icon.getId());
    viewParams.addRule(RelativeLayout.CENTER_VERTICAL);

    view.setPadding(0, 0, MBScreenUtilities.SIXTEEN, 0);
    view.setLayoutParams(viewParams);

    addView(view);

    return this;
  }

  public MBTab setListener(MBTabListenerI listener)
  {
    _listener = listener;

    return this;
  }
}

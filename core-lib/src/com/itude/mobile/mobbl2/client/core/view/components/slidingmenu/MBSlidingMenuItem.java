package com.itude.mobile.mobbl2.client.core.view.components.slidingmenu;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.android.util.UniqueIntegerGenerator;
import com.itude.mobile.mobbl2.client.core.services.MBLocalizationService;
import com.itude.mobile.mobbl2.client.core.view.builders.MBStyleHandler;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilderFactory;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MBSlidingMenuItem extends RelativeLayout
{
  private ImageView          _icon     = null;
  private TextView           _textView = null;

  private final LinearLayout _content;

  public MBSlidingMenuItem(Context context)
  {
    super(context);

    setFocusable(true);
    setClickable(true);

    MBStyleHandler styleHandler = MBViewBuilderFactory.getInstance().getStyleHandler();
    styleHandler.styleSlidingMenuItem(this);

    setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    _content = new LinearLayout(context);
    _content.setId(UniqueIntegerGenerator.getId());
    _content.setOrientation(LinearLayout.HORIZONTAL);
    _content.setGravity(Gravity.CENTER);

    _icon = new ImageView(context);
    _icon.setId(UniqueIntegerGenerator.getId());

    _textView = new TextView(context);
    _textView.setId(UniqueIntegerGenerator.getId());
    _textView.setSingleLine();

    styleHandler.styleSlidingMenuItemText(_textView);

    _content.addView(_icon);
    _content.addView(_textView);

    addView(_content);
  }

  public MBSlidingMenuItem setIcon(Drawable drawable)
  {
    _icon.setImageDrawable(drawable);
    return this;
  }

  public MBSlidingMenuItem setText(String text)
  {
    if (StringUtil.isNotBlank(text))
    {
      _textView.setText(MBLocalizationService.getInstance().getTextForKey(text));
    }

    return this;
  }
}

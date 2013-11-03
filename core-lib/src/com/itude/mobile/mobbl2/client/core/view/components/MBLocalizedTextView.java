package com.itude.mobile.mobbl2.client.core.view.components;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.itude.mobile.mobbl2.client.core.MBException;
import com.itude.mobile.mobbl2.client.core.services.MBLocalizationService;
import com.itude.mobile.mobbl2.client.core.util.Constants;

public class MBLocalizedTextView extends TextView
{

  public MBLocalizedTextView(Context context)
  {
    super(context);
  }

  public MBLocalizedTextView(Context context, AttributeSet attrs)
  {
    super(context, attrs);
  }

  public MBLocalizedTextView(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);
  }

  @Override
  public void setText(CharSequence text, BufferType type)
  {
    super.setText(getValue(text.toString()), type);
  }

  protected String getValue(String text)
  {
    String mbText;
    try
    {
      mbText = MBLocalizationService.getInstance().getTextForKey(text.toString());
    }
    catch (MBException mbe)
    {
      // You are probably previewing the XML, and MOBBL isn't loaded at this point. 
      Log.w(Constants.APPLICATION_NAME, mbe.getMessage());
      mbText = text.toString();
    }
    return mbText;
  }

}

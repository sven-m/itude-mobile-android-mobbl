package com.itude.mobile.mobbl2.client.core.view.builders.field;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.WebView;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.services.MBResourceService;
import com.itude.mobile.mobbl2.client.core.util.StringUtilities;
import com.itude.mobile.mobbl2.client.core.view.MBField;

public class WebFieldBuilder extends MBBaseFieldBuilder
{

  @Override
  public View buildField(MBField field)
  {
    WebView webView = new WebView(MBApplicationController.getInstance().getViewManager());
    webView.setScrollContainer(false);

    if (StringUtilities.isNotBlank(field.getSource()))
    {
      webView.setOnTouchListener(new OnTouchListener()
      {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
          return true;
        }
      });

      String url = MBResourceService.getInstance().getUrlById(field.getSource());
      webView.loadUrl(url);
    }
    else
    {
      webView.loadDataWithBaseURL(null, field.getValuesForDisplay(), null, "UTF-8", null);

    }
    getStyleHandler().styleWebView(webView, field);

    return webView;
  }

}

package com.itude.mobile.mobbl2.client.core.view.builders;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;

import com.itude.mobile.mobbl2.client.core.controller.MBViewManager;
import com.itude.mobile.mobbl2.client.core.view.MBComponent;
import com.itude.mobile.mobbl2.client.core.view.MBField;

public class MBViewBuilder
{

  protected void buildChildren(List<? extends MBComponent> children, ViewGroup view)
  {
    for (MBComponent child : children)
    {
      getStyleHandler().applyInsetsForComponent(child);

      View childView = child.buildView();
      if (childView == null) continue;

      view.addView(childView);
    }
  }

  protected MBStyleHandler getStyleHandler()
  {
    return MBViewBuilderFactory.getInstance().getStyleHandler();
  }
  

  protected boolean isFieldWithType(MBComponent child, String type)
  {
    return child instanceof MBField && ((MBField) child).getType() != null && ((MBField) child).getType().equals(type);
  }


}

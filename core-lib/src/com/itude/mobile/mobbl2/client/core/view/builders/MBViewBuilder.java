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
package com.itude.mobile.mobbl2.client.core.view.builders;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;

import com.itude.mobile.mobbl2.client.core.view.MBComponent;
import com.itude.mobile.mobbl2.client.core.view.MBField;

public class MBViewBuilder
{
  public static interface BuildChildrenCallback
  {
    public void onConstructChild(MBComponent child, View view);
  }

  private static final BuildChildrenCallback NULL_CALLBACK = new BuildChildrenCallback()
                                                           {

                                                             @Override
                                                             public void onConstructChild(MBComponent child, View view)
                                                             {
                                                             }
                                                           };

  protected List<View> buildChildren(List<? extends MBComponent> children, ViewGroup view)
  {
    return buildChildren(children, view, NULL_CALLBACK);
  }

  protected List<View> buildChildren(List<? extends MBComponent> children, ViewGroup view, BuildChildrenCallback callback)
  {
    List<View> addedViews = new ArrayList<View>(children.size());
    for (MBComponent child : children)
    {
      getStyleHandler().applyInsetsForComponent(child);

      View childView = child.buildView();
      if (childView == null) continue;

      callback.onConstructChild(child, childView);

      view.addView(childView);
      addedViews.add(childView);
    }

    return addedViews;
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

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
package com.itude.mobile.mobbl.core.view.builders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.itude.mobile.android.util.AssertUtil;
import com.itude.mobile.mobbl.core.MBException;
import com.itude.mobile.mobbl.core.controller.MBApplicationController;
import com.itude.mobile.mobbl.core.view.builders.dialog.SingleDialogBuilder;
import com.itude.mobile.mobbl.core.view.builders.dialog.SplitDialogBuilder;

/**
 * @author Coen Houtman
 *
 * Base class for all DialogBuilders
 */
public class MBDialogContentBuilder
{
  public static abstract class Builder
  {
    public abstract ViewGroup buildDialog(List<Integer> sortedDialogIds);

    /**
     * Build the container in which to place the fragments. A RelativeLayout should provide
     * enough flexibility to build any possible view. The view ids can be retrieved from the {@link #__sortedDialogIds}.
     * @return
     */
    protected RelativeLayout buildContainer()
    {
      RelativeLayout mainContainer = new RelativeLayout(MBApplicationController.getInstance().getBaseContext());
      mainContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

      return mainContainer;
    }

    protected MBStyleHandler getStyleHandler()
    {
      return MBViewBuilderFactory.getInstance().getStyleHandler();
    }
  }

  public static final String   DEFAULT_SINGLE = "SINGLE";
  public static final String   DEFAULT_SPLIT  = "SPLIT";

  private Map<String, Builder> _registry;

  public MBDialogContentBuilder()
  {
    _registry = new HashMap<String, Builder>();
    registerBuilder(DEFAULT_SPLIT, new SplitDialogBuilder());
    registerBuilder(DEFAULT_SINGLE, new SingleDialogBuilder());
  }

  public void registerBuilder(String type, Builder builder)
  {
    AssertUtil.notNull("type", type);
    AssertUtil.notNull("builder", builder);
    _registry.put(type, builder);
  }

  public ViewGroup buildDialog(String dialogType, List<Integer> sortedDialogIds)
  {
    Builder builder = _registry.get(dialogType);
    if (builder == null) throw new MBException("No dialog content builder for " + dialogType + " registered!");
    return builder.buildDialog(sortedDialogIds);
  }

}

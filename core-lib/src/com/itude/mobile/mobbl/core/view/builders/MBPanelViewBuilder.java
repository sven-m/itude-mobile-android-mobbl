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

import android.view.ViewGroup;

import com.itude.mobile.mobbl.core.util.Constants;
import com.itude.mobile.mobbl.core.view.MBPanel;
import com.itude.mobile.mobbl.core.view.builders.panel.ListPanelBuilder;
import com.itude.mobile.mobbl.core.view.builders.panel.MatrixHeaderBuilder;
import com.itude.mobile.mobbl.core.view.builders.panel.MatrixPanelBuilder;
import com.itude.mobile.mobbl.core.view.builders.panel.MatrixRowPanelBuilder;
import com.itude.mobile.mobbl.core.view.builders.panel.PlainPanelBuilder;
import com.itude.mobile.mobbl.core.view.builders.panel.RowPanelBuilder;
import com.itude.mobile.mobbl.core.view.builders.panel.SectionPanelBuilder;
import com.itude.mobile.mobbl.core.view.builders.panel.SegmentedControlPanelBuilder;

public class MBPanelViewBuilder extends MBViewBuilder
{

  private MBBuilderRegistry<MBPanel, Builder, String> _builders;
  private final BuildState                            _buildState;

  private void registerBuilders()
  {
    MBBuilderRegistry<MBPanel, Builder, String> builders = new MBBuilderRegistry<MBPanel, MBPanelViewBuilder.Builder, String>();
    builders.registerBuilder(Constants.C_PLAIN, new PlainPanelBuilder());
    builders.registerBuilder(Constants.C_LIST, new ListPanelBuilder());
    builders.registerBuilder(Constants.C_SECTION, new SectionPanelBuilder());
    builders.registerBuilder(Constants.C_ROW, new RowPanelBuilder());
    builders.registerBuilder(Constants.C_MATRIX, new MatrixPanelBuilder());
    builders.registerBuilder(Constants.C_MATRIXHEADER, new MatrixHeaderBuilder());
    builders.registerBuilder(Constants.C_MATRIXROW, new MatrixRowPanelBuilder());
    builders.registerBuilder(Constants.C_SEGMENTEDCONTROL, new SegmentedControlPanelBuilder());
    builders.registerBuilder(null, new PlainPanelBuilder());
    _builders = builders;
  }

  public MBPanelViewBuilder()
  {
    registerBuilders();
    _buildState = new BuildState();
  }

  public ViewGroup buildPanelView(MBPanel panel)
  {
    Builder builder = getBuilder(panel.getType(), panel.getStyle());

    ViewGroup view = builder.buildPanel(panel, _buildState);
    panel.attachView(view);

    getStyleHandler().applyStyle(panel, view);

    return view;
  }

  public Builder getBuilder(String type, String style)
  {
    return _builders.getBuilder(type, style);
  }

  public void registerBuilder(String type, Builder builder)
  {
    _builders.registerBuilder(type, builder);
  }

  public void registerBuilder(String type, String style, Builder builder)
  {
    _builders.registerBuilder(type, style, builder);
  }

  /////////////////////

  public static interface Builder
  {
    public ViewGroup buildPanel(MBPanel panel, BuildState buildState);
  }

  public static class BuildState
  {
    private int _matrixRowNumber;

    public void resetMatrixRow()
    {
      _matrixRowNumber = 0;
    }

    public void increaseMatrixRow()
    {
      _matrixRowNumber++;
    }

    public int getMatrixRow()
    {
      return _matrixRowNumber;
    }
  }

}

package com.itude.mobile.mobbl2.client.core.view.builders;

import android.view.ViewGroup;

import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.view.MBPanel;
import com.itude.mobile.mobbl2.client.core.view.builders.panel.ListPanelBuilder;
import com.itude.mobile.mobbl2.client.core.view.builders.panel.MatrixHeaderBuilder;
import com.itude.mobile.mobbl2.client.core.view.builders.panel.MatrixPanelBuilder;
import com.itude.mobile.mobbl2.client.core.view.builders.panel.MatrixRowPanelBuilder;
import com.itude.mobile.mobbl2.client.core.view.builders.panel.PlainPanelBuilder;
import com.itude.mobile.mobbl2.client.core.view.builders.panel.RowPanelBuilder;
import com.itude.mobile.mobbl2.client.core.view.builders.panel.SectionPanelBuilder;
import com.itude.mobile.mobbl2.client.core.view.builders.panel.SegmentedControlPanelBuilder;

public class MBPanelViewBuilder extends MBViewBuilder
{

  private MBBuilderRegistry<MBPanel, Builder> _builders;
  private final BuildState                    _buildState;

  private void registerBuilders()
  {
    MBBuilderRegistry<MBPanel, Builder> builders = new MBBuilderRegistry<MBPanel, MBPanelViewBuilder.Builder>();
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

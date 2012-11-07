package com.itude.mobile.mobbl2.client.core.view.builders;

import java.util.HashMap;
import java.util.Map;

import android.view.ViewGroup;

import com.itude.mobile.mobbl2.client.core.controller.MBViewManager;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.view.MBPanel;
import com.itude.mobile.mobbl2.client.core.view.builders.panel.EditableMatrixPanelBuilder;
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

  public static interface Builder
  {
    public ViewGroup buildPanel(MBPanel panel, MBViewManager.MBViewState viewState, BuildState buildState);
  }
  
  public static class BuildState {
    private int _matrixRowNumber;
    
    public void resetMatrixRow() {
      _matrixRowNumber = 0;
    }
    
    public void increaseMatrixRow () {
      _matrixRowNumber++;
    }
    
    public int getMatrixRow () {
      return _matrixRowNumber;
    }
  }

  private Map<String, Builder> _builders;
  private BuildState _buildState;

  private void registerBuilders()
  {
    Map<String, Builder> builders = new HashMap<String, Builder>();
    builders.put(Constants.C_PLAIN, new PlainPanelBuilder());
    builders.put(Constants.C_LIST, new ListPanelBuilder());
    builders.put(Constants.C_SECTION, new SectionPanelBuilder());
    builders.put(Constants.C_ROW, new RowPanelBuilder());
    builders.put(Constants.C_MATRIX, new MatrixPanelBuilder());
    builders.put(Constants.C_MATRIXHEADER, new MatrixHeaderBuilder());
    builders.put(Constants.C_MATRIXROW, new MatrixRowPanelBuilder());
    builders.put(Constants.C_EDITABLEMATRIX, new EditableMatrixPanelBuilder());
    builders.put(Constants.C_SEGMENTEDCONTROL, new SegmentedControlPanelBuilder());
    _builders = builders;
  }

  public MBPanelViewBuilder()
  {
    registerBuilders();
    _buildState = new BuildState();
  }

  public ViewGroup buildPanelView(MBPanel panel, MBViewManager.MBViewState viewState)
  {
    Builder builder = getBuilderForType(panel.getType());
    if (builder == null) builder = getBuilderForType(Constants.C_PLAIN);

    ViewGroup view = builder.buildPanel(panel, viewState, _buildState);

    getStyleHandler().applyStyle(panel, view, viewState);
    return view;

  }

  public Builder getBuilderForType(String type)
  {
    return _builders.get(type);
  }

}

package com.itude.mobile.mobbl2.client.core.view.listeners;

import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.view.MBPanel;

public class MBMatrixConstructionListener extends MBBasePageConstructionListener
{
  @Override
  public void onConstructedPanel(MBPanel panel)
  {
    if (Constants.C_MATRIX.equals(panel.getType()) || Constants.C_MATRIXROW.equals(panel.getType()))

    if (panel.getDiffableMarkerPath() == null || panel.getDiffablePrimaryPath() == null)
    {
      if (Constants.C_MATRIXROW.equals(panel.getType()))
      {
        // assume inter-row diffables so make the matrix panel the master and move diffable knowledge to the parent
        panel.setDiffableMaster(false);
        MBPanel parent = panel.getFirstParentPanelWithType(Constants.C_MATRIX);
        if (parent == null)
        {
          //   parent = getFirstParentPanelWithType(Constants.C_EDITABLEMATRIX);
        }
        parent.setDiffableMaster(true);
        if (panel.getDiffableMarkerPath() != null)
        {
          parent.setDiffableMarkerPath(panel.getDiffableMarkerPath());
        }

        if (panel.getDiffablePrimaryPath() != null)
        {
          parent.setDiffablePrimaryPath(panel.getDiffablePrimaryPath());
        }
      }
    }
    else
    {
      panel.setDiffableMaster(true);
    }
  }

}

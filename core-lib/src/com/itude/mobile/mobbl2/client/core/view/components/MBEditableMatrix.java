package com.itude.mobile.mobbl2.client.core.view.components;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationFactory;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.view.MBPanel;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilderFactory;
import com.itude.mobile.mobbl2.client.core.view.helpers.MBEditableMatrixListener;

public class MBEditableMatrix extends LinearLayout
{

  private MBPanel                  matrixPanel;
  private MBEditableMatrixListener matrixListener;
  private boolean                  inEditMode = false;
  private LinearLayout             listView;
  private RelativeLayout           editView;

  public MBEditableMatrix(Context context, boolean initialiseInEditMode)
  {
    super(context);

    inEditMode = initialiseInEditMode;

    if (initialiseInEditMode)
    {
      createEditView();
    }
    else
    {
      createListView();
    }

  }

  public MBEditableMatrix(Context context, boolean initialiseInEditMode, MBPanel panel)
  {
    this(context, initialiseInEditMode);
    setMatrixPanel(panel);
  }

  public MBPanel getMatrixPanel()
  {
    return matrixPanel;
  }

  public void setMatrixPanel(MBPanel matrixPanel)
  {
    this.matrixPanel = matrixPanel;
  }

  public MBEditableMatrixListener getMatrixListener()
  {
    return matrixListener;
  }

  public void setMatrixListener(MBEditableMatrixListener matrixListener)
  {
    if (this.matrixListener == null)
    {

    }

    this.matrixListener = matrixListener;
  }

  public ViewGroup getCurrentContentView()
  {
    if (listView == null && editView != null)
    {
      return editView;
    }
    else if (editView == null && listView != null)
    {
      return listView;
    }

    return null;
  }

  public void refreshEditableMatrix(boolean switchToDifferentMode)
  {

    if (switchToDifferentMode)
    {
      removeAllViews();
      listView = null;
      editView = null;

      if (matrixPanel != null)
      {
        String panelMode = matrixPanel.getMode();

        if (panelMode == null || panelMode.equals(Constants.C_EDITABLEMATRIX_MODE_VIEW))
        {
          // Switch to edit mode (current mode is view)
          matrixPanel.setMode(Constants.C_EDITABLEMATRIX_MODE_EDIT);
          setInEditMode(!isInEditMode());
          createEditView();
        }
        else
        {
          matrixPanel.setMode(Constants.C_EDITABLEMATRIX_MODE_VIEW);
          setInEditMode(!isInEditMode());
          createListView();
        }
      }
    }
    else
    {
      getCurrentContentView().removeAllViews();
    }

    MBViewBuilderFactory.getInstance().getPanelViewBuilder()
        .buildChildrenForEditableMatrix(matrixPanel.getChildren(), getCurrentContentView(), matrixPanel.getPage().getCurrentViewState());

    connectMatrixListener();
  }

  public void connectMatrixListener()
  {

    if (matrixListener == null && matrixPanel != null && matrixPanel.getName() != null)
    {
      matrixListener = MBApplicationFactory.getInstance().getEditableMatrixListener(matrixPanel.getName());
    }

    matrixListener.clearCaches();
    matrixListener.setEditableMatrix(this);
    matrixListener.setMatrixPanel(matrixPanel);

    // Make sure onclick of the edit button triggers the right action
    if (!matrixPanel.getMode().equals(Constants.C_EDITABLEMATRIX_MODE_EDITONLY))
    {
      matrixListener.onPrepareEditButton();
    }
    // Make sure buttons and possibly childrows itself get an onclicklistener
    if (getCurrentContentView() instanceof RelativeLayout && editView.getChildCount() > 0)
    {
      int index = 0;

      for (int i = 0; i < editView.getChildCount(); i++)
      {

        final int j = index;

        View child = editView.getChildAt(i);

        if (child.getTag().equals(Constants.C_MATRIXHEADER_CONTAINER))
        {
          matrixListener.setMatrixHeaderView(child.findViewWithTag(Constants.C_MATRIXHEADER));
        }
        else if (child.getTag().equals(Constants.C_MATRIXROW))
        {
          // Add view to array, this array will be used to perform view related stuff
          matrixListener.addRowView(child);
          matrixListener.prepareRowListeners(j, child);

          index++;
        }

      }
    }

  }

  @Override
  public boolean isInEditMode()
  {
    return inEditMode;
  }

  public void setInEditMode(boolean inEditMode)
  {
    this.inEditMode = inEditMode;
  }

  private void createEditView()
  {
    editView = new RelativeLayout(getContext());
    editView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    addView(editView);
  }

  private void createListView()
  {
    listView = new LinearLayout(getContext());
    listView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    listView.setOrientation(LinearLayout.VERTICAL);
    addView(listView);
  }

}

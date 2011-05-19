package com.itude.mobile.mobbl2.client.core.view.helpers;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.view.MBPanel;
import com.itude.mobile.mobbl2.client.core.view.components.MBEditableMatrix;

public abstract class MBEditableMatrixListener
{

  protected List<View>       _rowViews         = new ArrayList<View>();
  protected MBPanel          _matrixPanel;
  protected View             _matrixHeaderView = null;
  protected MBEditableMatrix _editableMatrix   = null;

  public void clearCaches()
  {
    _rowViews = new ArrayList<View>();
    _matrixPanel = null;
    _matrixHeaderView = null;
    _editableMatrix = null;
  }

  public void setMatrixPanel(MBPanel matrixPanel)
  {
    _matrixPanel = matrixPanel;
  }

  public MBPanel matrixPanel()
  {
    return _matrixPanel;
  }

  public void setMatrixHeaderView(View matrixHeaderView)
  {
    _matrixHeaderView = matrixHeaderView;
  }

  public void setEditableMatrix(MBEditableMatrix editableMatrix)
  {
    _editableMatrix = editableMatrix;
  }

  public abstract int getRowCount();

  public void addRowView(View rowView)
  {
    _rowViews.add(rowView);
  }

  public List<View> getRowViews()
  {
    return _rowViews;
  }

  public boolean onPrepareEditButton()
  {

    Button editButton = (Button) _editableMatrix.getCurrentContentView().findViewWithTag(Constants.C_MATRIXHEADER)
        .findViewWithTag(Constants.C_MATRIXTITLEROW).findViewWithTag(Constants.C_EDITABLEMATRIX_EDITBUTTON);

    if (editButton != null)
    {
      editButton.setOnClickListener(new OnClickListener()
      {
        public void onClick(View v)
        {
          if (_editableMatrix.isInEditMode())
          {
            if (saveMatrix())
            {
              onAfterEditMode();
            }
          }
          else
          {
            onBeforeEditMode();
          }
        }
      });
    }

    return true;
  }

  public boolean onPrepareClickableRow(final int itemAtPosition, View rowView)
  {
    rowView.setOnClickListener(new OnClickListener()
    {
      public void onClick(View v)
      {
        if (onBeforeRowClick(itemAtPosition))
        {
          onRowClick(itemAtPosition);
        }
      }
    });

    return true;
  }

  public boolean onPrepareDeleteButton(final int itemAtPosition, View rowView)
  {
    if (rowView != null)
    {
      Button deleteButton = (Button) rowView.findViewWithTag(Constants.C_EDITABLEMATRIX_LEFTBUTTONSCONTAINER)
          .findViewWithTag(Constants.C_EDITABLEMATRIX_DELETEBUTTON);

      deleteButton.setOnClickListener(new OnClickListener()
      {

        public void onClick(View v)
        {
          if (onBeforeDelete(itemAtPosition) && onDelete(itemAtPosition))
          {
            _editableMatrix.refreshEditableMatrix(false);
          }
        }
      });
    }

    return true;
  }

  public boolean onBeforeEditMode()
  {
    _editableMatrix.refreshEditableMatrix(true);
    return true;
  }

  public boolean onAfterEditMode()
  {
    _editableMatrix.getMatrixPanel().rebuild();
    _editableMatrix.refreshEditableMatrix(true);
    return true;
  }

  public boolean saveMatrix()
  {
    return true;
  }

  public boolean onBeforeDelete(int itemAtPosition)
  {
    return true;
  }

  public boolean onDelete(int itemAtPosition)
  {
    return true;
  }

  public boolean onBeforeRowClick(int itemAtPosition)
  {
    return true;
  }

  public boolean onRowClick(int itemAtPosition)
  {
    return true;
  }

  public boolean onPrepareSelectListener(int itemAtPosition, View rowView)
  {
    return true;
  }

  public boolean onBeforeSelect(int itemAtPosition, boolean isChangeable)
  {
    return true;
  }

  public boolean onSelect(int itemAtPosition, boolean isChangeable)
  {
    return true;
  }

  public boolean onPrepareDraggableControls(final int itemAtPosition)
  {
    final View rowView = _rowViews.get(itemAtPosition);

    final Button upButton = (Button) rowView.findViewWithTag(Constants.C_EDITABLEMATRIX_RIGHTBUTTONSCONTAINER)
        .findViewWithTag(Constants.C_EDITABLEMATRIX_UPBUTTON);
    final Button downButton = (Button) rowView.findViewWithTag(Constants.C_EDITABLEMATRIX_RIGHTBUTTONSCONTAINER)
        .findViewWithTag(Constants.C_EDITABLEMATRIX_DOWNBUTTON);

    upButton.setOnClickListener(new OnClickListener()
    {

      public void onClick(View v)
      {
        if (onBeforeChangePosition(itemAtPosition, (itemAtPosition - 1)))
        {
          onChangePosition(itemAtPosition, (itemAtPosition - 1));
        }
      }
    });

    downButton.setOnClickListener(new OnClickListener()
    {

      public void onClick(View v)
      {
        if (onBeforeChangePosition(itemAtPosition, (itemAtPosition + 1)))
        {
          onChangePosition(itemAtPosition, (itemAtPosition + 1));
        }
      }
    });

    return true;
  }

  protected void showUpButton(int itemAtPosition, boolean showUpButton)
  {
    final View rowView = _rowViews.get(itemAtPosition);
    final Button upButton = (Button) rowView.findViewWithTag(Constants.C_EDITABLEMATRIX_RIGHTBUTTONSCONTAINER)
        .findViewWithTag(Constants.C_EDITABLEMATRIX_UPBUTTON);

    if (showUpButton)
    {
      upButton.setVisibility(Button.VISIBLE);
      upButton.setEnabled(true);
    }
    else
    {
      //upButton.setVisibility(Button.INVISIBLE);
      upButton.setEnabled(false);
    }
  }

  protected void showDownButton(int itemAtPosition, boolean showDownButton)
  {
    final View rowView = _rowViews.get(itemAtPosition);
    final Button downButton = (Button) rowView.findViewWithTag(Constants.C_EDITABLEMATRIX_RIGHTBUTTONSCONTAINER)
        .findViewWithTag(Constants.C_EDITABLEMATRIX_DOWNBUTTON);

    if (showDownButton)
    {
      downButton.setVisibility(Button.VISIBLE);
      downButton.setEnabled(true);
    }
    else
    {
      //downButton.setVisibility(Button.INVISIBLE);
      downButton.setEnabled(false);
    }
  }

  public boolean onBeforeChangePosition(int currentPosition, int newPosition)
  {
    return true;
  }

  public boolean onChangePosition(int currentPosition, int newPosition)
  {
    View currentRow = _rowViews.get(currentPosition);
    View newRow = _rowViews.get(newPosition);

    if (currentPosition > newPosition)
    {
      // Move up
      if ((currentPosition + 1) < _rowViews.size())
      {
        View belowCurrentRow = _rowViews.get(currentPosition + 1);
        RelativeLayout.LayoutParams belowCurrentRowParams = (RelativeLayout.LayoutParams) belowCurrentRow.getLayoutParams();
        belowCurrentRowParams.addRule(RelativeLayout.BELOW, newRow.getId());
        belowCurrentRow.setLayoutParams(belowCurrentRowParams);
      }

      RelativeLayout.LayoutParams newRowParams = (RelativeLayout.LayoutParams) newRow.getLayoutParams();
      newRowParams.addRule(RelativeLayout.BELOW, currentRow.getId());
      newRow.setLayoutParams(newRowParams);

      if ((newPosition - 1) >= 0 || (newPosition - 1) == -1 && _matrixHeaderView != null)
      {
        View aboveNewRow;
        if (newPosition - 1 == -1)
        {
          aboveNewRow = _matrixHeaderView;
        }
        else
        {
          aboveNewRow = _rowViews.get(newPosition - 1);
        }
        RelativeLayout.LayoutParams currentRowParams = (RelativeLayout.LayoutParams) currentRow.getLayoutParams();
        currentRowParams.addRule(RelativeLayout.BELOW, aboveNewRow.getId());
        currentRow.setLayoutParams(currentRowParams);
      }

    }
    else
    {
      // Move down

      if ((newPosition + 1) < _rowViews.size())
      {
        View belowNewRow = _rowViews.get(newPosition + 1);
        RelativeLayout.LayoutParams belowNewRowParams = (RelativeLayout.LayoutParams) belowNewRow.getLayoutParams();
        belowNewRowParams.addRule(RelativeLayout.BELOW, currentRow.getId());
        belowNewRow.setLayoutParams(belowNewRowParams);
      }

      if ((currentPosition - 1) >= 0 || currentPosition - 1 == -1 && _matrixHeaderView != null)
      {
        View aboveCurrentRow;
        if (currentPosition - 1 == -1)
        {
          aboveCurrentRow = _matrixHeaderView;
        }
        else
        {
          aboveCurrentRow = _rowViews.get(currentPosition - 1);
        }
        RelativeLayout.LayoutParams newRowParams = (RelativeLayout.LayoutParams) newRow.getLayoutParams();
        newRowParams.addRule(RelativeLayout.BELOW, aboveCurrentRow.getId());
        newRow.setLayoutParams(newRowParams);
      }

      RelativeLayout.LayoutParams currentRowParams = (RelativeLayout.LayoutParams) currentRow.getLayoutParams();
      currentRowParams.addRule(RelativeLayout.BELOW, newRow.getId());
      currentRow.setLayoutParams(currentRowParams);

    }

    _rowViews.set(newPosition, currentRow);
    _rowViews.set(currentPosition, newRow);

    // Make sure buttons and onclickListeners are being changed 
    prepareRowListeners(newPosition, currentRow);
    prepareRowListeners(currentPosition, newRow);
    //

    return true;
  }

  public void prepareRowListeners(int itemAtPosition, View rowView)
  {
    // Add draggable buttons if allowed
    if (_matrixPanel.isChildrenDraggable())
    {
      onPrepareDraggableControls(itemAtPosition);
    }

    // Add row onclicklistener if allowed
    if (_matrixPanel.isChildrenClickable())
    {
      onPrepareClickableRow(itemAtPosition, rowView);
    }

    // Add delete onclicklistener if allowed
    if (_matrixPanel.isChildrenDeletable())
    {
      onPrepareDeleteButton(itemAtPosition, rowView);
    }

    // Add select onclickListener if allowed
    if (_matrixPanel.isChildrenSelectable())
    {
      onPrepareSelectListener(itemAtPosition, rowView);
    }
  }

}

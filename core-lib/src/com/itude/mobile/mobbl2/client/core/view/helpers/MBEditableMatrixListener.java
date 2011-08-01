package com.itude.mobile.mobbl2.client.core.view.helpers;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.MBDevice;
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

  public boolean onPrepareLongClickableRow(final int itemAtPosition, View rowView)
  {

    rowView.setOnLongClickListener(new OnLongClickListener()
    {

      public boolean onLongClick(View v)
      {
        if (onBeforeLongRowClick(itemAtPosition))
        {
          return onLongRowClick(itemAtPosition);
        }

        return false;
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

  public boolean onBeforeLongRowClick(int itemAtPosition)
  {
    return true;
  }

  public boolean onLongRowClick(int itemAtPosition)
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

    final Button dragButton = (Button) rowView.findViewWithTag(Constants.C_EDITABLEMATRIX_RIGHTBUTTONSCONTAINER)
        .findViewWithTag(Constants.C_EDITABLEMATRIX_DRAGBUTTON);

    dragButton.setOnTouchListener(new OnTouchListener()
    {
      @Override
      public boolean onTouch(View v, MotionEvent arg1)
      {
        v.startDrag(null, new DragShadowBuilder(rowView), rowView, 0);
        //        rowView.setVisibility(View.INVISIBLE);
        return true;
      }
    });

    rowView.setOnDragListener(new OnDragListener()
    {
      public boolean onDrag(View v, DragEvent event)
      {
        View draggedView = (View) event.getLocalState();

        switch (event.getAction())
        {
          case DragEvent.ACTION_DRAG_STARTED :
            if (v.equals(draggedView))
            {
              draggedView.setVisibility(View.INVISIBLE);
              draggedView.setBackgroundColor(Color.WHITE);
            }
            break;
          case DragEvent.ACTION_DRAG_ENTERED :
            if (!v.equals(draggedView))
            {
              //              int indexOfStatic = _rowViews.indexOf(v);
              //              int indexOfDragged = _rowViews.indexOf(draggedView);
              //              onChangePosition(indexOfDragged, indexOfStatic);
              v.setBackgroundColor(Color.GRAY);
              //              MBViewBuilderFactory.getInstance().getStyleHandler().styleDropView
            }
            break;
          case DragEvent.ACTION_DRAG_EXITED :
            if (!v.equals(draggedView))
            {
              v.setBackgroundColor(Color.WHITE);
            }
            break;
          case DragEvent.ACTION_DROP :
            if (!v.equals(draggedView))
            {
              int indexOfStatic = _rowViews.indexOf(v);
              int indexOfDragged = _rowViews.indexOf(draggedView);
              onChangePosition(indexOfDragged, indexOfStatic);
            }
            break;
          case DragEvent.ACTION_DRAG_ENDED :
            if (v.equals(draggedView))
            {
              draggedView.setVisibility(View.VISIBLE);
              draggedView.setBackgroundColor(Color.GRAY);
            }
            else
            {
              v.setBackgroundColor(Color.WHITE);
            }
            break;
          default :
            break;
        }
        return true;
      }
    });

    return true;
  }

  public boolean onPrepareUpDownControls(final int itemAtPosition)
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
        onChangePosition(itemAtPosition, (itemAtPosition - 1));
      }
    });

    downButton.setOnClickListener(new OnClickListener()
    {

      public void onClick(View v)
      {
        onChangePosition(itemAtPosition, (itemAtPosition + 1));
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

  public boolean onChangePosition(int fromPosition, int toPosition)
  {
    View currentRow;
    View newRow;
    try
    {
      currentRow = _rowViews.get(fromPosition);
      newRow = _rowViews.get(toPosition);
    }
    catch (IndexOutOfBoundsException e)
    {
      Log.e(Constants.APPLICATION_NAME, "Error replacing row of position: " + fromPosition + " to: " + toPosition);
      return false;
    }

    // handle views around from position

    RelativeLayout rowBelowCurrent = (RelativeLayout) _rowViews.get(fromPosition + 1);
    RelativeLayout rowAboveCurrent = (RelativeLayout) _rowViews.get(fromPosition - 1);

    LayoutParams newLayoutParamsForViewBelow = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
    newLayoutParamsForViewBelow.addRule(RelativeLayout.BELOW, rowAboveCurrent.getId());
    rowBelowCurrent.setLayoutParams(newLayoutParamsForViewBelow);

    // handle views around to position

    RelativeLayout rowBelowToPosition = (RelativeLayout) _rowViews.get(toPosition + 1);
    RelativeLayout rowAboveToPosition = (RelativeLayout) _rowViews.get(toPosition - 1);

    LayoutParams newLayoutParamsForViewBelowTo = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
    newLayoutParamsForViewBelowTo.addRule(RelativeLayout.BELOW, currentRow.getId());
    rowBelowToPosition.setLayoutParams(newLayoutParamsForViewBelowTo);

    LayoutParams newLayoutParamsForViewAboveTo = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
    newLayoutParamsForViewAboveTo.addRule(RelativeLayout.BELOW, currentRow.getId());

    //
    //    _rowViews.set(newPosition, currentRow);
    //    _rowViews.set(currentPosition, newRow);
    //
    //    // Make sure buttons and onclickListeners are being changed 
    //    prepareRowListeners(newPosition, currentRow);
    //    prepareRowListeners(currentPosition, newRow);
    //

    return true;
  }

  public void prepareRowListeners(int itemAtPosition, View rowView)
  {
    // Add draggable buttons if allowed
    if (_matrixPanel.isChildrenDraggable())
    {
      if (MBDevice.getInstance().isTablet())
      {
        onPrepareDraggableControls(itemAtPosition);
      }
      else
      {
        onPrepareUpDownControls(itemAtPosition);
      }
    }

    // Add row onclicklistener if allowed
    if (_matrixPanel.isChildrenClickable())
    {
      onPrepareClickableRow(itemAtPosition, rowView);
    }

    // Add row onLongClickListener if allowed
    if (_matrixPanel.isChildrenLongClickable())
    {
      onPrepareLongClickableRow(itemAtPosition, rowView);
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

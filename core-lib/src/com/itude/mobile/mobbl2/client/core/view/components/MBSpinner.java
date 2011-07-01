package com.itude.mobile.mobbl2.client.core.view.components;

import android.app.ActionBar;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * @author Coen Houtman
 *
 * Custom implementation of Android's {@link android.widget.Spinner}.
 * It is possible to set a prompt view. When set, the prompt view is displayed instead of the
 * selected dropdown item. This can be expanded to also display an image next to it for example.
 */
public class MBSpinner extends Spinner
{
  private View _promptView = null;
  private int  _mode;
  private int  _widthMeasureSpec;
  private int  _heightMeasureSpec;

  public MBSpinner(Context context)
  {
    this(context, MODE_DROPDOWN);
  }

  public MBSpinner(Context context, int mode)
  {
    super(context, mode);
    _mode = mode;

    //    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    //    layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
    //    setLayoutParams(layoutParams);

    //FIXME using ActionBar.LayoutParams is way too specific here. Maybe use the comment above in a way that it does work.
    ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(Gravity.FILL);
    setLayoutParams(layoutParams);
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b)
  {
    if (_promptView == null)
    {
      super.onLayout(changed, l, t, r, b);
    }
    else
    {
      int childrenLeft = getPaddingLeft();
      int childrenWidth = getRight() - getLeft() - getPaddingLeft() - getPaddingRight();

      removeAllViewsInLayout();

      setupChild(_promptView);

      View sel = _promptView;
      int width = sel.getMeasuredWidth();
      int selectedOffset = childrenLeft + (childrenWidth / 2) - (width / 2);
      sel.offsetLeftAndRight(selectedOffset);

      invalidate();
    }
  }

  public void setupChild(View child)
  {
    ViewGroup.LayoutParams lp = child.getLayoutParams();
    if (lp == null)
    {
      lp = generateDefaultLayoutParams();
    }

    addViewInLayout(child, 0, lp);

    int childHeightMeasureSpec = ViewGroup.getChildMeasureSpec(_heightMeasureSpec, getPaddingTop() + getPaddingBottom(), lp.height);
    int childWidthMeasureSpec = ViewGroup.getChildMeasureSpec(_widthMeasureSpec, getPaddingLeft() + getPaddingRight(), lp.width);

    child.measure(childWidthMeasureSpec, childHeightMeasureSpec);

    int childTop = getPaddingTop() + ((getMeasuredHeight() - getPaddingBottom() - getPaddingTop() - child.getMeasuredHeight()) / 2);
    int childBottom = childTop + child.getMeasuredHeight();

    int width = child.getMeasuredWidth();
    int childLeft = 0;
    int childRight = childLeft + width;

    child.layout(childLeft, childTop, childRight, childBottom);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
  {
    if (_promptView == null)
    {
      super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    else
    {
      int widthSize;
      int heightSize;

      int paddingLeft = super.getPaddingLeft() > _promptView.getPaddingLeft() ? super.getPaddingLeft() : _promptView.getPaddingLeft();
      int paddingTop = super.getPaddingTop() > _promptView.getPaddingTop() ? super.getPaddingTop() : _promptView.getPaddingTop();
      int paddingRight = super.getPaddingRight() > _promptView.getPaddingRight() ? super.getPaddingRight() : _promptView.getPaddingRight();
      int paddingBottom = super.getPaddingBottom() > _promptView.getPaddingBottom() ? super.getPaddingBottom() : _promptView
          .getPaddingBottom();

      setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

      int preferredHeight = 0;
      int preferredWidth = 0;

      View view = _promptView;
      if (view.getLayoutParams() == null)
      {
        view.setLayoutParams(generateDefaultLayoutParams());
      }
      measureChild(view, widthMeasureSpec, heightMeasureSpec);

      preferredHeight = view.getMeasuredHeight() + getPaddingTop() + getPaddingBottom();
      preferredWidth = view.getMeasuredWidth() + getPaddingLeft() + getPaddingRight();

      preferredHeight = Math.max(preferredHeight, getSuggestedMinimumHeight());
      preferredWidth = Math.max(preferredWidth, getSuggestedMinimumWidth());

      heightSize = resolveSize(preferredHeight, heightMeasureSpec);
      widthSize = resolveSize(preferredWidth, widthMeasureSpec);

      setMeasuredDimension(widthSize, heightSize);

      _widthMeasureSpec = widthMeasureSpec;
      _heightMeasureSpec = heightMeasureSpec;
    }
  }

  public int getMode()
  {
    return _mode;
  }

  public void setPromptView(View promptView)
  {
    //FIXME don't take a View, but only allow to add prompt text and an optional accompanying image

    _promptView = promptView;
    if (_promptView instanceof TextView)
    {
      ((TextView) _promptView).setTextSize(18);
      ((TextView) _promptView).setTextColor(getContext().getResources().getColor(android.R.color.primary_text_dark));
    }
  }
}

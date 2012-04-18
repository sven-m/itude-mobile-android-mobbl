package com.itude.mobile.mobbl2.client.core.view.components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.itude.mobile.mobbl2.client.core.util.MBDevice;
import com.itude.mobile.mobbl2.client.core.util.UniqueIntegerGenerator;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilderFactory;

/**
 * @author Coen Houtman
 *
 * Custom implementation of Android's {@link android.widget.Spinner}.
 * It is possible to set a prompt view. When set, the prompt view is displayed instead of the
 * selected dropdown item. This can be expanded to also display an image next to it for example.
 */
public class MBSpinner extends Spinner
{
  private TextView       _textView = null;
  private ImageView      _icon     = null;
  private RelativeLayout _layout   = null;
  private int            _mode;
  private int            _widthMeasureSpec;
  private int            _heightMeasureSpec;

  public MBSpinner(Context context)
  {
    this(context, MODE_DROPDOWN);
  }

  public MBSpinner(Context context, int mode)
  {
    super(context, mode);
    if (MBDevice.getInstance().isPhone())
    {
      throw new UnsupportedOperationException("This widget is not designed for smartphone environments");
    }
    _mode = mode;

    MBViewBuilderFactory.getInstance().getStyleHandler().styleTabSpinner(this);
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b)
  {
    super.onLayout(changed, l, t, r, b);

    if (_textView != null)
    {
      int childrenLeft = getPaddingLeft();
      int childrenWidth = getRight() - getLeft() - getPaddingLeft() - getPaddingRight();

      removeAllViewsInLayout();

      View view = _textView;
      if (_layout != null)
      {
        view = _layout;
      }
      setupChild(view);

      View sel = view;
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
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    if (_textView != null)
    {
      int widthSize;
      int heightSize;

      View view = _textView;
      if (_layout != null)
      {
        view = _layout;
      }

      int paddingLeft = super.getPaddingLeft() > view.getPaddingLeft() ? super.getPaddingLeft() : view.getPaddingLeft();
      int paddingTop = super.getPaddingTop() > view.getPaddingTop() ? super.getPaddingTop() : view.getPaddingTop();
      int paddingRight = super.getPaddingRight() > view.getPaddingRight() ? super.getPaddingRight() : view.getPaddingRight();
      int paddingBottom = super.getPaddingBottom() > view.getPaddingBottom() ? super.getPaddingBottom() : view.getPaddingBottom();

      setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

      int preferredHeight = 0;
      int preferredWidth = 0;

      if (view.getLayoutParams() == null)
      {
        view.setLayoutParams(generateDefaultLayoutParams());
      }
      measureChild(view, widthMeasureSpec, heightMeasureSpec);
      preferredWidth = view.getMeasuredWidth();

      for (int i = 0; i < getAdapter().getCount(); i++)
      {
        int measuredWidth = measureChildForWidth(getAdapter().getView(i, null, this), widthMeasureSpec, heightMeasureSpec);
        preferredWidth = Math.max(measuredWidth, preferredWidth);
      }

      preferredHeight = view.getMeasuredHeight() + getPaddingTop() + getPaddingBottom();
      preferredWidth = preferredWidth + getPaddingLeft() + getPaddingRight();

      preferredHeight = Math.max(preferredHeight, getSuggestedMinimumHeight());
      preferredWidth = Math.max(preferredWidth, getSuggestedMinimumWidth());

      heightSize = resolveSize(preferredHeight, heightMeasureSpec);
      widthSize = resolveSize(preferredWidth, widthMeasureSpec);

      setMeasuredDimension(widthSize, heightSize);

      _widthMeasureSpec = widthMeasureSpec;
      _heightMeasureSpec = heightMeasureSpec;
    }
  }

  private int measureChildForWidth(View view, int widthMeasureSpec, int heightMeasureSpec)
  {
    measureChild(view, widthMeasureSpec, heightMeasureSpec);
    return view.getMeasuredWidth();
  }

  public int getMode()
  {
    return _mode;
  }

  public void setText(CharSequence text)
  {
    _textView = new TextView(getContext());
    _textView.setSingleLine();
    _textView.setText(text);
    _textView.setTextSize(18);

    MBViewBuilderFactory.getInstance().getStyleHandler().styleTabSpinnerText(_textView);
  }

  public void setIcon(Drawable drawable)
  {
    _icon = new ImageView(getContext());
    _icon.setImageDrawable(drawable);
    _icon.setId(UniqueIntegerGenerator.getId());

    _layout = new RelativeLayout(getContext());
    _layout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

    RelativeLayout.LayoutParams iconParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    iconParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
    _layout.addView(_icon, iconParams);

    RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    textParams.addRule(RelativeLayout.RIGHT_OF, _icon.getId());
    textParams.addRule(RelativeLayout.CENTER_VERTICAL);
    _layout.addView(_textView, textParams);
  }
}

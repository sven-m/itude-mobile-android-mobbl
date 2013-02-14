package com.itude.mobile.mobbl2.client.core.view.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.widget.RadioButton;

import com.itude.mobile.android.util.ScreenUtil;
import com.itude.mobile.mobbl2.client.core.services.MBResourceService;

public class MBSegmentedItem extends RadioButton
{

  private Paint     _textPaint;
  private String    _text;
  private final int _currentTextColor   = Color.WHITE;
  private float     _textSize           = ScreenUtil.ELEVEN;
  private int       _ascent;
  private String    _defaultBackground  = "button-segmented-normal";
  private String    _selectedBackground = "button-segmented-selected";
  private String     _pressedBackground = "button-segmented-pressed";
 

  public MBSegmentedItem(Context context)
  {
    super(context);
    initView();
  }

  private void initView()
  {
    _textPaint = new Paint();
    _textPaint.setAntiAlias(true);
    _textPaint.setTextSize(_textSize);
    _textPaint.setColor(_currentTextColor);
    _textPaint.setTextAlign(Paint.Align.CENTER);
  }

  @Override
  protected void onDraw(Canvas canvas)
  {
    super.onDraw(canvas);

    if (isChecked())
    {

      Drawable drawable = MBResourceService.getInstance().getImageByID(_selectedBackground);
      if (drawable != null)
      {
        drawable.setBounds(0, 0, this.getWidth(), this.getHeight());
        drawable.draw(canvas);
      }

    }
    else if(isPressed())
    {
      Drawable drawable = MBResourceService.getInstance().getImageByID(_pressedBackground);
      if (drawable != null)
      {
        drawable.setBounds(0, 0, this.getWidth(), this.getHeight());
        drawable.draw(canvas);
      }
    }
    else
    {
      Drawable drawable = MBResourceService.getInstance().getImageByID(_defaultBackground);
      if (drawable != null)
      {
        drawable.setBounds(0, 0, this.getWidth(), this.getHeight());
        drawable.draw(canvas);
      }
    }

    canvas.drawText(_text, getWidth() / 2, getPaddingTop() - _ascent, _textPaint);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
  {
    setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
  }

  /**
   * Determines the width of this view
   * @param measureSpec A measureSpec packed into an int
   * @return The width of the view, honoring constraints from measureSpec
   */
  private int measureWidth(int measureSpec)
  {
    int result = 0;
    int specMode = MeasureSpec.getMode(measureSpec);
    int specSize = MeasureSpec.getSize(measureSpec);

    if (specMode == MeasureSpec.EXACTLY)
    {
      // We were told how big to be
      result = specSize;
    }
    else
    {
      // Measure the text
      result = (int) _textPaint.measureText(_text) + getPaddingLeft() + getPaddingRight();
      if (specMode == MeasureSpec.AT_MOST)
      {
        // Respect AT_MOST value if that was what is called for by measureSpec
        result = Math.min(result, specSize);
      }
    }

    return result;
  }

  /**
   * Determines the height of this view
   * @param measureSpec A measureSpec packed into an int
   * @return The height of the view, honoring constraints from measureSpec
   */
  private int measureHeight(int measureSpec)
  {
    int result = 0;
    int specMode = MeasureSpec.getMode(measureSpec);
    int specSize = MeasureSpec.getSize(measureSpec);

    _ascent = (int) _textPaint.ascent();
    if (specMode == MeasureSpec.EXACTLY)
    {
      // We were told how big to be
      result = specSize;
    }
    else
    {
      // Measure the text (beware: ascent is a negative number)
      result = (int) (-_ascent + _textPaint.descent()) + getPaddingTop() + getPaddingBottom();
      if (specMode == MeasureSpec.AT_MOST)
      {
        // Respect AT_MOST value if that was what is called for by measureSpec
        result = Math.min(result, specSize);
      }
    }
    return result;
  }

  public void setText(String text)
  {
    _text = text;
    requestLayout();
    invalidate();
  }

  @Override
  public String getText()
  {
    return _text;
  }

  @Override
  public void setTextSize(float textSize)
  {
    _textSize = ScreenUtil.convertDimensionPixelsToPixels(textSize);
    _textPaint.setTextSize(_textSize);
    requestLayout();
    invalidate();
  }

  @Override
  public float getTextSize()
  {
    return _textSize;
  }

  public void setDefaultBackground(String defaultBackground)
  {
    _defaultBackground = defaultBackground;
    requestLayout();
    invalidate();
  }

  public void setSelectedBackground(String selectedBackground)
  {
    _selectedBackground = selectedBackground;
    requestLayout();
    invalidate();
  }
  
  public void setPressedBackground(String pressedBackground)
  {
    _pressedBackground = pressedBackground;
    requestLayout();
    invalidate();
  }

}

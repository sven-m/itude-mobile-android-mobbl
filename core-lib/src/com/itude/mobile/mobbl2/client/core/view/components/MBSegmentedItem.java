package com.itude.mobile.mobbl2.client.core.view.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.graphics.drawable.LayerDrawable;
import android.widget.RadioButton;

import com.itude.mobile.mobbl2.client.core.util.MBScreenUtilities;

public class MBSegmentedItem extends RadioButton
{

  private Paint     _textPaint;
  private String    _text;
  private final int _currentTextColor        = Color.WHITE;
  private float     _textSize                = MBScreenUtilities.ELEVEN;
  private int       _ascent;
  private int       _backgroundColor         = Color.parseColor("#515050");
  private int       _selectedBackgroundColor = 0xff111111;

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
      GradientDrawable grad = new GradientDrawable(Orientation.TOP_BOTTOM, new int[]{0xffa5a5a5, _selectedBackgroundColor});
      grad.setBounds(0, 0, this.getWidth(), this.getHeight());
      Drawable drawable = new LayerDrawable(new Drawable[]{new ColorDrawable(_selectedBackgroundColor), grad});
      drawable.draw(canvas);
    }
    else
    {
      GradientDrawable grad = new GradientDrawable(Orientation.TOP_BOTTOM, new int[]{0xffa5a5a5, _backgroundColor});
      grad.setBounds(0, 0, this.getWidth(), this.getHeight());
      Drawable drawable = new LayerDrawable(new Drawable[]{new ColorDrawable(_backgroundColor), grad});
      drawable.draw(canvas);
    }

    canvas.drawText(_text, getWidth() / 2, getPaddingTop() - _ascent, _textPaint);

    Paint paint = new Paint();
    paint.setColor(Color.BLACK);
    paint.setStyle(Style.STROKE);
    paint.setStrokeWidth(1);
    canvas.drawRect(0, 0, this.getWidth() - paint.getStrokeWidth(), this.getHeight() - paint.getStrokeWidth(), paint);
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
    _textSize = MBScreenUtilities.convertDimensionPixelsToPixels(textSize);
    _textPaint.setTextSize(_textSize);
    requestLayout();
    invalidate();
  }

  @Override
  public float getTextSize()
  {
    return _textSize;
  }

  public void setSelectedBackgroundColor(int selectedBackgroundColor)
  {
    _selectedBackgroundColor = selectedBackgroundColor;
    requestLayout();
    invalidate();
  }

  @Override
  public void setBackgroundColor(int backgroundColor)
  {
    _backgroundColor = backgroundColor;
    requestLayout();
    invalidate();
  }
}

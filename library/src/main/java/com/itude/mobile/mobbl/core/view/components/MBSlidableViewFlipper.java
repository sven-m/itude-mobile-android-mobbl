/*
 * (C) Copyright Itude Mobile B.V., The Netherlands
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.itude.mobile.mobbl.core.view.components;

import android.content.Context;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.itude.mobile.mobbl.core.view.listeners.MBViewFlipListener;

public class MBSlidableViewFlipper extends ViewGroup
{
  private final Scroller     _scroller;
  private VelocityTracker    _velocityTracker;

  private int                _scrollX              = 0;
  private int                _currentScreen        = 0;

  private float              _lastMotionX;

  private static final int   SNAP_VELOCITY         = 1000;

  private final static int   TOUCH_STATE_REST      = 0;
  private final static int   TOUCH_STATE_SCROLLING = 1;
  private static final int   INVALID_SCREEN        = -1;

  private int                _touchState           = TOUCH_STATE_REST;

  private int                _touchSlop            = 0;

  private MBViewFlipListener _listener;
  private int                _nextScreen           = INVALID_SCREEN;

  public MBSlidableViewFlipper(Context context)
  {
    super(context);
    _scroller = new Scroller(context);

    _touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

    this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
  }

  public void setViewFlipListener(MBViewFlipListener listener)
  {
    _listener = listener;
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev)
  {
    /*
     * This method JUST determines whether we want to intercept the motion.
     * If we return true, onTouchEvent will be called and we do the actual
     * scrolling there.
     */

    /*
     * Shortcut the most recurring case: the user is in the dragging state
     * and he is moving his finger. We want to intercept this motion.
     */
    final int action = ev.getAction();
    if ((action == MotionEvent.ACTION_MOVE) && (_touchState != TOUCH_STATE_REST))
    {
      return true;
    }

    final float x = ev.getX();

    switch (action)
    {
      case MotionEvent.ACTION_MOVE :
        /*
         * mIsBeingDragged == false, otherwise the shortcut would have caught it. Check
         * whether the user has moved far enough from his original down touch.
         */

        /*
         * Locally do absolute value. mLastMotionX is set to the y value
         * of the down event.
         */
        final int xDiff = (int) Math.abs(x - _lastMotionX);

        boolean xMoved = xDiff > _touchSlop;

        if (xMoved)
        {
          // Scroll if the user moved far enough along the X axis
          _touchState = TOUCH_STATE_SCROLLING;
        }
        break;

      case MotionEvent.ACTION_DOWN :
        // Remember location of down touch
        _lastMotionX = x;

        /*
         * If being flinged and user touches the screen, initiate drag;
         * otherwise don't.  mScroller.isFinished should be false when
         * being flinged.
         */
        _touchState = _scroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
        break;

      case MotionEvent.ACTION_CANCEL :
      case MotionEvent.ACTION_UP :
        // Release the drag
        _touchState = TOUCH_STATE_REST;
        break;
    }

    /*
     * The only time we want to intercept motion events is if we are in the
     * drag mode.
     */
    return _touchState != TOUCH_STATE_REST;
  }

  @Override
  public boolean onTouchEvent(MotionEvent event)
  {

    if (_velocityTracker == null)
    {
      _velocityTracker = VelocityTracker.obtain();
    }
    _velocityTracker.addMovement(event);

    final int action = event.getAction();
    final float x = event.getX();

    switch (action)
    {
      case MotionEvent.ACTION_DOWN :
        /*
         * If being flinged and user touches, stop the fling. isFinished
         * will be false if being flinged.
         */
        if (!_scroller.isFinished())
        {
          _scroller.abortAnimation();
        }

        // Remember where the motion event started
        _lastMotionX = x;
        break;
      case MotionEvent.ACTION_MOVE :
        // Log.i(LOG_TAG,"event : move");
        // if (mTouchState == TOUCH_STATE_SCROLLING) {
        // Scroll to follow the motion event
        final int deltaX = (int) (_lastMotionX - x);
        _lastMotionX = x;

        //Log.i(LOG_TAG, "event : move, deltaX " + deltaX + ", mScrollX " + mScrollX);

        if (deltaX < 0)
        {
          if (_scrollX > 0)
          {
            scrollBy(Math.max(-_scrollX, deltaX), 0);
          }
        }
        else if (deltaX > 0)
        {
          if (getChildCount() > 0)
          {
            final int availableToScroll = getChildAt(getChildCount() - 1).getRight() - _scrollX - getWidth();
            if (availableToScroll > 0)
            {
              scrollBy(Math.min(availableToScroll, deltaX), 0);
            }
          }
        }
        // }
        break;
      case MotionEvent.ACTION_UP :
        // if (mTouchState == TOUCH_STATE_SCROLLING) {
        final VelocityTracker velocityTracker = _velocityTracker;
        velocityTracker.computeCurrentVelocity(1000);
        int velocityX = (int) velocityTracker.getXVelocity();

        if (velocityX > SNAP_VELOCITY && _currentScreen > 0)
        {
          // Fling hard enough to move left
          snapToScreen(_currentScreen - 1);
        }
        else if (velocityX < -SNAP_VELOCITY && _currentScreen < getChildCount() - 1)
        {
          // Fling hard enough to move right
          snapToScreen(_currentScreen + 1);
        }
        else
        {
          snapToDestination();
        }

        if (_velocityTracker != null)
        {
          _velocityTracker.recycle();
          _velocityTracker = null;
        }
        // }
        _touchState = TOUCH_STATE_REST;
        break;
      case MotionEvent.ACTION_CANCEL :
        _touchState = TOUCH_STATE_REST;
    }
    _scrollX = this.getScrollX();

    return true;
  }

  private void snapToDestination()
  {
    final int screenWidth = getWidth();
    final int whichScreen = (_scrollX + (screenWidth / 2)) / screenWidth;
    snapToScreen(whichScreen);
  }

  public void snapToScreen(int whichScreen)
  {
    _nextScreen = whichScreen;
    final int newX = whichScreen * getWidth();
    final int delta = newX - _scrollX;
    _scroller.startScroll(_scrollX, 0, delta, 0, Math.abs(delta) * 2);
    invalidate();
  }

  public void setToScreen(int whichScreen)
  {
    _nextScreen = whichScreen;
    final int newX = whichScreen * getWidth();
    _scroller.startScroll(newX, 0, 0, 0, 10);
    invalidate();
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b)
  {
    int childLeft = 0;

    final int count = getChildCount();
    for (int i = 0; i < count; i++)
    {
      final View child = getChildAt(i);
      if (child.getVisibility() != View.GONE)
      {
        final int childWidth = child.getMeasuredWidth();
        child.layout(childLeft, 0, childLeft + childWidth, child.getMeasuredHeight());
        childLeft += childWidth;
      }
    }

  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
  {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    final int width = MeasureSpec.getSize(widthMeasureSpec);
    final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
    if (widthMode != MeasureSpec.EXACTLY)
    {
      throw new IllegalStateException("error mode.");
    }

    final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    if (heightMode != MeasureSpec.EXACTLY)
    {
      throw new IllegalStateException("error mode.");
    }

    // The children are given the same width and height as the workspace
    final int count = getChildCount();
    for (int i = 0; i < count; i++)
    {
      getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
    }
    scrollTo(_currentScreen * width, 0);
  }

  @Override
  public void computeScroll()
  {
    if (_scroller.computeScrollOffset())
    {
      _scrollX = _scroller.getCurrX();
      scrollTo(_scrollX, 0);
      postInvalidate();
    }
    else if (_nextScreen != INVALID_SCREEN)
    {
      boolean viewFlipped = _nextScreen != _currentScreen;
      _currentScreen = Math.max(0, Math.min(_nextScreen, getChildCount() - 1));
      _nextScreen = INVALID_SCREEN;

      if (viewFlipped && _listener != null) _listener.viewFlipped(getChildAt(_currentScreen));
    }
  }

  public View getCurrentView()
  {
    return getChildAt(_currentScreen);
  }

  public int getDisplayedChild()
  {
    return _currentScreen;
  }
}

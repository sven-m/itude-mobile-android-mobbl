package com.itude.mobile.mobbl.core.view.bindings;

import android.view.View;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;

import com.itude.mobile.mobbl.core.view.MBField;

public class RatingBarBinder extends BaseViewBinder
{

  private final int id;

  protected RatingBarBinder(int id)
  {
    this.id = id;
  }

  public static RatingBarBinder getInstance(int id)
  {
    return new RatingBarBinder(id);
  }

  @Override
  protected View bindSpecificView(BuildState state)
  {
    RatingBar bar = (RatingBar) state.parent.findViewById(id);

    if (bar != null)
    {
      MBField field = (MBField) state.component;
      bar.setRating(Float.parseFloat(field.getValue()));
      bar.setOnRatingBarChangeListener(new RatingBarChangeListener(field));
    }

    return bar;
  }

  private static class RatingBarChangeListener implements OnRatingBarChangeListener
  {
    private final MBField field;

    RatingBarChangeListener(MBField field)
    {
      this.field = field;
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser)
    {
      field.setValue(Float.toString(rating));
    }
  }

}

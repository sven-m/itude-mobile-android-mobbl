package com.itude.mobile.mobbl.core.view.bindings;

import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;

import com.itude.mobile.mobbl.core.view.MBField;

public class RatingBarBinder extends SingleViewBinder<RatingBar, MBField> {

    protected RatingBarBinder(int id) {
        super(id);
    }

    public static RatingBarBinder getInstance(int id) {
        return new RatingBarBinder(id);
    }

    @Override
    protected void bindSingleView(RatingBar view, MBField component) {
        view.setRating(Float.parseFloat(component.getValue()));
        view.setOnRatingBarChangeListener(new RatingBarChangeListener(component));
    }

    private static class RatingBarChangeListener implements OnRatingBarChangeListener {
        private final MBField field;

        RatingBarChangeListener(MBField field) {
            this.field = field;
        }

        @Override
        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            field.setValue(Float.toString(rating));
        }
    }

}

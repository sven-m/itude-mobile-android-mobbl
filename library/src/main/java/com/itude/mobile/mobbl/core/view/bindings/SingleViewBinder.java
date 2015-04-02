package com.itude.mobile.mobbl.core.view.bindings;

import android.view.View;

import com.itude.mobile.mobbl.core.view.MBComponent;

public abstract class SingleViewBinder<ViewType extends View, ComponentType extends MBComponent> extends BaseViewBinder {
    private final int id;

    protected SingleViewBinder(int id) {
        this.id = id;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected View bindSpecificView(BuildState state) {
        ViewType view = (ViewType) state.parent.findViewById(id);

        if (view != null) {
            bindSingleView(view, (ComponentType) state.component);
        }

        return view;
    }

    protected abstract void bindSingleView(ViewType view, ComponentType component);

}

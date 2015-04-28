package com.itude.mobile.mobbl.core.view.bindings;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.itude.mobile.mobbl.core.model.MBElement;
import com.itude.mobile.mobbl.core.model.MBElementContainer;
import com.itude.mobile.mobbl.core.view.MBComponent;

public abstract class BaseViewBinder implements ViewBinder {

    @Override
    public View bindView(BuildState state) {
        View result = bindSpecificView(state);
        if (result != null) {
            state.component.attachView(result);
            bindOutcome(state, result);
        }

        // process children
        for (MBComponent child : state.component.getChildrenOfKind(MBComponent.class)) {
            BuildState newState = state.clone();
            newState.component = child;
            Object element = child.getDocument().getValueForPath(child.getAbsoluteDataPath());
            newState.element = (MBElementContainer) (element instanceof MBElement ? element : null);
            newState.parent = (ViewGroup) (result instanceof ViewGroup ? result : state.parent);
            newState.mainViewBinder.bindView(newState);
        }

        return result;
    }

    protected void bindOutcome(BuildState state, View view) {
        if (state.component != null) {
            final MBComponent component = state.component;
            if (component.getOutcomeName() != null) {
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        component.handleOutcome(component.getOutcomeName(), component.getAbsoluteDataPath());
                    }
                });
            }
        }
    }

    protected abstract View bindSpecificView(BuildState state);

}

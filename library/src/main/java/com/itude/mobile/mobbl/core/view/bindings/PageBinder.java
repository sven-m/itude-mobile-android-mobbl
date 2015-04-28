package com.itude.mobile.mobbl.core.view.bindings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itude.mobile.mobbl.core.controller.util.MBBasicViewController;

import java.util.HashMap;
import java.util.Map;

public class PageBinder extends BaseViewBinder {
    private final BuildState state;
    private final MBBasicViewController controller;
    private final Map<String, ViewBinder> childViewBinders = new HashMap<>();

    public PageBinder(MBBasicViewController controller, ViewGroup rootView) {

        state = new BuildState();
        state.element = controller.getPage().getDocument();
        state.component = controller.getPage();
        state.mainViewBinder = this;
        state.context = controller.getActivity();
        state.inflater = LayoutInflater.from(state.context);
        state.document = controller.getPage().getDocument();
        state.parent = rootView;

        this.controller = controller;
    }

    public void bind() {
        controller.getPage().rebuild();
        bindView(state);
    }

    public void registerBinding(String componentName, ViewBinder viewBinder) {
        childViewBinders.put(componentName, viewBinder);
    }

    @Override
    protected View bindSpecificView(BuildState state) {
        View result = null;
        ViewBinder binder = childViewBinders.get(state.component.getName());
        if (binder != null) {
            result = binder.bindView(state);
        }
        return result;
    }

}

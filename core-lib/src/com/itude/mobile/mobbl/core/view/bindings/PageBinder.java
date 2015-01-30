package com.itude.mobile.mobbl.core.view.bindings;

import java.util.HashMap;
import java.util.Map;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itude.mobile.mobbl.core.controller.util.MBBasicViewController;

public class PageBinder extends BaseViewBinder
{
  private final BuildState              state;
  private final Map<String, ViewBinder> childViewBinders;

  public PageBinder(LayoutInflater inflater, MBBasicViewController controller)
  {
    controller.getPage().rebuild();

    state = new BuildState();
    state.element = controller.getPage().getDocument();
    state.component = controller.getPage();
    state.mainViewBinder = this;
    state.context = controller.getActivity();
    state.inflater = inflater;
    state.document = controller.getPage().getDocument();
    childViewBinders = new HashMap<String, ViewBinder>();
  }

  public ViewGroup bind(int initialView)
  {
    ViewGroup result = (ViewGroup) state.inflater.inflate(initialView, null);
    state.parent = result;

    bindView(state);
    return result;
  }

  public void registerBinding(String componentName, ViewBinder viewBinder)
  {
    childViewBinders.put(componentName, viewBinder);
  }

  @Override
  protected View bindSpecificView(BuildState state)
  {
    View result = null;
    ViewBinder binder = childViewBinders.get(state.component.getName());
    if (binder != null)
    {
      result = binder.bindView(state);
    }
    return result;
  }

}

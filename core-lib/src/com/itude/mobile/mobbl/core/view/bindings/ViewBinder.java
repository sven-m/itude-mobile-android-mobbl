package com.itude.mobile.mobbl.core.view.bindings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itude.mobile.mobbl.core.MBException;
import com.itude.mobile.mobbl.core.model.MBDocument;
import com.itude.mobile.mobbl.core.model.MBElementContainer;
import com.itude.mobile.mobbl.core.view.MBComponent;

public interface ViewBinder
{

  public static class BuildState implements Cloneable
  {
    public ViewGroup          parent;
    public MBElementContainer element;
    public MBComponent        component;
    public ViewBinder         mainViewBinder;
    public Context            context;
    public LayoutInflater     inflater;
    public View               recycledView;
    public MBDocument         document;

    public BuildState clone()
    {
      try
      {
        return (BuildState) super.clone();
      }
      catch (CloneNotSupportedException e)
      {
        throw new MBException("Meuh?", e);
      }
    }
  }

  public View bindView(BuildState state);

}

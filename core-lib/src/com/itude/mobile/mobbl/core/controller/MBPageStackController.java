package com.itude.mobile.mobbl.core.controller;

import android.os.Bundle;

import com.itude.mobile.mobbl.core.controller.util.MBBasicViewController;
import com.itude.mobile.mobbl.core.util.Constants;

public class MBPageStackController
{
  private final int                _id;
  private final String             _name;
  private final String             _mode;
  private final MBDialogController _parent;
  private int                      _fragmentInc = 0;

  public MBPageStackController(MBDialogController parent, int id, String name, String mode)
  {
    _parent = parent;
    _id = id;
    _name = name;
    _mode = mode;
  }

  public int getId()
  {
    return _id;
  }

  public String getName()
  {
    return _name;
  }

  public String getMode()
  {
    return _mode;
  }

  public MBDialogController getParent()
  {
    return _parent;
  }

  void showPage(MBDialogController.ShowPageEntry entry)
  {
    if ("POP".equals(entry.getDisplayMode()))
    {
      getParent().popView();
    }
    else if ((Constants.C_DISPLAY_MODE_REPLACE.equals(entry.getDisplayMode()) //
             || Constants.C_DISPLAY_MODE_BACKGROUNDPIPELINEREPLACE.equals(entry.getDisplayMode()))
             || ("SINGLE".equals(getMode())))
    {
      entry.setAddToBackStack(false);
    }
    else if ("REPLACEDIALOG".equals(entry.getDisplayMode()) && !getParent().getFragmentStack().isBackStackEmpty())
    {
      getParent().getFragmentStack().emptyBackStack(false);
    }

    MBBasicViewController fragment = MBApplicationFactory.getInstance().createFragment(entry.getPage().getPageName());
    fragment.setPage(entry.getPage());
    fragment.setDialogController(getParent());
    Bundle args = new Bundle();
    args.putString("id", entry.getId());
    fragment.setArguments(args);

    getParent().getDecorator().presentFragment(fragment, getId(), entry.getId() + (_fragmentInc++), entry.isAddToBackStack());
  }

}
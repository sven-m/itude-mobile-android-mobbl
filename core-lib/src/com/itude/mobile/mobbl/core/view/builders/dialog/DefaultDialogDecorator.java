package com.itude.mobile.mobbl.core.view.builders.dialog;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.itude.mobile.mobbl.core.controller.MBDialogController;
import com.itude.mobile.mobbl.core.controller.MBViewManager;
import com.itude.mobile.mobbl.core.view.builders.MBDialogDecorator;

public class DefaultDialogDecorator extends MBDialogDecorator
{

  public DefaultDialogDecorator(MBDialogController dialog)
  {
    super(dialog);
  }

  @Override
  public void show()
  {
    MBViewManager.getInstance().setContentView(getDialog().getMainContainer());
    MBViewManager.getInstance().setTitle(getDialog().getTitle());
  }

  @Override
  public void presentFragment(Fragment fragment, int containerId, String name, boolean addToBackStack)
  {

    FragmentManager manager = MBViewManager.getInstance().getSupportFragmentManager();
    FragmentTransaction transaction = manager.beginTransaction();
    if (addToBackStack)
    {
      transaction.addToBackStack(name);
    }
    else
    {
      if (manager.getBackStackEntryCount() != 0)
      {
        manager.popBackStack();
        transaction.addToBackStack(name);
      }
    }
    transaction.replace(containerId, fragment, name);

    transaction.commitAllowingStateLoss();

  }

}

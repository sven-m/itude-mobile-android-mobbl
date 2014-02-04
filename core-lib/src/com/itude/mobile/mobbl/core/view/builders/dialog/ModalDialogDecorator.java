package com.itude.mobile.mobbl.core.view.builders.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.itude.mobile.mobbl.core.configuration.mvc.MBDialogDefinition;
import com.itude.mobile.mobbl.core.controller.MBDialogController;
import com.itude.mobile.mobbl.core.controller.MBViewManager;
import com.itude.mobile.mobbl.core.services.MBMetadataService;
import com.itude.mobile.mobbl.core.view.builders.MBDialogDecorator;

public class ModalDialogDecorator extends MBDialogDecorator
{

  private boolean _fullscreen;
  private boolean _cancelable;
  private boolean _closable;
  private String  _previousDialog;
  private boolean _shown;

  public ModalDialogDecorator(MBDialogController dialog)
  {
    super(dialog);
  }

  @Override
  public void show()
  {
    if (!_shown)
    {
      _previousDialog = MBViewManager.getInstance().getActiveDialogName();
      Log.d("ModalDialogDecorator", "Grabbed previous " + _previousDialog);
      MBDialogDefinition dialogDefinition = MBMetadataService.getInstance().getDefinitionForDialogName(getDialog().getName());
      _fullscreen = Boolean.parseBoolean(dialogDefinition.getCustom().get("fullscreen"));
      _cancelable = dialogDefinition.getCustom().containsKey("cancelable") ? Boolean.parseBoolean(dialogDefinition.getCustom()
          .get("cancelable")) : true;
      _closable = Boolean.parseBoolean(dialogDefinition.getCustom().get("closable"));
      _shown = true;
    }
    else Log.d("ModalDialogDecorator", "Already grabbed previous " + _previousDialog);

  }

  @Override
  public void hide()
  {
    _shown = false;
    if (_previousDialog != null)
    {
      Log.d("ModalDialogDecorator", "Enqueueing " + _previousDialog);
      MBViewManager.getInstance().getDialogManager().enqueueDialog(_previousDialog);
    }
  }

  @Override
  public void presentFragment(final Fragment fragment, int containerId, String name, boolean addToBackStack)
  {
    FragmentManager manager = MBViewManager.getInstance().getSupportFragmentManager();
    FragmentTransaction transaction = manager.beginTransaction();

    Bundle args = fragment.getArguments();
    if (addToBackStack)
    {
      transaction.addToBackStack(name);
    }

    if (_fullscreen)
    {
      args.putBoolean("fullscreen", true);
      fragment.setArguments(args);
    }

    if (_cancelable)
    {
      args.putBoolean("cancelable", true);
      fragment.setArguments(args);
    }

    if (_closable)
    {
      args.putBoolean("closable", true);
      fragment.setArguments(args);
    }

    ((DialogFragment) fragment).show(transaction, name);

  }

  @Override
  public void emptiedBackStack()
  {
    if (_shown)
    {
      _shown = false;
      getDialog().dismiss();
    }

  }

  @Override
  public boolean handlesOwnDismiss()
  {
    return true;
  }

  @Override
  public boolean maintainPreviousStack()
  {
    return true;
  }
}

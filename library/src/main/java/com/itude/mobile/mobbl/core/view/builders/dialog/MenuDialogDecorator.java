package com.itude.mobile.mobbl.core.view.builders.dialog;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.itude.mobile.mobbl.core.controller.MBDialogController;
import com.itude.mobile.mobbl.core.controller.MBViewManager;
import com.itude.mobile.mobbl.core.view.builders.MBDialogDecorator;

public class MenuDialogDecorator extends MBDialogDecorator {

    public MenuDialogDecorator(MBDialogController dialog) {
        super(dialog);
        if (_controller != null) Log
                .w(this.getClass().getSimpleName(), "Trying to load multiple dialogs with menu; this is not supported yet!");
        _controller = dialog;
    }

    private static MBDialogController _controller;

    public static MBDialogController getMenuDialog() {
        return _controller;
    }

    @Override
    public void presentFragment(Fragment fragment, int containerId, String name, boolean addToBackStack) {
        FragmentManager manager = MBViewManager.getInstance().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(containerId, fragment, name);

        transaction.commitAllowingStateLoss();
    }

}

package com.itude.mobile.mobbl.core.controller;

import android.os.Message;

public class MockApplicationController extends MBApplicationController {

    public MockApplicationController(){
        super();
        _viewManager = new MockViewManager();
        _viewManager.onCreate(null);
    }

}

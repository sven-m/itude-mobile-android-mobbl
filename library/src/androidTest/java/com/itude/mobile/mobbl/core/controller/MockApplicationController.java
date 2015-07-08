package com.itude.mobile.mobbl.core.controller;

import android.os.Message;

public class MockApplicationController extends MBApplicationController {

    public boolean outcomeHandlerStarted, initialOutcomesFired;

    public MockApplicationController(){
        super();
        _viewManager = new MockViewManager();
        _viewManager.onCreate(null);
    }

    @Override
    public void startOutcomeHandler() {
        outcomeHandlerStarted = true;
    }

    @Override
    public void fireInitialOutcomes() {
        initialOutcomesFired = true;
    }

}

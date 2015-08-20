package com.itude.mobile.mobbl.core.controller;

public class MockViewManager extends MBViewManager {

    private String mock;

    public String validate() {
        return mock;
    }

    @Override
    public void prepareForApplicationStart() {

    }

    @Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        mock = "Test";
        _instance = new MockViewManager();
    }
}

package com.itude.mobile.mobbl.core.controller;

import android.test.ApplicationTestCase;
import android.view.ViewManager;

import com.itude.mobile.mobbl.core.MBApplicationCore;
import com.itude.mobile.mobbl.core.services.MBMetadataService;

import junit.framework.TestCase;

public class MBApplicationControllerTest extends ApplicationTestCase<MBApplicationCore> {

    private MBApplicationController app;

    public MBApplicationControllerTest()
    {
        super(MBApplicationCore.class);
    }

    @Override
    public void setUp() throws Exception {
        app = MBApplicationController.getInstance();
    }

    public void testGetInstance() throws Exception {
        assertNotNull(app);
    }

//    public void testGetViewManager() throws Exception {
//        MBViewManager viewManager = this.app.getViewManager();
//        assertNotNull(viewManager);
//    }

    public void testCurrentInstance() throws Exception {
        MBApplicationController curInstance = this.app.currentInstance();
        assertEquals(app, curInstance);
    }
}
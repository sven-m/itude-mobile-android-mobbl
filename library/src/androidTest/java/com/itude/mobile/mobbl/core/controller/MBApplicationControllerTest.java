package com.itude.mobile.mobbl.core.controller;
import android.test.ApplicationTestCase;

public class MBApplicationControllerTest extends ApplicationTestCase<MBApplicationController> {

    private MBApplicationController app;

    public MBApplicationControllerTest()
    {
        super(MBApplicationController.class);
    }

    /**
     * Get the application instance
     * @throws Exception
     */
    @Override
    public void setUp() throws Exception {
        app = MBApplicationController.getInstance();
    }

    /**
     * Test if the app is not null
     * @throws Exception
     */
    public void testGetInstance() throws Exception {
        assertNotNull(app);
    }

    /**
     * Test if getViewManager returns the correct instance
     * @throws Exception
     */
    public void testGetViewManager() throws Exception {
        MBViewManager viewManager = this.app.getViewManager();
        assertEquals(viewManager, MBViewManager.getInstance());
    }

    /**
     * Test if currentinstance equals app
     * @throws Exception
     */
    public void testCurrentInstance() throws Exception {
        MBApplicationController curInstance = this.app.currentInstance();
        assertEquals(app, curInstance);
    }

    /**
     * If this test fails, something is really wrong
     */
    public void testShouldHandleOutcome()
    {
        boolean result = app.shouldHandleOutcome(new MBOutcome());
        assertTrue(result);
    }
}


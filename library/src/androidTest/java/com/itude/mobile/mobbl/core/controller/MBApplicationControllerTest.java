package com.itude.mobile.mobbl.core.controller;
import android.test.ApplicationTestCase;

public class MBApplicationControllerTest extends ApplicationTestCase<MockApplicationController> {

    private MBApplicationController app;

    public MBApplicationControllerTest()
    {
        super(MockApplicationController.class);
    }

    /**
     * Get the application instance
     * @throws Exception
     */
    @Override
    public void setUp() throws Exception {
        createApplication();
        app = this.getApplication();
    }

    /**
     * Test if the app is not null
     * @throws Exception
     */
    public void testGetInstance() throws Exception {
        assertNotNull(app);
        assertEquals(app, MBApplicationController.getInstance());
    }

    /**
     * Test if getViewManager returns a ViewManager
     */
    public void testGetViewManager() {
        MBViewManager viewManager = this.app.getViewManager();
        try {
            String test = ((MockViewManager) viewManager).validate();
            assertEquals(test, "Test");
        }
        catch(Exception e) {
            fail();
        }
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


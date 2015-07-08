package com.itude.mobile.mobbl.core.controller;
import android.test.ApplicationTestCase;

import com.itude.mobile.mobbl.core.configuration.mvc.MBDocumentDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBPageDefinition;
import com.itude.mobile.mobbl.core.model.MBDocument;
import com.itude.mobile.mobbl.core.services.MBDataManagerService;
import com.itude.mobile.mobbl.core.view.MBPage;

public class MBApplicationControllerTest extends ApplicationTestCase<MockApplicationController> {

    private MockApplicationController app;

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
     * Test that checks if the application starts correctly.
     */
    public void testStartApplication() {
        app.startController();
        assertTrue(app.outcomeHandlerStarted && app.initialOutcomesFired);
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

    public void testPageBuildResult() {
        MBOutcome testOutcome = new MBOutcome();
        MBPage testPage = new MBPage(new MBPageDefinition(),new MBDocument(new MBDocumentDefinition(), MBDataManagerService.getInstance()),"");
        MBApplicationController.PageBuildResult result = new MBApplicationController.PageBuildResult(testOutcome,testPage,false);
        assertEquals(result.outcome, testOutcome);
        assertEquals(result.page, testPage);
        assertEquals(result.backstackEnabled, false);
    }


}


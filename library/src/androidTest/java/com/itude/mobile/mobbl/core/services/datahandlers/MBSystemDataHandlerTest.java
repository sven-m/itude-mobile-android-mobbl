package com.itude.mobile.mobbl.core.services.datahandlers;

import android.test.ApplicationTestCase;

import com.itude.mobile.android.util.FileUtil;
import com.itude.mobile.mobbl.core.MBApplicationCore;
import com.itude.mobile.mobbl.core.configuration.mvc.MBDocumentDefinition;
import com.itude.mobile.mobbl.core.model.MBDocument;
import com.itude.mobile.mobbl.core.services.MBDataManagerService;
import com.itude.mobile.mobbl.core.services.MBMetadataService;
import com.itude.mobile.mobbl.core.services.datamanager.handlers.MBSystemDataHandler;

/**
 * Created by Kevin on 6/25/15.
 */
public class MBSystemDataHandlerTest extends ApplicationTestCase<MBApplicationCore> {

    MBSystemDataHandler handler;

    public MBSystemDataHandlerTest() {
        super(MBApplicationCore.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        createApplication();
        MBMetadataService.setConfigName("config.xml");
        FileUtil.getInstance().setContext(getContext());

        handler = new MBSystemDataHandler();
    }

    /**
     * Test if changing the property filename works.
     */
    public void testGetAndSetFilename() {
        handler.setFileName("test.xml");
        assertEquals(handler.getFileName(), "test.xml");
    }

    /**
     * Test for creating a new SystemDataHandler with custom property-path.
     */
    public void testConstructor() {
        handler = new MBSystemDataHandler("applicationproperties.xml");
        assertNotNull(handler);
        assertEquals(handler.getFileName(), "applicationproperties.xml");
    }

    /**
     * Test for Storing and loading a new document.
     */
    public void testStoreAndLoadDocument() {
        MBDocument doc = new MBDocument(new MBDocumentDefinition(), MBDataManagerService.getInstance());
        doc.getDefinition().setName("testDoc");

        handler.storeDocument(doc);

        MBDocument doc2 = handler.loadDocument(doc.getName());

        assertEquals(doc,doc2);
    }

    /**
     * This test is for loading a document that isn't in the cache.
     * For some reason this doesn't seem to work.
     */
    public void testLoadDocument() {
        MBDocument doc = handler.loadDocument("Books");
        assertNull(doc);
    }

}

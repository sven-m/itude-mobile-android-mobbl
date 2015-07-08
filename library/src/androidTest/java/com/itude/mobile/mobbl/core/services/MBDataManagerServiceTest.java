package com.itude.mobile.mobbl.core.services;

import android.test.ApplicationTestCase;

import com.itude.mobile.android.util.FileUtil;
import com.itude.mobile.mobbl.core.MBApplicationCore;
import com.itude.mobile.mobbl.core.model.MBDocument;

/**
 * Created by Kevin on 6/18/15.
 */
public class MBDataManagerServiceTest extends ApplicationTestCase<MBApplicationCore> {

    private MBDataManagerService dataManagerService;

    public MBDataManagerServiceTest() {
        super(MBApplicationCore.class);
    }

    @Override
    public void setUp() throws Exception{
        super.setUp();
        createApplication();
        MBMetadataService.setConfigName("config.xml");
        FileUtil.getInstance().setContext(getContext());
        dataManagerService = MBDataManagerService.getInstance();
    }

    /**
     * Test getting the instance.
     */
    public void testGetInstance() {
        assertEquals(MBDataManagerService.getInstance(), dataManagerService);
    }

    /**
     * Test creating a new document
     */
    public void testCreateDocument() {
        MBDocument doc = dataManagerService.createDocument("TestDocument1");
        assertNotNull(doc);
        assertEquals(doc.getName(), "TestDocument1");
    }

    /**
     * Test loading a document.
     */
    public void testLoadDocument() {
        MBDocument doc = dataManagerService.loadDocument("Books");
        assertEquals(doc.getName(), "Books");
    }

    /**
     * Test loading a fresh copy of a document.
     */
    public void testLoadFreshDocument() {
        MBDocument doc = dataManagerService.loadDocument("Books");
        MBDocument cloneDoc = dataManagerService.loadFreshDocument("Books");

        assertNotSame(doc, cloneDoc);
        assertEquals(doc.getName(), cloneDoc.getName());
    }


}

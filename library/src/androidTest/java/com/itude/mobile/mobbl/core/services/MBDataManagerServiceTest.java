package com.itude.mobile.mobbl.core.services;

import android.test.ApplicationTestCase;

import com.itude.mobile.android.util.FileUtil;
import com.itude.mobile.mobbl.core.MBApplicationCore;
import com.itude.mobile.mobbl.core.configuration.mvc.MBDocumentDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBElementDefinition;
import com.itude.mobile.mobbl.core.model.MBDocument;
import com.itude.mobile.mobbl.core.model.MBElement;
import com.itude.mobile.mobbl.core.services.datamanager.MBDataHandler;

import java.util.List;

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
        MBMetadataService.setConfigName("config/config.xml");
        FileUtil.getInstance().setContext(getContext());
        dataManagerService = MBDataManagerService.getInstance();
    }

    public void testGetInstance() {
        assertEquals(MBDataManagerService.getInstance(), dataManagerService);
    }

    public void testCreateDocument() {
        MBDocument doc = dataManagerService.createDocument("TestDocument1");
        assertNotNull(doc);
    }

    public void testLoadDocument() {
        MBDocument doc = dataManagerService.loadDocument("Books");
        assertEquals(doc.getName(), "Books");
    }

    public void testLoadFreshDocument() {
        MBDocument doc = dataManagerService.loadDocument("Books");
        MBDocument cloneDoc = dataManagerService.loadFreshDocument("Books");

        assertNotSame(doc, cloneDoc);
        assertEquals(doc.getName(), cloneDoc.getName());
    }


}

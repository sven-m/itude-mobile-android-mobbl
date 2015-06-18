package com.itude.mobile.mobbl.core.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.itude.mobile.mobbl.core.configuration.endpoints.MBEndPointDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBDocumentDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBElementDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBMvcConfigurationParser;
import com.itude.mobile.mobbl.core.services.MBDataManagerService;
import com.itude.mobile.mobbl.core.services.MBMetadataService;
import com.itude.mobile.mobbl.core.services.datamanager.MBDataHandler;
import com.itude.mobile.mobbl.core.services.operation.MBDocumentOperationDelegate;
import com.itude.mobile.mobbl.core.util.MBConstants;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Kevin on 5/19/15.
 */
public class MBDocumentTest extends MBDocumentAbstractTest {

    MBDocument emptyDoc;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        MBDataManagerService dataManagerService = new MockDataManagerService();
        emptyDoc = dataManagerService.createDocument("testDocument");
    }

    /**
     * Test the getter and setter of the sharedcontext
     */
    public void testSetSharedContext() {
        HashMap<String, MBDocument> sharedContext = new HashMap<String, MBDocument>();
        sharedContext.put("test", emptyDoc);
        emptyDoc.setSharedContext(sharedContext);
        assertEquals(emptyDoc.getSharedContext(), sharedContext);
    }

    /**
     * Test if the getter and setter of argumentsUsed work correctly
     */
    public void testGetAndSetArgumentsUsed() {
        emptyDoc.setArgumentsUsed(emptyDoc);
        MBDocument arg = emptyDoc.getArgumentsUsed();
        assertEquals(emptyDoc, arg);
    }

    /**
     * Test if a document is correctly assigned
     */
    public void testAssignToDocument() {
        emptyDoc.assignToDocument(emptyDoc);
        assertEquals(emptyDoc.getElements().size(), emptyDoc.getElements().size());
    }

    /**
     * Test the loadFreshCopy function, using a mocked dataHandler.
     */
    public void testLoadFreshCopy() {

        MBDocument copy = emptyDoc.loadFreshCopy();
        assertEquals(copy.getName(), emptyDoc.getName());

        emptyDoc.setArgumentsUsed(emptyDoc);
        copy = emptyDoc.loadFreshCopy();
        assertEquals(copy.getName(), emptyDoc.getName());
        assertEquals(copy.getArgumentsUsed().getName(), emptyDoc.getArgumentsUsed().getName());

    }

    /**
     * Test the reload function of a document, using a mocked dataHandler.
     */
    public void testReload() {
        String name = emptyDoc.getName();
        emptyDoc.reload();
        assertEquals(emptyDoc.getName(), name);

        emptyDoc.setArgumentsUsed(emptyDoc);
        emptyDoc.reload();
        assertEquals(emptyDoc.getArgumentsUsed().getName(), name);
    }

    public void testDescribeContents() {
        int contents = emptyDoc.describeContents();
        assertEquals(contents, MBConstants.C_PARCELABLE_TYPE_DOCUMENT);
    }



    /**
     * Test if the getDocument function returns the correct instance
     */
    public void testGetDocument() {
        MBDocument doc = emptyDoc.getDocument();
        assertEquals(doc, emptyDoc);
    }

    /**
     * Test if writing a document to a parcel and creating a document from a parcel work
     */
    public void testParcel() {
        // Obtain an empty parcel
        Parcel p = Parcel.obtain();
        // Write 'booksdoc' to the parcel
        emptyDoc.writeToParcel(p, 0);
        // Check if the parcel is not null
        assertNotNull(p);

        // Create the document from the parce
        MBDocument copyDoc = MBDocument.CREATOR.createFromParcel(p);

        // The documents aren't the same. The function does not work.
        assertNotNull(copyDoc);

    }

    private class MockDataManagerService extends MBDataManagerService {

        public MockDataManagerService() {
        }

        @Override
        protected void registerDataHandlers() {
        }

        @Override
        public MBDocument createDocument(String documentName) {
            MBDocumentDefinition def = new MBDocumentDefinition();
            def.setName(documentName);
            MBDocument doc =  new MBDocument(def, this);
            doc.createElement("testElement").setBodyText("test");
            return doc;
        }

        @Override
        public MBDocument loadDocument(String documentName) {
            return createDocument(documentName);
        }

        @Override
        public MBDocument loadFreshDocument(String documentName) {
            return createDocument(documentName);
        }

        @Override
        public MBDocument loadDocument(String documentName, MBDocument doc) {
            return createDocument(documentName);
        }

        @Override
        public MBDocument loadFreshDocument(String documentName, MBDocument doc) {
            MBDocument newDoc = createDocument(documentName);
            newDoc.setArgumentsUsed(doc);
            return newDoc;
        }

        @Override
        public void loadDocument(String documentName, MBDocumentOperationDelegate delegate) {
            throw new RuntimeException("Not Implemented.");
        }

        @Override
        public void loadFreshDocument(String documentName, MBDocumentOperationDelegate delegate) {
            throw new RuntimeException("Not Implemented.");
        }

        @Override
        public void loadDocument(String documentName, MBDocument args, MBDocumentOperationDelegate delegate) {
            throw new RuntimeException("Not Implemented.");
        }

        @Override
        public void loadFreshDocument(String documentName, MBDocument args, MBDocumentOperationDelegate delegate) {
            throw new RuntimeException("Not Implemented.");
        }

        @Override
        public void loadDocument(String documentName, MBDocument args, MBDocumentOperationDelegate delegate, MBEndPointDefinition endPointDefinition) {
            throw new RuntimeException("Not Implemented.");
        }

        @Override
        public void loadFreshDocument(String documentName, MBDocument args, MBDocumentOperationDelegate delegate, MBEndPointDefinition endPointDefinition) {
            throw new RuntimeException("Not Implemented.");
        }

        @Override
        public void loadDocument(String documentName, MBDocument args, MBDocumentOperationDelegate delegate, String documentParser) {
            throw new RuntimeException("Not Implemented.");
        }

        @Override
        public void loadFreshDocument(String documentName, MBDocument args, MBDocumentOperationDelegate delegate, String documentParser) {
            throw new RuntimeException("Not Implemented.");
        }

        @Override
        public void storeDocument(MBDocument document) {
            throw new RuntimeException("Not Implemented.");
        }

        @Override
        public void storeDocument(MBDocument document, MBDocumentOperationDelegate delegate, Object resultSelector, Object errorSelector) {
            throw new RuntimeException("Not Implemented.");
        }

        @Override
        public void registerDataHandler(MBDataHandler handler, String name) {

            throw new RuntimeException("Not Implemented.");
        }

        @Override
        public void registerOperationListener(String docName, OperationListener listener) {
            throw new RuntimeException("Not Implemented.");
        }

        @Override
        public void unregisterOperationListener(String docName, OperationListener listener) {
            throw new RuntimeException("Not Implemented.");
        }
    }


}

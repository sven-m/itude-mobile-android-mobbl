package com.itude.mobile.mobbl.core.services.datahandlers;

import android.test.ApplicationTestCase;

import com.itude.mobile.mobbl.core.model.MBDocument;
import com.itude.mobile.mobbl.core.services.datamanager.handlers.MBMetadataDataHandler;
import com.itude.mobile.mobbl.core.MBApplicationCore;

/**
 * Created by Kevin on 7/2/15.
 */
public class MBMetaDataHandlerTest extends ApplicationTestCase<MBApplicationCore> {

    MBMetadataDataHandler handler;

    public MBMetaDataHandlerTest() {
        super(MBApplicationCore.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        handler = new MBMetadataDataHandler();
    }

    /**
     * Test the loadDocument function of this class.
     * Anything other than DIALOGS_DOCUMENT should throw an Exception.
     */
    public void testLoadDocument() {
        try {
            handler.loadDocument("Books");
            fail();
        }
        catch (Exception e) {
            assertEquals(e.getClass(), IllegalArgumentException.class);
        }

        try {
            handler.loadFreshDocument("Books");
            fail();
        }
        catch (Exception e) {
            assertEquals(e.getClass(), IllegalArgumentException.class);
        }
    }

    /**
     * Test the load dialogs function of this class.
     */
    public void testLoadDialogs() {
        MBDocument dialogs = handler.loadDocument(handler.DIALOGS_DOCUMENT);
        assertNotNull(dialogs);
        assertEquals(dialogs.getName(), handler.DIALOGS_DOCUMENT);

        MBDocument dialogs2 = handler.loadFreshDocument(handler.DIALOGS_DOCUMENT);
        assertNotNull(dialogs2);
        assertEquals(dialogs2.getName(), dialogs.getName());
        assertNotSame(dialogs, dialogs2);
    }

}

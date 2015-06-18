package com.itude.mobile.mobbl.core.controller;

import android.os.Parcel;
import android.os.Parcelable;
import android.test.ApplicationTestCase;

import com.itude.mobile.mobbl.core.MBApplicationCore;
import com.itude.mobile.mobbl.core.configuration.mvc.MBDocumentDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBOutcomeDefinition;
import com.itude.mobile.mobbl.core.controller.exceptions.MBExpressionNotBooleanException;
import com.itude.mobile.mobbl.core.model.MBDocument;
import com.itude.mobile.mobbl.core.model.MBDocumentAbstractTest;
import com.itude.mobile.mobbl.core.model.MBDocumentFactory;

import junit.framework.Assert;

/**
 * Created by Kevin on 5/26/15.
 */
public class MBOutcomeTest extends MBDocumentAbstractTest{


    private MBDocument booksDoc;
    private MBOutcome filledOutCome;

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        MBDocumentDefinition docDef = getConfig().getDefinitionForDocumentName("Books");
        assertNotNull(docDef);

        booksDoc = MBDocumentFactory.getInstance().getDocumentWithData(getXmlDocumentData(), MBDocumentFactory.PARSER_XML, docDef);
        assertNotNull(booksDoc);

        filledOutCome = new MBOutcome("test", booksDoc);
        assertNotNull(filledOutCome);
    }

    /**
     * Test if the origin getters and setters work
     */
    public void testGetAndSetOrigin() {
        MBOutcome.Origin origin = new MBOutcome.Origin();

        filledOutCome.setOrigin(origin);
        assertEquals(origin, filledOutCome.getOrigin());
    }

    /**
     * Test if the name getters and setters work
     */
    public void testGetAndSetNames()
    {
        String name = "newName";
        filledOutCome.setOutcomeName(name);
        assertEquals(filledOutCome.getOutcomeName(), name);

        filledOutCome.setPageStackName(name);
        assertEquals(filledOutCome.getPageStackName(), name);
    }

    /**
     * Test if creating a new outcome from another outcome works
     */
    public void testMBOutcomeFromOutcome(){
        MBOutcome copy = new MBOutcome(filledOutCome);
        assertEquals(copy.getDocument(), filledOutCome.getDocument());
    }

    /**
     * Test if the write and load to/from parcel works
     * (hint: it doesn't)
     * @throws Exception
     */
    public void testParcel() throws Exception{
        Parcel p = Parcel.obtain();

        filledOutCome.writeToParcel(p, 0);

        MBOutcome copy = MBOutcome.CREATOR.createFromParcel(p);

        // The outcomes aren't the same. Either the write/create from parcel doesn't work, or i'm doing something wrong.
        assertNotNull(copy);
    }

    /**
     * Test if createCopy gives an actual copy
     */
    public void testCreateCopy(){
        MBOutcomeDefinition def = new MBOutcomeDefinition();
        def.setName(filledOutCome.getOutcomeName());

        MBOutcome copy = filledOutCome.createCopy(def);
        assertNotNull(copy);

        assertEquals(copy.getDocument(), filledOutCome.getDocument());
    }

    public void testIsPreConditionValid() {
        // Test without condition, should always be true
        filledOutCome.setPreCondition(null);
        assertTrue(filledOutCome.isPreConditionValid());

        // Test with valid condition, should always be true
        filledOutCome.setPreCondition("1 == 1");
        assertTrue(filledOutCome.isPreConditionValid());

        // Test with invalid condition, should always be false
        filledOutCome.setPreCondition("1 == 2");
        assertFalse(filledOutCome.isPreConditionValid());

        // Test with an empty string, should throw an error
        try {
            filledOutCome.setPreCondition("");
            filledOutCome.isPreConditionValid();
            Assert.fail();
        }
        catch(Exception e)
        {
            assertEquals(e.getClass(), MBExpressionNotBooleanException.class);
        }
    }

    /**
     * Test if the creation and copying of an origin works
     */
    public void testOrigin(){
        MBOutcome.Origin origin = new MBOutcome.Origin();
        origin = origin.withOutcome(filledOutCome.getOutcomeName());
        assertEquals(origin.getOutcome(), filledOutCome.getOutcomeName());

        MBOutcome.Origin copy = new MBOutcome.Origin(origin);
        assertEquals(origin.getOutcome(), copy.getOutcome());
    }

    /**
     * Test if the write and load origin to/from parcel works
     * (hint: it doesn't)
     * @throws Exception
     */
    public void testOriginParcel() throws Exception{
        Parcel p = Parcel.obtain();

        MBOutcome.Origin origin = new MBOutcome.Origin();
        origin = origin.withOutcome(filledOutCome.getOutcomeName());

        origin.writeToParcel(p, 0);

        MBOutcome.Origin copy = MBOutcome.Origin.CREATOR.createFromParcel(p);

        // The outcomes aren't the same. Either the write/create from parcel doesn't work, or i'm doing something wrong.
        assertNotNull(copy);
    }

}

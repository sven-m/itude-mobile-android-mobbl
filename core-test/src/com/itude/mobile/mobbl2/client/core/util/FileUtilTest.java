package com.itude.mobile.mobbl2.client.core.util;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import android.test.ApplicationTestCase;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.util.exceptions.MBDataParsingException;

public class FileUtilTest extends ApplicationTestCase<MBApplicationController>
{

  public FileUtilTest()
  {
    super(MBApplicationController.class);
  }

  @Override
  protected void setUp() throws Exception
  {
    createApplication();

    super.setUp();
  }

  public void testAssetFileReading()
  {
    byte[] data = AssetUtil.getInstance().getByteArray("testconfig/testconfig.xml");

    assertNotNull(data);
    assertTrue(data.length > 0);
  }

  public void testDirectoryStorage()
  {
    String filePath = "tests/files/FileUtilTest.xml";
    String fileContents = "Hello File " + (new Date()).getTime();

    boolean success = FileUtil.getInstance().writeToFile(fileContents.getBytes(), filePath, null);
    assertTrue(success);

    byte[] data = FileUtil.getInstance().getByteArray(filePath);

    assertNotNull(data);
    assertEquals(new String(data), fileContents);
  }

  public void testFileStorage()
  {
    String filePath = "FileUtilTest.xml";
    String fileContents = "Hello File " + (new Date()).getTime();

    boolean success = FileUtil.getInstance().writeToFile(fileContents.getBytes(), filePath, null);
    assertTrue(success);

    byte[] data = FileUtil.getInstance().getByteArray(filePath);

    assertNotNull(data);
    assertEquals(new String(data), fileContents);
  }
  
  public void testObjectStorage()
  {
    String filePath = "/tests/files/ObjectTest.xml";
    Properties p = new Properties();
    p.put("key", "value");

    boolean success = FileUtil.getInstance().writeObjectToFile(p, filePath);
    assertTrue("Could not write object to file", success);

    Object o = FileUtil.getInstance().readObjectFromFile(filePath);
    assertNotNull(o);

    assertTrue("Deserialization failure", o instanceof Properties);

    Properties result = (Properties)o;
    assertEquals("value", result.get("key"));
  }
  
  public void testRemove()
  {
    //first create a file
    String filePath = "testRemove.file";
    String content = "To be removed";
    boolean success = FileUtil.getInstance().writeToFile(content.getBytes(), filePath, null);
    assertTrue(success);
    
    byte[] result = FileUtil.getInstance().getByteArray(filePath);
    assertTrue(result != null && result.length > 0);
    
    //then remove it
    FileUtil.getInstance().remove(filePath);
    result = null;
    
    try
    {
      result = FileUtil.getInstance().getByteArray(filePath);
      fail("Content of file could be read, thus it has not been removed");
    }
    catch (MBDataParsingException e)
    {
      //expected behavior.
    }
    assertNull(result);
  }
  
  @Override
  protected void tearDown() throws Exception
  {
    File objectToFileTest = new File("/tests/files/ObjectToFileTest.xml");
    if (objectToFileTest.exists()) objectToFileTest.delete();
    File fileToObjectTest = new File("/tests/files/FileToObjectTest.xml");
    if (fileToObjectTest.exists()) fileToObjectTest.delete();
  }

}

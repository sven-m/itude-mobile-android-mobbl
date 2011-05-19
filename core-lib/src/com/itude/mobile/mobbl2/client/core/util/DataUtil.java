package com.itude.mobile.mobbl2.client.core.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.apache.commons.codec.binary.Base64;

import android.content.Context;
import android.util.Log;

public class DataUtil
{

  private static DataUtil _instance;
  private Context         _context;

  private Map<String, Reader> _filenameToReader = new HashMap<String, Reader>();
  private Reader _readerNone = new ReadFromNone();
  private ReadFromAll _readerAll = new ReadFromAll();

  
  private DataUtil()
  {
  }

  public static DataUtil getInstance()
  {
    if (_instance == null)
    {
      _instance = new DataUtil();
    }

    return _instance;
  }

  public void setContext(Context context)
  {
    _context = context;
    AssetUtil.getInstance().setContext(_context);
    FileUtil.getInstance().setContext(_context);
  }

  /**
   * Should be called after someone creates a file otherwise
   * the file will not be seen by  {@link #readFromAssetOrFile(String)}.
   * Currently called automatically by {@link FileUtil#writeObjectToFile(Object, String)}
   * and {@link FileUtil#writeToFile(byte[], String, String)}.
   */
  public void clearReaderCachForFile(String p_filename)
  {
    _filenameToReader.remove(p_filename);
  }
  
  public byte[] readFromAssetOrFile(String p_filename)
  {
    if (_filenameToReader.containsKey(p_filename))
    {
      // we have previously read this file, so use the correct reader directly.
      Reader readerForThisFile = _filenameToReader.get(p_filename);
      return readerForThisFile.read(p_filename);
    }
    else
    {
      // first time we try to read this file, try all readers
      TwinResult<byte[], Reader> result = _readerAll.read(p_filename);
      if (result._mainResult == null)
      {
        // in future we just dont try for this file anymore
        _filenameToReader.put(p_filename, _readerNone);
        // and only the first time we give a message
        String message = "DataUtil.readFromAssetOrFile: unable to read file or asset data from file with name " + p_filename;
        Log.i(Constants.APPLICATION_NAME, message);
      }
      else
      {
        // in future immediately use the correct reader for this filename
        _filenameToReader.put(p_filename, result._secondResult);
      }
      return result._mainResult;
    }
  }

  

  public byte[] compress(byte[] uncompressed)
  {
    byte[] result = null;

    Deflater deflater = new Deflater();
    deflater.setInput(uncompressed);
    deflater.finish();

    // Create an expandable byte array to hold the compressed data. 
    // You cannot use an array that's the same size as the original because 
    // there is no guarantee that the compressed data will be smaller than 
    // the uncompressed data. 

    ByteArrayOutputStream bos = new ByteArrayOutputStream(uncompressed.length);

    // Compress the data 
    byte[] buf = new byte[1024];
    while (!deflater.finished())
    {
      int count = deflater.deflate(buf);
      bos.write(buf, 0, count);
    }

    try
    {
      bos.close();
    }
    catch (IOException e)
    {
      Log.w(Constants.APPLICATION_NAME, "Unable to close stream");
    }

    // Get the compressed data 

    result = bos.toByteArray();

    return result;
  }

  public byte[] decompress(byte[] compressed)
  {
    return decompress(compressed, 0);
  }
  
  public byte[] decompress(byte[] compressed, int bytesToSkip)
  {
    Inflater decompressor = new Inflater();
    decompressor.setInput(compressed, bytesToSkip, compressed.length - bytesToSkip);

    // Create an expandable byte array to hold the decompressed data 
    ByteArrayOutputStream bos = new ByteArrayOutputStream(compressed.length);

    // Decompress the data 
    byte[] buf = new byte[1024];
    while (!decompressor.finished())
    {
      try
      {
        int count = decompressor.inflate(buf);
        bos.write(buf, 0, count);
      }
      catch (DataFormatException e)
      {
        return null;
      }
    }
    try
    {
      if (bos != null) bos.close();
    }
    catch (IOException e)
    {
      Log.w(Constants.APPLICATION_NAME, "Unable to close stream");
    }

    // Get the decompressed data 
    byte[] decompressedData = bos.toByteArray();
    return decompressedData;
  }

}
interface Reader
{
  byte[] read(String p_filename);
}

// support for encrypted xml files
// those are stored in corresponding .glb files
// note: this method should just be called with .xml filenames
class ReadFromGLB implements Reader
{
  // this cache contains xml files which are already unobfuscated
  private Map<String, byte[]> _cacheOfXml = new HashMap<String, byte[]>();

  public byte[] read(String p_filename)
  {
    if (p_filename.endsWith(".xml")==false)
      return null;
    
    synchronized (_cacheOfXml)
    {
      // note: the cache has the XML filename as the key
      byte[] dataFromCache = _cacheOfXml.get(p_filename);
      if (dataFromCache != null)
      {
        return dataFromCache;
      }
      // is the xml filename in the cache with a null value?
      // in that case, it means we tried to load the .glb before but it failed (not present)
      if (_cacheOfXml.containsKey(p_filename))
        return null;
    } // end sync block
    
    // first time we try to load the corresponding .glb
    String glbName = p_filename.substring(0, p_filename.lastIndexOf('.')) + ".glb";
    byte[] glbFileData = DataUtil.getInstance().readFromAssetOrFile(glbName);
    if (glbFileData != null)
    {
      // glb found, but is it valid?
      if (glbFileData.length>4&&glbFileData[0]=='-'&&glbFileData[1]=='i'&&glbFileData[2]=='t'&&glbFileData[3]=='u')
      {
        glbFileData = unobfuscate(glbFileData);
        synchronized (_cacheOfXml)
        {
          _cacheOfXml.put(p_filename, glbFileData);
        }
        return glbFileData;
      }
    }
    // glb not found
    // in future do not try to load the GLB as it isnt present
    synchronized (_cacheOfXml)
    {
      _cacheOfXml.put(p_filename, null);
    }
    return null;
  }
  
  // xml files can be (slightly) obfuscated at deploy time
  // obfuscation is done by:
  // step 1: convert file using base64
  // step 2: use java.util.zip.Deflator to compress
  // step 3: write a special signature (binary -itu)
  // step 4: write the compressed stream
  // this routine reverses those steps
  private byte[] unobfuscate(byte[] data)
  {
    // reverse step 3:  write a special signature (binary -itu)
    // and 
    // Reversing step 2: use java.util.zip.Deflator to compress
    data = DataUtil.getInstance().decompress(data, 4);
    // reverse step 1: convert using base64
    Base64 b64 = new Base64();
    
    return b64.decode(data);
  }
}
class ReadFromAsset implements Reader
{

  public byte[] read(String p_filename)
  {
    try
    {
      return AssetUtil.getInstance().getByteArray(p_filename);
    }
    catch (Exception e)
    {
      // do not show or propagate exception,
    }
    return null;
  }
  
}
class ReadFromFile implements Reader
{
  public byte[] read(String p_filename)
  {
    try
    {
      return FileUtil.getInstance().getByteArray(p_filename);
    }
    catch (Exception e)
    {
      // do not show or propagate exception,
    }
    return null;
  }
}
/**
 * A special Reader that always returns null.
 * @author Gert
 *
 */
class ReadFromNone implements Reader
{
  public byte[] read(String p_filename)
  {
    return null;
  }
}

class ReadFromAll
{
  private ReadFromAsset _fromAsset = new ReadFromAsset();
  private ReadFromFile _fromFile = new ReadFromFile();
  private ReadFromGLB _fromGlb = new ReadFromGLB();
  
  public TwinResult<byte[], Reader> read(String p_filename)
  {
    Reader theReaderThatRead = _fromGlb;
    byte[] result = theReaderThatRead.read(p_filename); 
    if (result == null)
    {
      theReaderThatRead = _fromFile;
      result = theReaderThatRead.read(p_filename);
      if (result == null)
      {
        theReaderThatRead = _fromAsset;
        result = theReaderThatRead.read(p_filename);
        if (result == null)
          theReaderThatRead = null;
      }
    }
    return new TwinResult<byte[], Reader>(result, theReaderThatRead);
  }
}

package com.itude.mobile.mobbl2.client.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;
import android.util.Log;

import com.itude.mobile.mobbl2.client.core.util.exceptions.MBDataParsingException;

public class FileUtil
{

  private static FileUtil _instance;
  private Context         _context;

  private FileUtil()
  {
  }

  public static FileUtil getInstance()
  {
    if (_instance == null)
    {
      _instance = new FileUtil();
    }

    return _instance;
  }

  public void setContext(Context context)
  {
    _context = context;
  }

  public byte[] getByteArray(String fileName) throws MBDataParsingException
  {
    FileInputStream fis = null;
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    try
    {
      File root = _context.getFilesDir();
      Log.d("MOBBL", "FileUtil.getByteArray: reading from file " + fileName);
      int read;
      byte[] buffer = new byte[1024];
      File file = new File(root, fileName);
      fis = new FileInputStream(file);
      while ((read = fis.read(buffer, 0, buffer.length)) != -1)
      {
        bytes.write(buffer, 0, read);
      }
      bytes.flush();
    }
    catch (Exception e)
    {
      String message = "FileUtil.getByteArray: unable to read file data from filename " + fileName;
      throw new MBDataParsingException(message, e);
    }
    finally
    {
      try
      {

        if (fis != null) fis.close();
      }
      catch (Exception e)
      {
        Log.w(Constants.APPLICATION_NAME, "Unable to close stream");
      }
    }
    return bytes.toByteArray();
  }

  public boolean writeToFile(byte[] fileContents, String fileName, String encoding)
  {
    DataUtil.getInstance().clearReaderCachForFile(fileName);

    FileOutputStream fos = null;
    boolean success = false;
    try
    {
      File root = _context.getFilesDir();
      Log.d("MOBBL", "FileUtil.writeToFile: writing to file " + fileName);

      int lastPathSeparator = fileName.lastIndexOf(File.separator);
      if (lastPathSeparator > -1)
      {
        String directoryName = fileName.substring(0, lastPathSeparator);
        Log.d("MOBBL", "FileUtil.writeToFile: creating directory " + directoryName);
        File dir = new File(root, directoryName);
        dir.mkdirs();
      }

      File file = new File(root, fileName);
      fos = new FileOutputStream(file);
      fos.write(fileContents);

      success = true;
    }
    catch (Exception e)
    {
      Log.w("MOBBL", "FileUtil.writeToFile: error writing file " + fileName, e);
    }
    finally
    {
      try
      {
        if (fos != null) fos.close();
      }
      catch (Exception e)
      {
        Log.w(Constants.APPLICATION_NAME, "Unable to close stream", e);
      }
    }

    return success;
  }

  public boolean writeObjectToFile(Object object, String fileName)
  {
    DataUtil.getInstance().clearReaderCachForFile(fileName);
    ByteArrayOutputStream byteStream = null;
    ObjectOutputStream out = null;
    boolean success = false;

    try
    {
      byteStream = new ByteArrayOutputStream();
      out = new ObjectOutputStream(byteStream);
      out.writeObject(object);
      writeToFile(byteStream.toByteArray(), fileName, null);
      success = true;
    }
    catch (Exception e)
    {
      Log.w(Constants.APPLICATION_NAME, "Failed to serialize object, or to write to file", e);
    }
    finally
    {
      try
      {
        if (out != null) out.close();
        if (byteStream != null) byteStream.close();
      }
      catch (Exception e)
      {
        Log.w(Constants.APPLICATION_NAME, "Unable to close streams", e);
      }
    }
    return success;
  }

  public Object readObjectFromFile(String fileName)
  {
    ByteArrayInputStream byteStream = null;
    ObjectInputStream in = null;
    Object result = null;
    try
    {
      byte[] bytes = getByteArray(fileName);
      byteStream = new ByteArrayInputStream(bytes);
      in = new ObjectInputStream(byteStream);
      result = in.readObject();
    }
    catch (Exception e)
    {
      Log.w(Constants.APPLICATION_NAME, "Failed to read from file, or to deserialize", e);
    }
    finally
    {
      try
      {
        if (in != null) in.close();
        if (byteStream != null) byteStream.close();
      }
      catch (Exception e)
      {
        Log.w(Constants.APPLICATION_NAME, "Unable to close streams", e);
      }
    }
    return result;
  }

  /***
   * Remove file based on the file name
   * 
   * @param fileName
   * @return true if remove was successful, false otherwise
   */
  public boolean remove(String fileName)
  {
    boolean success = false;

    try
    {
      File root = _context.getFilesDir();
      Log.d("MOBBL", "FileUtil.remove: removing file " + fileName);

      File file = new File(root, fileName);

      if (file.exists() && file.isFile())
      {
        file.delete();
        success = true;
      }
      else
      {
        if (!file.exists()) Log.w(Constants.APPLICATION_NAME, "FileUtil.remove: Could not remove file because it doesn't exist");
        else if (!file.isFile()) Log.w(Constants.APPLICATION_NAME, "FileUtil.remove: Could not remove file because it is not a file");
      }

    }
    catch (Exception e)
    {
      Log.w("MOBBL", "FileUtil.remove: error removing file " + fileName, e);
    }

    return success;
  }

}

package com.itude.mobile.mobbl2.client.core.util.imagecache;

import java.lang.ref.SoftReference;
import java.util.Date;

import android.graphics.Bitmap;

public class ImageCacheObject implements Comparable<ImageCacheObject>
{
  private final Date                   _timeLastUsed;
  private final SoftReference<Bitmap>  _bitmap;
  private final int                    _size;

  public static final ImageCacheObject NULL = new ImageCacheObject();

  public ImageCacheObject(Bitmap bitmap)
  {
    _timeLastUsed = new Date();
    _bitmap = new SoftReference<Bitmap>(bitmap);
    _size = bitmap.getRowBytes() * bitmap.getHeight();
  }

  private ImageCacheObject()
  {
    _size = 0;
    _timeLastUsed = new Date();
    _bitmap = null;
  }

  public Date getTimeLastUsed()
  {
    return _timeLastUsed;
  }

  public Bitmap getBitmap()
  {
    _timeLastUsed.setTime(System.currentTimeMillis());
    if (_bitmap != null) return _bitmap.get();
    else return null;
  }

  public boolean isValid()
  {
    return _bitmap == null || _bitmap.get() != null;
  }

  public int getSize()
  {
    return _size;
  }

  @Override
  public int compareTo(ImageCacheObject another)
  {
    return getTimeLastUsed().compareTo(another.getTimeLastUsed());
  }

}

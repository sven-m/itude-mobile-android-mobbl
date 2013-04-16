package com.itude.mobile.mobbl2.client.core.util.imagecache;

import java.io.File;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.itude.mobile.mobbl2.client.core.MBException;

public final class ImageUtil
{
  private static ImageCache _cache;

  /**
   * The method you're looking for ;) Loads the image stored at uri (either from cache or the internet) and puts it in the ImageView. 
   */
  public static void loadImage(final ImageView view, final Uri uri)
  {
    loadImage(uri, new LoadedImageCallback()
    {

      @Override
      public void doneLoading(Bitmap bitmap)
      {
        view.setImageBitmap(bitmap);
      }
    });
  }

  public static void loadImage(final ImageView view, final String uri)
  {
    loadImage(view, Uri.parse(uri));
  }

  public static void loadImageCache(File cacheDir)
  {
    _cache = new ImageCache(cacheDir);
  }

  public static ImageCache getCache()
  {
    return _cache;
  }

  // not entirely threadsafe, but is only called from the UI thread
  public static void loadImage(Uri uri, LoadedImageCallback callback)
  {
    ImageRequest request = new ImageRequest(uri, callback);
    Bitmap bitmap = request.performRequestOnCache();
    if (bitmap == null)
    {
      ImageRequestTask task = new ImageRequestTask();
      task.execute(request);
    }
    else request.performCallback(bitmap);
  }

  private static final class ImageRequestTask extends AsyncTask<ImageRequest, Void, Bitmap>
  {
    private ImageRequest request;

    @Override
    protected Bitmap doInBackground(ImageRequest... params)
    {
      try
      {
        request = params[0];
        return request.performRequest();
      }
      catch (Exception e)
      {
        e.printStackTrace();
        throw new MBException("Error loading image " + request._uri, e);
      }

    }

    @Override
    protected void onPostExecute(Bitmap result)
    {
      request.performCallback(result);
    }

  }

  private static final class ImageRequest
  {
    private final Uri                 _uri;
    private final LoadedImageCallback _callback;

    ImageRequest(Uri uri, LoadedImageCallback callback)
    {
      _uri = uri;
      _callback = callback;
    }

    public Bitmap performRequest()
    {
      return getCache().getBitmap(_uri);
    }

    public Bitmap performRequestOnCache()
    {
      return getCache().getBitmapFromCache(_uri);
    }

    public void performCallback(Bitmap bitmap)
    {
      if (bitmap != null) _callback.doneLoading(bitmap);
    }
  }

}

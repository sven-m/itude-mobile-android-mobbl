/*
 * (C) Copyright Itude Mobile B.V., The Netherlands
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.itude.mobile.mobbl.core.util.imagecache;

import java.io.File;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.itude.mobile.mobbl.core.MBException;

public final class MBImageUtil
{
  private static MBImageCache _cache;

  /**
   * The method you're looking for ;) Loads the image stored at uri (either from cache or the internet) and puts it in the ImageView. 
   */
  public static void loadImage(final ImageView view, final Uri uri)
  {
    loadImage(uri, new MBLoadedImageCallback()
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
    _cache = new MBImageCache(cacheDir);
  }

  public static MBImageCache getCache()
  {
    return _cache;
  }

  // not entirely threadsafe, but is only called from the UI thread
  public static void loadImage(Uri uri, MBLoadedImageCallback callback)
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
    private final MBLoadedImageCallback _callback;

    ImageRequest(Uri uri, MBLoadedImageCallback callback)
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

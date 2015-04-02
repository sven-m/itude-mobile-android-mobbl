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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.itude.mobile.android.util.log.MBLog;
import com.itude.mobile.mobbl.core.util.MBConstants;
import com.itude.mobile.mobbl.core.util.MBProperties;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class MBImageCache {
    private final Map<Uri, MBImageCacheObject> _images;
    private long _cacheSize = 0;
    private long _localCacheSize = 0;

    private final File _localCache;

    private static final int IO_BUFFER_SIZE = 4 * 1024;

    private static final long MEMORY_CACHE_SIZE = MBProperties.getInstance()
            .getIntegerProperty(MBConstants.C_PROPERTY_IMAGE_CACHE_MEMORY, 3) * 1024 * 1024;
    private static final long LOCAL_CACHE_SIZE = MBProperties.getInstance()
            .getIntegerProperty(MBConstants.C_PROPERTY_IMAGE_CACHE_DISK, 10) * 1024 * 1024;

    private static final String LOG_TAG = MBImageCache.class.getSimpleName();

    public MBImageCache(File localCache) {
        _images = new LinkedHashMap<Uri, MBImageCacheObject>(100, 0.75f, true);
        _localCache = localCache;
        _localCacheSize = calculateLocalCacheSize();
        cleanLocalCache();
    }

    public Bitmap getBitmapFromCache(Uri uri) {
        MBImageCacheObject ico = getFromMemoryCache(uri);
        if (ico != null) {
            if (ico.isValid()) return ico.getBitmap();
            else {
                removeFromCache(uri, ico);
                //android.util.Log.d(LOG_TAG, "Removed " + uri + " from cache, since it has been garbage collected");
            }
        }
        return null;
    }

    public Bitmap getBitmap(Uri uri) {
        try {
            MBImageCacheObject ico = getFromMemoryCache(uri);
            if (ico != null) {
                if (ico.isValid()) return ico.getBitmap();
                else {
                    removeFromCache(uri, ico);
                    //android.util.Log.d(LOG_TAG, "Removed " + uri + " from cache, since it has been garbage collected");
                }
            }

            ico = getFromLocalCache(uri);
            if (ico != null) {
                if (ico.isValid()) return ico.getBitmap();
                else removeFromCache(uri, ico);
            }

            ico = getFromInternet(uri);
            return ico.getBitmap();
        } finally {
            //android.util.Log.d(LOG_TAG, "Returned " + uri + " Cache size: " + _cacheSize);
        }
    }

    private MBImageCacheObject getFromMemoryCache(Uri uri) {
        synchronized (_images) {
            MBImageCacheObject ico = _images.get(uri);
            return ico;
        }
    }

    private MBImageCacheObject getFromLocalCache(Uri uri) {
        File file = new File(_localCache, "" + uri.hashCode());
        if (file.exists()) {
            Uri localUri = Uri.fromFile(file);
            MBImageCacheObject result = loadFromStream(localUri);
            if (result != null && result.isValid()) {
                file.setLastModified(System.currentTimeMillis());
                addToCache(uri, result);
            }
            return result;
        } else return null;
    }

    private MBImageCacheObject getFromInternet(Uri uri) {
        MBImageCacheObject result = MBImageCacheObject.NULL;
        try {

            URL url = new URL(uri.toString());

            InputStream in = new BufferedInputStream(url.openStream(), IO_BUFFER_SIZE);
            ByteArrayOutputStream boas = new ByteArrayOutputStream();
            copy(in, boas);
            Uri localUri = storeLocally(uri, boas.toByteArray());

            result = loadFromStream(localUri);
            if (result == null) result = MBImageCacheObject.NULL;

        } catch (IllegalStateException e) {
            // this is possible if the redirection goes wrong while trying to retrieve an image
            MBLog.e(LOG_TAG, "Error loading from: " + uri, e);
        } catch (IOException e) {
            MBLog.e(LOG_TAG, "Error loading from: " + uri, e);
        }

        addToCache(uri, result);
        return result;

    }

    private void addToCache(Uri uri, MBImageCacheObject object) {
        synchronized (_images) {

            _images.put(uri, object);
            _cacheSize += object.getSize();

            cleanMemoryCache();
        }
    }

    private void removeFromCache(Uri uri, MBImageCacheObject object) {
        synchronized (_images) {

            _images.remove(uri);
            _cacheSize -= object.getSize();
        }
    }

    private Uri storeLocally(Uri uri, byte[] bytes) {
        Uri result = null;
        if (_localCache != null) {
            OutputStream out = null;
            InputStream in = null;
            try {
                File file = new File(_localCache, "" + uri.hashCode());
                out = new BufferedOutputStream(new FileOutputStream(file), IO_BUFFER_SIZE);
                in = new ByteArrayInputStream(bytes);
                copy(in, out);
                _localCacheSize += bytes.length;

                in.reset();

                result = Uri.fromFile(file);

                //android.util.Log.d(LOG_TAG, "Stored " + uri + " as " + file.getAbsolutePath() + " Local cache is now: " + _localCacheSize);
            } catch (FileNotFoundException e) {
                MBLog.e(LOG_TAG, "Could not cache photo " + uri + " to file " + uri.hashCode(), e);
            } catch (IOException e) {
                MBLog.e(LOG_TAG, "Could not cache photo " + uri + " to file " + uri.hashCode(), e);
            } finally {
                closeStream(in);
                closeStream(out);
            }

            cleanLocalCache();
        }

        return result;
    }

    public static Bitmap loadBitmap(Uri uri) {
        BufferedOutputStream out = null;
        Bitmap bitmap = null;
        InputStream in = null;
        try {
            in = new BufferedInputStream(new URL(uri.toString()).openStream(), IO_BUFFER_SIZE);
            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE);
            copy(in, out);
            out.flush();

            final byte[] data = dataStream.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

        } catch (IOException e) {
            MBLog.e(LOG_TAG, "Could not load photo: " + uri, e);
        } finally {
            closeStream(in);
            closeStream(out);
        }

        return bitmap;
    }

    private MBImageCacheObject loadFromStream(Uri uri) {

        Bitmap bitmap = null;
        MBImageCacheObject result = null;

        bitmap = loadBitmap(uri);
        // decoding went wrong, apparently
        if (bitmap == null) return MBImageCacheObject.NULL;
        else result = new MBImageCacheObject(bitmap);

        return result;
    }

    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] b = new byte[IO_BUFFER_SIZE];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }

    private static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                MBLog.e(LOG_TAG, "Could not close stream", e);
            }
        }
    }

    private long calculateLocalCacheSize() {
        long size = 0;

        File[] files = _localCache.listFiles();
        for (File file : files)
            if (file.isFile()) size += file.length();

        //android.util.Log.d(LOG_TAG, "Local cache is now: " + size);
        return size;
    }

    private void cleanLocalCache() {
        if (_localCacheSize > LOCAL_CACHE_SIZE) {
            synchronized (_localCache) {

                File[] files = _localCache.listFiles();
                Arrays.sort(files, new Comparator<File>() {

                    @Override
                    public int compare(File arg0, File arg1) {
                        long dif = arg1.lastModified() - arg0.lastModified();
                        if (dif < 0) return -1;
                        else if (dif == 0) return 0;
                        else return 1;
                    }
                });

                long size = _localCacheSize;
                for (File file : files) {
                    if (size > LOCAL_CACHE_SIZE) {
                        size -= file.length();
                        file.delete();
                        //android.util.Log.d(LOG_TAG, "Purged " + file.getAbsolutePath() + " from local cache. Size is now " + size);
                    }
                }

                size = calculateLocalCacheSize();
            }
        }
    }

    private void cleanMemoryCache() {
        if (_cacheSize > MEMORY_CACHE_SIZE) {
            synchronized (_images) {

                purgeInvalid();
                if (_cacheSize > MEMORY_CACHE_SIZE) {
                    long size = _cacheSize;
                    Iterator<Map.Entry<Uri, MBImageCacheObject>> it = _images.entrySet().iterator();
                    while (it.hasNext() && size > MEMORY_CACHE_SIZE) {
                        Map.Entry<Uri, MBImageCacheObject> entry = it.next();
                        size -= entry.getValue().getSize();
                        //android.util.Log.d(LOG_TAG, "Removed " + entry.getKey() + " from memory cache. Size is now " + size);
                        it.remove();
                    }
                    _cacheSize = size;
                }
            }
        }
    }

    private void purgeInvalid() {
        synchronized (_images) {
            Iterator<Map.Entry<Uri, MBImageCacheObject>> it = _images.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Uri, MBImageCacheObject> entry = it.next();
                if (!entry.getValue().isValid()) {
                    it.remove();
                    _cacheSize -= entry.getValue().getSize();
                }
            }
        }
    }

}

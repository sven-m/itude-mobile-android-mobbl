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

import java.lang.ref.SoftReference;
import java.util.Date;

public class MBImageCacheObject implements Comparable<MBImageCacheObject> {
    private final Date _timeLastUsed;
    private final SoftReference<Bitmap> _bitmap;
    private final int _size;

    public static final MBImageCacheObject NULL = new MBImageCacheObject();

    public MBImageCacheObject(Bitmap bitmap) {
        _timeLastUsed = new Date();
        _bitmap = new SoftReference<Bitmap>(bitmap);
        _size = bitmap.getRowBytes() * bitmap.getHeight();
    }

    private MBImageCacheObject() {
        _size = 0;
        _timeLastUsed = new Date();
        _bitmap = null;
    }

    public Date getTimeLastUsed() {
        return _timeLastUsed;
    }

    public Bitmap getBitmap() {
        _timeLastUsed.setTime(System.currentTimeMillis());
        if (_bitmap != null) return _bitmap.get();
        else return null;
    }

    public boolean isValid() {
        return _bitmap == null || _bitmap.get() != null;
    }

    public int getSize() {
        return _size;
    }

    @Override
    public int compareTo(MBImageCacheObject another) {
        return getTimeLastUsed().compareTo(another.getTimeLastUsed());
    }

}

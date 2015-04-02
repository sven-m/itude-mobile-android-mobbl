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
package com.itude.mobile.mobbl.core.util;

import com.itude.mobile.mobbl.core.configuration.mvc.MBConfigurationDefinition;
import com.itude.mobile.mobbl.core.model.MBDocument;
import com.itude.mobile.mobbl.core.services.MBDataManagerService;

import java.util.Hashtable;
import java.util.Map;

public final class MBProperties {
    private final MBDocument _propertiesDoc;
    private final Map<String, String> _propertiesCache;
    private final Map<String, String> _systemPropertiesCache;

    private static MBProperties _instance;

    private MBProperties() {
        _propertiesDoc = MBDataManagerService.getInstance().loadDocument(MBConfigurationDefinition.DOC_SYSTEM_PROPERTIES);
        _propertiesCache = new Hashtable<String, String>();
        _systemPropertiesCache = new Hashtable<String, String>();

    }

    public static MBProperties getInstance() {
        if (_instance == null) _instance = new MBProperties();
        return _instance;
    }

    public String getValueForProperty(String key) {
        String value = _propertiesCache.get(key);
        if (value == null) {
            String path = "/Application[0]/Property[name='" + key + "']/@value";
            value = (String) _propertiesDoc.getValueForPath(path);
            if (value != null) _propertiesCache.put(key, value);
        }
        return value;
    }

    public boolean getBooleanProperty(String key) {
        return MBParseUtil.booleanValue(getValueForProperty(key));
    }

    public int getIntegerProperty(String key, int dflt) {
        String value = getValueForProperty(key);
        if (value == null) return dflt;
        return Integer.parseInt(value);
    }

    public String getValueForSystemProperty(String key) {
        String value = _systemPropertiesCache.get(key);
        if (value == null) {
            String path = "/System[0]/Property[name='" + key + "']/@value";
            value = (String) _propertiesDoc.getValueForPath(path);
            _systemPropertiesCache.put(key, value);
        }
        return value;
    }

}

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
package com.itude.mobile.mobbl.core.configuration.endpoints;

import com.itude.mobile.mobbl.core.configuration.MBDefinition;
import com.itude.mobile.mobbl.core.services.MBResultListenerDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link MBDefinition} Class for a default service configuration
 */
public class MBEndpointsConfiguration extends MBDefinition {
    private final Map<String, MBEndPointDefinition> _endPoints;
    private final List<MBResultListenerDefinition> _resultListeners;

    public MBEndpointsConfiguration() {
        _endPoints = new HashMap<String, MBEndPointDefinition>();
        _resultListeners = new ArrayList<MBResultListenerDefinition>();
    }

    @Override
    public void addEndPoint(MBEndPointDefinition definition) {
        _endPoints.put(definition.getDocumentOut(), definition);
    }

    public MBEndPointDefinition getEndPointForDocumentName(String documentName) {
        return _endPoints.get(documentName);
    }

    @Override
    public void addResultListener(MBResultListenerDefinition lsnr) {
        _resultListeners.add(lsnr);
    }

    public void linkGlobalListeners() {
        for (MBEndPointDefinition endPointDef : _endPoints.values()) {
            for (MBResultListenerDefinition resultDef : _resultListeners) {
                endPointDef.addResultListener(resultDef);
            }
        }

    }

}

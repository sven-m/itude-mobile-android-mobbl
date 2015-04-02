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
package com.itude.mobile.mobbl.core.view;

import com.itude.mobile.mobbl.core.configuration.resources.MBItemDefinition;
import com.itude.mobile.mobbl.core.model.MBDocument;

public class MBItem extends MBComponent {
    private String _resource;
    private String _state;
    private String _align;

    public MBItem(MBItemDefinition definition, MBDocument document, MBComponentContainer parent) {
        super(definition, document, parent);

        _resource = definition.getResource();
        _state = definition.getState();
        _align = definition.getAlign();
    }

    public String getResource() {
        return _resource;
    }

    public void setResource(String resource) {
        _resource = resource;
    }

    public String getState() {
        return _state;
    }

    public void setState(String state) {
        _state = state;
    }

    public String getAlign() {
        return _align;
    }

    public void setAlign(String align) {
        _align = align;
    }

}

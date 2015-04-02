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

import com.itude.mobile.mobbl.core.configuration.MBDefinition;
import com.itude.mobile.mobbl.core.model.MBDocument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MBComponentContainer extends MBComponent {

    private List<MBComponent> _children;

    public MBComponentContainer(MBDefinition definition, MBDocument document, MBComponentContainer parent) {
        super(definition, document, parent);
        _children = new ArrayList<MBComponent>();
    }

    public List<MBComponent> getChildren() {
        return _children;
    }

    public void setChildren(List<MBComponent> children) {
        _children = children;
    }

    public void addChild(MBComponent child) {
        if (child != null && !child.isMarkedForDestruction()) {
            _children.add(child);
            child.setParent(this);
        }
    }

    @Override
    public void translatePath() {
        for (MBComponent child : _children) {
            child.translatePath();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends MBComponent> List<T> getChildrenOfKind(Class<T> clazz) {
        List<T> result = new ArrayList<T>();
        for (MBComponent child : _children) {
            if (clazz.isInstance(child)) {
                result.add((T) child);
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends MBComponent> List<T> getChildrenOfKindWithType(Class<T> clazz, String... types) {
        List<T> result = new ArrayList<T>();
        List<String> typeArray = Arrays.asList(types);
        for (MBComponent child : _children) {
            if (clazz.isInstance(child) && typeArray.contains(child.getType())) {
                result.add((T) child);
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public <T extends MBComponent> T getComponentOfKindWithName(Class<?> clazz, String name) {

        // Walk through all children
        for (MBComponent child : _children) {

            if (clazz.isInstance(child) && name.equals(child.getName())) {
                return (T) child;
            } else if (child instanceof MBComponentContainer) {
                MBComponent recursiveChild = ((MBComponentContainer) child).getComponentOfKindWithName(clazz, name);
                if (recursiveChild != null) {
                    return (T) recursiveChild;
                }
            }

        }

        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends MBComponent> List<T> getDescendantsOfKind(Class<T> clazz) {
        List<T> result = new ArrayList<T>();
        for (MBComponent child : _children) {

            if (clazz.isInstance(child)) {
                result.add((T) child);
            }

            result.addAll(child.getDescendantsOfKind(clazz));
        }

        return result;
    }

    public StringBuffer childrenAsXmlWithLevel(StringBuffer appendToMe, int level) {
        for (MBComponent child : _children) {
            child.asXmlWithLevel(appendToMe, level);
        }

        return appendToMe;
    }

}

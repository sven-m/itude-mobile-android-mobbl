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
package com.itude.mobile.mobbl.core.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl.core.configuration.mvc.MBAttributeDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBElementDefinition;
import com.itude.mobile.mobbl.core.model.exceptions.MBCannotAssignException;
import com.itude.mobile.mobbl.core.model.exceptions.MBInvalidAttributeNameException;
import com.itude.mobile.mobbl.core.util.MBConstants;
import com.itude.mobile.mobbl.core.util.MBParseUtil;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A node in an Element tree.
 */
public class MBElement extends MBElementContainer {

    private static final String TEXT_ATTRIBUTE = "text()";

    private final Map<String, String> _values;                  // Dictionaryofstrings
    private MBElementDefinition _definition;

    public MBElement() {
        super();
        _values = new HashMap<String, String>();
    }

    /**
     * Creating and Initializing an Element
     *
     * @param definition {@link MBElementDefinition}
     */
    public MBElement(MBElementDefinition definition) {
        super();
        _definition = definition;
        _values = new HashMap<String, String>();
    }

    @Override
    public MBElement clone() {
        MBElement newElement = new MBElement(_definition);
        newElement._values.putAll(_values);
        copyChildrenInto(newElement);

        return newElement;
    }

    @Override
    public void setValue(String value, String path) {
        if (path.startsWith("@")) {
            setValue(value, path.substring(1));
        } else {
            super.setValue(value, path);
        }
    }

    /**
     * Working with attribute values
     *
     * @param value         value
     * @param attributeName attribute name
     */
    public void setAttributeValue(boolean value, String attributeName) {
        setAttributeValue(value ? MBConstants.C_TRUE : MBConstants.C_FALSE, attributeName, true);
    }

    /**
     * Working with attribute values
     *
     * @param value         value
     * @param attributeName attribute name
     */
    public void setAttributeValue(String value, String attributeName) {
        setAttributeValue(value, attributeName, true);
    }

    /**
     * Working with attribute values
     *
     * @param value          value
     * @param attributeName  attribute name
     * @param throwIfInvalid true if an exeption needs to be thrown if attribute is invalid
     */
    public void setAttributeValue(String value, String attributeName, boolean throwIfInvalid) {
        if (throwIfInvalid) {
            validateAttribute(attributeName);
            _values.put(attributeName, value);
        } else {
            if (isValidAttribute(attributeName)) {
                _values.put(attributeName, value);
            }
        }

    }

    public String getValueForAttribute(String attributeName) {
        String rtrn = _values.get(attributeName);
        if (rtrn == null) {
            // above we assumed the attributeName is valid (should be true 100% of the time in production)
            // so we only validate the attrib if we do not have a value for it.
            validateAttribute(attributeName);
        }
        return rtrn;
    }

    public boolean getBooleanForAttribute(String attributeName) {
        return MBParseUtil.booleanValue(getValueForAttribute(attributeName));
    }

    public String getValueForKey(String key) {
        return getValueForAttribute(key);
    }

    public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level) {
        return asXmlWithLevel(appendToMe, level, false);
        // TODO: it should always be true (like in iOS), but to be a 100% sure, every project needs to be tested in order to do that as it changes the behaviour of the toString
    }

    public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level, boolean escapeContent) {
        String bodyText = getBodyText();
        boolean hasBodyText = (bodyText != null && bodyText.length() > 0);

        StringUtil.appendIndentString(appendToMe, level).append("<").append(_definition.getName());
        for (MBAttributeDefinition def : _definition.getAttributes()) {
            String attrName = def.getName();
            String attrValue = _values.get(attrName);
            if (!attrName.equals(TEXT_ATTRIBUTE)) {
                appendToMe.append(attributeAsXml(attrName, attrValue));
            }
        }

        if (_definition.getChildren().isEmpty() && !hasBodyText) {
            appendToMe.append("/>\n");
        } else {
            appendToMe.append(">");
            if (hasBodyText) {
                appendToMe.append(StringEscapeUtils.escapeXml10(getBodyText().trim()));
            } else {
                appendToMe.append("\n");
            }

            for (MBElementDefinition elemDef : _definition.getChildren()) {
                List<MBElement> lst = getElements().get(elemDef.getName());
                if (lst != null) {
                    for (MBElement elem : lst) {
                        elem.asXmlWithLevel(appendToMe, level + 2, escapeContent);
                    }
                }
            }

            int closingLevel = 0;
            if (!hasBodyText) {
                closingLevel = level;
            }
            StringUtil.appendIndentString(appendToMe, closingLevel).append("</").append(_definition.getName()).append(">\n");
        }

        return appendToMe;
    }

    @Override
    public MBElementDefinition getDefinition() {
        return _definition;
    }

    public void setDefinition(MBElementDefinition definition) {
        _definition = definition;
    }

    /**
     * Checking existence of attributes
     *
     * @param attributeName attribute name
     * @return true if attribute is valid
     */
    public boolean isValidAttribute(String attributeName) {
        return (getDefinition()).isValidAttribute(attributeName);
    }

    private void validateAttribute(String attributeName) {
        if (!isValidAttribute(attributeName)) {
            String message = "Attribute \"" + attributeName + "\" not defined for element with name \"" + getDefinition().getName()
                    + "\". Use one of \"" + getDefinition().getAttributeNames() + "\"";

            throw new MBInvalidAttributeNameException(message);
        }
    }

    /**
     * Working with the 'text()' attribute
     *
     * @return get 'text()' value
     */
    public String getBodyText() {
        if (isValidAttribute(TEXT_ATTRIBUTE)) {
            return getValueForAttribute(TEXT_ATTRIBUTE);
        }
        return null;
    }

    public void setBodyText(String text) {
        setAttributeValue(text, TEXT_ATTRIBUTE);
    }

    /**
     * Copying element state
     *
     * @param target {@link MBElement}
     */
    public void assignToElement(MBElement target) {
        if (!target.getDefinition().getName().equals(_definition.getName())) {
            String message = "Cannot assign element since types differ: " + target.getDefinition().getName() + " != " + _definition.getName()
                    + " (use assignByName:)";
            throw new MBCannotAssignException(message);
        }

        target._values.clear();
        target._values.putAll(_values);
        target.getElements().clear();
        copyChildrenInto(target);
    }

    @Override
    public String getUniqueId() {
        StringBuilder uid = new StringBuilder();
        uid.append(getDefinition().getName());
        for (MBAttributeDefinition def : _definition.getAttributes()) {
            String attrName = def.getName();
            if (!attrName.equals("xmlns")) {
                String attrValue = _values.get(attrName);
                uid.append('_');
                if (attrValue != null) {
                    uid.append(cookValue(attrValue));
                }
            }
        }
        uid.append(super.getUniqueId());

        return uid.toString();
    }

    @Override
    public void addAllPathsTo(Set<String> set, String currentPath) {
        String pathPrefix = currentPath + "/@";
        for (String attribute : _values.keySet()) {
            set.add(pathPrefix + attribute);
        }
        super.addAllPathsTo(set, currentPath);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getValueForPathComponents(List<String> pathComponents, String originalPath, boolean nillIfMissing,
                                           List<String> translatedPathComponents) {
        if (pathComponents.size() > 0 && pathComponents.get(0).startsWith("@")) {
            String attrName = pathComponents.get(0);
            if (translatedPathComponents != null) translatedPathComponents.add(attrName);

            return (T) getValueForAttribute(attrName.substring(1));
        } else {
            return (T) super.getValueForPathComponents(pathComponents, originalPath, nillIfMissing, translatedPathComponents);
        }
    }

    public String cookValue(String uncooked) {
        // TODO Double check if this method was implemented properly

        if (uncooked == null) {
            return null;
        }

        StringBuilder cooked = new StringBuilder();
        for (int i = 0; i < uncooked.length(); i++) {
            char c = uncooked.charAt(i);
            if (c < 32 || c == '&' || c == '\'' || c > 126) {
                cooked.append("&#").append((int) c).append(';');
            } else {
                cooked.append(c);
            }
        }

        return cooked.toString();
    }

    public String attributeAsXml(String name, Object attrValue) {
        attrValue = StringEscapeUtils.escapeXml10((String) attrValue);

        if (attrValue == null) {
            return "";
        }

        return " " + name + "='" + attrValue + "'";
    }

    public void assignByName(MBElementContainer other) {
        other.deleteAllChildElements();

        MBElementDefinition def = getDefinition();
        for (MBAttributeDefinition attrDef : def.getAttributes()) {
            if (((MBElementDefinition) other.getDefinition()).isValidAttribute(attrDef.getName())) {
                ((MBElement) other).setAttributeValue(getValueForAttribute(attrDef.getName()), attrDef.getName());
            }
        }

        for (String elementName : getElements().keySet()) {
            for (MBElement src : getElements().get(elementName)) {
                MBElement newElem = other.createElementWithName(src.getDefinition().getName());
                src.assignByName(newElem);
            }
        }

    }

    @Override
    public String getName() {
        return getDefinition().getName();
    }

    @Override
    public String toString() {
        StringBuffer rt = new StringBuffer();
        return asXmlWithLevel(rt, 0).toString();
    }

    // Parcelable stuff

    private MBElement(Parcel in) {
        _values = new HashMap<String, String>();

        Bundle valueBundle = in.readBundle();

        for (String key : valueBundle.keySet()) {
            _values.put(key, valueBundle.getString(key));
        }

        _definition = in.readParcelable(MBElementDefinition.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return MBConstants.C_PARCELABLE_TYPE_ELEMENT;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        Bundle valueBundle = new Bundle();

        for (String key : _values.keySet()) {
            valueBundle.putString(key, _values.get(key));
        }

        out.writeBundle(valueBundle);
        out.writeParcelable(_definition, flags);
    }

    public static final Parcelable.Creator<MBElement> CREATOR = new Creator<MBElement>() {
        @Override
        public MBElement[] newArray(int size) {
            return new MBElement[size];
        }

        @Override
        public MBElement createFromParcel(Parcel in) {
            return new MBElement(in);
        }
    };

    // End of parcelable stuff

}

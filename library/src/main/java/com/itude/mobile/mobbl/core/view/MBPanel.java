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

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.itude.mobile.android.util.DeviceUtil;
import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl.core.configuration.MBDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBPanelDefinition;
import com.itude.mobile.mobbl.core.controller.MBApplicationFactory;
import com.itude.mobile.mobbl.core.model.MBDocument;
import com.itude.mobile.mobbl.core.services.MBLocalizationService;
import com.itude.mobile.mobbl.core.view.builders.MBViewBuilderFactory;

public class MBPanel extends MBComponentContainer implements OnClickListener {
    private String _type;
    private String _title;
    private int _width;
    private int _height;
    private String _outcomeName;
    private String _path;
    private String _translatedPath;
    private String _mode;
    private boolean _focused;
    private boolean _scrollable;

    private String _diffableMarkerPath = null;
    private String _diffablePrimaryPath = null;
    private boolean _diffableMaster = false;

    public MBPanel(MBPanelDefinition definition, MBDocument document, MBComponentContainer parent) {
        this(definition, document, parent, true);
    }

    public MBPanel(MBPanelDefinition definition, MBDocument document, MBComponentContainer parent, boolean buildViewStructure) {
        super(definition, document, parent);
        setTitle(definition.getTitle());
        setType(definition.getType());
        setWidth(definition.getWidth());
        setHeight(definition.getHeight());
        setOutcomeName(substituteExpressions(definition.getOutcomeName()));
        setPath(definition.getPath());
        setMode(definition.getMode());
        setFocused(definition.isFocused());

        if (buildViewStructure) {
            buildChildren(definition, document, parent);
            MBApplicationFactory.getInstance().getPageConstructor().onConstructedPanel(this);
        }

    }

    final protected void buildChildren(MBPanelDefinition definition, MBDocument document, MBComponentContainer parent) {
        for (MBDefinition def : definition.getChildren()) {
            String parentAbsoluteDataPath = null;
            if (parent != null) {
                parentAbsoluteDataPath = parent.getAbsoluteDataPath();
            }

            if (def.isPreConditionValid(document, parentAbsoluteDataPath)) {
                addChild(MBComponentFactory.getComponentFromDefinition(def, document, this));
            }
        }
    }

    @Override
    public String getType() {
        return _type;
    }

    private void setType(String type) {
        _type = type;
    }

    public String getTitle() {
        String result = _title;

        if (_title != null) {
            result = _title;
        } else {
            MBPanelDefinition definition = (MBPanelDefinition) getDefinition();
            if (definition.getTitle() != null) {
                result = definition.getTitle();
            } else if (definition.getTitlePath() != null) {
                String path = definition.getTitlePath();
                if (!path.startsWith("/")) {
                    path = getAbsoluteDataPath() + "/" + path;
                }

                result = (String) getDocument().getValueForPath(path);
                //        return result;
            }
        }

        return MBLocalizationService.getInstance().getTextForKey(result);
    }

    //This will translate any expression that are part of the path to their actual values
    @Override
    public void translatePath() {
        _translatedPath = substituteExpressions(getAbsoluteDataPath());
        super.translatePath();
    }

    @Override
    public String getAbsoluteDataPath() {
        if (_translatedPath != null) {
            return _translatedPath;
        }

        return super.getAbsoluteDataPath();
    }

    @Override
    public ViewGroup buildView() {
        return MBViewBuilderFactory.getInstance().getPanelViewBuilder().buildPanelView(this);
    }

    @Override
    public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level) {
        StringUtil.appendIndentString(appendToMe, level)//
                .append("<MBPanel ")//
                .append(attributeAsXml("type", _type))//
                .append(" ")//
                .append(attributeAsXml("title", _title))//
                .append(" ")//
                .append(attributeAsXml("width", _width))//
                .append(" ")//
                .append(attributeAsXml("height", _height))//
                .append(" ")//
                .append(attributeAsXml("outcomeName", getOutcomeName()))//
                .append(" ")//
                .append(attributeAsXml("focused", isFocused()))//
                .append(" ")//
                .append(attributeAsXml("mode", getMode()))//
                .append(" ")//
                .append(attributeAsXml("path", getPath()))//
                .append(">\n");

        childrenAsXmlWithLevel(appendToMe, level + 2);
        return StringUtil.appendIndentString(appendToMe, level).append("</MBPanel>\n");
    }

    @Override
    public String toString() {
        StringBuffer rt = new StringBuffer();
        return asXmlWithLevel(rt, 0).toString();
    }

    public void setTitle(String title) {
        _title = title;
    }

    public int getWidth() {
        return _width;
    }

    public void setWidth(int width) {
        _width = width;
    }

    public int getHeight() {
        return _height;
    }

    public void setHeight(int height) {
        _height = height;
    }

    public void setOutcomeName(String outcomeName) {
        _outcomeName = outcomeName;
    }

    @Override
    public String getOutcomeName() {
        return _outcomeName;
    }

    public void setPath(String path) {
        _path = path;
    }

    public String getPath() {
        return _path;
    }

    public synchronized void rebuild() {
        getChildren().clear();
        MBPanelDefinition panelDef = (MBPanelDefinition) getDefinition();
        for (MBDefinition def : panelDef.getChildren()) {
            String absoluteDataPath = null;

            if (getParent() != null) {
                absoluteDataPath = getParent().getAbsoluteDataPath();
            }

            MBDocument document = getDocument();

            if (def.isPreConditionValid(document, absoluteDataPath)) {
                addChild(MBComponentFactory.getComponentFromDefinition(def, document, this));
            }
        }
    }

    public String getMode() {
        return _mode;
    }

    public void setMode(String mode) {
        _mode = mode;
    }

    public boolean isFocused() {
        return _focused;
    }

    private void setFocused(boolean focused) {
        _focused = focused;
    }

    public boolean isScrollable() {
        return _scrollable;
    }

    public void setScrollable(boolean scrollable) {
        _scrollable = scrollable;
    }

    public void setDiffableMaster(boolean diffableMaster) {
        _diffableMaster = diffableMaster;
    }

    public boolean isDiffableMaster() {
        return _diffableMaster;
    }

    public void setDiffableMarkerPath(String diffableMarkerPath) {
        _diffableMarkerPath = diffableMarkerPath;
    }

    public String getDiffableMarkerPath() {
        return _diffableMarkerPath;
    }

    public void setDiffablePrimaryPath(String diffablePrimaryPath) {
        _diffablePrimaryPath = diffablePrimaryPath;
    }

    public String getDiffablePrimaryPath() {
        return _diffablePrimaryPath;
    }

    // android.view.View.OnClickListener method

    @Override
    public void onClick(View v) {
        if (DeviceUtil.isBigDeviceType()) {
            View selectedView = getPage().getSelectedView();

            if (selectedView != null) {
                selectedView.setSelected(false);
            }
            getPage().setSelectedView(v);
            v.setSelected(true);
        }

        if (getPath() != null) {
            handleOutcome(getOutcomeName(), getAbsoluteDataPath() + "/" + getPath());
        } else {
            handleOutcome(getOutcomeName(), getAbsoluteDataPath());
        }
    }

}

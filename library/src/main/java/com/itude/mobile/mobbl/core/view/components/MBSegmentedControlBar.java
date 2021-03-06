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
package com.itude.mobile.mobbl.core.view.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.itude.mobile.mobbl.core.MBException;
import com.itude.mobile.mobbl.core.view.builders.MBStyleHandler;
import com.itude.mobile.mobbl.core.view.builders.MBViewBuilderFactory;

import java.util.ArrayList;
import java.util.List;

public class MBSegmentedControlBar extends LinearLayout {

    private final MBStyleHandler _styleHandler;
    private int _selectedIndex = 0;

    private MBOnSelectedListener _onSelectedListener;
    private MBOnClickListener _onClickListener;

    private final List<Button> _itemButtons;


    public MBSegmentedControlBar(Context context) {
        super(context);
        _itemButtons = new ArrayList<Button>();
        _styleHandler = MBViewBuilderFactory.getInstance().getStyleHandler();
    }

    public MBSegmentedControlBar(Context context, AttributeSet attrs) {
        this(context);
    }

    public MBSegmentedControlBar(Context context, AttributeSet attrs, int defStyle) {
        this(context);
    }


    public MBSegmentedControlBar(Context context, List<String> titles, String style) {
        this(context);

        _styleHandler.styleSegmentedControlBar(this, style);

    /*
     * Let's process our titles
     */
        processTitles(context, titles, style);

    }

    /**
     * Updates the titles of the buttons; useful when different titles appear in landscape vs. portrait mode.
     * Note that only the titles are updated; no new buttons are made or removed, so the amount of passed titles should be equal to the amount of buttons.
     *
     * @param titles
     */
    public void updateTitles(List<String> titles) {
        if (titles.size() != _itemButtons.size())
            throw new MBException("Passed titles " + titles.size() + " does not equal buttons "
                    + _itemButtons.size());
        for (int i = 0; i < titles.size(); ++i) {
            Button button = _itemButtons.get(i);
            String title = titles.get(i);
            button.setText(title);
            button.requestLayout();
        }
    }

    private void processTitles(Context context, List<String> titles, String style) {
        int childCount = titles.size();
        for (int i = 0; i < childCount; i++) {

            String title = titles.get(i);

            // Add the button item to the list
            Button button = createSegmentedControlItem(context, title, style, i, childCount);
            _itemButtons.add(button);
            addView(button);
        }

    }

    private Button createSegmentedControlItem(Context context, String title, String style, final int itemIndex, final int count) {
        Button item = new Button(context);
        item.setText(title);
        item.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (getMBOnSelectedListener() != null && _selectedIndex != itemIndex) {
                    getMBOnSelectedListener().onSelected(itemIndex, count);
                }
                if (getMBOnClickListener() != null) {
                    _onClickListener.onClick(itemIndex, count);
                }

                setFocusedItem(itemIndex);

            }
        });

        // Let's do some styling
        if (itemIndex == 0) {
            // Style our first item
            _styleHandler.styleFirstSegmentedItem(item, style);
        } else if (itemIndex == (count - 1)) {
            // Style our last item
            _styleHandler.styleLastSegmentedItem(item, style);
        } else {
            // Style our centered items
            _styleHandler.styleCenterSegmentedItem(item, style);
        }

        return item;
    }

    public void setFocusedItem(int itemIndex) {
    /*
     * Make the control bar change it's items states
     */
        if (itemIndex != _selectedIndex) {
            _itemButtons.get(_selectedIndex).setSelected(false);
        }
        _itemButtons.get(itemIndex).setSelected(true);
        _selectedIndex = itemIndex;

        if (getMBOnSelectedListener() != null) {
            getMBOnSelectedListener().onSelected(itemIndex, _itemButtons.size());
        }
    }

    public void setTitle(int index, String title) {
        Button button = _itemButtons.get(index);
        if (button != null) {
            button.setText(title);
        }
    }

    public int getSelectedIndex() {
        return _selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        _selectedIndex = selectedIndex;
    }

    public MBOnSelectedListener getMBOnSelectedListener() {
        return _onSelectedListener;
    }

    public void setMBOnSelectedListener(MBOnSelectedListener onSelectedListener) {
        _onSelectedListener = onSelectedListener;
    }

    public MBOnClickListener getMBOnClickListener() {
        return _onClickListener;
    }

    public void setMBOnClickListener(MBOnClickListener onClickListener) {
        _onClickListener = onClickListener;
    }

    public interface MBOnSelectedListener {
        void onSelected(int selectedIndex, int itemCount);
    }

    public interface MBOnClickListener {
        void onClick(int clickedIndex, int itemCount);
    }

}

package com.itude.mobile.mobbl.core.view.bindings;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.itude.mobile.mobbl.core.view.MBComponent;

import java.util.List;

public class AdapterViewBinder implements ViewBinder {
    private final int id;

    protected AdapterViewBinder(int id) {
        this.id = id;
    }

    public static AdapterViewBinder getInstance(int id) {
        return new AdapterViewBinder(id);
    }

    @Override
    public View bindView(BuildState state) {
        @SuppressWarnings("unchecked")
        AdapterView<MOBBLListViewAdapter> result = (AdapterView<MOBBLListViewAdapter>) state.parent.findViewById(id);
        result.setAdapter(new MOBBLListViewAdapter(state));
        return result;
    }

    private static class MOBBLListViewAdapter extends ArrayAdapter<MBComponent> {

        private BuildState state;

        public MOBBLListViewAdapter(BuildState buildState) {
            super(buildState.context, 0);

            state = buildState.clone();

            List<MBComponent> children = buildState.component.getChildrenOfKind(MBComponent.class);

            setNotifyOnChange(false);
            for (MBComponent child : children)
                add(child);

            notifyDataSetChanged();

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            BuildState state = this.state.clone();
            state.component = getItem(position);
            state.element = state.component.getDocument().getValueForPath(state.component.getAbsoluteDataPath());
            state.parent = parent;
            state.recycledView = convertView;

            return state.mainViewBinder.bindView(state);
        }

    }

}
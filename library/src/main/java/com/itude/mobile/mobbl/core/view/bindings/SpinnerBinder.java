package com.itude.mobile.mobbl.core.view.bindings;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.itude.mobile.android.util.ComparisonUtil;
import com.itude.mobile.mobbl.core.configuration.mvc.MBDomainDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBDomainValidatorDefinition;
import com.itude.mobile.mobbl.core.services.MBLocalizationService;
import com.itude.mobile.mobbl.core.view.MBField;

public class SpinnerBinder extends BaseViewBinder {
    private final int id;
    private final int dropDownResource;
    private final int spinnerItemResource;

    protected SpinnerBinder(int id, int dropDownResource, int spinnerItemResource) {
        this.id = id;
        this.dropDownResource = dropDownResource;
        this.spinnerItemResource = spinnerItemResource;
    }

    public static SpinnerBinder getInstance(int id) {
        return new SpinnerBinder(id, android.R.layout.simple_spinner_dropdown_item, android.R.layout.simple_spinner_item);
    }

    @Override
    protected View bindSpecificView(BuildState state) {
        Spinner spinner = (Spinner) state.parent.findViewById(id);

        if (state.component instanceof MBField) {
            MBField field = (MBField) state.component;
            if (field.getDomain() != null) {
                DomainAdapter adapter = new DomainAdapter(state.context, spinnerItemResource, field.getDomain());
                adapter.setDropDownViewResource(dropDownResource);

                spinner.setAdapter(adapter);

                String fieldValue = field.getValue();
                if (fieldValue == null) fieldValue = field.getValueIfNil();
                int selected = 0;
                for (MBDomainValidatorDefinition validator : field.getDomain().getDomainValidators()) {
                    String value = validator.getValue();
                    if (ComparisonUtil.safeEquals(fieldValue, value)) break;
                    ++selected;
                }

                spinner.setSelection(selected);
            }

            spinner.setOnItemSelectedListener(field);
            spinner.setOnKeyListener(field);
        }

        return spinner;
    }

    private static class DomainAdapter extends ArrayAdapter<String> {

        public DomainAdapter(Context context, int spinnerItemResource, MBDomainDefinition domain) {
            super(context, spinnerItemResource);
            setNotifyOnChange(false);
            for (MBDomainValidatorDefinition validator : domain.getDomainValidators()) {
                add(MBLocalizationService.getInstance().getText(validator.getTitle()));
            }
            notifyDataSetChanged();
        }

    }
}

package com.itude.mobile.mobbl.core.view.builders.datatypes;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl.core.configuration.mvc.MBDomainValidatorDefinition;
import com.itude.mobile.mobbl.core.services.MBLocalizationService;
import com.itude.mobile.mobbl.core.view.MBField;

public class DomainFormatter implements MBDataTypeFormatter {

    @Override
    public String format(MBField field) {
        String value = field.getValue();
        if (StringUtil.isEmpty(value)) value = field.getValueIfNil();
        if (value != null) {
            for (MBDomainValidatorDefinition domainValue : field.getDomain().getDomainValidators()) {
                if (value.equals(domainValue.getValue()))
                    return MBLocalizationService.getLocalizedString(domainValue.getTitle());
            }
        }

        return null;
    }

}

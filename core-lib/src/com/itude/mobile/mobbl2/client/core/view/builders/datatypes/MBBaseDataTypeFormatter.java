package com.itude.mobile.mobbl2.client.core.view.builders.datatypes;

import java.util.Locale;

import com.itude.mobile.mobbl2.client.core.services.MBLocalizationService;
import com.itude.mobile.mobbl2.client.core.view.MBField;

public abstract class MBBaseDataTypeFormatter implements MBDataTypeFormatter
{

  protected Locale getLocale()
  {
    return MBLocalizationService.getInstance().getLocale();
  }

  @Override
  public String format(MBField field)
  {
    try
    {
      String value = field.getValue();
      if (value == null) return null;
      boolean fieldValueSameAsNilValue = value.equals(field.getValueIfNil());

      if (!value.equals(fieldValueSameAsNilValue)) return actuallyFormat(value);
      else return value;

    }
    catch (NumberFormatException nfe)
    {
      throw new NumberFormatException("Unable to format value for field: " + field.toString());
    }

  }

  protected abstract String actuallyFormat(String value);

}

package com.itude.mobile.mobbl2.client.core.view.builders.datatypes;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.itude.mobile.android.util.DateUtil;
import com.itude.mobile.mobbl2.client.core.services.MBLocalizationService;
import com.itude.mobile.mobbl2.client.core.view.MBField;

public class DateTimeFormatter implements MBDataTypeFormatter
{

  @Override
  public String format(MBField field)
  {
    if (field.getFormatMask() != null)
    {
      // Get a date from a xml-dateFormat
      String xmlDate = field.getValue();

      // Formats the date depending on the current date. 
      if (field.getFormatMask().equals("dateOrTimeDependingOnCurrentDate"))
      {
        return DateUtil.formatDateDependingOnCurrentDate(MBLocalizationService.getInstance().getLocale(), xmlDate);
      }
      else
      {
        Date date = DateUtil.dateFromXML(xmlDate);

        if (date == null) return null;
        SimpleDateFormat df = new SimpleDateFormat(field.getFormatMask(), MBLocalizationService.getInstance().getLocale());
        return df.format(date);
      }

    }
    return field.getValue();
  }
}

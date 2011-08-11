package com.itude.mobile.mobbl2.client.core.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.itude.mobile.mobbl2.client.core.util.exceptions.MBDateParsingException;

public final class DateUtilities
{
  private static final String                        DEFAULT_DATE_FORMAT    = "yyyy-MM-dd'T'HH:mm:ss";
  private static final ThreadLocal<SimpleDateFormat> TLDEFAULTDATEFORMATTER = new ThreadLocal<SimpleDateFormat>()
                                                                            {
                                                                              @Override
                                                                              protected SimpleDateFormat initialValue()
                                                                              {
                                                                                return new SimpleDateFormat(DEFAULT_DATE_FORMAT);
                                                                              }
                                                                            };

  private DateUtilities()
  {
  }

  // Formats the date depending on the current date assuming the receiver is a date string 
  // If the date is equal to the current date, the time is given back as a string
  // If the date is NOT equal to the current date, then a a date is presented back as a string
  public static String formatDateDependingOnCurrentDate(String dateString)
  {
    String result = dateString;
    Date date = dateFromXML(dateString);

    String dateFormatMask = "";

    // We can't just compare two dates, because the time is also compared.
    // Therefore the time is removed and the two dates without time are compared
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);

    Calendar today = Calendar.getInstance();
    today.setTime(new Date());

    if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH)
        && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR))
    {
      dateFormatMask = "HH:mm:ss";
    }
    else
    {
      dateFormatMask = "dd-MM-yy";
    }

    // Format the date
    try
    {
      SimpleDateFormat df = new SimpleDateFormat(dateFormatMask);
      result = df.format(date);

      return result;
    }
    catch (Exception e)
    {
      throw new MBDateParsingException("Could not get format date depending on current date with input string: " + dateString, e);
    }
  }

  public synchronized static String formatString(String stringToFormat, String format)
  {
    try
    {
      Date date = dateFromXML(stringToFormat);
      if (date != null)
      {
        return dateToString(date, format);
      }
      else
      {
        return null;
      }
    }
    catch (Exception e)
    {
      throw new MBDateParsingException("Could not parse date from xml value: " + stringToFormat, e);
    }
  }

  public synchronized static Date dateFromXML(String stringToFormat)
  {
    try
    {
      String dateString = stringToFormat.substring(0, 19);
      if (dateString != null) return TLDEFAULTDATEFORMATTER.get().parse(dateString);
      else return null;
    }
    catch (Exception e)
    {
      throw new MBDateParsingException("Could not parse date from xml value: " + stringToFormat, e);
    }
  }

  public synchronized static Date dateFromXML(String stringToFormat, String format)
  {
    if (StringUtilities.isEmpty(format))
    {
      return dateFromXML(stringToFormat);
    }
    else
    {

      try
      {
        String dateString = stringToFormat.substring(0, 19);
        if (dateString != null)
        {
          SimpleDateFormat df = new SimpleDateFormat(format);

          return df.parse(dateString);
        }
        else
        {
          return null;
        }
      }
      catch (Exception e)
      {
        throw new MBDateParsingException("Could not parse date from xml value: " + stringToFormat, e);
      }
    }
  }

  public static String dateToString(Date date)
  {
    return dateToStringDefaultFormat(date);
  }

  public static String dateToString(Date date, String format)
  {
    if (StringUtilities.isEmpty(format)) return dateToStringDefaultFormat(date);
    SimpleDateFormat df = new SimpleDateFormat(format);

    try
    {
      return df.format(date);
    }
    catch (Exception e)
    {
      throw new MBDateParsingException("Could not convert date to string with input date: " + date, e);
    }

  }

  private static String dateToStringDefaultFormat(Date dateToFormat)
  {
    return TLDEFAULTDATEFORMATTER.get().format(dateToFormat);
  }

  public static String longToString(long time)
  {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(time);
    return dateToStringDefaultFormat(calendar.getTime());
  }

  public static String longToString(long time, String format)
  {
    if (StringUtilities.isEmpty(format)) return longToString(time);
    try
    {
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(time);
      SimpleDateFormat df = new SimpleDateFormat(format);
      return df.format(calendar.getTime());
    }
    catch (Exception e)
    {
      throw new MBDateParsingException("Could not convert long to string with input long: " + time, e);
    }
  }
}
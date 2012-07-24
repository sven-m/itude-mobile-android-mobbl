package com.itude.mobile.mobbl2.client.core.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.util.Log;

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

    DateFormat df;
    // We can't just compare two dates, because the time is also compared.
    // Therefore the time is removed and the two dates without time are compared
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);

    Calendar today = Calendar.getInstance();
    today.setTime(new Date());

    if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH)
        && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR))
    {
      df = new SimpleDateFormat("HH:mm:ss");
    }
    else
    {
      df = DateFormat.getDateInstance(DateFormat.SHORT,
                                      StringUtilities.getDefaultFormattingLocale() != null
                                          ? StringUtilities.getDefaultFormattingLocale()
                                          : Locale.getDefault());
    }

    // Format the date
    try
    {
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
    Date value = null;
    if (StringUtilities.isNotBlank(stringToFormat))
    {
      try
      {
        String dateString = stringToFormat.substring(0, 19);
        if (dateString != null)
        {
          value = TLDEFAULTDATEFORMATTER.get().parse(dateString);
        }
      }
      catch (Exception e)
      {
        throw new MBDateParsingException("Could not parse date from xml value: " + stringToFormat, e);
      }
    }
    return value;
  }

  public synchronized static Date dateFromString(String stringToFormat, String format)
  {
    Date value = null;
    if (StringUtilities.isNotBlank(stringToFormat))
    {
      try
      {
        SimpleDateFormat df = new SimpleDateFormat(format);
        value = df.parse(stringToFormat);
      }
      catch (Exception e)
      {
        throw new MBDateParsingException("Could not parse date from value: " + stringToFormat, e);
      }
    }
    return value;
  }

  public synchronized static String getYear(Date date, String format)
  {
    try
    {
      SimpleDateFormat df = new SimpleDateFormat(format);
      return df.format(date);
    }
    catch (Exception e)
    {
      throw new MBDateParsingException("Could not get year from value: " + date.getYear(), e);
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

  public static void setCalanderTime(Calendar calender, String time)
  {
    if (StringUtilities.isNotBlank(time))
    {
      try
      {
        calender.setTime(TLDEFAULTDATEFORMATTER.get().parse(time));
      }
      catch (ParseException e)
      {
        Log.e(Constants.APPLICATION_NAME, "Couldn't parse date/time value" + time, e);
      }
    }
  }

  public static Calendar createNewCalenderWithTime(String time)
  {
    Calendar calender = null;
    if (StringUtilities.isNotBlank(time))
    {
      calender = Calendar.getInstance();
      setCalanderTime(calender, time);
    }
    return calender;
  }

  public static int subtractDays(Date date1, Date date2)
  {
    GregorianCalendar gc1 = new GregorianCalendar();
    if (date1 != null)
    {
      gc1.setTime(date1);
    }
    GregorianCalendar gc2 = new GregorianCalendar();
    if (date2 != null)
    {
      gc2.setTime(date2);
    }

    int days1 = 0;
    int days2 = 0;
    int maxYear = Math.max(gc1.get(Calendar.YEAR), gc2.get(Calendar.YEAR));

    GregorianCalendar gctmp = (GregorianCalendar) gc1.clone();
    for (int f = gctmp.get(Calendar.YEAR); f < maxYear; f++)
    {
      days1 += gctmp.getActualMaximum(Calendar.DAY_OF_YEAR);
      gctmp.add(Calendar.YEAR, 1);
    }

    gctmp = (GregorianCalendar) gc2.clone();
    for (int f = gctmp.get(Calendar.YEAR); f < maxYear; f++)
    {
      days2 += gctmp.getActualMaximum(Calendar.DAY_OF_YEAR);
      gctmp.add(Calendar.YEAR, 1);
    }

    days1 += gc1.get(Calendar.DAY_OF_YEAR) - 1;
    days2 += gc2.get(Calendar.DAY_OF_YEAR) - 1;

    return (days2 - days1);
  }

  public static Date addDays(Date date, int days)
  {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.add(Calendar.DATE, days);
    return cal.getTime();
  }

}
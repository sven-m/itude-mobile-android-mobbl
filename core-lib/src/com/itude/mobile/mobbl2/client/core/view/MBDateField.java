package com.itude.mobile.mobbl2.client.core.view;

import java.util.Calendar;

public class MBDateField
{
  private int _year;
  private int _month;
  private int _day;

  public MBDateField()
  {
    final Calendar c = Calendar.getInstance();
    _year = c.get(Calendar.YEAR);
    _month = c.get(Calendar.MONTH);
    _day = c.get(Calendar.DAY_OF_MONTH);
  }

  public void setDate(int year, int month, int day)
  {
    _year = year;
    _month = month;
    _day = day;
  }

  @Override
  public String toString()
  {
    return new StringBuilder().append(_day).append("-").append(_month + 1).append("-").append(_year).toString();
  }

  public int getYear()
  {
    return _year;
  }

  public int getMonth()
  {
    return _month;
  }

  public int getDay()
  {
    return _day;
  }

  public Calendar getCalender()
  {
    final Calendar c = Calendar.getInstance();
    c.set(_year, _month, _day);
    return c;
  }

}

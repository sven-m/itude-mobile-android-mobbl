/*
 * (C) Copyright ItudeMobile.
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
package com.itude.mobile.mobbl2.client.core.view;

import java.util.Calendar;

import com.itude.mobile.android.util.DateUtil;
import com.itude.mobile.android.util.StringUtil;

public class MBDateField
{
  private final Calendar _calendar;

  public MBDateField()
  {
    _calendar = Calendar.getInstance();
  }

  public void setDateAndTime(int year, int month, int day, int hourOfDay, int minute)
  {
    setYear(year);
    setMonth(month);
    setDay(day);
    setHourOfDay(hourOfDay);
    setMinute(minute);
  }

  public void setDate(int year, int month, int day)
  {
    setYear(year);
    setMonth(month);
    setDay(day);
  }

  public void setTime(int hourOfDay, int minute)
  {
    setHourOfDay(hourOfDay);
    setMinute(minute);
  }

  @Override
  public String toString()
  {
    return new StringBuilder().append(getDay())//
        .append("-")//
        .append(getMonth() + 1)//
        .append("-")//
        .append(getYear())//
        .append(" ")//
        .append(getHourOfDay())//
        .append(getMinute())//
        .toString();
  }

  public int getYear()
  {
    return _calendar.get(Calendar.YEAR);
  }

  public int getMonth()
  {
    return _calendar.get(Calendar.MONTH);
  }

  public int getDay()
  {
    return _calendar.get(Calendar.DAY_OF_MONTH);
  }

  public int getHourOfDay()
  {
    return _calendar.get(Calendar.HOUR_OF_DAY);
  }

  public int getMinute()
  {
    return _calendar.get(Calendar.MINUTE);
  }

  public Calendar getCalender()
  {
    return _calendar;
  }

  public void setTime(String dateTimeString)
  {
    if (StringUtil.isNotBlank(dateTimeString))
    {
      DateUtil.setCalanderTime(_calendar, dateTimeString);
    }
  }

  public void setMinute(int minute)
  {
    _calendar.set(Calendar.MINUTE, minute);
  }

  public void setHourOfDay(int hourOfDay)
  {
    _calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
  }

  public void setDay(int day)
  {
    _calendar.set(Calendar.DAY_OF_MONTH, day);
  }

  public void setMonth(int month)
  {
    _calendar.set(Calendar.MONTH, month);
  }

  public void setYear(int year)
  {
    _calendar.set(Calendar.YEAR, year);
  }
}

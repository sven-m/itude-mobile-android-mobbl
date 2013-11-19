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
package com.itude.mobile.mobbl2.client.core.view.builders.datatypes;

import java.util.HashMap;
import java.util.Map;

import com.itude.mobile.mobbl2.client.core.view.MBField;

public final class MBDataTypeFormatterFactory
{
  private static MBDataTypeFormatterFactory      _instance = new MBDataTypeFormatterFactory();

  private final Map<String, MBDataTypeFormatter> _formatters;

  protected MBDataTypeFormatterFactory()
  {
    _formatters = new HashMap<String, MBDataTypeFormatter>();
    registerDefaultFormatters();
  }

  private void registerDefaultFormatters()
  {

    registerFormatter("dateTime", new DateTimeFormatter());
    registerFormatter("numberWithTwoDecimals", new NumberWithTwoDecimalsFormatter());
    registerFormatter("numberWithThreeDecimals", new NumberWithThreeDecimalsFormatter());
    registerFormatter("priceWithTwoDecimals", new PriceWithTwoDecimalsFormatter());
    registerFormatter("priceWithThreeDecimals", new PriceWithThreeDecimalsFormatter());
    registerFormatter("priceWithFourDecimals", new PriceWithFourDecimalsFormatter());
    registerFormatter("volume", new VolumeFormatter());
    registerFormatter("percentageWithTwoDecimals", new PercentageWithTwoDecimalsFormatter());
  }

  public void registerFormatter(String name, MBDataTypeFormatter formatter)
  {
    _formatters.put(name, formatter);
  }

  public String formatField(MBField field)
  {
    String dataType = field.getDataType();
    MBDataTypeFormatter formatter = _formatters.get(dataType);
    if (formatter != null) return formatter.format(field);
    else return field.getValue();
  }

  public static MBDataTypeFormatterFactory getInstance()
  {
    return _instance;
  }

}

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

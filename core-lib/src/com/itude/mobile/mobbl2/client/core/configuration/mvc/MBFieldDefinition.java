package com.itude.mobile.mobbl2.client.core.configuration.mvc;

import com.itude.mobile.mobbl2.client.core.util.StringUtilities;
import com.itude.mobile.mobbl2.client.core.view.MBConditionalDefinition;
import com.itude.mobile.mobbl2.client.core.view.MBStylableDefinition;

public class MBFieldDefinition extends MBConditionalDefinition implements MBStylableDefinition
{
  private String _label;
  private String _path;
  private String _style;
  private String _displayType;
  private String _dataType;
  private String _text;
  private String _outcomeName;
  private String _width;
  private String _height;
  private String _formatMask;
  private String _alignment;
  private String _valueIfNil;
  private String _hidden;
  private String _custom1;
  private String _custom2;
  private String _custom3;
  private String _required;

  @Override
  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    String bodyText = null;
    if (!_text.equals(""))
    {
      bodyText = _text;
    }

    StringUtilities.appendIndentString(appendToMe, level)
                  .append("<Field")
                  .append(getAttributeAsXml("label", _label))
                  .append(getAttributeAsXml("path", _path))
                  .append(getAttributeAsXml("type", _displayType))
                  .append(getAttributeAsXml("dataType", _dataType))
                  .append(getAttributeAsXml("outcome", _outcomeName))
                  .append(getAttributeAsXml("formatMask", _formatMask))
                  .append(getAttributeAsXml("alignment", _alignment))
                  .append(getAttributeAsXml("valueIfNil", _valueIfNil))
                  .append(getAttributeAsXml("width", _width))
                  .append(getAttributeAsXml("height", _height))
                  .append(getAttributeAsXml("hidden", _hidden))
                  .append(getAttributeAsXml("required", _required));

    if (bodyText != null)
    {
      appendToMe.append(">")
                  .append(bodyText)
                  .append("</Field>\n");
    }
    else
    {
      appendToMe.append("/>\n");
    }

    return appendToMe;
  }

  public String getOutcomeName()
  {
    return _outcomeName;
  }

  public void setOutcomeName(String outcomeName)
  {
    _outcomeName = outcomeName;
  }

  public String getLabel()
  {
    return _label;
  }

  public void setLabel(String label)
  {
    _label = label;
  }

  public String getPath()
  {
    return _path;
  }

  public void setPath(String path)
  {
    _path = path;
  }

  public String getStyle()
  {
    return _style;
  }

  public void setStyle(String style)
  {
    _style = style;
  }

  public String getText()
  {
    return _text;
  }

  public void setText(String text)
  {
    _text = text;
  }

  public String getDisplayType()
  {
    return _displayType;
  }

  public void setDisplayType(String displayType)
  {
    _displayType = displayType;
  }

  public String getDataType()
  {
    return _dataType;
  }

  public void setDataType(String dataType)
  {
    _dataType = dataType;
  }

  public String getRequired()
  {
    return _required;
  }

  public void setRequired(String required)
  {
    _required = required;
  }

  public String getWidth()
  {
    return _width;
  }

  public void setWidth(String width)
  {
    _width = width;
  }

  public String getHeight()
  {
    return _height;
  }

  public void setHeight(String height)
  {
    _height = height;
  }

  public String getFormatMask()
  {
    return _formatMask;
  }

  public void setFormatMask(String formatMask)
  {
    _formatMask = formatMask;
  }

  public String getAlignment()
  {
    return _alignment;
  }

  public void setAlignment(String alignment)
  {
    _alignment = alignment;
  }

  public String getValueIfNil()
  {
    return _valueIfNil;
  }

  public void setValueIfNil(String valueIfNil)
  {
    _valueIfNil = valueIfNil;
  }

  public String getHidden()
  {
    return _hidden;
  }

  public void setHidden(String hidden)
  {
    _hidden = hidden;
  }

  public String getCustom1()
  {
    return _custom1;
  }

  public void setCustom1(String custom1)
  {
    _custom1 = custom1;
  }

  public String getCustom2()
  {
    return _custom2;
  }

  public void setCustom2(String custom2)
  {
    _custom2 = custom2;
  }

  public String getCustom3()
  {
    return _custom3;
  }

  public void setCustom3(String custom3)
  {
    _custom3 = custom3;
  }

}

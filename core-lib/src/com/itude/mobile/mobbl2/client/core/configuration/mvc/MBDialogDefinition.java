package com.itude.mobile.mobbl2.client.core.configuration.mvc;

import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.exceptions.MBInvalidDialogDefinitionException;
import com.itude.mobile.mobbl2.client.core.util.StringUtilities;

public class MBDialogDefinition extends MBDefinition
{
  private String _title;
  private String _mode;
  private String _icon;
  private String _groupName;
  private String _position;

  public StringBuffer asXmlWithLevel(StringBuffer p_appendToMe, int level)
  {
    return StringUtilities.appendIndentString(p_appendToMe, level)
                    .append("<Dialog name='")
                    .append(getName())
                    .append('\'')
                    .append(getAttributeAsXml("mode", _mode))
                    .append(getAttributeAsXml("title", _title))
                    .append(getAttributeAsXml("icon", _icon))
                    .append("/>\n");
  }

  public void validateDefinition()
  {
    if (getName() == null)
    {
      String message = "no name set for dialog";
      throw new MBInvalidDialogDefinitionException(message);
    }
  }

  public String getTitle()
  {
    return _title;
  }

  public void setTitle(String title)
  {
    _title = title;
  }

  public String getMode()
  {
    return _mode;
  }

  public void setMode(String mode)
  {
    _mode = mode;
  }

  public String getIcon()
  {
    return _icon;
  }

  public void setIcon(String icon)
  {
    _icon = icon;
  }

  public String getGroupName()
  {
    // TODO Auto-generated method stub
    return _groupName;
  }
  
  public void setGroupName(String groupName){
    _groupName = groupName;
  }

  public String getPosition()
  {
    // TODO Auto-generated method stub
    return _position;
  }

  public void setPosition(String position){
    _position = position;
  }
  
}

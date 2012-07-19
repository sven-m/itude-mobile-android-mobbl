package com.itude.mobile.mobbl2.client.core.configuration.mvc;

import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.exceptions.MBInvalidDialogDefinitionException;
import com.itude.mobile.mobbl2.client.core.util.StringUtilities;

public class MBDialogDefinition extends MBDefinition
{
  private String _title;
  private String _titlePortrait;
  private String _mode;
  private String _icon;
  private String _parent;
  private String _addToNavbar;
  private String _domain;
  private String _action;

  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    return StringUtilities.appendIndentString(appendToMe, level)//
        .append("<Dialog name='")//
        .append(getName())//
        .append('\'')//
        .append(getAttributeAsXml("mode", _mode))//
        .append(getAttributeAsXml("title", _title))//
        .append(getAttributeAsXml("titlePortrait", _titlePortrait))//
        .append(getAttributeAsXml("icon", _icon))//
        .append(getAttributeAsXml("addToNavbar", _addToNavbar))//
        .append(getAttributeAsXml("domain", _domain))//
        .append(getAttributeAsXml("action", _action))//
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

  public void setTitlePortrait(String titlePortrait)
  {
    _titlePortrait = titlePortrait;
  }

  public String getTitlePortrait()
  {
    return _titlePortrait;
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

  public String getParent()
  {
    return _parent;
  }

  public void setParent(String parent)
  {
    _parent = parent;
  }

  public String getAddToNavbar()
  {
    return _addToNavbar;
  }

  public void setAddToNavbar(String addToNavbar)
  {
    _addToNavbar = addToNavbar;
  }

  public void setDomain(String domain)
  {
    _domain = domain;
  }

  public String getDomain()
  {
    return _domain;
  }

  public void setAction(String action)
  {
    _action = action;
  }

  public String getAction()
  {
    return _action;
  }

  public boolean isGroup()
  {
    return false;
  }

}

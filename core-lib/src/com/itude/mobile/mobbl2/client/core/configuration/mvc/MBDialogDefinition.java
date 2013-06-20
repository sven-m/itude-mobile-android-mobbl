package com.itude.mobile.mobbl2.client.core.configuration.mvc;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.exceptions.MBInvalidDialogDefinitionException;
import com.itude.mobile.mobbl2.client.core.controller.exceptions.MBExpressionNotBooleanException;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.services.MBDataManagerService;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.MBParseUtil;
import com.itude.mobile.mobbl2.client.core.view.MBConditionalDefinition;

public class MBDialogDefinition extends MBConditionalDefinition
{
  private String _title;
  private String _titlePortrait;
  private String _mode;
  private String _icon;
  private String _parent;
  private String _showAs;
  private String _domain;
  private String _action;

  @Override
  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    return StringUtil.appendIndentString(appendToMe, level)//
        .append("<Dialog name='")//
        .append(getName())//
        .append('\'')//
        .append(getAttributeAsXml("mode", _mode))//
        .append(getAttributeAsXml("title", _title))//
        .append(getAttributeAsXml("titlePortrait", _titlePortrait))//
        .append(getAttributeAsXml("icon", _icon))//
        .append(getAttributeAsXml("showAs", _showAs))//
        .append(getAttributeAsXml("domain", _domain))//
        .append(getAttributeAsXml("action", _action))//
        .append("/>\n");
  }

  @Override
  public void validateDefinition()
  {
    if (getName() == null)
    {
      String message = "no name set for dialog";
      throw new MBInvalidDialogDefinitionException(message);
    }
  }

  public boolean isPreConditionValid()
  {
    boolean isValid = true;
    if (getPreCondition() != null)
    {
      MBDocument doc = MBDataManagerService.getInstance().loadDocument(MBConfigurationDefinition.DOC_SYSTEM_EMPTY);

      String result = doc.evaluateExpression(this.getPreCondition());
      Boolean bool = MBParseUtil.strictBooleanValue(result);
      if (bool != null) return bool;
      String msg = "Expression of dialog " + getName() + " precondition=" + getPreCondition() + " is not boolean (result=" + result + ")";
      throw new MBExpressionNotBooleanException(msg);
    }

    return isValid;
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
    return _titlePortrait != null ? _titlePortrait : getTitle();
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

  public String getShowAs()
  {
    return _showAs;
  }

  public void setShowAs(String showAs)
  {
    _showAs = showAs;
  }

  public boolean isShowAsTab()
  {
    return Constants.C_SHOW_AS_TAB.equals(_showAs);
  }

  public boolean isShowAsMenu()
  {
    return Constants.C_SHOW_AS_MENU.equals(_showAs);
  }

  public boolean isShowAsDocument()
  {
    return Constants.C_SHOW_AS_DOCUMENT.equals(_showAs);
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

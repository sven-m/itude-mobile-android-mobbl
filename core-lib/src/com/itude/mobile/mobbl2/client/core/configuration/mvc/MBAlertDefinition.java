package com.itude.mobile.mobbl2.client.core.configuration.mvc;

import java.util.ArrayList;
import java.util.List;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;

public class MBAlertDefinition extends MBDefinition
{

  private String             _type;
  private String             _documentName;
  private String             _style;
  private String             _title;
  private String             _titlePath;
  private List<MBDefinition> _children;

  public MBAlertDefinition()
  {
    _children = new ArrayList<MBDefinition>();
  }

  @Override
  public void addChildElement(MBDefinition definition)
  {
    addChild(definition);
  }

  @Override
  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {

    StringUtil.appendIndentString(appendToMe, level).append("<Alert name='").append(getName()).append("' document='")
        .append(getDocumentName()).append(getAttributeAsXml("title", getTitle())).append(">\n");

    for (MBDefinition child : getChildren())
    {
      child.asXmlWithLevel(appendToMe, level + 2);
    }

    return StringUtil.appendIndentString(appendToMe, level).append("</Alert>\n");
  }

  public void addChild(MBDefinition child)
  {
    _children.add(child);
  }

  public List<MBDefinition> getChildren()
  {
    return _children;
  }

  public void setChildren(List<MBDefinition> children)
  {
    _children = children;
  }

  public String getDocumentName()
  {
    return _documentName;
  }

  public void setDocumentName(String documentName)
  {
    this._documentName = documentName;
  }

  public String getType()
  {
    return _type;
  }

  public void setType(String type)
  {
    this._type = type;
  }

  public String getStyle()
  {
    return _style;
  }

  public void setStyle(String style)
  {
    this._style = style;
  }

  public String getTitle()
  {
    return _title;
  }

  public void setTitle(String title)
  {
    this._title = title;
  }

  public String getTitlePath()
  {
    return _titlePath;
  }

  public void setTitlePath(String titlePath)
  {
    this._titlePath = titlePath;
  }

}

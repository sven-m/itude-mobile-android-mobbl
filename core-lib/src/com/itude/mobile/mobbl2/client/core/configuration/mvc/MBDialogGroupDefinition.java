package com.itude.mobile.mobbl2.client.core.configuration.mvc;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.exceptions.MBInvalidDialogDefinitionException;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.StringUtilities;

public class MBDialogGroupDefinition extends MBDialogDefinition
{
  private List<MBDialogDefinition> _children;

  public MBDialogGroupDefinition()
  {
    _children = new ArrayList<MBDialogDefinition>();
  }

  public void addDialog(MBDialogDefinition dialogDef)
  {
    if (_children.contains(dialogDef))
    {
      Log.w(Constants.APPLICATION_NAME, "Group contains duplicate definitions for dialog " + dialogDef.getName() + " in group " + getName());
    }

    _children.add(dialogDef);
  }

  @Override
  public void addChildElement(MBDialogDefinition child)
  {
    addDialog(child);
  }

  public MBDialogDefinition getDialogDefinition(String name)
  {
    for (MBDialogDefinition dialogDef : _children)
    {
      if (dialogDef.getName().equals(name)) return dialogDef;
    }

    return null;
  }

  @Override
  public void validateDefinition()
  {
    if (getName() == null) throw new MBInvalidDialogDefinitionException("no name set for dialogGroup");
  }

  @Override
  public StringBuffer asXmlWithLevel(StringBuffer p_appendToMe, int level)
  {
    StringUtilities.appendIndentString(p_appendToMe, level).append("<DialogGroup name='").append(getName()).append('\'')
        .append(getAttributeAsXml("mode", getMode())).append(getAttributeAsXml("title", getTitle()))
        .append(getAttributeAsXml("icon", getIcon())).append(">\n");

    for (MBDialogDefinition dialog : _children)
    {
      dialog.asXmlWithLevel(p_appendToMe, level + 2);
    }

    StringUtilities.appendIndentString(p_appendToMe, level).append("<DialogGroup/>");

    return p_appendToMe;
  }

  public List<MBDialogDefinition> getChildren()
  {
    return _children;
  }
}

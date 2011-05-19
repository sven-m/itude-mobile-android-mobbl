package com.itude.mobile.mobbl2.client.core.configuration.mvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;
import com.itude.mobile.mobbl2.client.core.model.MBElement;
import com.itude.mobile.mobbl2.client.core.util.StringUtilities;

public class MBElementDefinition extends MBDefinition
{
  private final Map<String, MBAttributeDefinition> _attributes;
  private final List<MBAttributeDefinition>        _attributesSorted;
  private final Map<String, MBElementDefinition>   _children;
  private final List<MBElementDefinition>          _childrenSorted;
  private int                                      _minOccurs;
  private int                                      _maxOccurs;

  public MBElementDefinition()
  {
    _attributes = new HashMap<String, MBAttributeDefinition>();
    _attributesSorted = new ArrayList<MBAttributeDefinition>();
    _children = new HashMap<String, MBElementDefinition>();
    _childrenSorted = new ArrayList<MBElementDefinition>();
  }

  @Override
  public StringBuffer asXmlWithLevel(StringBuffer p_appendToMe, int level)
  {
    StringUtilities.appendIndentString(p_appendToMe, level)
                  .append("<Element name='")
                  .append(getName())
                  .append("' minOccurs='")
                  .append(_minOccurs)
                  .append("' maxOccurs='")
                  .append(_maxOccurs)
                  .append("'>\n");
    for (MBAttributeDefinition attr : _attributesSorted)
    {
      attr.asXmlWithLevel(p_appendToMe, level + 2);
    }
    for (MBElementDefinition elemDef : _childrenSorted)
    {
      elemDef.asXmlWithLevel(p_appendToMe, level + 2);
    }
    return StringUtilities.appendIndentString(p_appendToMe, level)
                        .append("</Element>\n");
  }

  @Override
  public void addChildElement(MBAttributeDefinition child)
  {
    addAttribute(child);
  }

  @Override
  public void addChildElement(MBElementDefinition child)
  {
    addElement(child);
  }

  /*@Override
  public void addChildElement(Object child)
  {
    if (child instanceof MBElementDefinition)
    {
      addElement((MBElementDefinition) child);
    }
    if (child instanceof MBAttributeDefinition)
    {
      addAttribute((MBAttributeDefinition) child);
    }
  }*/

  @Override
  public List<MBElementDefinition> getChildElements()
  {
    return _childrenSorted;
  }

  public void addElement(MBElementDefinition element)
  {
    _childrenSorted.add(element);
    _children.put(element.getName(), element);
  }

  public void addAttribute(MBAttributeDefinition attribute)
  {
    _attributes.put(attribute.getName(), attribute);
    _attributesSorted.add(attribute);
  }

  public MBAttributeDefinition getAttributeWithName(String name)
  {
    return _attributes.get(name);
  }

  public List<MBAttributeDefinition> getAttributes()
  {
    return _attributesSorted;
  }

  public String getAttributeNames()
  {
    String result = "";
    for (MBAttributeDefinition attributeDef : _attributesSorted)
    {
      if (result.length() > 0)
      {
        result += ", ";
      }
      result += attributeDef.getName();
    }

    return result;
  }

  public String getChildElementNames()
  {
    String result = "";
    for (MBElementDefinition elementDef : _childrenSorted)
    {
      if (result.length() > 0)
      {
        result += ", ";
      }
      result += elementDef.getName();
    }

    if (result.equals(""))
    {
      result = "[none]";
    }
    return result;
  }

  public List<MBElementDefinition> getChildren()
  {
    return _childrenSorted;
  }

  @Override
  public boolean isValidChild(String name)
  {
    return _children.get(name) != null;
  }

  public boolean isValidAttribute(String name)
  {
    return _attributes.get(name) != null;
  }

  public MBElementDefinition getElementWithPathComponents(List<String> pathComponents)
  {
    if (pathComponents.size() > 0)
    {
      MBElementDefinition root = getChildWithName(pathComponents.get(0));
      pathComponents.remove(0);
      return root.getElementWithPathComponents(pathComponents);
    }
    else
    {
      return this;
    }

  }

  public MBElement createElement()
  {
    MBElement element = new MBElement(this);

    for (MBAttributeDefinition attributeDef : _attributes.values())
    {
      if (attributeDef.getDefaultValue() != null)
      {
        element.setAttributeValue(attributeDef.getDefaultValue(), attributeDef.getName());
      }
    }

    for (MBElementDefinition elementDef : _childrenSorted)
    {
      for (int i = 0; i < elementDef.getMinOccurs(); i++)
      {
        element.addElement(elementDef.createElement());
      }
    }

    return element;
  }

  public MBElementDefinition getChildWithName(String name)
  {
    return _children.get(name);
  }

  public int getMinOccurs()
  {
    return _minOccurs;
  }

  public void setMinOccurs(int minOccurs)
  {
    _minOccurs = minOccurs;
  }

  public int getMaxOccurs()
  {
    return _maxOccurs;
  }

  public void setMaxOccurs(int maxOccurs)
  {
    _maxOccurs = maxOccurs;
  }

}

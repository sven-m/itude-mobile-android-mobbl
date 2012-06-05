package com.itude.mobile.mobbl2.client.core.configuration.mvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.exceptions.MBInvalidElementNameException;
import com.itude.mobile.mobbl2.client.core.model.MBElement;
import com.itude.mobile.mobbl2.client.core.util.Constants;
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
  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    StringUtilities.appendIndentString(appendToMe, level).append("<Element name='").append(getName()).append("' minOccurs='")
        .append(_minOccurs).append("' maxOccurs='").append(_maxOccurs).append("'>\n");
    for (MBAttributeDefinition attr : _attributesSorted)
    {
      attr.asXmlWithLevel(appendToMe, level + 2);
    }
    for (MBElementDefinition elemDef : _childrenSorted)
    {
      elemDef.asXmlWithLevel(appendToMe, level + 2);
    }
    return StringUtilities.appendIndentString(appendToMe, level).append("</Element>\n");
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
    if (!isValidChild(name))
    {
      throw new MBInvalidElementNameException(name);
    }
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

  //Parcelable stuff

  private MBElementDefinition(Parcel in)
  {
    super(in);

    Parcelable[] attributes = in.readParcelableArray(MBAttributeDefinition.class.getClassLoader());
    Parcelable[] children = in.readParcelableArray(MBElementDefinition.class.getClassLoader());
    _minOccurs = in.readInt();
    _maxOccurs = in.readInt();

    _attributes = new HashMap<String, MBAttributeDefinition>(attributes.length);
    _attributesSorted = new ArrayList<MBAttributeDefinition>(attributes.length);
    _children = new HashMap<String, MBElementDefinition>(children.length);
    _childrenSorted = new ArrayList<MBElementDefinition>(children.length);

    for (Parcelable def : attributes)
    {
      addAttribute((MBAttributeDefinition) def);
    }

    for (Parcelable def : children)
    {
      addElement((MBElementDefinition) def);
    }
  }

  @Override
  public int describeContents()
  {
    return Constants.C_PARCELABLE_TYPE_ELEMENT_DEFINITION;
  }

  @Override
  public void writeToParcel(Parcel out, int flags)
  {
    super.writeToParcel(out, flags);

    MBAttributeDefinition[] attributes = (MBAttributeDefinition[]) _attributesSorted.toArray(new MBAttributeDefinition[_attributesSorted
        .size()]);
    MBElementDefinition[] children = (MBElementDefinition[]) _childrenSorted.toArray(new MBElementDefinition[_childrenSorted.size()]);

    out.writeParcelableArray(attributes, flags);
    out.writeParcelableArray(children, flags);
    out.writeInt(_minOccurs);
    out.writeInt(_maxOccurs);
  }

  public static final Parcelable.Creator<MBElementDefinition> CREATOR = new Creator<MBElementDefinition>()
                                                                      {
                                                                        @Override
                                                                        public MBElementDefinition[] newArray(int size)
                                                                        {
                                                                          return new MBElementDefinition[size];
                                                                        }

                                                                        @Override
                                                                        public MBElementDefinition createFromParcel(Parcel in)
                                                                        {
                                                                          return new MBElementDefinition(in);
                                                                        }
                                                                      };

  // End of parcelable stuff

}

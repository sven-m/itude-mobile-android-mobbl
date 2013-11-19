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
package com.itude.mobile.mobbl2.client.core.configuration.mvc;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.exceptions.MBEmptyPathException;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.exceptions.MBInvalidElementNameException;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.exceptions.MBInvalidPathException;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.exceptions.MBUnknownVariableException;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.MBPathUtil;

public class MBDocumentDefinition extends MBDefinition
{
  private final Map<String, MBElementDefinition> _elements = new TreeMap<String, MBElementDefinition>();
  private String                                 _dataManager;
  private boolean                                _autoCreate;
  private String                                 _rootElement;

  public MBDocumentDefinition()
  {
  }

  @Override
  public String toString()
  {
    StringBuffer b = new StringBuffer();
    return asXmlWithLevel(b, 0).toString();
  }

  @Override
  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    StringUtil.appendIndentString(appendToMe, level).append("<Document name='").append(getName()).append("' dataManager='")
        .append(_dataManager).append("' autoCreate='" + _autoCreate).append("' rootElement='" + _rootElement).append("'>\n");
    for (MBElementDefinition elem : _elements.values())
      elem.asXmlWithLevel(appendToMe, level + 2);

    return StringUtil.appendIndentString(appendToMe, level).append("</Document>\n");
  }

  @Override
  public void addChildElement(MBElementDefinition child)
  {
    if (child instanceof MBElementDefinition)
    {
      addElement(child);
    }

  }

  @Override
  public Collection<MBElementDefinition> getChildElements()
  {
    return _elements.values();
  }

  public void addElement(MBElementDefinition element)
  {
    _elements.put(element.getName(), element);
  }

  @Override
  public boolean isValidChild(String elementName)
  {
    return _elements.get(elementName) != null;
  }

  public Collection<MBElementDefinition> getChildren()
  {
    return _elements.values();
  }

  public MBElementDefinition getChildWithName(String elementName)
  {
    if (!isValidChild(elementName))
    {
      throw new MBInvalidElementNameException(elementName);
    }
    return _elements.get(elementName);
  }

  public MBElementDefinition getElementWithPathComponents(List<String> pathComponents)
  {
    if (pathComponents.size() > 0)
    {
      MBElementDefinition root = getChildWithName(pathComponents.get(0));
      pathComponents.remove(0);
      return root.getElementWithPathComponents(pathComponents);
    }
    throw new MBEmptyPathException("No path specified");
  }

  public MBElementDefinition getElementWithPath(String path)
  {
    List<String> pathComponents = MBPathUtil.splitPath(path);

    // If there is a ':' in the name of the first component; we might need a different document than 'self'
    if (pathComponents.size() > 0)
    {
      int location = path.indexOf(":");

      if (location > -1)
      {
        String documentName = pathComponents.get(0).substring(0, location);
        String rootElementName = pathComponents.get(0).substring(location + 1);

        if (!documentName.equals(getName()))
        {
          // Different document! Dispatch the valueForPath
          MBDocumentDefinition docDef = MBMetadataService.getInstance().getDefinitionForDocumentName(documentName);
          if (rootElementName.length() > 0)
          {
            pathComponents.set(0, rootElementName);
          }
          else
          {
            pathComponents.remove(0);
          }

          return docDef.getElementWithPathComponents(pathComponents);
        }
      }
    }

    return getElementWithPathComponents(pathComponents);
  }

  public MBAttributeDefinition getAttributeWithPath(String path) throws MBInvalidPathException
  {
    int location = path.indexOf("@");

    if (location == -1)
    {
      throw new MBInvalidPathException(path);
    }
    String elementPath = path.substring(0, location);
    String attrName = path.substring(location + 1);

    MBElementDefinition elemDef = getElementWithPath(elementPath);
    return elemDef.getAttributeWithName(attrName);
  }

  public String getChildElementNames()
  {
    String result = "";
    for (MBElementDefinition ed : _elements.values())
    {
      if (result.length() > 0)
      {
        result += ", ";
      }
      result += ed.getName();
    }

    if (result.equals(""))
    {
      result = "[none]";
    }

    return result;
  }

  public MBDocument createDocument()
  {
    MBDocument doc = new MBDocument(this);

    for (MBElementDefinition elementDef : _elements.values())
    {
      for (int i = 0; i < elementDef.getMinOccurs(); i++)
      {
        doc.addElement(elementDef.createElement());
      }
    }

    return doc;
  }

  public String evaluateExpression(String variableName)
  {
    throw new MBUnknownVariableException("Unknown variable: " + variableName);
  }

  public String getDataManager()
  {
    return _dataManager;
  }

  public void setDataManager(String dataManager)
  {
    _dataManager = dataManager;
  }

  public boolean getAutoCreate()
  {
    return _autoCreate;
  }

  public void setAutoCreate(boolean autoCreate)
  {
    _autoCreate = autoCreate;
  }

  //Parcelable stuff

  private MBDocumentDefinition(Parcel in)
  {
    super(in);

    Bundle elements = in.readBundle(MBDocumentDefinition.class.getClassLoader());
    _dataManager = in.readString();
    _autoCreate = (Boolean) in.readValue(null);
    _rootElement = in.readString();

    for (String key : elements.keySet())
    {
      _elements.put(key, (MBElementDefinition) elements.get(key));
    }
  }

  @Override
  public int describeContents()
  {
    return Constants.C_PARCELABLE_TYPE_DOCUMENT_DEFINITION;
  }

  @Override
  public void writeToParcel(Parcel out, int flags)
  {
    super.writeToParcel(out, flags);

    Bundle elements = new Bundle();

    for (String key : _elements.keySet())
    {
      elements.putParcelable(key, _elements.get(key));
    }

    out.writeBundle(elements);
    out.writeString(_dataManager);
    out.writeValue(_autoCreate);
  }

  public void setRootElement(String rootElement)
  {
    _rootElement = rootElement;
  }

  public String getRootElement()
  {
    return _rootElement;
  }

  public static final Parcelable.Creator<MBDocumentDefinition> CREATOR = new Creator<MBDocumentDefinition>()
                                                                       {
                                                                         @Override
                                                                         public MBDocumentDefinition[] newArray(int size)
                                                                         {
                                                                           return new MBDocumentDefinition[size];
                                                                         }

                                                                         @Override
                                                                         public MBDocumentDefinition createFromParcel(Parcel in)
                                                                         {
                                                                           return new MBDocumentDefinition(in);
                                                                         }
                                                                       };

  // End of parcelable stuff

}

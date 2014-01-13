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
package com.itude.mobile.mobbl.core.configuration.mvc;

import android.os.Parcel;
import android.os.Parcelable;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl.core.configuration.MBDefinition;
import com.itude.mobile.mobbl.core.services.MBMetadataService;
import com.itude.mobile.mobbl.core.util.Constants;

public class MBAttributeDefinition extends MBDefinition
{
  private String             _type;
  private String             _defaultValue;
  private String             _dataType;
  private boolean            _required;
  private MBDomainDefinition _domainDefinition;

  public MBAttributeDefinition()
  {

  }

  @Override
  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    return StringUtil.appendIndentString(appendToMe, level).append("<Attribute name='").append(getName()).append("' type='").append(_type)
        .append("' required='").append(_required).append("'").append(getAttributeAsXml("defaultValue", _defaultValue)).append("/>\n");
  }

  public String getType()
  {
    return _type;
  }

  public void setType(String type)
  {
    _type = type;
  }

  public String getDefaultValue()
  {
    return _defaultValue;
  }

  public void setDefaultValue(String defaultValue)
  {
    _defaultValue = defaultValue;
  }

  public boolean getRequired()
  {
    return _required;
  }

  public void setRequired(boolean required)
  {
    _required = required;
  }

  public MBDomainDefinition getDomainDefinition()
  {
    if (_domainDefinition == null)
    {
      _domainDefinition = MBMetadataService.getInstance().getDefinitionForDomainName(_type);
    }

    return _domainDefinition;
  }

  public String getDataType()
  {
    if (_dataType == null)
    {
      MBDomainDefinition domDef = MBMetadataService.getInstance().getDefinitionForDomainName(_type, false);

      String type = domDef.getType();
      if (type == null)
      {
        type = getType();
      }
      if (_dataType != type)
      {
        _dataType = null;
        _dataType = type;
      }

    }

    return _dataType;
  }

  // Parcelable stuff

  private MBAttributeDefinition(Parcel in)
  {
    super(in);

    _type = in.readString();
    _defaultValue = in.readString();
    _dataType = in.readString();
    _required = (Boolean) in.readValue(null);
    _domainDefinition = in.readParcelable(null);
  }

  @Override
  public int describeContents()
  {
    return Constants.C_PARCELABLE_TYPE_ATTRIBUTE_DEFINITION;
  }

  @Override
  public void writeToParcel(Parcel out, int flags)
  {
    super.writeToParcel(out, flags);

    out.writeString(_type);
    out.writeString(_defaultValue);
    out.writeString(_dataType);
    out.writeValue(_required);
    out.writeParcelable(_domainDefinition, flags);
  }

  public static final Parcelable.Creator<MBAttributeDefinition> CREATOR = new Creator<MBAttributeDefinition>()
                                                                        {
                                                                          @Override
                                                                          public MBAttributeDefinition[] newArray(int size)
                                                                          {
                                                                            return new MBAttributeDefinition[size];
                                                                          }

                                                                          @Override
                                                                          public MBAttributeDefinition createFromParcel(Parcel in)
                                                                          {
                                                                            return new MBAttributeDefinition(in);
                                                                          }
                                                                        };

  // End of parcelable stuff

}

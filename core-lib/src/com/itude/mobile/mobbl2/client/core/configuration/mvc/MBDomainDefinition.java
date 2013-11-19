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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;
import com.itude.mobile.mobbl2.client.core.util.Constants;

public class MBDomainDefinition extends MBDefinition
{
  private String                            _type;
  private BigDecimal                        _maxLength;
  private List<MBDomainValidatorDefinition> _validators;

  public MBDomainDefinition()
  {
    _validators = new ArrayList<MBDomainValidatorDefinition>();
  }

  @Override
  public void addChildElement(MBDomainValidatorDefinition child)
  {
    addValidator(child);
  }

  public void addValidator(MBDomainValidatorDefinition validator)
  {
    _validators.add(validator);
  }

  public void removeValidatorAtIndex(int index)
  {
    if (_validators.get(index) != null)
    {
      _validators.remove(index);
    }
  }

  @Override
  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    StringUtil.appendIndentString(appendToMe, level).append("<Domain name='").append(getName()).append("' type='").append(_type)
        .append("'").append(getAttributeAsXml("maxLength", _maxLength)).append(">\n");

    for (MBDomainValidatorDefinition vld : _validators)
    {
      vld.asXmlWithLevel(appendToMe, level + 2);
    }

    return StringUtil.appendIndentString(appendToMe, level).append("</Domain>\n");
  }

  public String getType()
  {
    return _type;
  }

  public void setType(String type)
  {
    _type = type;
  }

  public List<MBDomainValidatorDefinition> getDomainValidators()
  {
    return _validators;
  }

  public void setDomainValidators(List<MBDomainValidatorDefinition> domainValidators)
  {
    _validators = domainValidators;
  }

  public BigDecimal getMaxLength()
  {
    return _maxLength;
  }

  public void setMaxLength(BigDecimal maxLength)
  {
    _maxLength = maxLength;
  }

  //Parcelable stuff

  private MBDomainDefinition(Parcel in)
  {
    super(in);

    _type = in.readString();
    _maxLength = (BigDecimal) in.readSerializable();
    MBDomainValidatorDefinition[] validators = (MBDomainValidatorDefinition[]) in.readParcelableArray(null);
    _validators = Arrays.asList(validators);
  }

  @Override
  public int describeContents()
  {
    return Constants.C_PARCELABLE_TYPE_DOMAIN_DEFINITION;
  }

  @Override
  public void writeToParcel(Parcel out, int flags)
  {
    super.writeToParcel(out, flags);

    MBDomainValidatorDefinition[] validators = (MBDomainValidatorDefinition[]) _validators.toArray();

    out.writeString(_type);
    out.writeSerializable(_maxLength);
    out.writeParcelableArray(validators, flags);
  }

  public static final Parcelable.Creator<MBDomainDefinition> CREATOR = new Creator<MBDomainDefinition>()
                                                                     {
                                                                       @Override
                                                                       public MBDomainDefinition[] newArray(int size)
                                                                       {
                                                                         return new MBDomainDefinition[size];
                                                                       }

                                                                       @Override
                                                                       public MBDomainDefinition createFromParcel(Parcel in)
                                                                       {
                                                                         return new MBDomainDefinition(in);
                                                                       }
                                                                     };

  // End of parcelable stuff

}

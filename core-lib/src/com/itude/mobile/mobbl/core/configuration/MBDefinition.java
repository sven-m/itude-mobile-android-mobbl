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
package com.itude.mobile.mobbl.core.configuration;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.itude.mobile.mobbl.core.configuration.endpoints.MBEndPointDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBActionDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBAlertDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBAttributeDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBBundleDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBDialogDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBDocumentDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBDomainDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBDomainValidatorDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBElementDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBOutcomeDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBPageDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBPageStackDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBToolDefinition;
import com.itude.mobile.mobbl.core.configuration.resources.MBItemDefinition;
import com.itude.mobile.mobbl.core.configuration.resources.MBResourceDefinition;
import com.itude.mobile.mobbl.core.model.MBDocument;
import com.itude.mobile.mobbl.core.services.MBResultListenerDefinition;
import com.itude.mobile.mobbl.core.util.Constants;

public class MBDefinition implements Parcelable
{
  private String              _name;
  private Map<String, String> _custom = Collections.emptyMap();

  public MBDefinition()
  {

  }

  public String getName()
  {
    return _name;
  }

  public void setName(String name)
  {
    _name = name;
  }

  public String getAttributeAsXml(String name, Object attrValue)
  {
    if (attrValue == null)
    {
      return "";
    }

    return " " + name + "='" + (String) attrValue + "'";
  }

  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    return appendToMe;
  }

  public void validateDefinition()
  {
  }

  public boolean isPreConditionValid(MBDocument document, String currentPath)
  {
    return true;
  }

  public void addChildElement(MBDomainValidatorDefinition child)
  {
  }

  public void addChildElement(MBElementDefinition child)
  {
  }

  public void addChildElement(MBAttributeDefinition child)
  {
  }

  public void addChildElement(MBDefinition definition)
  {

  }

  public void addChildElement(MBDomainDefinition child)
  {
  }

  public void addChildElement(MBDocumentDefinition child)
  {
  }

  public void addChildElement(MBActionDefinition child)
  {
  }

  public void addChildElement(MBOutcomeDefinition child)
  {
  }

  public void addChildElement(MBPageDefinition child)
  {
  }

  public void addChildElement(MBPageStackDefinition child)
  {
  }

  public void addChildElement(MBDialogDefinition child)
  {
  }

  public void addChildElement(MBToolDefinition child)
  {
  }

  public void addChildElement(MBAlertDefinition child)
  {
  }

  public void addChildElement(MBResourceDefinition child)
  {
  }

  public void addChildElement(MBBundleDefinition child)
  {

  }

  public void addChildElement(MBItemDefinition child)
  {

  }

  public void addEndPoint(MBEndPointDefinition definition)
  {
  }

  public void addResultListener(MBResultListenerDefinition lsnr)
  {
  }

  public boolean isValidChild(String elementName)
  {
    return false;
  }

  public Collection<MBElementDefinition> getChildElements()
  {
    return null;
  }

  @Override
  public String toString()
  {
    return asXmlWithLevel(new StringBuffer(), 0).toString();
  }

  public Map<String, String> getCustom()
  {
    return _custom;
  }

  public void setCustom(Map<String, String> custom)
  {
    _custom = custom;
  }

  //Parcelable stuff

  protected MBDefinition(Parcel in)
  {
    _name = in.readString();
  }

  @Override
  public int describeContents()
  {
    return Constants.C_PARCELABLE_TYPE_DEFINITION;
  }

  @Override
  public void writeToParcel(Parcel out, int flags)
  {
    out.writeString(_name);
  }

  public static final Parcelable.Creator<MBDefinition> CREATOR = new Creator<MBDefinition>()
                                                               {
                                                                 @Override
                                                                 public MBDefinition[] newArray(int size)
                                                                 {
                                                                   return new MBDefinition[size];
                                                                 }

                                                                 @Override
                                                                 public MBDefinition createFromParcel(Parcel in)
                                                                 {
                                                                   return new MBDefinition(in);
                                                                 }
                                                               };

  // End of parcelable stuff
}

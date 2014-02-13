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

/**
* Common superclass of configuration definitions.
*
* A MOBBL application is for a large part defined in XML configuration files. On startup,
* the framework parses these configuration files and creates MBDefinition objects for the
* configuration.
*/
public class MBDefinition implements Parcelable
{
  private String              _name;
  private Map<String, String> _custom = Collections.emptyMap();

  public MBDefinition()
  {

  }

  /** 
   * Get the value of the `name` property of the XML element in the configuration.
   *  
   * @return the definition's name
   */
  public String getName()
  {
    return _name;
  }

  /** 
   * Set the value of the `name` property of the XML element in the configuration.
   *  
   * @name the definition's name
   */
  public void setName(String name)
  {
    _name = name;
  }

  /**
   * Exporting to XML
   * 
   * @param name name 
   * @param attrValue attribute value
   * @return exported XML
   */
  public String getAttributeAsXml(String name, Object attrValue)
  {
    if (attrValue == null)
    {
      return "";
    }

    return " " + name + "='" + (String) attrValue + "'";
  }

  /**
   * Structured String representation
   * 
   * @param level level
   * @return structured String representation
   */
  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    return appendToMe;
  }

  /** Checks the validity of the configuration.
  *
  * This method is called after parsing a configuration item and should be implemented by subclasses
  * to check the validity of the configuration. The method is expected to throw an NSException when
  * the definition is invalid.
  *
  * The default implementation is empty.
  */
  public void validateDefinition()
  {
  }

  /** Checks the validity of any preconditions that are defined with this definition.
  *
  * Definitions that use this should implement MBConditionalDefinition. 
  * 
  * @return the default implementation always returns true.
  */
  public boolean isPreConditionValid(MBDocument document, String currentPath)
  {
    return true;
  }

  /**
   * Add {@link MBDomainValidatorDefinition} as child element
   * 
   * @param child {@link MBDomainValidatorDefinition}
   */
  public void addChildElement(MBDomainValidatorDefinition child)
  {
  }

  /**
   * Add {@link MBElementDefinition} as child element
   * 
   * @param child {@link MBElementDefinition}
   */
  public void addChildElement(MBElementDefinition child)
  {
  }

  /**
   * Add {@link MBAttributeDefinition} as child element
   * 
   * @param child {@link MBAttributeDefinition}
   */
  public void addChildElement(MBAttributeDefinition child)
  {
  }

  /**
   * Add {@link MBDefinition} as child element
   * 
   * @param child {@link MBDefinition}
   */
  public void addChildElement(MBDefinition definition)
  {

  }

  /**
   * Add {@link MBDomainDefinition} as child element
   * 
   * @param child {@link MBDomainDefinition}
   */
  public void addChildElement(MBDomainDefinition child)
  {
  }

  /**
   * Add {@link MBDocumentDefinition} as child element
   * 
   * @param child {@link MBDocumentDefinition}
   */
  public void addChildElement(MBDocumentDefinition child)
  {
  }

  /**
   * Add {@link MBActionDefinition} as child element
   * 
   * @param child {@link MBActionDefinition}
   */
  public void addChildElement(MBActionDefinition child)
  {
  }

  /**
   * Add {@link MBOutcomeDefinition} as child element
   * 
   * @param child {@link MBOutcomeDefinition}
   */
  public void addChildElement(MBOutcomeDefinition child)
  {
  }

  /**
   * Add {@link MBPageDefinition} as child element
   * 
   * @param child {@link MBPageDefinition}
   */
  public void addChildElement(MBPageDefinition child)
  {
  }

  /**
   * Add {@link MBPageStackDefinition} as child element
   * 
   * @param child {@link MBPageStackDefinition}
   */
  public void addChildElement(MBPageStackDefinition child)
  {
  }

  /**
   * Add {@link MBDialogDefinition} as child element
   * 
   * @param child {@link MBDialogDefinition}
   */
  public void addChildElement(MBDialogDefinition child)
  {
  }

  /**
   * Add {@link MBToolDefinition} as child element
   * 
   * @param child {@link MBToolDefinition}
   */
  public void addChildElement(MBToolDefinition child)
  {
  }

  /**
   * Add {@link MBAlertDefinition} as child element
   * 
   * @param child {@link MBAlertDefinition}
   */
  public void addChildElement(MBAlertDefinition child)
  {
  }

  /**
   * Add {@link MBResourceDefinition} as child element
   * 
   * @param child {@link MBResourceDefinition}
   */
  public void addChildElement(MBResourceDefinition child)
  {
  }

  /**
   * Add {@link MBBundleDefinition} as child element
   * 
   * @param child {@link MBBundleDefinition}
   */
  public void addChildElement(MBBundleDefinition child)
  {

  }

  /**
   * Add {@link MBItemDefinition} as child element
   * 
   * @param child {@link MBItemDefinition}
   */
  public void addChildElement(MBItemDefinition child)
  {

  }

  /**
   * Add {@link MBEndPointDefinition} as child element
   * 
   * @param child {@link MBEndPointDefinition}
   */
  public void addEndPoint(MBEndPointDefinition definition)
  {
  }

  /**
   * Add {@link MBResultListenerDefinition} as child element
   * 
   * @param child {@link MBResultListenerDefinition}
   */
  public void addResultListener(MBResultListenerDefinition lsnr)
  {
  }

  /**
   * Check if Child with elementName is a valid child
   * @param elementName element name
   * @return the default implementation always returns false.
   */
  public boolean isValidChild(String elementName)
  {
    return false;
  }

  /**
   * Get a {@link Collection} of {@link MBElementDefinition}s
   * @return {@link Collection} of {@link MBElementDefinition}s
   */
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

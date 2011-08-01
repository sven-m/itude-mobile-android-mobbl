package com.itude.mobile.mobbl2.client.core.configuration;

import java.util.Collection;

import android.os.Parcel;
import android.os.Parcelable;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBActionDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBAttributeDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDialogDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDocumentDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDomainDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDomainValidatorDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBElementDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBOutcomeDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBPageDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBToolDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.webservices.MBEndPointDefinition;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.services.MBResultListenerDefinition;
import com.itude.mobile.mobbl2.client.core.util.Constants;

public class MBDefinition implements Parcelable
{
  private String _name;

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

  public void addChildElement(MBDialogDefinition child)
  {
  }

  public void addChildElement(MBToolDefinition child)
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

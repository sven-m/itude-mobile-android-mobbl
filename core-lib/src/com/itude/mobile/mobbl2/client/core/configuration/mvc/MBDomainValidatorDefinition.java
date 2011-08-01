package com.itude.mobile.mobbl2.client.core.configuration.mvc;

import java.math.BigDecimal;

import android.os.Parcel;
import android.os.Parcelable;

import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.StringUtilities;

public class MBDomainValidatorDefinition extends MBDefinition
{
  private String     _title;
  private String     _value;
  private BigDecimal _lowerBound;
  private BigDecimal _upperBound;

  public MBDomainValidatorDefinition()
  {

  }

  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    return StringUtilities.appendIndentString(appendToMe, level).append("<DomainValidator").append(getAttributeAsXml("title", _title))
        .append(getAttributeAsXml("value", _value)).append(getAttributeAsXml("lowerBound", _lowerBound))
        .append(getAttributeAsXml("upperBound", _upperBound)).append("/>\n");
  }

  public String getTitle()
  {
    return _title;
  }

  public void setTitle(String title)
  {
    _title = title;
  }

  public String getValue()
  {
    return _value;
  }

  public void setValue(String value)
  {
    _value = value;
  }

  public BigDecimal getLowerBound()
  {
    return _lowerBound;
  }

  public void setLowerBound(BigDecimal lowerBound)
  {
    _lowerBound = lowerBound;
  }

  public BigDecimal getUpperBound()
  {
    return _upperBound;
  }

  public void setUpperBound(BigDecimal upperBound)
  {
    _upperBound = upperBound;
  }

  //Parcelable stuff

  private MBDomainValidatorDefinition(Parcel in)
  {
    super(in);

    _title = in.readString();
    _value = in.readString();
    _lowerBound = (BigDecimal) in.readSerializable();
    _upperBound = (BigDecimal) in.readSerializable();
  }

  @Override
  public int describeContents()
  {
    return Constants.C_PARCELABLE_TYPE_DOMAIN_VALIDATOR_DEFINITION;
  }

  @Override
  public void writeToParcel(Parcel out, int flags)
  {
    super.writeToParcel(out, flags);

    out.writeString(_title);
    out.writeString(_value);
    out.writeSerializable(_lowerBound);
    out.writeSerializable(_upperBound);
  }

  public static final Parcelable.Creator<MBDomainValidatorDefinition> CREATOR = new Creator<MBDomainValidatorDefinition>()
                                                                              {
                                                                                @Override
                                                                                public MBDomainValidatorDefinition[] newArray(int size)
                                                                                {
                                                                                  return new MBDomainValidatorDefinition[size];
                                                                                }

                                                                                @Override
                                                                                public MBDomainValidatorDefinition createFromParcel(Parcel in)
                                                                                {
                                                                                  return new MBDomainValidatorDefinition(in);
                                                                                }
                                                                              };

  // End of parcelable stuff

}

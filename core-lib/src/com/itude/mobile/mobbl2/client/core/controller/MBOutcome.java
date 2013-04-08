package com.itude.mobile.mobbl2.client.core.controller;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBConfigurationDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBOutcomeDefinition;
import com.itude.mobile.mobbl2.client.core.controller.exceptions.MBExpressionNotBooleanException;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.services.MBDataManagerService;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.MBParseUtil;

public class MBOutcome implements Parcelable
{
  private String     _originName;
  private String     _outcomeName;
  private String     _dialogName;
  private String     _originDialogName;
  private String     _displayMode;
  private String     _path;
  private boolean    _persist;
  private boolean    _transferDocument;
  private boolean    _noBackgroundProcessing;
  private MBDocument _document;
  private String     _preCondition;
  private String     _indicator;

  public String getOriginName()
  {
    return _originName;
  }

  public void setOriginName(String originName)
  {
    _originName = originName;
  }

  public String getOutcomeName()
  {
    return _outcomeName;
  }

  public void setOutcomeName(String outcomeName)
  {
    _outcomeName = outcomeName;
  }

  public String getDialogName()
  {
    return _dialogName;
  }

  public void setDialogName(String dialogName)
  {
    _dialogName = dialogName;
  }

  public String getOriginDialogName()
  {
    return _originDialogName;
  }

  public void setOriginDialogName(String originDialogName)
  {
    _originDialogName = originDialogName;
  }

  public String getPath()
  {
    return _path;
  }

  public void setPath(String path)
  {
    _path = path;
  }

  public String getDisplayMode()
  {
    return _displayMode;
  }

  public void setDisplayMode(String displayMode)
  {
    _displayMode = displayMode;
  }

  public String getPreCondition()
  {
    return _preCondition;
  }

  public void setPreCondition(String preCondition)
  {
    _preCondition = preCondition;
  }

  public MBDocument getDocument()
  {
    return _document;
  }

  public void setDocument(MBDocument document)
  {
    _document = document;
  }

  public boolean getPersist()
  {
    return _persist;
  }

  public void setPersist(boolean persist)
  {
    _persist = persist;
  }

  public boolean getTransferDocument()
  {
    return _transferDocument;
  }

  public void setTransferDocument(boolean transferDocument)
  {
    _transferDocument = transferDocument;
  }

  public boolean getNoBackgroundProcessing()
  {
    return _noBackgroundProcessing;
  }

  public void setNoBackgroundProcessing(boolean noBackgroundProcessing)
  {
    _noBackgroundProcessing = noBackgroundProcessing;
  }

  public void setIndicator(String indicator)
  {
    _indicator = indicator;
  }

  public String getIndicator()
  {
    return _indicator;
  }

  public MBOutcome(MBOutcome outcome)
  {
    _originName = outcome.getOriginName();
    _outcomeName = outcome.getOutcomeName();
    _originDialogName = outcome.getOriginDialogName();
    _dialogName = outcome.getDialogName();
    _displayMode = outcome.getDisplayMode();
    _document = outcome.getDocument();
    _path = outcome.getPath();
    _persist = outcome.getPersist();
    _transferDocument = outcome.getTransferDocument();
    _preCondition = outcome.getPreCondition();
    _noBackgroundProcessing = outcome.getNoBackgroundProcessing();
    _indicator = outcome.getIndicator();
  }

  public MBOutcome(MBOutcomeDefinition definition)
  {
    _originName = definition.getOrigin();
    _outcomeName = definition.getName();
    _dialogName = definition.getDialog();
    _displayMode = definition.getDisplayMode();
    _persist = definition.getPersist();
    _transferDocument = definition.getTransferDocument();
    _noBackgroundProcessing = definition.getNoBackgroundProcessing();
    _document = null;
    _path = null;
    _preCondition = definition.getPreCondition();
    _indicator = definition.getIndicator();
  }

  public MBOutcome(String outcomeName, MBDocument document)
  {
    _outcomeName = outcomeName;
    _document = document;
  }

  public MBOutcome(String outcomeName, MBDocument document, String dialogName)
  {
    this(outcomeName, document);
    _dialogName = dialogName;
  }

  public MBOutcome()
  {
  }

  public boolean isPreConditionValid()
  {
    boolean isValid = true;
    if (getPreCondition() != null)
    {
      MBDocument doc = getDocument();
      if (doc == null)
      {
        doc = MBDataManagerService.getInstance().loadDocument(MBConfigurationDefinition.DOC_SYSTEM_EMPTY);
      }
      String result = doc.evaluateExpression(this.getPreCondition());
      Boolean bool = MBParseUtil.strictBooleanValue(result);
      if (bool != null) return bool;
      String msg = "Expression of outcome with origin=" + getOriginName() + " name=" + getOutcomeName() + " precondition="
                   + getPreCondition() + " is not boolean (result=" + result + ")";
      throw new MBExpressionNotBooleanException(msg);
    }

    return isValid;
  }

  // Parcel stuff

  /**
   * Private constructor to create an instance of MBOutcome based on a previously
   * created/written Parcel.
   * @param in
   */
  private MBOutcome(Parcel in)
  {
    Bundle data = in.readBundle();

    _originName = data.getString("originName");
    _outcomeName = data.getString("outcomeName");
    _dialogName = data.getString("dialogName");
    _originDialogName = data.getString("originDialogName");
    _displayMode = data.getString("displayMode");
    _path = data.getString("path");
    _preCondition = data.getString("preCondition");

    _persist = data.getBoolean("persist");
    _transferDocument = data.getBoolean("transferDocument");
    _noBackgroundProcessing = data.getBoolean("noBackgroundProcessing");

    _document = data.getParcelable("document");
  }

  @Override
  public int describeContents()
  {
    return Constants.C_PARCELABLE_TYPE_OUTCOME;
  }

  @Override
  public void writeToParcel(Parcel out, int flags)
  {
    Bundle data = new Bundle();

    data.putString("originName", _originName);
    data.putString("outcomeName", _outcomeName);
    data.putString("dialogName", _dialogName);
    data.putString("originDialogName", _originDialogName);
    data.putString("displayMode", _displayMode);
    data.putString("path", _path);
    data.putString("preCondition", _preCondition);

    data.putBoolean("persist", _persist);
    data.putBoolean("transferDocument", _transferDocument);
    data.putBoolean("noBackgroundProcessing", _noBackgroundProcessing);

    data.putParcelable("document", _document);

    out.writeBundle(data);
  }

  public static final Parcelable.Creator<MBOutcome> CREATOR = new Creator<MBOutcome>()
                                                            {
                                                              @Override
                                                              public MBOutcome[] newArray(int size)
                                                              {
                                                                return new MBOutcome[size];
                                                              }

                                                              @Override
                                                              public MBOutcome createFromParcel(Parcel in)
                                                              {
                                                                return new MBOutcome(in);
                                                              }
                                                            };

  // End of parcel stuff

  @Override
  public String toString()
  {
    return "Outcome: dialog=" + getDialogName() + " originName=" + getOriginName() + " outcomeName=" + getOutcomeName() + " path="
           + getPath() + " persist=" + getPersist() + " displayMode=" + getDisplayMode() + " preCondition=" + getPreCondition()
           + " noBackgroundProsessing=" + getNoBackgroundProcessing();
  }
}

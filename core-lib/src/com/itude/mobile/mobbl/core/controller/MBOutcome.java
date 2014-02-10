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
package com.itude.mobile.mobbl.core.controller;

import static com.itude.mobile.android.util.ComparisonUtil.coalesce;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.itude.mobile.mobbl.core.configuration.mvc.MBConfigurationDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBOutcomeDefinition;
import com.itude.mobile.mobbl.core.controller.exceptions.MBExpressionNotBooleanException;
import com.itude.mobile.mobbl.core.model.MBDocument;
import com.itude.mobile.mobbl.core.services.MBDataManagerService;
import com.itude.mobile.mobbl.core.util.Constants;
import com.itude.mobile.mobbl.core.util.MBCustomAttributeContainer;
import com.itude.mobile.mobbl.core.util.MBParseUtil;

public class MBOutcome extends MBCustomAttributeContainer implements Parcelable
{
  private String     _outcomeName;
  private String     _pageStackName;
  private String     _displayMode;
  private String     _path;
  private boolean    _persist;
  private boolean    _transferDocument;
  private boolean    _noBackgroundProcessing;
  private MBDocument _document;
  private String     _preCondition;
  private String     _indicator;
  private String     _action;
  private Origin     _origin;

  public Origin getOrigin()
  {
    return _origin;
  }

  public void setOrigin(Origin origin)
  {
    _origin = origin;
  }

  public String getOutcomeName()
  {
    return _outcomeName;
  }

  public void setOutcomeName(String outcomeName)
  {
    _outcomeName = outcomeName;
  }

  /**
   * @deprecated - Use getPageStackName () instead
   */
  @Deprecated
  public String getDialogName()
  {
    return _pageStackName;
  }

  /*
   * @deprecated - Use setPageStackName instead
   */
  @Deprecated
  public void setDialogName(String pageStackName)
  {
    _pageStackName = pageStackName;
  }

  public String getPageStackName()
  {
    return _pageStackName;
  }

  public void setPageStackName(String pageStackName)
  {
    _pageStackName = pageStackName;
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

  public String getAction()
  {
    return _action;
  }

  public void setAction(String action)
  {
    _action = action;
  }

  public String getIndicator()
  {
    return _indicator;
  }

  public MBOutcome(MBOutcome outcome)
  {
    super(outcome);
    _origin = new Origin(outcome.getOrigin());
    _outcomeName = outcome.getOutcomeName();
    _pageStackName = outcome.getPageStackName();
    _displayMode = outcome.getDisplayMode();
    _document = outcome.getDocument();
    _path = outcome.getPath();
    _persist = outcome.getPersist();
    _transferDocument = outcome.getTransferDocument();
    _preCondition = outcome.getPreCondition();
    _noBackgroundProcessing = outcome.getNoBackgroundProcessing();
    _indicator = outcome.getIndicator();
    _action = outcome.getAction();
  }

  public MBOutcome(MBOutcomeDefinition definition)
  {
    super(definition);
    _origin = new Origin().withOutcome(definition.getOrigin());
    _outcomeName = definition.getName();
    _pageStackName = definition.getPageStack();
    _displayMode = definition.getDisplayMode();
    _persist = definition.getPersist();
    _transferDocument = definition.getTransferDocument();
    _noBackgroundProcessing = definition.getNoBackgroundProcessing();
    _document = null;
    _path = null;
    _preCondition = definition.getPreCondition();
    _indicator = definition.getIndicator();
    _action = definition.getAction();
  }

  public MBOutcome(String outcomeName, MBDocument document)
  {
    _outcomeName = outcomeName;
    _document = document;
  }

  public MBOutcome(String outcomeName, MBDocument document, String pageStackName)
  {
    this(outcomeName, document);
    _pageStackName = pageStackName;
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
      String msg = "Expression of outcome with origin=" + getOrigin() + " name=" + getOutcomeName() + " precondition=" + getPreCondition()
                   + " is not boolean (result=" + result + ")";
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

    _outcomeName = data.getString("outcomeName");
    _pageStackName = data.getString("pageStackName");
    _displayMode = data.getString("displayMode");
    _path = data.getString("path");
    _preCondition = data.getString("preCondition");

    _persist = data.getBoolean("persist");
    _transferDocument = data.getBoolean("transferDocument");
    _noBackgroundProcessing = data.getBoolean("noBackgroundProcessing");
    _action = data.getString("action");

    _document = data.getParcelable("document");

    _origin = data.getParcelable("origin");
    readFromBundle(data);
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

    data.putString("outcomeName", _outcomeName);
    data.putString("pageStackName", _pageStackName);
    data.putString("displayMode", _displayMode);
    data.putString("path", _path);
    data.putString("preCondition", _preCondition);

    data.putBoolean("persist", _persist);
    data.putBoolean("transferDocument", _transferDocument);
    data.putBoolean("noBackgroundProcessing", _noBackgroundProcessing);
    data.putString("action", _action);

    data.putParcelable("document", _document);

    data.putParcelable("origin", _origin);

    writeToBundle(data);

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
    return "Outcome: pageStack=" + getPageStackName() + " origin=" + getOrigin() + " outcomeName=" + getOutcomeName() + " path="
           + getPath() + " persist=" + getPersist() + " displayMode=" + getDisplayMode() + " preCondition=" + getPreCondition()
           + " noBackgroundProsessing=" + getNoBackgroundProcessing() + " action = " + getAction();
  }

  public MBOutcome createCopy(MBOutcomeDefinition outcomeDef)
  {
    MBOutcome outcomeToProcess = new MBOutcome(outcomeDef);
    outcomeToProcess.setPath(getPath());
    outcomeToProcess.setDocument(getDocument());
    outcomeToProcess.setNoBackgroundProcessing(getNoBackgroundProcessing() || outcomeDef.getNoBackgroundProcessing());
    outcomeToProcess.setPersist(getPersist() || outcomeDef.getPersist());

    // note that the precedence of either this' or outcomeDef's values are not identical; this is not a mistake
    outcomeToProcess.setIndicator(coalesce(getIndicator(), outcomeDef.getIndicator()));
    outcomeToProcess.setPageStackName(coalesce(outcomeDef.getPageStack(), getPageStackName()));
    outcomeToProcess.setDisplayMode(coalesce(getDisplayMode(), outcomeDef.getDisplayMode()));
    outcomeToProcess.setOrigin(new Origin(getOrigin()).fillBlanks(outcomeDef));
    outcomeToProcess.setAction(coalesce(getAction(), outcomeDef.getAction()));

    return outcomeToProcess;
  }

  public static class Origin implements Parcelable
  {
    private String _dialog;
    private String _pageStack;
    private String _outcome;
    private String _action;
    private String _page;

    public Origin()
    {
      _dialog = _pageStack = _outcome = _action = _page = null;
    }

    public Origin(Origin toCopy)
    {
      if (toCopy != null)
      {
        _dialog = toCopy.getOutcome();
        _pageStack = toCopy.getPageStack();
        _outcome = toCopy.getOutcome();
        _action = toCopy.getAction();
        _page = toCopy.getPage();
      }
    }

    private Origin(Parcel in)
    {
      Bundle data = in.readBundle();

      _dialog = data.getString("dialog");
      _pageStack = data.getString("pageStack");
      _outcome = data.getString("outcome");
      _action = data.getString("action");
      _page = data.getString("page");
    }

    public String getDialog()
    {
      return _dialog;
    }

    public String getPageStack()
    {
      return _pageStack;
    }

    public String getOutcome()
    {
      return _outcome;
    }

    public String getAction()
    {
      return _action;
    }

    public String getPage()
    {
      return _page;
    }

    public Origin withDialog(String name)
    {
      _dialog = name;
      return this;
    }

    public Origin withPageStack(String name)
    {
      _pageStack = name;
      return this;
    }

    public Origin withOutcome(String name)
    {
      _outcome = name;
      return this;
    }

    public Origin withAction(String name)
    {
      _action = name;
      return this;
    }

    public Origin withPage(String pageName)
    {
      _page = pageName;
      return this;
    }

    public Origin fillBlanks(MBOutcomeDefinition def)
    {
      _pageStack = coalesce(getPageStack(), def.getPageStack());
      return this;
    }

    public boolean matches(String whatever)
    {
      boolean result = false;
      if (getDialog() != null) result |= getDialog().equalsIgnoreCase(whatever);
      if (getPageStack() != null) result |= getPageStack().equalsIgnoreCase(whatever);
      if (getOutcome() != null) result |= getOutcome().equalsIgnoreCase(whatever);
      if (getAction() != null) result |= getAction().equalsIgnoreCase(whatever);
      if (getPage() != null) result |= getPage().equalsIgnoreCase(whatever);
      return result;
    }

    @Override
    public String toString()
    {
      StringBuilder description = new StringBuilder("(dialog: ");
      description.append(_dialog);
      description.append(" pageStack: ").append(_pageStack);
      description.append(" outcome: ").append(_outcome);
      description.append(" action: ").append(_action);
      description.append(" page: ").append(_page);
      description.append(")");
      return description.toString();
    }

    @Override
    public int describeContents()
    {
      return Constants.C_PARCELABLE_TYPE_ORIGIN;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
      Bundle data = new Bundle();

      data.putString("dialog", _dialog);
      data.putString("pageStack", _pageStack);
      data.putString("outcome", _outcome);
      data.putString("action", _action);
      data.putString("page", _page);

      dest.writeBundle(data);
    }

    public static final Parcelable.Creator<Origin> CREATOR  = new Creator<Origin>()
                                                            {
                                                              @Override
                                                              public Origin[] newArray(int size)
                                                              {
                                                                return new Origin[size];
                                                              }

                                                              @Override
                                                              public Origin createFromParcel(Parcel in)
                                                              {
                                                                return new Origin(in);
                                                              }
                                                            };
    public static final Origin                     WILDCARD = new Origin()
                                                            {
                                                              @Override
                                                              public String toString()
                                                              {
                                                                return "WILDCARD";
                                                              }

                                                              @Override
                                                              public boolean matches(String whatever)
                                                              {
                                                                return true;
                                                              }
                                                            };

  }
}

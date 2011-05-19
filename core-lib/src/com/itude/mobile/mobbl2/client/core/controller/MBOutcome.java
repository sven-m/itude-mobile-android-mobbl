package com.itude.mobile.mobbl2.client.core.controller;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBConfigurationDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBOutcomeDefinition;
import com.itude.mobile.mobbl2.client.core.controller.exceptions.MBExpressionNotBooleanException;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.services.MBDataManagerService;

public class MBOutcome
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

  public MBOutcome(MBOutcome outcome)
  {
    ;
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
  }

  public MBOutcome(MBOutcomeDefinition definition)
  {
    ;
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
      if ("1".equals(result) || "YES".equalsIgnoreCase(result) || "TRUE".equalsIgnoreCase(result))
      {
        return true;
      }
      if ("0".equals(result) || "NO".equalsIgnoreCase(result) || "FALSE".equalsIgnoreCase(result))
      {
        return false;
      }
      String msg = "Expression of outcome with origin=" + getOriginName() + " name=" + getOutcomeName() + " precondition="
                   + getPreCondition() + " is not boolean (result=" + result + ")";
      throw new MBExpressionNotBooleanException(msg);
    }

    return isValid;
  }

  @Override
  public String toString()
  {
    return "Outcome: dialog=" + getDialogName() + " originName=" + getOriginName() + " outcomeName=" + getOutcomeName() + " path="
           + getPath() + " persist=" + getPersist() + " displayMode=" + getDisplayMode() + " preCondition=" + getPreCondition()
           + " noBackgroundProsessing=" + getNoBackgroundProcessing();
  }

}

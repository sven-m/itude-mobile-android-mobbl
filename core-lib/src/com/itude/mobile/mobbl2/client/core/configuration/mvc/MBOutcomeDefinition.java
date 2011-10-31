package com.itude.mobile.mobbl2.client.core.configuration.mvc;

import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;
import com.itude.mobile.mobbl2.client.core.util.StringUtilities;

public class MBOutcomeDefinition extends MBDefinition
{
  private String  _origin;
  private String  _action;
  private String  _dialog;
  private String  _displayMode;
  private String  _preCondition;
  private boolean _persist;
  private boolean _transferDocument;
  private boolean _noBackgroundProcessing;
  private String  _indicator;

  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    String persistBool;
    if (_persist)
    {
      persistBool = "TRUE";
    }
    else
    {
      persistBool = "FALSE";
    }
    String transferDocumentBool;
    if (_transferDocument)
    {
      transferDocumentBool = "TRUE";
    }
    else
    {
      transferDocumentBool = "FALSE";
    }

    String noBackgroundProcessingBool;
    if (_noBackgroundProcessing)
    {
      noBackgroundProcessingBool = "TRUE";
    }
    else
    {
      noBackgroundProcessingBool = "FALSE";
    }

    return StringUtilities.appendIndentString(appendToMe, level).append("<Outcome origin='").append(_origin).append("' name='")
        .append(getName()).append("' action='").append(_action).append("' transferDocument='").append(transferDocumentBool)
        .append("' persist='").append(persistBool).append("' noBackgroundProcessing='").append(noBackgroundProcessingBool).append("'")
        .append(getAttributeAsXml("dialog", _dialog)).append(getAttributeAsXml("preCondition", _preCondition))
        .append(getAttributeAsXml("displayMode", _displayMode)).append(getAttributeAsXml("indicator", _indicator)).append("/>\n");
  }

  public String getOrigin()
  {
    return _origin;
  }

  public void setOrigin(String origin)
  {
    _origin = origin;
  }

  public String getAction()
  {
    return _action;
  }

  public void setAction(String action)
  {
    _action = action;
  }

  public String getDialog()
  {
    return _dialog;
  }

  public void setDialog(String dialog)
  {
    _dialog = dialog;
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

  public String getIndicator()
  {
    return _indicator;
  }

  public void setIndicator(String indicator)
  {
    _indicator = indicator;
  }
}

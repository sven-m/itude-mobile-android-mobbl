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

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;

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

  @Override
  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {

    return StringUtil.appendIndentString(appendToMe, level).append("<Outcome origin='").append(_origin).append("' name='")
        .append(getName()).append("' action='").append(_action).append("' transferDocument='").append(_transferDocument)
        .append("' persist='").append(_persist).append("' noBackgroundProcessing='").append(_noBackgroundProcessing).append("'")
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

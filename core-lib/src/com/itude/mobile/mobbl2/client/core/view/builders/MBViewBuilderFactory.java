/*
 * (C) Copyright ItudeMobile.
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
package com.itude.mobile.mobbl2.client.core.view.builders;

public class MBViewBuilderFactory
{
  private static MBViewBuilderFactory _instance;

  private final MBPanelViewBuilder    _panelViewBuilder;
  private MBPageViewBuilder           _pageViewBuilder;
  private MBForEachViewBuilder        _forEachViewBuilder;
  private MBForEachItemViewBuilder    _forEachItemViewBuilder;
  private final MBFieldViewBuilder    _fieldViewBuilder;
  private MBStyleHandler              _styleHandler;
  private MBDialogViewBuilder         _dialogViewBuilder;
  private final MBAlertDialogBuilder  _alertViewBuilder;

  private MBViewBuilderFactory()
  {
    _panelViewBuilder = new MBPanelViewBuilder();
    _pageViewBuilder = new MBPageViewBuilder();
    _forEachViewBuilder = new MBForEachViewBuilder();
    _forEachItemViewBuilder = new MBForEachItemViewBuilder();
    _fieldViewBuilder = new MBFieldViewBuilder();
    _styleHandler = new MBStyleHandler();
    _dialogViewBuilder = new MBDialogViewBuilder();
    _alertViewBuilder = new MBAlertDialogBuilder();
  }

  public static MBViewBuilderFactory getInstance()
  {
    if (_instance == null)
    {
      _instance = new MBViewBuilderFactory();
    }

    return _instance;
  }

  public static void setInstance(MBViewBuilderFactory factory)
  {
    if (_instance != null && _instance != factory)
    {
      _instance = null;
    }
    _instance = factory;
  }

  public MBPanelViewBuilder getPanelViewBuilder()
  {
    return _panelViewBuilder;
  }

  public MBPageViewBuilder getPageViewBuilder()
  {
    return _pageViewBuilder;
  }

  public void setPageViewBuilder(MBPageViewBuilder pageViewBuilder)
  {
    _pageViewBuilder = pageViewBuilder;
  }

  public MBForEachViewBuilder getForEachViewBuilder()
  {
    return _forEachViewBuilder;
  }

  public void setForEachViewBuilder(MBForEachViewBuilder forEachViewBuilder)
  {
    _forEachViewBuilder = forEachViewBuilder;
  }

  public MBForEachItemViewBuilder getForEachItemViewBuilder()
  {
    return _forEachItemViewBuilder;
  }

  public void setForEachItemViewBuilder(MBForEachItemViewBuilder forEachItemViewBuilder)
  {
    _forEachItemViewBuilder = forEachItemViewBuilder;
  }

  public MBFieldViewBuilder getFieldViewBuilder()
  {
    return _fieldViewBuilder;
  }

  public MBStyleHandler getStyleHandler()
  {
    return _styleHandler;
  }

  public void setStyleHandler(MBStyleHandler styleHandler)
  {
    _styleHandler = styleHandler;
  }

  public void setDialogViewBuilder(MBDialogViewBuilder dialogViewBuilder)
  {
    _dialogViewBuilder = dialogViewBuilder;
  }

  public MBDialogViewBuilder getDialogViewBuilder()
  {
    return _dialogViewBuilder;
  }

  public MBAlertDialogBuilder getAlertViewBuilder()
  {
    return _alertViewBuilder;
  }

}

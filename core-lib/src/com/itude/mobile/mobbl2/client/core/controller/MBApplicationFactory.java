package com.itude.mobile.mobbl2.client.core.controller;

import android.content.Intent;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDialogDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBPageDefinition;
import com.itude.mobile.mobbl2.client.core.controller.util.MBBasicViewController;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;
import com.itude.mobile.mobbl2.client.core.services.MBResultListener;
import com.itude.mobile.mobbl2.client.core.view.MBPage;
import com.itude.mobile.mobbl2.client.core.view.helpers.MBEditableMatrixListener;

/*
 * Factory class for Pages and Actions.
 * Subclass and use setInstance() to provide custom Pages and custom Actions
 */

public class MBApplicationFactory
{

  private static MBApplicationFactory _instance = null;

  public static MBApplicationFactory getInstance()
  {
    if (_instance == null)
    {
      _instance = new MBApplicationFactory();
    }
    return _instance;
  }

  public static void setInstance(MBApplicationFactory factory)
  {
    _instance = factory;
  }

  public MBBasicViewController createViewController(MBPage page)
  {
    return new MBBasicViewController();
  }

  public MBPage createPage(MBPageDefinition definition, MBDocument document, String rootPath, MBViewManager.MBViewState viewState)
  {
    MBPage page = new MBPage(definition, document, rootPath, viewState);
    return page;
  }

  public MBAction createAction(String actionClassName)
  {
    return null;
  }

  public MBResultListener createResultListener(String listenerClassName)
  {
    return null;
  }

  public MBBasicViewController createFragment(String pageName)
  {
    return new MBBasicViewController();
  }

  public MBEditableMatrixListener getEditableMatrixListener(String panelName)
  {
    return null;
  }

}

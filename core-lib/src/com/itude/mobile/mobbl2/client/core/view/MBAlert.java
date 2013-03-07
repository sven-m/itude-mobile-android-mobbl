package com.itude.mobile.mobbl2.client.core.view;

import android.app.AlertDialog;

import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBAlertDefinition;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilderFactory;

public class MBAlert extends MBComponentContainer
{

  private String _alertName;
  private String _rootPath;
  private String _title;

  public MBAlert(MBAlertDefinition definition, MBDocument document, String rootPath)
  {
    super(definition, document, null);
    setRootPath(rootPath);
    setAlertName(definition.getName());
    setTitle(definition.getTitle());

    // Ok, now we can build the children
    buildChildren(definition, document, getParent());
  }

  final private void buildChildren(MBAlertDefinition definition, MBDocument document, MBComponentContainer parent)
  {
    for (MBDefinition def : definition.getChildren())
    {
      String parentAbsoluteDataPath = null;
      if (parent != null)
      {
        parentAbsoluteDataPath = parent.getAbsoluteDataPath();
      }

      if (def.isPreConditionValid(document, parentAbsoluteDataPath))
      {
        addChild(MBComponentFactory.getComponentFromDefinition(def, document, this));
      }
    }
  }

  public AlertDialog buildAlertDialog()
  {
    return MBViewBuilderFactory.getInstance().getAlertViewBuilder().buildAlertDialog(this);
  }

  public String getAlertName()
  {
    return _alertName;
  }

  public void setAlertName(String _alertName)
  {
    this._alertName = _alertName;
  }

  public String getRootPath()
  {
    return _rootPath;
  }

  public void setRootPath(String _rootPath)
  {
    this._rootPath = _rootPath;
  }

  public String getTitle()
  {
    return _title;
  }

  public void setTitle(String _title)
  {
    this._title = _title;
  }

}

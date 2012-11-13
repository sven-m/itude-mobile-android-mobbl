package com.itude.mobile.mobbl2.client.core.view.listeners;

import com.itude.mobile.mobbl2.client.core.view.MBField;
import com.itude.mobile.mobbl2.client.core.view.MBPanel;

public interface MBPageConstructionListener
{
  public void onConstructedField(MBField field);

  public void onConstructedPanel(MBPanel panel);
}

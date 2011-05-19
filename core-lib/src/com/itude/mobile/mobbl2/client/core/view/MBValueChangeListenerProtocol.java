package com.itude.mobile.mobbl2.client.core.view;

public interface MBValueChangeListenerProtocol
{

  public boolean valueWillChange(String value, String originalValue, String path);

  public void valueChanged(String value, String originalValue, String path);

}

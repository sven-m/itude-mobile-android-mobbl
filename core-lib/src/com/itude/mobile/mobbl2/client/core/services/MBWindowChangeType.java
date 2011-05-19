package com.itude.mobile.mobbl2.client.core.services;

public class MBWindowChangeType
{

  private final WindowChangeType eventType;

  public enum WindowChangeType {
    ACTIVATE, LEAVING, DESTROY
  }

  public MBWindowChangeType(WindowChangeType type)
  {
    this.eventType = type;
  }

  public WindowChangeType getEventType()
  {
    return this.eventType;
  }

}

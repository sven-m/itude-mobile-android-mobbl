package com.itude.mobile.mobbl.core.controller;

public class MBPageStackController
{
  private final int    _id;
  private final String _name;
  private final String _mode;

  public MBPageStackController(int id, String name, String mode)
  {
    _id = id;
    _name = name;
    _mode = mode;
  }

  public int getId()
  {
    return _id;
  }

  public String getName()
  {
    return _name;
  }

  public String getMode()
  {
    return _mode;
  }

}
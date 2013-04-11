package com.itude.mobile.mobbl2.client.core.util.resources;

public class MBResourceBuilderFactory
{
  private static MBResourceBuilderFactory _instance;

  private MBResourceBuilder               _resourceBuilder;
  private MBBundleBuilder                 _bundleResourceBuilder;

  private MBResourceBuilderFactory()
  {
    _resourceBuilder = new MBResourceBuilder();
    _bundleResourceBuilder = new MBBundleBuilder();
  }

  public static MBResourceBuilderFactory getInstance()
  {
    if (_instance == null)
    {
      _instance = new MBResourceBuilderFactory();
    }

    return _instance;
  }

  public MBResourceBuilder getResourceBuilder()
  {
    return _resourceBuilder;
  }

  public void setResourceBuilder(MBResourceBuilder resourceBuilder)
  {
    _resourceBuilder = resourceBuilder;
  }

  public MBBundleBuilder getBundleResourceBuilder()
  {
    return _bundleResourceBuilder;
  }

  public void setBundleResourceBuilder(MBBundleBuilder bundleResourceBuilder)
  {
    _bundleResourceBuilder = bundleResourceBuilder;
  }
}

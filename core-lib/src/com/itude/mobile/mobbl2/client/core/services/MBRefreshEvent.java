package com.itude.mobile.mobbl2.client.core.services;

public class MBRefreshEvent extends MBEvent
{

  private boolean _forceDocumentRefreshing = false;

  public MBRefreshEvent()
  {
    super();
  }

  public MBRefreshEvent(boolean forceDocumentRefreshing)
  {
    this._forceDocumentRefreshing = forceDocumentRefreshing;
  }

  public boolean isForceDocumentRefreshing()
  {
    return _forceDocumentRefreshing;
  }

  public void setForceDocumentRefreshing(boolean forceDocumentRefreshing)
  {
    this._forceDocumentRefreshing = forceDocumentRefreshing;
  }

}

package com.itude.mobile.mobbl2.client.core.services;

public interface MBEventListener
{

  public void addEventToQueue(MBEvent event);

  public void removeEventFromQueue(MBEvent event);

  public void onAfterHandlingEvents();

}

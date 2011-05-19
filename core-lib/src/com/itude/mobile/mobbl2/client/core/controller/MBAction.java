package com.itude.mobile.mobbl2.client.core.controller;

import com.itude.mobile.mobbl2.client.core.model.MBDocument;

public interface MBAction
{
  public MBOutcome execute(MBDocument document,  String path);

}

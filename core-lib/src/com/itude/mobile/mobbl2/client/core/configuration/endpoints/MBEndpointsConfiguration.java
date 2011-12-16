package com.itude.mobile.mobbl2.client.core.configuration.endpoints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;
import com.itude.mobile.mobbl2.client.core.services.MBResultListenerDefinition;

public class MBEndpointsConfiguration extends MBDefinition
{
  private final Map<String, MBEndPointDefinition> _endPoints;
  private final List<MBResultListenerDefinition>  _resultListeners;

  public MBEndpointsConfiguration()
  {
    _endPoints = new HashMap<String, MBEndPointDefinition>();
    _resultListeners = new ArrayList<MBResultListenerDefinition>();
  }

  @Override
  public void addEndPoint(MBEndPointDefinition definition)
  {
    _endPoints.put(definition.getDocumentOut(), definition);
  }

  public MBEndPointDefinition getEndPointForDocumentName(String documentName)
  {
    return _endPoints.get(documentName);
  }

  @Override
  public void addResultListener(MBResultListenerDefinition lsnr)
  {
    _resultListeners.add(lsnr);
  }

  public void linkGlobalListeners()
  {
    for (MBEndPointDefinition endPointDef : _endPoints.values())
    {
      for (MBResultListenerDefinition resultDef : _resultListeners)
      {
        endPointDef.addResultListener(resultDef);
      }
    }

  }

}

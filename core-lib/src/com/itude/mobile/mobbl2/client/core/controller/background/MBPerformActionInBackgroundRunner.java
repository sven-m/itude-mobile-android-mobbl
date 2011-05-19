package com.itude.mobile.mobbl2.client.core.controller.background;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBActionDefinition;
import com.itude.mobile.mobbl2.client.core.controller.MBOutcome;

public class MBPerformActionInBackgroundRunner extends MBApplicationControllerBackgroundRunner
{

  MBOutcome          _outcome          = null;
  MBActionDefinition _actionDefinition = null;

  public void setOutcome(MBOutcome mbOutcome)
  {
    _outcome = mbOutcome;
  }

  public void setActionDefinition(MBActionDefinition actionDef)
  {
    _actionDefinition = actionDef;
  }

  @Override
  protected Object[] doInBackground(Object[]... params)
  {
    getController().performActionInBackground(_outcome, _actionDefinition);
    return null;
  }

}

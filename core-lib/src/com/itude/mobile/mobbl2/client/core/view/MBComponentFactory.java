package com.itude.mobile.mobbl2.client.core.view;

import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBFieldDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBForEachDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBPanelDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBVariableDefinition;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.view.exceptions.MBInvalidComponentTypeException;

public class MBComponentFactory
{
  // This is an internal utility class; not meant to be extended or modified by applications
  public static MBComponent getComponentFromDefinition(MBDefinition definition, MBDocument document, MBComponentContainer parent)
  {

    MBComponent result = null;

    if (definition instanceof MBPanelDefinition)
    {
      result = new MBPanel((MBPanelDefinition) definition, document, parent);
    }
    else if (definition instanceof MBForEachDefinition)
    {
      result = new MBForEach((MBForEachDefinition) definition, document, parent);
    }
    else if (definition instanceof MBFieldDefinition)
    {
      result = new MBField(definition, document, parent);
    } 
    else if(definition instanceof MBVariableDefinition)
    {
      result = new MBVariable(definition, document, parent);
    }
    else
    {
      String message = "Unsupported child type: " + definition.getClass().getSimpleName() + " in page or panel";
      throw new MBInvalidComponentTypeException(message);
    }

    return result;
  }

}

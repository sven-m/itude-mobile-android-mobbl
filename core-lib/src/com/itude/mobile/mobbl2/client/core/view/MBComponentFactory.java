package com.itude.mobile.mobbl2.client.core.view;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBBundleDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBFieldDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBForEachDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBPanelDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBVariableDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.resources.MBResourceDefinition;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.view.exceptions.MBInvalidComponentTypeException;

public final class MBComponentFactory
{

  /**
   * Default constructor
   */
  private MBComponentFactory()
  {
  }

  /**
   * This is an internal utility class; not meant to be extended or modified by applications
   * 
   * @param definition {@link MBDefinition}
   * @param document {@link MBDocument}
   * @param parent {@link MBComponentContainer}
   * @return {@link MBComponent}
   */
  public static <T extends MBComponent> T getComponentFromDefinition(MBDefinition definition, MBDocument document,
                                                                     MBComponentContainer parent)
  {

    T result = null;

    if (definition instanceof MBPanelDefinition)
    {
      result = (T) new MBPanel((MBPanelDefinition) definition, document, parent);
    }
    else if (definition instanceof MBForEachDefinition)
    {
      result = (T) new MBForEach((MBForEachDefinition) definition, document, parent);
    }
    else if (definition instanceof MBFieldDefinition)
    {
      result = (T) new MBField(definition, document, parent);
    }
    else if (definition instanceof MBVariableDefinition)
    {
      result = (T) new MBVariable(definition, document, parent);
    }
    else if (definition instanceof MBResourceDefinition)
    {
      MBResourceDefinition resourceDef = (MBResourceDefinition) definition;

      if (StringUtil.isNotBlank(resourceDef.getColor()))
      {
        result = (T) new MBColorResource(resourceDef, document, parent);
      }
      else
      {
        result = (T) new MBImageResource((MBResourceDefinition) definition, document, parent);
      }
    }
    else if (definition instanceof MBBundleDefinition)
    {
      result = (T) new MBBundle((MBBundleDefinition) definition, document, parent);
    }
    else
    {
      String message = "Unsupported child type: " + definition.getClass().getSimpleName() + " in page or panel";
      throw new MBInvalidComponentTypeException(message);
    }

    return result;
  }

}

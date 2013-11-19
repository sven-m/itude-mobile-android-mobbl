/*
 * (C) Copyright Itude Mobile B.V., The Netherlands
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.itude.mobile.mobbl2.client.core.view;

import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBBundleDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBFieldDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBForEachDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBPanelDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBVariableDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.resources.MBItemDefinition;
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
      result = (T) new MBResource((MBResourceDefinition) definition, document, parent);
    }
    else if (definition instanceof MBBundleDefinition)
    {
      result = (T) new MBBundle((MBBundleDefinition) definition, document, parent);
    }
    else if (definition instanceof MBItemDefinition)
    {
      result = (T) new MBItem((MBItemDefinition) definition, document, parent);
    }
    else
    {
      String message = "Unsupported child type: " + definition.getClass().getSimpleName() + " in page or panel";
      throw new MBInvalidComponentTypeException(message);
    }

    return result;
  }

}

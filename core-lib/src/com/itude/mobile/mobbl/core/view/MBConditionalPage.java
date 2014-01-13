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
package com.itude.mobile.mobbl.core.view;

import com.itude.mobile.mobbl.core.configuration.mvc.MBPageDefinition;
import com.itude.mobile.mobbl.core.controller.MBViewManager;
import com.itude.mobile.mobbl.core.controller.MBViewManager.MBViewState;
import com.itude.mobile.mobbl.core.model.MBDocument;

public class MBConditionalPage extends MBPage
{
  public MBConditionalPage(MBPageDefinition definition, MBDocument document, String rootPath, MBViewState viewState)
  {
    super(definition, document, rootPath, viewState);
  }

  private MBPageDefinition _definitionWhenFalse;
  private MBPageDefinition _definitionWhenTrue;

  public MBPageDefinition getDefinitionWhenFalse()
  {
    return _definitionWhenFalse;
  }

  public void setDefinitionWhenFalse(MBPageDefinition definitionWhenFalse)
  {
    _definitionWhenFalse = definitionWhenFalse;
  }

  public MBPageDefinition getDefinitionWhenTrue()
  {
    return _definitionWhenTrue;
  }

  public void setDefinitionWhenTrue(MBPageDefinition definitionWhenTrue)
  {
    _definitionWhenTrue = definitionWhenTrue;
  }

  public Object initWithDefinitionWhenTrue(MBPageDefinition definitionWhenTrue, MBPageDefinition definitionWhenFalse,
                                           Object viewController, MBDocument document, String rootPath, MBViewState viewState)
  {
    return null;
  }

  public Object initWithDefinitionWhenTrue(MBPageDefinition definitionWhenTrue, MBPageDefinition definitionWhenFalse, MBDocument document,
                                           String rootPath, MBViewManager.MBViewState viewState, Object bounds)
  {
    return null;
  }

}

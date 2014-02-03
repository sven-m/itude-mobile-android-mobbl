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
package com.itude.mobile.mobbl.core.actions;

import java.util.List;

import android.util.Log;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl.core.configuration.mvc.MBPageStackDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBDialogDefinition;
import com.itude.mobile.mobbl.core.controller.MBAction;
import com.itude.mobile.mobbl.core.controller.MBApplicationController;
import com.itude.mobile.mobbl.core.controller.MBOutcome;
import com.itude.mobile.mobbl.core.model.MBDocument;
import com.itude.mobile.mobbl.core.model.MBElement;
import com.itude.mobile.mobbl.core.services.MBDataManagerService;
import com.itude.mobile.mobbl.core.services.MBMetadataService;
import com.itude.mobile.mobbl.core.util.Constants;

public class MBFireInitialOutcomes implements MBAction
{
  protected String getDocumentName()
  {
    return "InitialOutcomes";
  }

  protected boolean isFirstDialogSynchronized()
  {
    return true;
  }

  @Override
  public MBOutcome execute(MBDocument document, String path)
  {
    MBDocument initialOutcomes = MBDataManagerService.getInstance().loadDocument(getDocumentName());
    @SuppressWarnings("unchecked")
    List<MBElement> elements = (List<MBElement>) initialOutcomes.getValueForPath("/Outcome");
    boolean first = true;
    for (MBElement element : elements)
    {
      String action = element.getValueForAttribute("action");
      String dialog = element.getValueForAttribute("dialog");
      boolean isMenu = false;

      MBPageStackDefinition def = MBMetadataService.getInstance().getDefinitionForPageStackName(dialog);
      if (def != null)
      {
        String parentDialog = def.getParent();
        MBDialogDefinition parent = MBMetadataService.getInstance().getDefinitionForDialogName(parentDialog);

        if (!parent.isPreConditionValid())
        {
          continue;
        }

        if (!def.isPreConditionValid())
        {
          continue;
        }

        if (StringUtil.isNotBlank(action))
        {
          MBOutcome oc = new MBOutcome();
          oc.setOutcomeName(action);
          oc.setDialogName(dialog);
          oc.setNoBackgroundProcessing(true);
          oc.setTransferDocument(false);
          oc.setOrigin(new MBOutcome.Origin().withAction("FireInitialOutcomes"));

          if (isMenu || (first && isFirstDialogSynchronized()))
          {
            Log.d(this.getClass().getSimpleName(), "Firing in foreground: " + oc);
            MBApplicationController.getInstance().getOutcomeHandler().handleOutcomeSynchronously(oc, false);

            if (!isMenu)
            {

              MBMetadataService.getInstance().setHomeDialogDefinition(parent);
            }
            first = false;

          }
          else
          {
            oc.setDisplayMode(Constants.C_DISPLAY_MODE_BACKGROUND);
            Log.d(this.getClass().getSimpleName(), "Firing in background: " + oc);
            MBApplicationController.getInstance().handleOutcome(oc);

          }
        }
      }
      else
      {
        Log.e(this.getClass().getSimpleName(), "Could not find dialog " + dialog);
      }
    }

    return null;
  }

}

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
package com.itude.mobile.mobbl2.client.core.actions;

import java.util.List;

import android.util.Log;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDialogDefinition;
import com.itude.mobile.mobbl2.client.core.controller.MBAction;
import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.controller.MBOutcome;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.model.MBElement;
import com.itude.mobile.mobbl2.client.core.services.MBDataManagerService;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;

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

      MBDialogDefinition def = MBMetadataService.getInstance().getDefinitionForDialogName(dialog);
      if (StringUtil.isNotBlank(dialog))
      {
        String parentDialog = def.getParent();

        // TODO: refactor this; the dialog manager should already know whether a dialog is visible
        if (parentDialog != null)
        {
          MBDialogDefinition parent = MBMetadataService.getInstance().getDefinitionForDialogName(parentDialog);

          if (!parent.isPreConditionValid())
          {
            continue;
          }
        }
        else if (!def.isPreConditionValid())
        {
          continue;
        }

        if (def != null && def.isShowAsMenu())
        {
          //MBViewManager.getInstance().addSortedDialogName(dialog);
          //isMenu = true;
        }
      }

      if (StringUtil.isNotBlank(action))
      {
        MBOutcome oc = new MBOutcome();
        oc.setOutcomeName(action);
        oc.setDialogName(dialog);
        oc.setNoBackgroundProcessing(true);
        oc.setTransferDocument(false);

        if (isMenu || (first && isFirstDialogSynchronized()))
        {
          Log.d(this.getClass().getSimpleName(), "Firing in foreground: " + oc);
          MBApplicationController.getInstance().getOutcomeHandler().handleOutcomeSynchronously(oc, false);

          if (def.getParent() != null)
          {
            def = MBMetadataService.getInstance().getDefinitionForDialogName(def.getParent());
          }

          if (!isMenu) MBMetadataService.getInstance().setHomeDialogDefinition(def);
          first = false;

        }
        else
        {
          oc.setDisplayMode("BACKGROUND");
          Log.d(this.getClass().getSimpleName(), "Firing in background: " + oc);
          MBApplicationController.getInstance().handleOutcome(oc);

        }
      }

    }

    return null;
  }

}

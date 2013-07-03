package com.itude.mobile.mobbl2.client.core.actions;

import java.util.List;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDialogDefinition;
import com.itude.mobile.mobbl2.client.core.controller.MBAction;
import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.controller.MBOutcome;
import com.itude.mobile.mobbl2.client.core.controller.MBViewManager;
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

        if (def != null && !def.isShowAsMenu())
        {
          MBViewManager.getInstance().addSortedDialogName(dialog);
          isMenu = true;
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
          MBApplicationController.getInstance().getOutcomeHandler().handleOutcomeSynchronously(oc, false);

          if (def.getParent() != null)
          {
            def = MBMetadataService.getInstance().getDefinitionForDialogName(def.getParent());
          }

          MBMetadataService.getInstance().setHomeDialogDefinition(def);
          first = false;
        }
        else
        {
          MBApplicationController.getInstance().handleOutcome(oc);
        }
      }

    }

    return null;
  }

}

package com.itude.mobile.mobbl2.client.core.actions;

import java.util.List;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl2.client.core.controller.MBAction;
import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.controller.MBOutcome;
import com.itude.mobile.mobbl2.client.core.controller.MBViewManager;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.model.MBElement;
import com.itude.mobile.mobbl2.client.core.services.MBDataManagerService;

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
      if (StringUtil.isNotBlank(action))
      {
        MBOutcome oc = new MBOutcome();
        oc.setOutcomeName(action);
        oc.setDialogName(dialog);
        oc.setNoBackgroundProcessing(true);
        oc.setTransferDocument(false);

        if (first && isFirstDialogSynchronized())
        {
          MBApplicationController.getInstance().getOutcomeHandler().handleOutcomeSynchronously(oc, false);
          first = false;
        }
        else
        {
          MBApplicationController.getInstance().handleOutcome(oc);
        }
      }

      if (StringUtil.isNotBlank(dialog) && !"DIALOG-menu".equals(dialog))
      {
        MBViewManager.getInstance().addSortedDialogName(dialog);
      }
    }

    return null;
  }

}

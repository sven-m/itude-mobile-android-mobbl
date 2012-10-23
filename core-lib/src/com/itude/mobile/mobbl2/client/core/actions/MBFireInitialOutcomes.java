package com.itude.mobile.mobbl2.client.core.actions;

import java.util.List;

import com.itude.mobile.mobbl2.client.core.controller.MBAction;
import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.controller.MBOutcome;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.model.MBElement;
import com.itude.mobile.mobbl2.client.core.services.MBDataManagerService;

public class MBFireInitialOutcomes implements MBAction
{
  protected String getDocumentName()
  {
    return "InitialOutcomes";
  }

  @Override
  public MBOutcome execute(MBDocument document, String path)
  {
    MBDocument initialOutcomes = MBDataManagerService.getInstance().loadDocument(getDocumentName());
    @SuppressWarnings("unchecked")
    List<MBElement> elements = (List<MBElement>) initialOutcomes.getValueForPath("/Outcome");
    for (MBElement element : elements)
    {
      MBOutcome oc = new MBOutcome();
      oc.setOutcomeName((String) element.getValueForPath("@action"));
      oc.setDialogName((String) element.getValueForPath("@dialog"));
      oc.setNoBackgroundProcessing(true);
      oc.setTransferDocument(false);

      MBApplicationController.getInstance().getOutcomeHandler().handleOutcomeSynchronously(oc, false);
    }

    return null;
  }

}

package com.itude.mobile.mobbl2.client.core.actions;

import com.itude.mobile.android.util.DeviceUtil;
import com.itude.mobile.mobbl2.client.core.controller.MBAction;
import com.itude.mobile.mobbl2.client.core.controller.MBOutcome;
import com.itude.mobile.mobbl2.client.core.controller.MBViewManager;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;

public abstract class MBSearchAction implements MBAction
{

  @Override
  public MBOutcome execute(MBDocument document, String path)
  {
    try
    {
      final String query = document.getValueForPath("SearchRequest[0]/@query");
      final boolean isProgressive = document.getBooleanForPath("SearchRequest[0]/@isProgressive");
      final String outcomeNameNormal = document.getValueForPath("SearchRequest[0]/@searchResultNormal");
      final String outcomeNameProgressive = document.getValueForPath("SearchRequest[0]/@searchResultProgressive");

      MBDocument searchResult = executeSearch(query);

      // outcome to display the list of search results
      MBOutcome outcome = displaySearchResults(searchResult, outcomeNameNormal, outcomeNameProgressive, path, isProgressive);
      return outcome;
    }
    finally
    {
      if (DeviceUtil.isTablet() || DeviceUtil.getInstance().isPhoneV14())
      {
        MBViewManager.getInstance().supportInvalidateOptionsMenu();
      }
    }
  }

  protected abstract MBDocument executeSearch(String query);

  protected abstract MBOutcome displaySearchResults(MBDocument searchResult, String outcomeNameNormal, String outcomeNameProgressive,
                                                    String path, boolean isProgressive);

}

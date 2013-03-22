package com.itude.mobile.mobbl2.client.core.actions;

import com.itude.mobile.android.util.DeviceUtil;
import com.itude.mobile.mobbl2.client.core.MBException;
import com.itude.mobile.mobbl2.client.core.controller.MBAction;
import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.controller.MBOutcome;
import com.itude.mobile.mobbl2.client.core.controller.MBViewManager;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.util.Constants;

public abstract class MBSearchAction implements MBAction
{
  private MBDocument _searchRequestDoc;
  private String     _query;
  private boolean    _progressiveSearch;
  private String     _outcomeNameNormal;
  private String     _outcomeNameProgressive;

  @Override
  public MBOutcome execute(MBDocument document, String path)
  {
    try
    {
      if (document == null || !Constants.C_DOC_SEARCH_REQUEST.equals(document.getDocumentName()))
      {
        throw new MBException("Wrong document! Expected an " + Constants.C_DOC_SEARCH_REQUEST);
      }

      _searchRequestDoc = document;

      _query = document.getValueForPath(Constants.C_EL_SEARCH_REQUEST + "/" + Constants.C_EL_SEARCH_REQUEST_ATTR_QUERY);
      _progressiveSearch = document.getBooleanForPath(Constants.C_EL_SEARCH_REQUEST + "/"
                                                      + Constants.C_EL_SEARCH_REQUEST_ATTR_IS_PROGRESSIVE);
      _outcomeNameNormal = document.getValueForPath(Constants.C_EL_SEARCH_REQUEST + "/"
                                                    + Constants.C_EL_SEARCH_REQUEST_ATTR_NORMAL_SEARCH_OUTCOME);
      _outcomeNameProgressive = document.getValueForPath(Constants.C_EL_SEARCH_REQUEST + "/"
                                                         + Constants.C_EL_SEARCH_REQUEST_ATTR_PROGRESSIVE_SEARCH_OUTCOME);

      MBDocument searchResult = executeSearch();

      // outcome to display the list of search results
      MBOutcome outcome = displaySearchResults(searchResult, path);
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

  protected abstract MBDocument executeSearch();

  protected MBOutcome displaySearchResults(MBDocument searchResult, String path)
  {
    if (isProgressiveSearch())
    {
      MBOutcome outcome = new MBOutcome();
      outcome.setOutcomeName(getOutcomeNameNormal());
      outcome.setDocument(searchResult);
      outcome.setPath(path);

      MBApplicationController.getInstance().handleOutcomeSynchronously(outcome);
    }

    MBOutcome outcome = new MBOutcome();
    outcome.setOutcomeName(isProgressiveSearch() ? getOutcomeNameProgressive() : getOutcomeNameNormal());
    outcome.setDocument(searchResult);
    outcome.setPath(path);

    return outcome;
  }

  protected String getQuery()
  {
    return _query;
  }

  protected boolean isProgressiveSearch()
  {
    return _progressiveSearch;
  }

  protected String getOutcomeNameNormal()
  {
    return _outcomeNameNormal;
  }

  protected String getOutcomeNameProgressive()
  {
    return _outcomeNameProgressive;
  }

  public MBDocument getSearchRequestDoc()
  {
    return _searchRequestDoc;
  }

}

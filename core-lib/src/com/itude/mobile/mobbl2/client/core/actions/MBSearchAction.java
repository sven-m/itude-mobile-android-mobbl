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

import com.itude.mobile.android.util.DeviceUtil;
import com.itude.mobile.mobbl2.client.core.MBException;
import com.itude.mobile.mobbl2.client.core.controller.MBAction;
import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.controller.MBOutcome;
import com.itude.mobile.mobbl2.client.core.controller.MBViewManager;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.util.Constants;

/***
 * 
 * Important: make sure your application has the MBSearchRequestDoc definition.
 * 
 * Perform the search based on the SearchRequest. The method {@link #executeSearch()} must be implemented in a subclass.
 * The attributes of the search request document can be accessed via their getters. It is also possible to access the 
 * MBSearchRequestDoc in case you implemented a data handler that can handle the MBSearchRequestDoc.
 * 
 * More info: https://mobiledev.itude.com/confluence/display/MOBBL/Search
 *
 * @author Coen Houtman
 * @since 3.0.0.16
 */
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

  /***
   * 
   * @return an MBDocument that is used on the page with the search results
   */
  protected abstract MBDocument executeSearch();

  /***
   * The page with the search results is always being displayed. In case a progressive search result
   * is selected, the user automatically navigates to the specific search item.
   * 
   * @param searchResult the MBDocument that is used on the page with the search results
   * @param path the path to the selected progressive search result
   * @return MBOutcome to display the search results
   */
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

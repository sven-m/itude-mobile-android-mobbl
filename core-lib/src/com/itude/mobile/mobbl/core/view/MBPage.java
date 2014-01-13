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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl.core.configuration.mvc.MBPageDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBPageDefinition.MBPageType;
import com.itude.mobile.mobbl.core.configuration.mvc.exceptions.MBInvalidPathException;
import com.itude.mobile.mobbl.core.controller.MBApplicationController;
import com.itude.mobile.mobbl.core.controller.MBOutcome;
import com.itude.mobile.mobbl.core.controller.MBViewManager;
import com.itude.mobile.mobbl.core.controller.MBViewManager.MBViewState;
import com.itude.mobile.mobbl.core.controller.util.MBBasicViewController;
import com.itude.mobile.mobbl.core.model.MBDocument;
import com.itude.mobile.mobbl.core.model.MBDocumentDiff;
import com.itude.mobile.mobbl.core.util.Constants;
import com.itude.mobile.mobbl.core.util.MBPathUtil;
import com.itude.mobile.mobbl.core.view.builders.MBViewBuilderFactory;

public class MBPage extends MBPanel
{
  private static final Pattern                                   NUMBERPATTERN = Pattern.compile("\\[[0-9]+\\]");

  private String                                                 _pageName;
  private String                                                 _rootPath;
  private String                                                 _dialogName;
  private MBDocument                                             _document;
  private MBApplicationController                                _controller;
  private MBBasicViewController                                  _viewController;
  private List                                                   _childViewControllers;
  private MBDocumentDiff                                         _documentDiff;
  private final Map<String, List<MBValueChangeListenerProtocol>> _valueChangedListeners;
  private MBPageDefinition.MBPageType                            _pageType;
  private Object                                                 _maxBounds;
  private final MBViewManager.MBViewState                        _viewState;
  private boolean                                                _scrollable;
  //  private boolean                                                _allowedPortraitOrientation;
  //  private boolean                                                _allowedLandscapeOrientation;
  private boolean                                                _reloadOnDocChange;
  private View                                                   _selectedView;

  private OrientationPermission                                  _orientationPermission;

  public MBPage(MBPageDefinition definition, MBDocument document, String rootPath, MBViewState viewState)
  {
    // Make sure that the Panel does not start building the view based on the children OF THIS PAGE because that is too early
    // The children need the additional information that is set after the constructor of super. So pass buildViewStructure: FALSE
    // and build the children ourselves here
    super(definition, document, null, false);

    setDefinition(definition);
    setRootPath(rootPath);
    setPageName(definition.getName());
    setDocument(document);
    setPageType(definition.getPageType());
    setTitle(definition.getTitle());
    parseOrientationPermissions(definition.getOrientationPermissions());
    setScrollable(definition.isScrollable());
    setReloadOnDocChange(definition.isReloadOnDocChange());

    _viewState = viewState;
    _valueChangedListeners = new Hashtable<String, List<MBValueChangeListenerProtocol>>();

    // Ok, now we can build the children
    buildChildren(definition, document, getParent());
  }

  public enum OrientationPermission {
    UNDEFINED, ANY, PORTRAIT, LANDSCAPE
  }

  public String getPageName()
  {
    return _pageName;
  }

  public void setPageName(String pageName)
  {
    _pageName = pageName;
  }

  public String getDialogName()
  {
    return _dialogName;
  }

  public void setDialogName(String dialogName)
  {
    _dialogName = dialogName;
  }

  public MBApplicationController getController()
  {
    return _controller;
  }

  public void setController(MBApplicationController controller)
  {
    _controller = controller;
  }

  @Override
  public MBDocument getDocument()
  {
    return _document;
  }

  @Override
  public synchronized void setDocument(MBDocument document)
  {
    _document = document;
  }

  public MBDocumentDiff getDocumentDiff()
  {
    return _documentDiff;
  }

  public void setDocumentDiff(MBDocumentDiff documentDiff)
  {
    _documentDiff = documentDiff;
  }

  public List getChildViewControllers()
  {
    return _childViewControllers;
  }

  public void setChildViewControllers(List childViewControllers)
  {
    _childViewControllers = childViewControllers;
  }

  public MBPageDefinition.MBPageType getPageType()
  {
    return _pageType;
  }

  public void setPageType(MBPageType pageType)
  {
    _pageType = pageType;
  }

  public void handleOutcome(String outcomeName)
  {
    handleTheOutcome(outcomeName, null, false);
  }

  public void handleOutcomeSynchrone(String outcomeName)
  {
    handleTheOutcome(outcomeName, null, true);
  }

  @Override
  public void handleOutcome(String outcomeName, String path)
  {
    handleTheOutcome(outcomeName, path, false);
  }

  public void handleTheOutcome(String outcomeName, String path, boolean synchro)
  {
    MBOutcome outcome = new MBOutcome();
    outcome.setOriginName(getPageName());
    outcome.setOutcomeName(outcomeName);
    outcome.setDocument(getDocument());
    outcome.setDialogName(getDialogName());
    outcome.setPath(path);

    if (synchro)
    {
      _controller.handleOutcomeSynchronously(outcome);
    }
    else
    {
      _controller.handleOutcome(outcome);
    }
  }

  @Override
  public String getComponentDataPath()
  {
    return getRootPath();
  }

  @Override
  public String getDescription()
  {
    return "Page: pageID=" + _pageName;
  }

  public String getRootPath()
  {
    return _rootPath;
  }

  public void setRootPath(String path)
  {
    boolean ignorePath = false;

    if (path == null)
    {
      path = "";
    }
    else if (!path.endsWith("/"))
    {
      path = path + "/";
    }

    if (path.length() > 0)
    {
      MBPageDefinition pd = (MBPageDefinition) getDefinition();
      String stripped = MBPathUtil.normalizedPath(NUMBERPATTERN.matcher(path).replaceAll(""));
      if (!stripped.endsWith("/"))
      {
        stripped = stripped + "/";
      }
      String mustBe = pd.getRootPath();
      if (mustBe == null || mustBe.equals(""))
      {
        mustBe = "/";
      }

      if (!stripped.equals(mustBe))
      {
        if (mustBe.equals("/"))
        {
          Log.w(Constants.APPLICATION_NAME, "Ignoring path " + stripped + " because the document definition used root path " + mustBe
                                            + ". Check your document attribute in your page definition. Maybe you meant: "
                                            + ((MBPageDefinition) getDefinition()).getDocumentName() + "/" + stripped);
          ignorePath = true;
        }
        else
        {
          String message = "Invalid root path " + path + "->" + stripped + "; does not conform to defined document root path " + mustBe;
          throw new MBInvalidPathException(message);
        }
      }
    }

    if (!ignorePath && _rootPath != path)
    {
      _rootPath = path;
    }

  }

  public Object getView()
  {
    // TODO UIViewController is an objective C class, what should be the Android implementation of it?
    return null;
  }

  public void setViewController(MBBasicViewController viewController)
  {
    _viewController = viewController;
  }

  public MBBasicViewController getViewController()
  {
    return _viewController;
  }

  public MBDocumentDiff diffDocument(MBDocument other)
  {

    MBDocumentDiff diff = new MBDocumentDiff(getDocument(), other);
    setDocumentDiff(diff);

    return getDocumentDiff();
  }

  public List<MBValueChangeListenerProtocol> getListenersForPath(String path)
  {
    if (!path.startsWith("/"))
    {
      path = "/" + path;
    }

    path = MBPathUtil.normalizedPath(path);
    List<MBValueChangeListenerProtocol> lsnrList = _valueChangedListeners.get(path);
    if (lsnrList == null)
    {
      lsnrList = new ArrayList<MBValueChangeListenerProtocol>();
      _valueChangedListeners.put(path, lsnrList);
    }

    return lsnrList;
  }

  public void registerValueChangeListener(MBValueChangeListenerProtocol listener, String path)
  {
    // Check that the path is valid by reading the value:
    getDocument().getValueForPath(path);

    List<MBValueChangeListenerProtocol> lsnrList = getListenersForPath(path);
    lsnrList.add(listener);
  }

  public void unregisterValueChangeListener(MBValueChangeListenerProtocol listener, String path)
  {
    // Check that the path is valid by reading the value:
    getDocument().getValueForPath(path);

    List<MBValueChangeListenerProtocol> lsnrList = getListenersForPath(path);
    lsnrList.remove(listener);
  }

  public void unregisterValueChangeListener(MBValueChangeListenerProtocol listener)
  {
    // Check that the path is valid by reading the value:

    for (List<MBValueChangeListenerProtocol> list : _valueChangedListeners.values())
    {
      list.remove(listener);
    }
  }

  @Override
  public boolean notifyValueWillChange(String value, String originalValue, String path)
  {
    boolean result = true;

    List<MBValueChangeListenerProtocol> lsnrList = getListenersForPath(path);
    for (MBValueChangeListenerProtocol lsnr : lsnrList)
    {
      result &= lsnr.valueWillChange(value, originalValue, path);
    }

    return result;
  }

  @Override
  public void notifyValueChanged(String value, String originalValue, String path)
  {
    List<MBValueChangeListenerProtocol> lsnrList = getListenersForPath(path);

    for (MBValueChangeListenerProtocol lsnr : lsnrList)
    {
      lsnr.valueChanged(value, originalValue, path);
    }

  }

  @Override
  public void rebuild()
  {
    getDocument().clearAllCaches();
    super.rebuild();
  }

  public void rebuildView()
  {
    // Make sure we clear the cache of all related documents:
    rebuild();
    // TODO UIViewController stuff
    //    CGRect bounds = [UIScreen mainScreen].applicationFrame; 
    //    self.viewController.view = [self buildViewWithMaxBounds: bounds viewState: _viewState];
  }

  public void parseOrientationPermissions(String permissions)
  {

    if (permissions == null)
    {
      _orientationPermission = OrientationPermission.UNDEFINED;
    }
    else if (permissions.equals(Constants.C_PAGE_ORIENTATION_PERMISSION_ANY))
    {
      _orientationPermission = OrientationPermission.ANY;
    }
    else
    {
      boolean allowedLandscapeOrientation = false;
      boolean allowedPortraitOrientation = false;

      String[] permissionList = permissions.split("\\|");
      for (String permission : permissionList)
      {
        if (permission.equals(Constants.C_PAGE_ORIENTATION_PERMISSION_LANDSCAPE))
        {
          allowedLandscapeOrientation = true;
        }
        else if (permission.equals(Constants.C_PAGE_ORIENTATION_PERMISSION_PORTRAIT))
        {
          allowedPortraitOrientation = true;
        }
      }

      if (allowedLandscapeOrientation)
      {
        _orientationPermission = OrientationPermission.LANDSCAPE;
      }
      else if (allowedPortraitOrientation)
      {
        _orientationPermission = OrientationPermission.PORTRAIT;
      }
    }

  }

  @Override
  public MBPage getPage()
  {
    return this;
  }

  @Override
  public ViewGroup buildView()
  {
    return MBViewBuilderFactory.getInstance().getPageViewBuilder().buildPageView(this, null);
  }

  public void handleException(Exception exception)
  {
    MBOutcome outcome = new MBOutcome(getPageName(), _document);
    _controller.handleException(exception, outcome);
  }

  public MBViewState getCurrentViewState()
  {
    return _viewState;
  }

  public void unregisterAllViewControllers()
  {
    setChildViewControllers(null);
  }

  @Override
  public boolean isScrollable()
  {
    return _scrollable;
  }

  @Override
  public void setScrollable(boolean scrollable)
  {
    _scrollable = scrollable;
  }

  public OrientationPermission getOrientationPermissions()
  {
    return _orientationPermission;
  }

  @Override
  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    StringUtil.appendIndentString(appendToMe, level).append("<MBPage ").append(attributeAsXml("pageName", _pageName)).append(" ")
        .append(attributeAsXml("rootPath", _rootPath)).append(" ").append(attributeAsXml("dialogName", _dialogName)).append(" ")
        .append(attributeAsXml("document", _document.getDocumentName())).append(">\n");

    childrenAsXmlWithLevel(appendToMe, level + 2);

    return StringUtil.appendIndentString(appendToMe, level).append("</MBPage>\n");

  }

  @Override
  public String toString()
  {
    StringBuffer rt = new StringBuffer();
    return asXmlWithLevel(rt, 0).toString();
  }

  public View getSelectedView()
  {
    return _selectedView;
  }

  public void setSelectedView(View selectedView)
  {
    _selectedView = selectedView;
  }

  public boolean isReloadOnDocChange()
  {
    return _reloadOnDocChange;
  }

  public void setReloadOnDocChange(boolean reloadOnDocChange)
  {
    _reloadOnDocChange = reloadOnDocChange;
  }

}

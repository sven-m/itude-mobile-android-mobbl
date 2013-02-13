package com.itude.mobile.mobbl2.client.core.view;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBPageDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBPageDefinition.MBPageType;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.exceptions.MBInvalidPathException;
import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.controller.MBOutcome;
import com.itude.mobile.mobbl2.client.core.controller.MBViewManager;
import com.itude.mobile.mobbl2.client.core.controller.MBViewManager.MBViewState;
import com.itude.mobile.mobbl2.client.core.controller.util.MBBasicViewController;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.model.MBDocumentDiff;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.StringUtilities;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilderFactory;

public class MBPage extends MBPanel
{
  private static final Pattern                                   NUMBERPATTERN          = Pattern.compile("\\[[0-9]+\\]");

  private String                                                 _pageName;
  private String                                                 _rootPath;
  private String                                                 _dialogName;
  private MBDocument                                             _document;
  private MBApplicationController                                _controller;
  private MBBasicViewController                                  _viewController;
  private List                                                   _childViewControllers;
  private MBDocumentDiff                                         _documentDiff;
  private final Map<String, List<MBValueChangeListenerProtocol>> _valueChangedListeners;
  private final List<MBOutcomeListenerProtocol>                  _outcomeListeners;
  private MBPageDefinition.MBPageType                            _pageType;
  private Object                                                 _maxBounds;
  private final MBViewManager.MBViewState                        _viewState;
  private final boolean                                          _allowedAnyOrientation = true;
  private boolean                                                _scrollable;
  private boolean                                                _allowedPortraitOrientation;
  private boolean                                                _allowedLandscapeOrientation;
  private boolean                                                _reloadOnDocChange;
  private View                                                   _selectedView;

  public MBPage(MBPageDefinition definition, MBDocument document, String rootPath, MBViewState viewState)
  {
    // https://dev.itude.com/jira/browse/BINCKMOBILE-320#action_15310
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
    _outcomeListeners = new ArrayList<MBOutcomeListenerProtocol>();
    _valueChangedListeners = new Hashtable<String, List<MBValueChangeListenerProtocol>>();

    // Ok, now we can build the children
    buildChildren(definition, document, getParent());
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
  public void setDocument(MBDocument document)
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

    for (MBOutcomeListenerProtocol lsnr : _outcomeListeners)
    {
      lsnr.outcomeProduced(outcome);
    }
    if (synchro)
    {
      _controller.handleOutcomeSynchronously(outcome);
    }
    else
    {
      _controller.handleOutcome(outcome);
    }
    for (MBOutcomeListenerProtocol lsnr : _outcomeListeners)
    {
      lsnr.afterOutcomeHandled(outcome);
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
      String stripped = StringUtilities.normalizedPath(NUMBERPATTERN.matcher(path).replaceAll(""));
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
          Log.w(Constants.APPLICATION_NAME, "Ignoring path " + stripped + " because the document definition used root path " + mustBe);
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

    path = StringUtilities.normalizedPath(path);
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

  public void registerOutcomeListener(MBOutcomeListenerProtocol listener)
  {
    if (!_outcomeListeners.contains(listener))
    {
      _outcomeListeners.add(listener);
    }
  }

  public void unregisterOutcomeListener(MBOutcomeListenerProtocol listener)
  {
    _outcomeListeners.remove(listener);
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
    if (permissions != null && !permissions.equals(Constants.C_PAGE_ORIENTATION_PERMISSION_ANY))
    {
      _allowedLandscapeOrientation = false;
      _allowedPortraitOrientation = false;

      String[] permissionList = permissions.split("\\|");
      for (String permission : permissionList)
      {
        if (permission.equals(Constants.C_PAGE_ORIENTATION_PERMISSION_LANDSCAPE))
        {
          _allowedLandscapeOrientation = true;
        }
        else if (permission.equals(Constants.C_PAGE_ORIENTATION_PERMISSION_PORTRAIT))
        {
          _allowedPortraitOrientation = true;
        }
      }
    }
    else
    {
      _allowedLandscapeOrientation = true;
      _allowedPortraitOrientation = true;
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

  public boolean isAllowedPortraitOrientation()
  {
    return _allowedPortraitOrientation;
  }

  public boolean isAllowedLandscapeOrientation()
  {
    return _allowedLandscapeOrientation;
  }

  public boolean isAllowedAnyOrientation()
  {
    return _allowedLandscapeOrientation && _allowedPortraitOrientation;
  }

  @Override
  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    StringUtilities.appendIndentString(appendToMe, level).append("<MBPage ").append(attributeAsXml("pageName", _pageName)).append(" ")
        .append(attributeAsXml("rootPath", _rootPath)).append(" ").append(attributeAsXml("dialogName", _dialogName)).append(" ")
        .append(attributeAsXml("document", _document.getDocumentName())).append(">\n");

    childrenAsXmlWithLevel(appendToMe, level + 2);

    return StringUtilities.appendIndentString(appendToMe, level).append("</MBPage>\n");

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

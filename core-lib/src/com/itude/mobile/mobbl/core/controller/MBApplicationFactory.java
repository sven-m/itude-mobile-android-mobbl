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
package com.itude.mobile.mobbl.core.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.itude.mobile.mobbl.core.MBException;
import com.itude.mobile.mobbl.core.configuration.mvc.MBAlertDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBPageDefinition;
import com.itude.mobile.mobbl.core.controller.util.MBBasicViewController;
import com.itude.mobile.mobbl.core.model.MBDocument;
import com.itude.mobile.mobbl.core.services.MBResultListener;
import com.itude.mobile.mobbl.core.view.MBAlert;
import com.itude.mobile.mobbl.core.view.MBField;
import com.itude.mobile.mobbl.core.view.MBPage;
import com.itude.mobile.mobbl.core.view.MBPanel;
import com.itude.mobile.mobbl.core.view.builders.MBContentViewWrapper;
import com.itude.mobile.mobbl.core.view.builders.contentview.MBDefaultContentViewWrapper;
import com.itude.mobile.mobbl.core.view.components.tabbar.MBActionBarBuilder;
import com.itude.mobile.mobbl.core.view.listeners.MBPageConstructionListener;

/**
 * Factory class for creating custom MBViewControllers, MBResultListeners and MBActions
 * <br/> 
 * In short there are three steps to using custom code with MOBBL framework:
 * <ol>
 *  <li>Create Pages, Actions and ResultListeners in the application definition files  (config.xml and endpoints.xml).</li>
 *  <li>Create a subclass of the MBApplicationFactory which can create custom ViewControllers, MBActions and MBResultListeners,,/li>
 *  <li>set the instance to your MBApplicationFactory subclass:
 *  <code>
 *      MBApplicationFactory.setInstance(new CustomApplicationFactory());
 *  </code>
 *  </ol>
 */
public class MBApplicationFactory
{
  private static MBApplicationFactory         _instance = null;
  private final ActionMappings                _actions;
  private final ControllerMappings            _controllers;
  private final PageConstructor               _pageConstructor;
  private Class<? extends MBActionBarBuilder> _actionBarBuilder;

  public MBApplicationFactory()
  {
    _actions = new ActionMappings(getActionRegistry());
    _controllers = new ControllerMappings(getControllerRegistry());
    _pageConstructor = new PageConstructor();
  }

  public static MBApplicationFactory getInstance()
  {
    if (_instance == null)
    {
      _instance = new MBApplicationFactory();
    }
    return _instance;
  }

  public static void setInstance(MBApplicationFactory factory)
  {
    _instance = factory;
  }

  public PageConstructor getPageConstructor()
  {
    return _pageConstructor;
  }

  public MBAction createAction(String actionClassName)
  {
    return _actions.createAction(actionClassName);
  }

  public MBResultListener createResultListener(String listenerClassName)
  {
    return null;
  }

  public MBBasicViewController createFragment(String pageName)
  {
    return _controllers.createController(pageName);
  }

  public final MBPage createPage(MBPageDefinition definition, MBDocument document, String rootPath)
  {
    return getPageConstructor().createPage(definition, document, rootPath);
  }

  public MBDialogController createDialogController()
  {
    return new MBDialogController();
  }

  public MBContentViewWrapper createContentViewWrapper()
  {
    return new MBDefaultContentViewWrapper();
  }

  public final MBAlert createAlert(MBAlertDefinition definition, MBDocument document, String rootPath)
  {
    return new MBAlert(definition, document, rootPath);
  }

  public Class<? extends MBActionBarBuilder> getActionBarBuilder()
  {
    return _actionBarBuilder;
  }

  public void setActionBarBuilder(Class<? extends MBActionBarBuilder> actionBarBuilder)
  {
    _actionBarBuilder = actionBarBuilder;
  }

  protected ActionMappings.Registry getActionRegistry()
  {
    return null;
  }

  protected ControllerMappings.Registry getControllerRegistry()
  {
    return null;
  }

  public static class ActionMappings
  {
    public static abstract class Registry
    {
      private final Map<String, Class<? extends MBAction>> actions;

      protected Registry()
      {
        actions = new HashMap<String, Class<? extends MBAction>>();
        registerMappings();
      }

      protected void registerAction(String name, Class<? extends MBAction> action)
      {
        actions.put(name, action);
      }

      protected abstract void registerMappings();
    }

    private final Map<String, Class<? extends MBAction>> _actions;

    protected ActionMappings(Registry registry)
    {
      if (registry != null) _actions = Collections.unmodifiableMap(registry.actions);
      else _actions = Collections.emptyMap();
    }

    MBAction createAction(String actionClassName)
    {
      Class<? extends MBAction> action = _actions.get(actionClassName);

      if (action != null) try
      {
        return action.newInstance();
      }
      catch (Exception e)
      {
        throw new MBException("Error instantiating " + action.getSimpleName(), e);
      }

      else throw new MBException("Action " + actionClassName + " not found");
    }
  }

  public static class ControllerMappings
  {
    public static abstract class Registry
    {
      private final Map<String, Class<? extends MBBasicViewController>> controllers;
      private Class<? extends MBBasicViewController>                    defaultController;

      protected Registry()
      {
        controllers = new HashMap<String, Class<? extends MBBasicViewController>>();
        defaultController = MBBasicViewController.class;
        registerMappings();
      }

      protected void registerController(String pageName, Class<? extends MBBasicViewController> controller)
      {
        controllers.put(pageName, controller);
      }

      protected void setDefaultController(Class<? extends MBBasicViewController> defaultController)
      {
        this.defaultController = defaultController;
      }

      protected abstract void registerMappings();
    }

    private final Map<String, Class<? extends MBBasicViewController>> _controllers;
    private final Class<? extends MBBasicViewController>              _defaultController;

    protected ControllerMappings(Registry registry)
    {
      if (registry != null)
      {
        _controllers = Collections.unmodifiableMap(registry.controllers);
        _defaultController = registry.defaultController;
      }
      else
      {
        _controllers = Collections.emptyMap();
        _defaultController = MBBasicViewController.class;
      }
    }

    MBBasicViewController createController(String pageName)
    {
      Class<? extends MBBasicViewController> controller = _controllers.get(pageName);
      if (controller == null) controller = _defaultController;

      if (controller != null) try
      {
        return controller.newInstance();
      }
      catch (Exception e)
      {
        throw new MBException("Error instantiating " + controller.getSimpleName(), e);
      }

      else throw new MBException("Controller for page  " + pageName + " not found");
    }
  }

  public static class PageConstructor implements MBPageConstructionListener
  {

    private final List<MBPageConstructionListener> _constructionListeners;

    public PageConstructor()
    {
      _constructionListeners = new ArrayList<MBPageConstructionListener>();
    }

    public void addListener(MBPageConstructionListener listener)
    {
      _constructionListeners.add(listener);
    }

    @Override
    public void onConstructedField(MBField field)
    {

      for (MBPageConstructionListener listener : _constructionListeners)
        listener.onConstructedField(field);
    }

    @Override
    public void onConstructedPanel(MBPanel panel)
    {
      for (MBPageConstructionListener listener : _constructionListeners)
        listener.onConstructedPanel(panel);
    }

    public MBPage createPage(MBPageDefinition definition, MBDocument document, String rootPath)
    {
      MBPage page = new MBPage(definition, document, rootPath);
      return page;
    }

  }

}

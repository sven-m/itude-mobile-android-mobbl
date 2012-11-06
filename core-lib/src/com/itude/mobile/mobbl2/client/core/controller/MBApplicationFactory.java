package com.itude.mobile.mobbl2.client.core.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.itude.mobile.mobbl2.client.core.MBException;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBPageDefinition;
import com.itude.mobile.mobbl2.client.core.controller.util.MBBasicViewController;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.services.MBResultListener;
import com.itude.mobile.mobbl2.client.core.view.MBPage;
import com.itude.mobile.mobbl2.client.core.view.helpers.MBEditableMatrixListener;

/*
 * Factory class for Pages and Actions.
 * Subclass and use setInstance() to provide custom Pages and custom Actions
 */

public class MBApplicationFactory
{
  private static MBApplicationFactory _instance = null;
  private final ActionMappings        _actions;
  private final ControllerMappings    _controllers;

  public MBApplicationFactory()
  {
    _actions = new ActionMappings(getActionRegistry());
    _controllers = new ControllerMappings(getControllerRegistry());
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

  public MBPage createPage(MBPageDefinition definition, MBDocument document, String rootPath, MBViewManager.MBViewState viewState)
  {
    MBPage page = new MBPage(definition, document, rootPath, viewState);
    return page;
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

  public MBDialogController createDialogController()
  {
    return new MBDialogController();
  }

  public MBEditableMatrixListener getEditableMatrixListener(String panelName)
  {
    return null;
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
      private Map<String, Class<? extends MBAction>> actions;

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
      private Map<String, Class<? extends MBBasicViewController>> controllers;
      private Class<? extends MBBasicViewController>              defaultController;

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
      if (registry != null) {
        _controllers = Collections.unmodifiableMap(registry.controllers);
        _defaultController = registry.defaultController;
      }
      else {
        _controllers = Collections.emptyMap();
        _defaultController = MBBasicViewController.class;ff
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

}

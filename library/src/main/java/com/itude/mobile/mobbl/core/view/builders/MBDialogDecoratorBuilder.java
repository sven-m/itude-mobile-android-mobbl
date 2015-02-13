package com.itude.mobile.mobbl.core.view.builders;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.itude.mobile.android.util.AssertUtil;
import com.itude.mobile.mobbl.core.MBException;
import com.itude.mobile.mobbl.core.controller.MBDialogController;
import com.itude.mobile.mobbl.core.view.builders.dialog.DefaultDialogDecorator;
import com.itude.mobile.mobbl.core.view.builders.dialog.MenuDialogDecorator;
import com.itude.mobile.mobbl.core.view.builders.dialog.ModalDialogDecorator;

public class MBDialogDecoratorBuilder
{
  private Map<String, Class<? extends MBDialogDecorator>> _registry;

  public MBDialogDecoratorBuilder()
  {
    _registry = new HashMap<String, Class<? extends MBDialogDecorator>>();
    registerDecorator("DEFAULT", DefaultDialogDecorator.class);
    registerDecorator("MODAL", ModalDialogDecorator.class);
    registerDecorator("MENU", MenuDialogDecorator.class);
  }

  public void registerDecorator(String type, Class<? extends MBDialogDecorator> decorator)
  {
    AssertUtil.notNull("type", type);
    AssertUtil.notNull("decorator", decorator);
    _registry.put(type, decorator);
  }

  public MBDialogDecorator createDecorator(String name, MBDialogController dialog)
  {
    try
    {
      Class<? extends MBDialogDecorator> builder = _registry.get(name);
      if (builder == null) throw new MBException("No dialog decorator for " + name + " registered!");

      return builder.getConstructor(MBDialogController.class).newInstance(dialog);
    }
    catch (IllegalAccessException e)
    {
      throw new MBException("Error instantiating " + name, e);
    }
    catch (InstantiationException e)
    {
      throw new MBException("Error instantiating " + name, e);
    }
    catch (IllegalArgumentException e)
    {
      throw new MBException("Error instantiating " + name, e);
    }
    catch (InvocationTargetException e)
    {
      throw new MBException("Error instantiating " + name, e);
    }
    catch (NoSuchMethodException e)
    {
      throw new MBException("Error instantiating " + name, e);
    }
  }

}

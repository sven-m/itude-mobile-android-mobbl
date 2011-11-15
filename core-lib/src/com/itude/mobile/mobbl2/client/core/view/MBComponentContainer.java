package com.itude.mobile.mobbl2.client.core.view;

import java.util.ArrayList;

import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;

public class MBComponentContainer extends MBComponent
{

  private ArrayList<MBComponent> _children;

  public MBComponentContainer(MBDefinition definition, MBDocument document, MBComponentContainer parent)
  {
    super(definition, document, parent);
    _children = new ArrayList<MBComponent>();
  }

  public ArrayList<MBComponent> getChildren()
  {
    return _children;
  }

  public void setChildren(ArrayList<MBComponent> children)
  {
    _children = children;
  }

  public void addChild(MBComponent child)
  {
    if (child != null && !child.isMarkedForDestruction())
    {
      _children.add(child);
      child.setParent(this);
    }
  }

  @Override
  public void translatePath()
  {
    for (MBComponent child : _children)
    {
      child.translatePath();
    }
  }

  @Override
  public ArrayList<MBComponent> getChildrenOfKind(Class<?> clazz)
  {
    ArrayList<MBComponent> result = new ArrayList<MBComponent>();
    for (MBComponent child : _children)
    {
      if (clazz.isInstance(child))
      {
        result.add(child);
      }
    }

    return result;
  }

  @SuppressWarnings("unchecked")
  public <T extends MBComponent> T getComponentOfKindWithName(Class<?> clazz, String name)
  {

    // Walk through all children
    for (MBComponent child : _children)
    {

      if (clazz.isInstance(child) && name.equals(child.getName()))
      {
        return (T) child;
      }
      else if (child instanceof MBComponentContainer)
      {
        MBComponent recursiveChild = ((MBComponentContainer) child).getComponentOfKindWithName(clazz, name);
        if (recursiveChild != null)
        {
          return (T) recursiveChild;
        }
      }

    }

    return null;
  }

  @Override
  public ArrayList<Object> getDescendantsOfKind(Class<?> clazz)
  {
    ArrayList<Object> result = new ArrayList<Object>();
    for (MBComponent child : _children)
    {

      if (clazz.isInstance(child))
      {
        result.add(child);
      }

      result.addAll(child.getDescendantsOfKind(clazz));
    }

    return result;
  }

  public StringBuffer childrenAsXmlWithLevel(StringBuffer appendToMe, int level)
  {
    for (MBComponent child : _children)
    {
      child.asXmlWithLevel(appendToMe, level);
    }

    return appendToMe;
  }

}

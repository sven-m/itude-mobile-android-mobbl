package com.itude.mobile.mobbl2.client.core.view;

import java.util.ArrayList;
import java.util.List;

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

  @Override
  public ArrayList<Object> getDescendantsOfKind(Class<?> clazz)
  {
    List<Object> result = new ArrayList<Object>();
    for (MBComponent child : _children)
    {

      if (clazz.isInstance(child))
      {
        result.add(child);
      }

      result.addAll(child.getDescendantsOfKind(clazz));
    }

    return super.getDescendantsOfKind(clazz);
  }

  public StringBuffer childrenAsXmlWithLevel(StringBuffer p_appendToMe, int level)
  {
    for (MBComponent child : _children)
    {
      child.asXmlWithLevel(p_appendToMe, level);
    }

    return p_appendToMe;
  }

}

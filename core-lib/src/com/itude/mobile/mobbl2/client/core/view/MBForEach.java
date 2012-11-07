package com.itude.mobile.mobbl2.client.core.view;

import java.util.ArrayList;
import java.util.List;

import android.view.ViewGroup;

import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBForEachDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBVariableDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.exceptions.MBInvalidPathException;
import com.itude.mobile.mobbl2.client.core.controller.MBViewManager.MBViewState;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.util.StringUtilities;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilderFactory;

public class MBForEach extends MBComponentContainer
{
  private ArrayList<MBForEachItem> _rows; // arrayofMBRows
  private String                   _value;

  public MBForEach(MBForEachDefinition definition, MBDocument document, MBComponentContainer parent)
  {
    super(definition, document, parent);

    setValue(definition.getValue());

    _rows = new ArrayList<MBForEachItem>();

    MBForEachDefinition def = (MBForEachDefinition) getDefinition();
    if (!def.isPreConditionValid(document, parent.getAbsoluteDataPath()))
    {
      // Our precondition is not true; so we must not exist:
      setMarkedForDestruction(true);
    }
    else
    {
      String fullPath = _value;
      if (!fullPath.startsWith("/") && fullPath.indexOf(":") == -1)
      {
        fullPath = parent.getAbsoluteDataPath() + "/" + _value;
      }

      Object pathResult = document.getValueForPath(fullPath);
      if (pathResult != null)
      {
        if (!(pathResult instanceof List<?>)) throw new MBInvalidPathException(_value);
        for (int i = 0; i < ((List<?>) pathResult).size(); i++)
        {

          MBForEachItem item = new MBForEachItem(getDefinition(), getDocument(), this);
          addItem(item);

          for (MBDefinition childDef : (ArrayList<MBDefinition>) def.getChildren())
          {
            if (childDef.isPreConditionValid(document, item.getAbsoluteDataPath()))
            {
              item.addChild(MBComponentFactory.getComponentFromDefinition(childDef, document, item));
            }
          }
        }
        if (definition.getSuppressRowComponent())
        {
          // Prune the rows and ourselves
          for (MBForEachItem row : _rows)
          {
            for (MBComponent child : row.getChildren())
            {
              child.translatePath();
              getParent().addChild(child);
            }
          }
          _rows.clear();
          // Now mark ourself for destruction so we will not be added to the child array of our parent.
          setMarkedForDestruction(true);
        }
      }
    }

  }

  public ArrayList<MBForEachItem> getRows()
  {
    return _rows;
  }

  public void setRows(ArrayList<MBForEachItem> rows)
  {
    _rows = rows;
  }

  public String getValue()
  {
    return _value;
  }

  public void setValue(String value)
  {
    _value = value;
  }

  private void addItem(MBForEachItem row)
  {
    row.setParent(this);
    row.setIndex(_rows.size());
    _rows.add(row);
  }

  @Override
  public ViewGroup buildViewWithMaxBounds(MBViewState viewState)
  {
    return MBViewBuilderFactory.getInstance().getForEachViewBuilder().buildForEachView(this, viewState);
  }

  //This method is overridden because we (may) have to the children of the rows too
  @SuppressWarnings("unchecked")
  @Override
  public <T extends MBComponent> ArrayList<T> getDescendantsOfKind(Class<T> clazz)
  {

    ArrayList<T> result = super.getDescendantsOfKind(clazz);
    for (MBForEachItem child : _rows)
    {
      if (clazz.isInstance(child)) result.add((T) child);
      result.addAll(child.getDescendantsOfKind(clazz));
    }
    return result;
  }

  //This method is overridden because we (may) have to the children of the rows too
  @Override
  public ArrayList<MBComponent> getChildrenOfKind(Class<?> clazz)
  {
    ArrayList<MBComponent> result = super.getChildrenOfKind(clazz);
    for (MBComponent child : _rows)
    {
      if (clazz.isInstance(child)) result.add(child);
    }
    return result;
  }

  @Override
  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    StringUtilities.appendIndentString(appendToMe, level).append("<MBForEach ").append(this.attributeAsXml("value", _value)).append(">\n");

    MBForEachDefinition def = (MBForEachDefinition) getDefinition();
    for (MBVariableDefinition var : def.getVariables().values())
      var.asXmlWithLevel(appendToMe, level + 2);
    for (MBForEachItem child : _rows)
      child.asXmlWithLevel(appendToMe, level + 2);

    childrenAsXmlWithLevel(appendToMe, level + 2);
    return StringUtilities.appendIndentString(appendToMe, level).append("</MBForEach>\n");
  }

  @Override
  public String toString()
  {
    return asXmlWithLevel(new StringBuffer(), 0).toString();
  }

}
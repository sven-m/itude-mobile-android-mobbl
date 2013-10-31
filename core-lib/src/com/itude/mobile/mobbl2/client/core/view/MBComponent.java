/*
 * (C) Copyright ItudeMobile.
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
package com.itude.mobile.mobbl2.client.core.view;

import java.util.ArrayList;
import java.util.List;

import android.view.View;

import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;
import com.itude.mobile.mobbl2.client.core.controller.MBOutcome;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.MBCustomAttributeContainer;

public class MBComponent extends MBCustomAttributeContainer

{
  private MBDefinition         _definition;
  private MBComponentContainer _parent;
  private String               _style;
  private boolean              _markedForDestruction;
  private MBDocument           _document;
  private View                 _view;

  public MBComponent(MBDefinition definition, MBDocument document, MBComponentContainer parent)
  {
    super(definition);
    _definition = definition;
    _document = document;
    _parent = parent;

    // Not all definitions have a style attribute; if they do set it
    if (MBStylableDefinition.class.isAssignableFrom(definition.getClass()))
    {
      String style = ((MBStylableDefinition) definition).getStyle();
      setStyle(substituteExpressions(style));
    }
  }

  public MBDefinition getDefinition()
  {
    return _definition;
  }

  public void setDefinition(MBDefinition definition)
  {
    _definition = definition;
  }

  public void setDocument(MBDocument document)
  {
    _document = document;
  }

  public MBComponentContainer getParent()
  {
    return _parent;
  }

  public void setParent(MBComponentContainer parent)
  {
    _parent = parent;
  }

  public String getStyle()
  {
    return _style;
  }

  public void setStyle(String style)
  {
    _style = style;
  }

  public boolean isMarkedForDestruction()
  {
    return _markedForDestruction;
  }

  public void setMarkedForDestruction(boolean markedForDestruction)
  {
    _markedForDestruction = markedForDestruction;
  }

  public View buildView()
  {
    return null;
  }

  public void handleOutcome(MBOutcome outcome)
  {
    _parent.handleOutcome(outcome);
  }

  public void handleOutcome(String outcomeName, String path)
  {
    _parent.handleOutcome(outcomeName, path);
  }

  public String substituteExpressions(String expression)
  {
    if (expression == null)
    {
      return expression;
    }

    if (expression.equalsIgnoreCase("YES"))
    {
      return Constants.C_TRUE;
    }

    if (expression.equalsIgnoreCase("NO"))
    {
      return Constants.C_FALSE;
    }

    if (expression.indexOf('{') < 0)
    {
      return expression;
    }

    StringBuilder result = new StringBuilder(); // we really want to end this statement

    int positionOf = 0;
    int previousPositionOf = 0;

    boolean evalToNil = false;
    while ((positionOf = expression.indexOf("${", previousPositionOf)) > -1)
    {
      // get everything before the ${
      result.append(expression.substring(previousPositionOf, positionOf));
      // for the next while-iteration, make sure we skip the ${ we are now processing
      previousPositionOf = positionOf + 2;

      int endingAccolade = expression.indexOf('}', positionOf + 2);
      if (endingAccolade != -1)
      {
        previousPositionOf = endingAccolade + 1;
        String theExpressionFound = expression.substring(positionOf + 2, endingAccolade);

        String resultOfTheExpresion = evaluateExpression(theExpressionFound);
        if (resultOfTheExpresion != null)
        {
          result.append(resultOfTheExpresion);
        }
        else
        {
          evalToNil = true;
        }
      }
    }

    // get the remainder of the original expression
    result.append(expression.substring(previousPositionOf));

    if (result.length() == 0 && evalToNil) return null;
    else return result.toString();
  }

  public String getComponentDataPath()
  {
    return "";
  }

  public String getAbsoluteDataPath()
  {
    String componentPath = getComponentDataPath();

    // If the path is not set (a field without a path specified for instance) return nil; since it then also does not have an absolute path:
    if (componentPath == null)
    {
      return null;
    }

    // Absolute path set for the component? (possibly using a doc:path expression) Than do not prefix with the parent path and return it:
    if (componentPath.startsWith("/") || componentPath.contains(":"))
    {
      return componentPath;
    }

    String pathToMe = null;
    if (_parent != null) pathToMe = _parent.getAbsoluteDataPath();
    if (pathToMe != null && !pathToMe.endsWith("/"))
    {
      pathToMe = pathToMe + "/";
    }
    if (pathToMe == null)
    {
      pathToMe = "";
    }

    if (componentPath != null)
    {
      pathToMe = pathToMe + componentPath;
    }

    return pathToMe;
  }

  public MBDocument getDocument()
  {
    return _document;
  }

  public MBPage getPage()
  {
    if (_parent != null)
    {
      return _parent.getPage();
    }
    else
    {
      return null;
    }
  }

  public String evaluateExpression(String variableName)
  {
    if (getParent() != null)
    {
      return getParent().evaluateExpression(variableName);
    }

    Object value = getDocument().getValueForPath(variableName);

    if (value != null) return value.toString();

    return null;
  }

  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    return appendToMe;
  }

  public String attributeAsXml(String name, Object attrValue)
  {
    return attrValue == null ? "" : name + "='" + attrValue + "'";
  }

  public String getDescription()
  {
    StringBuffer rt = new StringBuffer();
    return asXmlWithLevel(rt, 0).toString();
  }

  public void translatePath()
  {
  }

  public <T extends MBComponent> List<T> getDescendantsOfKind(Class<T> clazz)
  {
    // This method is overridden by the various subclasses; if this could be an abstract method it would be
    return new ArrayList<T>();
  }

  public <T extends MBComponent> List<T> getChildrenOfKind(Class<T> clazz)
  {
    // This method is overridden by the various subclasses; if this could be an abstract method it would be
    return new ArrayList<T>();
  }

  public <T extends MBComponent> List<T> getChildrenOfKindWithType(Class<T> clazz, String... types)
  {
    // This method is overridden by the various subclasses; if this could be an abstract method it would be
    return new ArrayList<T>();
  }

  public <T extends MBComponent> T getFirstDescendantOfKind(Class<T> clazz)
  {
    List<T> result = getDescendantsOfKind(clazz);

    if (result.isEmpty())
    {
      return null;
    }

    return result.get(0);
  }

  @SuppressWarnings("unchecked")
  public <T extends MBComponent> T getFirstDescendantOfKindWithName(Class<T> clazz, String name)
  {
    List<T> result = getDescendantsOfKind(clazz);

    for (MBComponent component : result)
    {
      if (name.equals(component.getName()))
      {
        return (T) component;
      }
    }

    return null;
  }

  public <T extends MBComponent> T getFirstChildOfKind(Class<T> clazz)
  {
    List<T> result = getChildrenOfKind(clazz);

    if (result.isEmpty())
    {
      return null;
    }

    return result.get(0);
  }

  public <T extends MBComponent> T getFirstChildOfKindWithName(Class<T> clazz, String name)
  {
    List<T> result = getChildrenOfKind(clazz);

    for (T component : result)
    {
      if (name.equals(component.getName()))
      {
        return component;
      }
    }

    return null;
  }

  public MBPanel getFirstChildOfPanelWithType(String type)
  {
    List<MBPanel> result = getChildrenOfKind(MBPanel.class);

    for (MBPanel panel : result)
    {
      if (panel.getType().equals(type))
      {
        return panel;
      }
    }

    return null;
  }

  @SuppressWarnings("unchecked")
  public <T extends MBForEachItem> T getRow(int index)
  {
    List<MBForEachItem> result = getDescendantsOfKind(MBForEachItem.class);

    for (MBForEachItem row : result)
    {

      if (row.getIndex() == index)
      {
        return (T) row;
      }
    }
    return null;
  }

  public MBComponentContainer getFirstParentOfKind(Class<?> clazz)
  {
    MBComponentContainer parent = getParent();
    if (parent == null || clazz.isInstance(parent)) return parent;
    else return parent.getFirstParentOfKind(clazz);
  }

  public MBPanel getFirstParentPanelWithType(String type)
  {
    MBPanel parent = (MBPanel) getFirstParentOfKind(MBPanel.class);
    if (parent == null || type.equals(parent.getType())) return parent;
    else return parent.getFirstParentPanelWithType(type);
  }

  // Listener logic is handled by the page; so delegate to parent until the page is reached:
  public void registerValueChangeListener(Object listener, String path)
  {
    getParent().registerValueChangeListener(listener, path);
  }

  public void unregisterValueChangeListener(Object listener)
  {
    getParent().unregisterValueChangeListener(listener);
  }

  public void unregisterValueChangeListener(Object listener, String path)
  {
    getParent().unregisterValueChangeListener(listener, path);
  }

  public boolean notifyValueWillChange(String value, String currentValue, String path)
  {
    return getParent().notifyValueWillChange(value, currentValue, path);
  }

  public void notifyValueChanged(String value, String currentValue, String path)
  {
    getParent().notifyValueChanged(value, currentValue, path);
  }

  public String getName()
  {
    return getDefinition().getName();
  }

  public void attachView(View view)
  {
    _view = view;
  }

  public View getAttachedView()
  {
    return _view;
  }

  public String getType()
  {
    // return something marginally useful; should be overridden by subclasses
    return this.getClass().getSimpleName();
  }

  @Override
  protected String parseCustomValue(String value)
  {
    return substituteExpressions(value);
  }

}

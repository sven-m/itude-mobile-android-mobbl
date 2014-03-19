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
package com.itude.mobile.mobbl.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.android.util.TwinResult;
import com.itude.mobile.mobbl.core.configuration.MBDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBDocumentDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBElementDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.exceptions.MBInvalidPathException;
import com.itude.mobile.mobbl.core.model.exceptions.MBDocumentNotFoundException;
import com.itude.mobile.mobbl.core.model.exceptions.MBIndexOutOfBoundsException;
import com.itude.mobile.mobbl.core.model.exceptions.MBNoIndexSpecifiedException;
import com.itude.mobile.mobbl.core.services.MBDataManagerService;
import com.itude.mobile.mobbl.core.services.MBScriptService;
import com.itude.mobile.mobbl.core.util.Constants;
import com.itude.mobile.mobbl.core.util.MBDynamicAttributeComparator;
import com.itude.mobile.mobbl.core.util.MBParseUtil;
import com.itude.mobile.mobbl.core.util.MBPathUtil;

/**
 * A node in a tree of MBElement instances.
 * <br/>
 * In MOBBL, data is represented in MBDocuments and MBElements. An MBDocument is a lightweight xml-like structure, optimised for mobile use.
 * 
 * MBDocuments and MBElements are both MBElementContainers. Each ElementContainer has an associated MBElementDefinition which defines its structure. 
 * The most frequently used methods are valueForPath:, setValue: forPath: and elementsWithName:
 */
public class MBElementContainer implements Parcelable
{
  private static final List<MBElement> EMPTY_LIST = Collections.emptyList();
  private Map<String, List<MBElement>> _elements;                           // Dictionaryoflistsofelements
  private MBElementContainer           _parent;

  private boolean                      _looping   = false;

  public MBElementContainer()
  {
    _elements = new HashMap<String, List<MBElement>>();
  }

  public void copyChildrenInto(MBElementContainer other)
  {
    for (String elementName : _elements.keySet())
    {
      for (MBElement src : _elements.get(elementName))
      {
        MBElement copy = src.clone();
        other.addElement(copy);
      }
    }
  }

  public void addAllPathsTo(Set<String> set, String currentPath)
  {
    _looping = true;
    for (Map.Entry<String, List<MBElement>> entry : _elements.entrySet())
    {
      int idx = 0;
      String pathPrefix = currentPath + "/" + entry.getKey() + "[";
      for (MBElement element : entry.getValue())
      {
        String path = pathPrefix + (idx++) + "]";
        element.addAllPathsTo(set, path);
      }
    }
    _looping = false;
  }

  public int evaluateIndexExpression(String combinedExpression, String elementName)
  {
    List<String> matchAttributes = new ArrayList<String>();
    List<String> matchValues = new ArrayList<String>();

    String[] expressions = combinedExpression.split(" and ");

    for (String expression : expressions)
    {

      int eqPos = expression.indexOf("=");
      String attrName = StringUtil.stripCharacters(expression.substring(0, eqPos), " ");
      String valueExpression = expression.substring(eqPos + 1);

      attrName = substituteExpressions(attrName, attrName, null);

      valueExpression = StringUtil.stripCharacters(valueExpression, "'\"");

      String value = substituteExpressions(valueExpression, valueExpression, null);

      matchAttributes.add(attrName);
      matchValues.add(value);
    }

    List<MBElement> elements = getElementsWithName(elementName);
    for (int i = 0; i < elements.size(); i++)
    {
      boolean match = true;
      for (int j = 0; match && j < matchAttributes.size(); j++)
      {
        String attrName = matchAttributes.get(j);
        String value = matchValues.get(j);
        match &= elements.get(i).getValueForAttribute(attrName).equals(value);
      }
      if (match)
      {
        return i;
      }
    }

    // Return an index that exceeds the size of the elements array; this will be handled by if([rootList count] <= idx) below
    // i.e. if nillIfMissing is TRUE then a not matching expression will also return nil because of this:

    return elements.size();
  }

  public MBElementContainer getParent()
  {
    return _parent;
  }

  public void setParent(MBElementContainer parent)
  {
    _parent = parent;
  }

  public Object init()
  {
    return null;
  }

  /***
   * @deprecated Please use {@link createElement(String)}
   *  createElement(String name)
   * @param name
   * @return
   */
  @Deprecated
  public MBElement createElementWithName(String name)
  {
    return createElement(name);
  }

  public MBElement createElement(String name)
  {
    TwinResult<MBElementContainer, MBElement> result = doCreateElement(name);

    MBElement element = result._secondResult;
    result._mainResult.addElement(element);

    return element;
  }

  /***
   * Create an element on the position specified with the index. After invoking this method, please consider clearing the
   * document's path cache by invoking {@link MBDocument.clearPathCache()}.
   * 
   * @param name
   * @param index
   * @return
   */
  public MBElement createElement(String name, int index)
  {
    TwinResult<MBElementContainer, MBElement> result = doCreateElement(name);

    MBElement element = result._secondResult;
    result._mainResult.addElement(element, index);

    return element;
  }

  protected TwinResult<MBElementContainer, MBElement> doCreateElement(String name)
  {
    Stack<String> pathComponents = new Stack<String>();
    pathComponents.addAll(MBPathUtil.splitPath(name));

    if (pathComponents.size() > 1)
    {
      String elementName = pathComponents.pop();

      MBElement target = (MBElement) getValueForPathComponents(pathComponents, name, false, null);

      return target.doCreateElement(elementName);
    }
    else
    {
      MBElementDefinition childDef = null;
      if (getDefinition() instanceof MBElementDefinition)
      {
        childDef = ((MBElementDefinition) getDefinition()).getChildWithName(name);
      }
      else if (getDefinition() instanceof MBDocumentDefinition)
      {
        childDef = ((MBDocumentDefinition) getDefinition()).getChildWithName(name);
      }
      MBElement element = childDef.createElement();

      return new TwinResult<MBElementContainer, MBElement>(this, element);
    }
  }

  public void deleteElementWithName(String name, int index)
  {
    List<MBElement> elementContainer = getElementsWithName(name);

    if (index < 0 || index >= elementContainer.size())
    {
      String message = "Invalid index (" + index + ") for element with name " + name + " (count=" + elementContainer.size() + ")";
      throw new MBInvalidPathException(message);
    }

    elementContainer.remove(index);
  }

  public void deleteAllChildElements()
  {
    if (_looping) throw new ConcurrentModificationException();
    _elements.clear();
  }

  public void addElement(MBElement element)
  {
    if (_looping) throw new ConcurrentModificationException();
    List<MBElement> elemContainer = prepareAddElement(element);

    elemContainer.add(element);
  }

  private List<MBElement> prepareAddElement(MBElement element)
  {
    if (_looping) throw new ConcurrentModificationException();
    String name = element.getDefinition().getName();
    element.setParent(this);

    List<MBElement> elemContainer = getElementsWithName(name);
    if (elemContainer == EMPTY_LIST)
    {
      elemContainer = new ArrayList<MBElement>();
      _elements.put(name, elemContainer);
    }
    return elemContainer;
  }

  /***
   * Add an element on the position specified with the index. After invoking this method, please consider clearing the
   * document's path cache by invoking {@link MBDocument.clearPathCache()}.
   * 
   * @param element
   * @param index
   */
  public void addElement(MBElement element, int index)
  {
    List<MBElement> elemContainer = prepareAddElement(element);

    if (index < 0 || index > elemContainer.size())
    {
      String message = "Invalid index (" + index + ") for element with name " + element.getDefinition().getName() + " (count="
                       + elemContainer.size() + ")";
      throw new MBInvalidPathException(message);
    }

    elemContainer.add(index, element);
  }

  public Map<String, List<MBElement>> getElements()
  {
    return _elements;
  }

  public void setElements(Map<String, List<MBElement>> elements)
  {
    _elements = elements;
  }

  public boolean getBooleanForPath(String path)
  {
    return MBParseUtil.booleanValue((String) getValueForPath(path));
  }

  public <T> T getValueForPath(String path)
  {
    return (T) getValueForPath(path, null);
  }

  public <T> T getValueForPath(String path, List<String> translatedPathComponents)
  {
    if (path == null)
    {
      return null;
    }

    List<String> pathComponents = MBPathUtil.splitPath(path);

    // If there is a ':' in the name of the first component; we might need a different document than 'self'
    if (pathComponents.size() > 0)
    {
      int range = pathComponents.get(0).indexOf(":");

      if (range > -1)
      {
        String documentName = pathComponents.get(0).substring(0, range);
        String rootElementName = pathComponents.get(0).substring(range + 1);

        if (!documentName.equals(this.getDocumentName()))
        {
          if (translatedPathComponents == null)
          {
            translatedPathComponents = new ArrayList<String>();
          }

          // Different document! Dispatch the valueForPath
          translatedPathComponents.add(documentName + ":");
          MBDocument doc = getDocumentFromSharedContext(documentName);
          if (rootElementName.length() > 0)
          {
            pathComponents.set(0, rootElementName);
          }
          else
          {
            pathComponents.remove(0);
          }

          return (T) doc.getValueForPathComponents(pathComponents, path, true, translatedPathComponents);
        }
        else
        {
          pathComponents.set(0, rootElementName);
        }
      }
    }

    return (T) getValueForPathComponents(pathComponents, path, true, translatedPathComponents);
  }

  public void setValue(boolean value, String path)
  {
    setValue(value ? Constants.C_TRUE : Constants.C_FALSE, path);
  }

  public void setValue(String value, String path)
  {
    Stack<String> pathComponents = new Stack<String>();
    pathComponents.addAll(MBPathUtil.splitPath(path));

    String attributeName = new String(pathComponents.lastElement());
    if (attributeName.startsWith("@"))
    {
      pathComponents.pop();
      attributeName = attributeName.substring(1);

      MBElement element = (MBElement) getValueForPathComponents(pathComponents, path, false, null);
      element.setAttributeValue(value, attributeName);
    }
    else
    {
      String message = "Identitifer " + attributeName + " in Path " + path + " does not specify an attribute; cannot set value";
      throw new MBInvalidPathException(message);
    }

  }

  public List<MBElement> getElementsWithName(String name)
  {
    if (name.equals("*"))
    {
      List<MBElement> result = new ArrayList<MBElement>();
      for (List<MBElement> lst : _elements.values())
      {
        result.addAll(lst);
      }

      return result;
    }
    else
    {
      // not found, check if the name is a valid child at all
      if (!getDefinition().isValidChild(name))
      {
        String message = "Child element with name " + name + " not present";
        throw new MBInvalidPathException(message);
      }
      List<MBElement> result = _elements.get(name);
      if (result == null)
      {
        return EMPTY_LIST;
      }

      return result;
    }

  }

  public MBDefinition getDefinition()
  {
    return null;
  }

  @SuppressWarnings("unchecked")
  public <T> T getValueForPathComponents(List<String> pathComponents, String originalPath, boolean nillIfMissing,
                                         List<String> translatedPathComponents)
  {
    if (pathComponents.isEmpty()) return (T) this;

    String[] rootNameParts = splitPathOnBrackets(pathComponents.get(0));
    // hello
    String childElementName = rootNameParts[0];

    int idx = determineIndex(rootNameParts, childElementName);
    pathComponents.remove(0);
    List<MBElement> allElementsWithSameNameAsChild = getElementsWithName(childElementName);
    if (idx == -99)
    {
      // this was not an indexed path (just hello, not hello[1234])
      if (pathComponents.isEmpty())
      {
        return (T) allElementsWithSameNameAsChild;
      }
      String message = "No index specified for " + childElementName + " in path" + originalPath;
      throw new MBNoIndexSpecifiedException(message);
    }
    else if (idx < 0)
    {
      String message = "Illegal index " + idx + " for " + childElementName + " in path " + originalPath;
      throw new MBIndexOutOfBoundsException(message);
    }

    if (allElementsWithSameNameAsChild.size() <= idx)
    {
      if (nillIfMissing)
      {
        return null;
      }
      String message = "Index " + idx + " exceeds " + (allElementsWithSameNameAsChild.size() - 1) + " for " + childElementName
                       + " in path " + originalPath;
      throw new MBIndexOutOfBoundsException(message);
    }

    MBElement root = allElementsWithSameNameAsChild.get(idx);
    if (translatedPathComponents != null) translatedPathComponents.add(root.getName() + "[" + idx + "]");
    return (T) root.getValueForPathComponents(pathComponents, originalPath, nillIfMissing, translatedPathComponents);
  }

  private String[] splitPathOnBrackets(String fullPath)
  {
    String[] rootNameParts;
    // hello[1] is split into hello and 1 (or just hello if the path is "hello")
    int indexOpenBracket = fullPath.indexOf('[');
    boolean isIndexedComponent = indexOpenBracket > -1;
    if (isIndexedComponent)
    {
      rootNameParts = new String[2];
      rootNameParts[0] = fullPath.substring(0, indexOpenBracket); // hello
      int indexCloseBracket = fullPath.indexOf(']', indexOpenBracket);
      if (indexCloseBracket < 0) rootNameParts[1] = fullPath.substring(indexOpenBracket + 1);
      else
      {
        rootNameParts[1] = fullPath.substring(indexOpenBracket + 1, indexCloseBracket);
      }

    }
    else
    {
      rootNameParts = new String[]{fullPath};
    }
    return rootNameParts;
  }

  // hello[0] returns 0, hello[123] returns 123, -99 when it is not an indexed path (just hello)
  private int determineIndex(String[] rootNameParts, String childElementName)
  {
    if (rootNameParts.length > 1)
    {
      // so it was an indexed path (hello[1])
      String idxStr = rootNameParts[1];
      if (idxStr.indexOf('=') > -1)
      {
        return evaluateIndexExpression(idxStr, childElementName);
      }
      else
      {
        return Integer.parseInt(idxStr);
      }
    }
    return -99;
  }

  public String getName()
  {
    return getDefinition().getName();
  }

  public Map<String, MBDocument> getSharedContext()
  {
    return getParent().getSharedContext();
  }

  public void setSharedContext(Map<String, MBDocument> sharedContext)
  {
    getParent().setSharedContext(sharedContext);
  }

  public MBDocument getDocumentFromSharedContext(String documentName)
  {
    MBDocument result = getSharedContext().get(documentName);
    if (result == null)
    {
      result = MBDataManagerService.getInstance().loadDocument(documentName);
      if (result == null)
      {
        String message = "Could not load document with name " + documentName;
        throw new MBDocumentNotFoundException(message);
      }
      registerDocumentWithSharedContext(result);
    }

    return result;
  }

  public void registerDocumentWithSharedContext(MBDocument document)
  {
    document.setSharedContext(getSharedContext());
    getSharedContext().put(document.getName(), document);
  }

  public MBDocument getDocument()
  {
    return getParent().getDocument();
  }

  public String getDocumentName()
  {
    return getParent().getDocumentName();
  }

  public String substituteExpressions(String expression, String nilMarker, String currentPath)
  {
    String variableOpenTag = "${";
    String variableCloseTag = "}";

    if (expression == null)
    {
      return null;
    }

    if (!expression.contains(variableOpenTag))
    {
      return expression;
    }

    Stack<StringBuilder> stack = new Stack<StringBuilder>();
    stack.push(new StringBuilder());

    for (int i = 0; i < expression.length(); ++i)
    {
      if (expression.charAt(i) == '$' && expression.charAt(i + 1) == '{')
      {
        // open tag
        i++;
        stack.push(new StringBuilder());
      }
      else if (expression.charAt(i) == '}')
      {
        StringBuilder top = stack.pop();
        if (top.charAt(0) == '.' && currentPath != null && currentPath.length() > 0)
        {
          top.insert(0, '/').insert(0, currentPath);
        }

        String evaluated = getValueForPath(top.toString());
        if (evaluated == null) evaluated = nilMarker;

        stack.peek().append(evaluated);

      }
      else
      {
        stack.peek().append(expression.charAt(i));
      }

    }

    return stack.peek().toString();
  }

  public String evaluateExpression(String expression)
  {
    return evaluateExpression(expression, null);
  }

  public String evaluateExpression(String expression, String currentPath)
  {
    String translated = substituteExpressions(expression, "null", currentPath);
    if (StringUtil.isNotBlank(translated))
    {
      translated = translated.replace("\n", "").replace("\r", "");
    }
    return MBScriptService.getInstance().evaluate(translated);
  }

  public String getUniqueId()
  {
    String uid = "";
    for (String elementName : _elements.keySet())
    {
      int idx = 0;
      for (MBElement element : _elements.get(elementName))
      {
        uid += "[" + idx + "_" + element.getUniqueId();
      }
    }

    return uid;
  }

  //Sorts on the given attribute(s) Multiple attributes must be separated by ,
  //Descending sort on an attribute can be done by prefixing the attribute with a -
  public void sortElements(String elementName, String attributeNames)
  {
    List<MBElement> elements = getElementsWithName(elementName);
    if (elements.isEmpty())
    {
      return;
    }

    Vector<Object[]> trace = new Vector<Object[]>();

    for (String attrSpec : attributeNames.split(","))
    {
      attrSpec = attrSpec.trim();
      boolean ascending = attrSpec.startsWith("+") || !attrSpec.startsWith("-");
      if (attrSpec.startsWith("+") || attrSpec.startsWith("-"))
      {
        attrSpec = attrSpec.substring(1);
      }

      trace.add(new Object[]{attrSpec, ascending});
    }

    Collections.sort(elements, new MBDynamicAttributeComparator(trace));

    getDocument().clearPathCache();
  }

  // Parcelable stuff

  protected MBElementContainer(Parcel in)
  {
    this();

    Bundle elementsBundle = in.readBundle(MBElement.class.getClassLoader());

    for (String key : elementsBundle.keySet())
    {
      Parcelable[] parcelableElements = elementsBundle.getParcelableArray(key);
      if (parcelableElements != null)
      {
        List<MBElement> elements = new ArrayList<MBElement>(parcelableElements.length);
        for (Parcelable parcelableElement : parcelableElements)
        {
          elements.add((MBElement) parcelableElement);
        }
        _elements.put(key, elements);
      }
    }

    _parent = in.readParcelable(MBElementContainer.class.getClassLoader());
  }

  @Override
  public int describeContents()
  {
    return Constants.C_PARCELABLE_TYPE_ELEMENT_CONTAINER;
  }

  @Override
  public void writeToParcel(Parcel out, int flags)
  {
    Bundle elementsBundle = new Bundle();

    for (String key : _elements.keySet())
    {
      List<MBElement> list = _elements.get(key);
      MBElement[] elements = list.toArray(new MBElement[list.size()]);
      elementsBundle.putParcelableArray(key, elements);
    }

    out.writeBundle(elementsBundle);
    out.writeParcelable(_parent, flags);
  }

  public static final Parcelable.Creator<MBElementContainer> CREATOR = new Creator<MBElementContainer>()
                                                                     {
                                                                       @Override
                                                                       public MBElementContainer[] newArray(int size)
                                                                       {
                                                                         return new MBElementContainer[size];
                                                                       }

                                                                       @Override
                                                                       public MBElementContainer createFromParcel(Parcel in)
                                                                       {
                                                                         return new MBElementContainer(in);
                                                                       }
                                                                     };

  // End of parcelable stuff

}

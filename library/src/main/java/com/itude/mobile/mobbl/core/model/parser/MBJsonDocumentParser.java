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
package com.itude.mobile.mobbl.core.model.parser;

import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONObject;

import com.itude.mobile.mobbl.core.configuration.MBDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBAttributeDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBDocumentDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBElementDefinition;
import com.itude.mobile.mobbl.core.model.MBDocument;
import com.itude.mobile.mobbl.core.model.MBElement;
import com.itude.mobile.mobbl.core.model.MBElementContainer;
import com.itude.mobile.mobbl.core.model.exceptions.MBParseErrorException;
import com.itude.mobile.mobbl.core.util.MBConstants;

/**
 * Parser for JSON type documents
 *
 */
public class MBJsonDocumentParser implements MBDocumentParser
{

  @Override
  public MBDocument getDocumentWithData(byte[] data, MBDocumentDefinition definition)
  {
    try
    {
      String string = new String(data, "UTF-8");
      MBDocument result = MBJsonDocumentParser.getDocumentWithString(string, definition);

      return result;
    }
    catch (Exception e)
    {
      String message = "Error parsing document " + definition.getName() + " : " + e.getMessage();
      throw new MBParseErrorException(message);
    }

  }

  public static MBDocument getDocumentWithString(String string, MBDocumentDefinition definition)
  {
    return parseJsonString(string, definition);

  }

  private static MBDocument parseJsonString(String jsonString, MBDocumentDefinition definition)
  {

    try
    {
      JSONObject jsonDoc = new JSONObject(jsonString);
      MBDocument document = new MBDocument(definition);

      JSONArray names = jsonDoc.names();
      for (int i = 0; i < names.length(); i++)
      {
        Object child = jsonDoc.get(names.getString(i));

        // Only parse it if the document exists in the jsonDoc
        if (child instanceof JSONObject)
        {
          // ignore the first Element, use its child as the root
          JSONObject jsonRoot = (JSONObject) child;

          // kick off recursive construction, starting with root element
          parseJsonValue(jsonRoot, definition, document);
        }
      }

      return document;
    }
    catch (Exception e)
    {
      String message = "Error parsing document " + definition.getName() + " : " + e.getMessage();
      throw new MBParseErrorException(message);
    }

  }

  private static void parseJsonValue(Object p_jsonValue, MBDefinition definition, MBElementContainer element)
  {

    parseJsonValue_object(p_jsonValue, definition, element);
    parseJsonValue_attribs(p_jsonValue, definition, element);

  }

  private static void parseJsonValue_attribs(Object p_jsonValue, MBDefinition definition, MBElementContainer element)
  {
    // get attributes
    if (definition instanceof MBElementDefinition && element instanceof MBElement)
    {

      try
      {
        for (MBAttributeDefinition attributeDefinition : ((MBElementDefinition) definition).getAttributes())
        {

          Object attributeValue = null;
          JSONObject jsonValue = (JSONObject) p_jsonValue;

          if (jsonValue.has(attributeDefinition.getName()))
          {
            attributeValue = jsonValue.get(attributeDefinition.getName());

            if (attributeValue instanceof Integer)
            {
              attributeValue = Integer.toString((Integer) attributeValue);
            }
            if (attributeValue instanceof Double)
            {
              attributeValue = Double.toString((Double) attributeValue);
            }
            if (attributeValue instanceof Long)
            {
              attributeValue = Long.toString((Long) attributeValue);
            }
            if (attributeValue instanceof Boolean)
            {
              if ((Boolean) attributeValue)
              {
                attributeValue = MBConstants.C_TRUE;
              }
              else
              {
                attributeValue = MBConstants.C_FALSE;
              }

            }
          }
          else
          {
            attributeValue = attributeDefinition.getDefaultValue();
          }

          if (attributeValue instanceof String)
          {
            ((MBElement) element).setAttributeValue((String) attributeValue, attributeDefinition.getName());
          }

        }
      }
      catch (Exception e)
      {
        String message = "Error parsing document " + definition.getName() + " : " + e.getMessage();
        throw new MBParseErrorException(message);
      }

    }
  }

  private static void parseJsonValue_object(Object p_jsonValue, MBDefinition definition, MBElementContainer element)
  {
    if (p_jsonValue instanceof JSONObject)
    {
      JSONObject jsonValue = (JSONObject) p_jsonValue;
      Collection<MBElementDefinition> elements = definition.getChildElements();

      for (MBElementDefinition childDefinition : elements)
      {

        try
        {
          if (!jsonValue.has(childDefinition.getName()))
          {
            if (childDefinition.getMinOccurs() > 0) element.addElement(childDefinition.createElement());
          }
          else
          {

            Object jsonChild = jsonValue.get(childDefinition.getName());

            if (!(jsonChild instanceof JSONObject) && !(jsonChild instanceof JSONArray))
            {
              MBElement childElement = childDefinition.createElement();

              if (jsonChild instanceof String)
              {
                childElement.setBodyText((String) jsonChild);
              }
              if (jsonChild instanceof Integer)
              {
                childElement.setBodyText(Integer.toString((Integer) jsonChild));
              }
              if (jsonChild instanceof Double)
              {
                childElement.setBodyText(Double.toString((Double) jsonChild));
              }
              if (jsonChild instanceof Long)
              {
                childElement.setBodyText(Long.toString((Long) jsonChild));
              }
              if (jsonChild instanceof Boolean)
              {
                if ((Boolean) jsonChild)
                {
                  childElement.setBodyText(MBConstants.C_TRUE);
                }
                else
                {
                  childElement.setBodyText(MBConstants.C_FALSE);
                }
              }

              element.addElement(childElement);
            }

            if (jsonChild instanceof JSONObject)
            {
              MBElement childElement = childDefinition.createElement();
              element.addElement(childElement);
              parseJsonValue(jsonChild, childDefinition, childElement);
            }

            if (jsonChild instanceof JSONArray)
            {
              JSONArray arr = (JSONArray) jsonChild;
              for (int i = 0; i < arr.length(); i++)
              {
                MBElement childElement = childDefinition.createElement();
                element.addElement(childElement);
                parseJsonValue(arr.get(i), childDefinition, childElement);
              }
            }
          }
        }
        catch (Exception e)
        {
          String message = "Error parsing value : " + childDefinition.getName() + " of document " + definition.getName() + " : "
                           + e.getMessage();
          throw new MBParseErrorException(message, e);
        }

      }

    }
  }
}

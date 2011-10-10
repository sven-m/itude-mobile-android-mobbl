package com.itude.mobile.mobbl2.client.core.configuration;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.itude.mobile.mobbl2.client.core.configuration.exceptions.MBUnknownElementException;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBActionDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBAttributeDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDialogDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDocumentDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDomainDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDomainValidatorDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBElementDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBOutcomeDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBPageDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBToolDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.exceptions.MBFileNotFoundException;
import com.itude.mobile.mobbl2.client.core.configuration.resources.MBItemDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.resources.MBResourceDefinition;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.DataUtil;
import com.itude.mobile.mobbl2.client.core.util.MBBundleDefinition;

public abstract class MBConfigurationParser extends DefaultHandler
{
  private Stack<MBDefinition>    _stack;
  private StringBuffer           _characters;
  private String                 _documentName;
  private MBIncludableDefinition _rootConfig;

  public String getDocumentName()
  {
    return _documentName;
  }

  public void setDocumentName(String documentName)
  {
    _documentName = documentName;
  }

  public MBDefinition parseData(byte[] data, String documentName)
  {

    _stack = new Stack<MBDefinition>();
    _characters = new StringBuffer();

    try
    {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser parser = factory.newSAXParser();

      parser.parse(new ByteArrayInputStream(data), this);

      return _stack.peek();
    }
    catch (Exception e)
    {
      Log.e(Constants.APPLICATION_NAME, "Unable to parse document " + documentName, e);
    }

    return null;
  }

  public void parser(Object parser, String string)
  {
  }

  public boolean processElement(String elementName, Map<String, String> attributeDict)
  {
    if (elementName.equals("Include"))
    {
      String name = attributeDict.get("name");

      MBConfigurationParser parser = null;
      try
      {
        // creates a new parser of the same type, e.g: an MBMvcConfigurationParser for the config
        parser = getClass().newInstance();
      }
      catch (InstantiationException e)
      {
        Log.e(Constants.APPLICATION_NAME, "Unable to create new parser for element Include", e);
      }
      catch (IllegalAccessException e)
      {
        Log.e(Constants.APPLICATION_NAME, "Unable to create new parser for element Include", e);
      }

      byte[] data = DataUtil.getInstance().readFromAssetOrFile(name);
      if (data == null)
      {
        throw new MBFileNotFoundException(name);
      }

      //      MBConfigurationDefinition include = (MBConfigurationDefinition) parser.parseData(data, name);
      MBIncludableDefinition definition = (MBIncludableDefinition) parser.parseData(data, name);

      // dynamically cast the definition
      _rootConfig.getClass().cast(definition);
      _rootConfig.addAll(definition);
    }
    else
    {
      return false;
    }

    return true;
  }

  //  public abstract MBConfigurationParser getNewInstance();

  public void didProcessElement(String elementName)
  {
  }

  public boolean isConcreteElement(String element)
  {
    return element.equals("Include");
  }

  public boolean isIgnoredElement(String element)
  {
    return false;
  }

  public boolean checkAttributesForElement(String elementName, Map<String, String> attributes, List<String> valids)
  {
    boolean result = true;

    Iterator<String> keys = attributes.keySet().iterator();
    String nextKey = "";
    while (keys.hasNext())
    {
      nextKey = keys.next();
      if (!valids.contains(nextKey))
      {
        Log.w(Constants.APPLICATION_NAME, "****WARNING Invalid attribute " + nextKey + " for element " + elementName + " in document "
                                          + _documentName);
        result = false;
      }
    }

    return result;
  }

  public void notifyProcessed(MBDefinition definition)
  {
    getStack().peek().addChildElement(definition);
    getStack().push(definition);
  }

  public void notifyProcessed(MBDocumentDefinition definition)
  {
    getStack().peek().addChildElement(definition);
    getStack().push(definition);
  }

  public void notifyProcessed(MBActionDefinition definition)
  {
    getStack().peek().addChildElement(definition);
    getStack().push(definition);
  }

  public void notifyProcessed(MBDomainValidatorDefinition definition)
  {
    getStack().peek().addChildElement(definition);
    getStack().push(definition);
  }

  public void notifyProcessed(MBElementDefinition definition)
  {
    getStack().peek().addChildElement(definition);
    getStack().push(definition);
  }

  public void notifyProcessed(MBAttributeDefinition definition)
  {
    getStack().peek().addChildElement(definition);
    getStack().push(definition);
  }

  public void notifyProcessed(MBDomainDefinition definition)
  {
    getStack().peek().addChildElement(definition);
    getStack().push(definition);
  }

  public void notifyProcessed(MBOutcomeDefinition definition)
  {
    getStack().peek().addChildElement(definition);
    getStack().push(definition);
  }

  public void notifyProcessed(MBPageDefinition definition)
  {
    getStack().peek().addChildElement(definition);
    getStack().push(definition);
  }

  public void notifyProcessed(MBDialogDefinition definition)
  {
    getStack().peek().addChildElement(definition);
    getStack().push(definition);
  }

  public void notifyProcessed(MBToolDefinition definition)
  {
    getStack().peek().addChildElement(definition);
    getStack().push(definition);
  }

  public void notifyProcessed(MBResourceDefinition definition)
  {
    getStack().peek().addChildElement(definition);
    getStack().push(definition);
  }

  public void notifyProcessed(MBBundleDefinition definition)
  {
    getStack().peek().addChildElement(definition);
    getStack().push(definition);
  }

  public void notifyProcessed(MBItemDefinition definition)
  {
    getStack().peek().addChildElement(definition);
    getStack().push(definition);
  }

  public Stack<MBDefinition> getStack()
  {
    if (_stack == null)
    {
      _stack = new Stack<MBDefinition>();
    }
    return _stack;
  }

  public String getCharacters()
  {
    return _characters.toString();
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException
  {
    _characters.append(ch, start, length);
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
  {
    _characters = new StringBuffer();

    if (isConcreteElement(localName))
    {
      HashMap<String, String> attributeValues = new HashMap<String, String>();
      for (int i = 0; i < attributes.getLength(); i++)
      {
        attributeValues.put(attributes.getLocalName(i), attributes.getValue(i));
      }

      if (!processElement(localName, attributeValues))
      {
        String message = "Document " + _documentName + ": Element " + localName + " not defined";
        throw new MBUnknownElementException(message);
      }

      _stack.peek().validateDefinition();
    }

  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException
  {

    if (!isIgnoredElement(localName))
    {
      if (!isConcreteElement(localName))
      {
        String message = "Document " + _documentName + ": Element " + localName + " not defined";
        throw new MBUnknownElementException(message);
      }

      didProcessElement(localName);
    }

  }

  protected MBIncludableDefinition getRootConfig()
  {
    return _rootConfig;
  }

  protected void setRootConfig(MBIncludableDefinition rootConfig)
  {
    _rootConfig = rootConfig;
  }

}

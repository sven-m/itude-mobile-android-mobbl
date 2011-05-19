package com.itude.mobile.mobbl2.client.core.model;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDocumentDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBElementDefinition;
import com.itude.mobile.mobbl2.client.core.model.exceptions.MBInvalidDocumentException;
import com.itude.mobile.mobbl2.client.core.model.exceptions.MBParseErrorException;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.StringUtilities;

public class MBXmlDocumentParser extends DefaultHandler
{
  private Stack<MBElementContainer> _stack;
  private Stack<String>             _pathStack;
  private MBDocumentDefinition      _definition;
  private StringBuffer              _characters;
  private String                    _rootElementName;
  private MBElementContainer        _rootElement;
  private boolean                   _copyRootAttributes;

  public static MBDocument getDocumentWithData(byte[] data, MBDocumentDefinition definition)
  {
    MBXmlDocumentParser documentParser = new MBXmlDocumentParser();
    MBDocument result = documentParser.parse(data, definition);

    return result;
  }

  public static void parseFragment(byte[] data, MBDocument document, String rootPath, boolean copyRootAttributes)
  {
    MBXmlDocumentParser documentParser = new MBXmlDocumentParser();
    documentParser.doParseFragment(data, document, rootPath, copyRootAttributes);
  }

  public MBDocument parse(byte[] data, MBDocumentDefinition definition)
  {
    if (data == null || data.length == 0)
    {
      return null;
    }

    MBDocument document = new MBDocument(definition);
    doParseFragment(data, document, null, false);

    return document;
  }

  private void doParseFragment(byte[] data, MBDocument document, String rootPath, boolean copyRootAttributes)
  {
    if (data != null)
    {
      try
      {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();

        _stack = new Stack<MBElementContainer>();
        _pathStack = new Stack<String>();
        _definition = (MBDocumentDefinition) document.getDefinition();
        _characters = new StringBuffer();
        _copyRootAttributes = copyRootAttributes;

        if (rootPath != null)
        {
          List<String> parts = StringUtilities.splitPath(rootPath);
          for (String part : parts)
          {
            _pathStack.add(StringUtilities.stripCharacters(part, "[]0123456789"));
          }

          _rootElementName = _pathStack.peek();
          _rootElement = (MBElementContainer) document.getValueForPath(rootPath);
        }
        else
        {
          _rootElement = document;
          _rootElementName = _definition.getName();
        }

        parser.parse(new ByteArrayInputStream(data), this);
      }
      catch (Exception e)
      {
        Log.d("MOBBL", new String(data));
        Log.e("MOBBL", "MBXmlDocumentParser.doParseFragment (for the data, see debug log above)", e);
      }
    }
  }

  public String getCurrentPath()
  {
    String path = "";
    for (String part : _pathStack)
    {
      path += "/" + part;
    }
    return path;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
  {

    MBElementContainer element;
    boolean copyAttributes = true;

    // check that we have the correct document type
    if (_stack.size() == 0)
    {

      if (!localName.equals(_rootElementName))
      {
        String message = "Error parsing document " + _definition.getName() + ": encountered an element with name " + localName
                         + " but expected " + _rootElementName;
        throw new MBInvalidDocumentException(message);
      }

      element = _rootElement;
      copyAttributes = _copyRootAttributes;
    }
    else
    {
      _pathStack.add(localName);
      MBElementDefinition elemDef = _definition.getElementWithPath(getCurrentPath());
      element = new MBElement(elemDef);

      _stack.peek().addElement((MBElement) element);
    }

    // Do not process elements that are not defined; so also check for nil definition
    if (copyAttributes && element.getDefinition() != null)
    {

      for (int i = 0; i < attributes.getLength(); i++)
      {
        ((MBElement) element).setAttributeValue(attributes.getValue(i), attributes.getLocalName(i), false);
      }
    }
    _stack.add(element);
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException
  {
    if (_stack.size() > 1)
    {
      String string = _characters.toString().trim();
      if (string.length() > 0)
      {
        if (_stack.peek() instanceof MBElement && ((MBElement) _stack.peek()).isValidAttribute("text()"))
        {
          ((MBElement) _stack.peek()).setAttributeValue(string, "text()");
        }
        else
        {
          Log.w(Constants.APPLICATION_NAME, "MBXmlDocumentParser.endElement: Text (" + string + ") specified in body of element "
                                            + localName + " is ignored because the element has no text() attribute defined");
        }
      }

      _stack.pop();
      _pathStack.pop();
    }

    _characters = new StringBuffer();
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException
  {
    _characters.append(ch, start, length);
  }

  @Override
  public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException
  {
    // Ignore tabs, newlines and spaces
  }

  @Override
  public void error(SAXParseException e) throws SAXException
  {

    String message = "Error parsing document " + _definition.getName() + " at line " + e.getLineNumber() + " column " + e.getColumnNumber()
                     + ": " + e.getMessage();

    throw new MBParseErrorException(message);
  }

}

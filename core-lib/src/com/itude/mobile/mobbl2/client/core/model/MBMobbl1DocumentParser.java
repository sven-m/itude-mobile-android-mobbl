package com.itude.mobile.mobbl2.client.core.model;

import java.io.ByteArrayInputStream;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDocumentDefinition;
import com.itude.mobile.mobbl2.client.core.util.Constants;

public class MBMobbl1DocumentParser extends DefaultHandler
{
  private Stack<MBDocument>    _stack;
  private MBDocumentDefinition _definition;
  private StringBuilder        _characters = new StringBuilder();

  public static MBDocument getDocumentWithData(byte[] data, MBDocumentDefinition definition)
  {
    MBMobbl1DocumentParser documentParser = new MBMobbl1DocumentParser();
    MBDocument result = documentParser.parse(data, definition);

    return result;
  }

  public MBDocument parse(byte[] data, MBDocumentDefinition definition)
  {

    try
    {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser parser = factory.newSAXParser();

      _stack = new Stack<MBDocument>();
      _definition = definition;
      _characters.setLength(0);

      parser.parse(new ByteArrayInputStream(data), this);

      MBDocument document = (MBDocument) _stack.peek();

      return document;
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    return null;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
  {
    _characters.setLength(0);
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException
  {
    _characters.append(ch, start, length);
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException
  {
    if (localName.equals("ServerResponse"))
    {
      // skip this wrapper element
      // TODO: implement text messages from server (text elements of this element)
    }
    else if (localName.equals("JsonObject"))
    {
      // Deserialize JSON object
      MBDocument document = MBJsonDocumentParser.getDocumentWithString(_characters.toString(), _definition);
      _stack.add(document);
    }
    else
    {
      Log.w(Constants.APPLICATION_NAME, "WARNING: Unexpected element during parsing of Mobbl document");
    }

    _characters.setLength(0);
  }

}

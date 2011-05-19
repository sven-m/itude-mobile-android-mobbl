package com.itude.mobile.mobbl2.client.core.model;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDocumentDefinition;
import com.itude.mobile.mobbl2.client.core.model.exceptions.MBUnknownDataTypeException;

public class MBDocumentFactory
{

  public static String             PARSER_XML    = "XML";
  public static String             PARSER_JSON   = "JSON";
  public static String             PARSER_MOBBL1 = "MOBBL1";

  private static MBDocumentFactory _instance;

  private MBDocumentFactory()
  {

  }

  public static MBDocumentFactory getInstance()
  {
    if (_instance == null)
    {
      _instance = new MBDocumentFactory();
    }

    return _instance;
  }

  public MBDocument getDocumentWithData(byte[] data, String type, MBDocumentDefinition definition)
  {
    if (type.equals(PARSER_XML))
    {
      return MBXmlDocumentParser.getDocumentWithData(data, definition);
    }
    else if (type.equals(PARSER_MOBBL1))
    {
      return MBMobbl1DocumentParser.getDocumentWithData(data, definition);
    }
    else if (type.equals(PARSER_JSON))
    {
      return MBJsonDocumentParser.getDocumentWithData(data, definition);
    }
    else
    {
      throw new MBUnknownDataTypeException(type);
    }
  }

}

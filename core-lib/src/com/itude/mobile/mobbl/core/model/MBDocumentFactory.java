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

import java.util.Hashtable;
import java.util.Map;

import com.itude.mobile.mobbl.core.configuration.mvc.MBDocumentDefinition;
import com.itude.mobile.mobbl.core.model.exceptions.MBUnknownDataTypeException;
import com.itude.mobile.mobbl.core.model.parser.MBDocumentParser;
import com.itude.mobile.mobbl.core.model.parser.MBJsonDocumentParser;
import com.itude.mobile.mobbl.core.model.parser.MBXmlDocumentParser;

public class MBDocumentFactory
{

  public static final String                  PARSER_XML  = "XML";
  public static final String                  PARSER_JSON = "JSON";

  private static MBDocumentFactory            _instance;

  private final Map<String, MBDocumentParser> _registeredDocumentParsers;

  private MBDocumentFactory()
  {
    _registeredDocumentParsers = new Hashtable<String, MBDocumentParser>();

    registerDocumentParser(new MBXmlDocumentParser(), PARSER_XML);
    registerDocumentParser(new MBJsonDocumentParser(), PARSER_JSON);
  }

  public static MBDocumentFactory getInstance()
  {
    if (_instance == null)
    {
      _instance = new MBDocumentFactory();
    }

    return _instance;
  }

  private MBDocumentParser getParserForType(String type)
  {
    MBDocumentParser parser = _registeredDocumentParsers.get(type);
    if (parser == null)
    {
      throw new MBUnknownDataTypeException(type);
    }
    return parser;
  }

  public MBDocument getDocumentWithData(byte[] data, String type, MBDocumentDefinition definition)
  {
    return getParserForType(type).getDocumentWithData(data, definition);
  }

  public void registerDocumentParser(MBDocumentParser parser, String name)
  {
    _registeredDocumentParsers.put(name, parser);
  }

}

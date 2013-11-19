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

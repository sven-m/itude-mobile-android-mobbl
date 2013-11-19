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
package com.itude.mobile.mobbl2.client.core.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBBundleDefinition;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;

public class MBBundle extends MBComponent
{
  private String                    _languageCode;
  private List<String>              _urls;
  private final Map<String, String> _texts;

  public MBBundle(MBBundleDefinition definition, MBDocument document, MBComponentContainer parent)
  {
    super(definition, document, parent);

    _languageCode = definition.getLanguageCode();
    _urls = new ArrayList<String>();
    _texts = new HashMap<String, String>();

    addUrl(definition.getUrl());
  }

  public void addUrl(String url)
  {
    getUrls().add(url);
  }

  public String getText(String key)
  {
    return getTexts().get(key);
  }

  public void putText(String key, String value)
  {
    getTexts().put(key, value);
  }

  public void clear()
  {
    getTexts().clear();
  }

  public String getLanguageCode()
  {
    return _languageCode;
  }

  public void setLanguageCode(String languageCode)
  {
    _languageCode = languageCode;
  }

  public List<String> getUrls()
  {
    return _urls;
  }

  public void setUrls(List<String> urls)
  {
    _urls = urls;
  }

  public Map<String, String> getTexts()
  {
    return _texts;
  }

}

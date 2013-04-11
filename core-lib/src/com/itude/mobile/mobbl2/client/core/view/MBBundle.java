package com.itude.mobile.mobbl2.client.core.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBBundleDefinition;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;

public class MBBundle extends MBComponent
{
  private String              _languageCode;
  private List<String>        _urls;
  private Map<String, String> _texts;

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

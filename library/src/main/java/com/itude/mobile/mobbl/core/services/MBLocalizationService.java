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
package com.itude.mobile.mobbl.core.services;

import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.MissingFormatArgumentException;

import com.itude.mobile.android.util.log.MBLog;
import com.itude.mobile.mobbl.core.util.Constants;
import com.itude.mobile.mobbl.core.util.MBProperties;
import com.itude.mobile.mobbl.core.view.MBBundle;

/**
 * Service Class containing localization methods
 *
 */
public class MBLocalizationService
{
  private final Map<String, MBBundle>  _languages;         //DictionaryofDictionaries(languagecode->(key->value))
  private String                       _currentLanguage;
  private MBBundle                     _currentLanguageMap;
  private String                       _localeCode;

  private static MBLocalizationService _instance;

  private MBLocalizationService()
  {
    _languages = new Hashtable<String, MBBundle>();

    // Let's set our language to the one we've set in our applicationproperties.xml or to a default one if none were found.
    setCurrentLanguage(getLocale().getLanguage());
  }

  public static MBLocalizationService getInstance()
  {
    // so called double-check
    if (_instance == null)
    {
      synchronized (MBLocalizationService.class)
      {
        if (_instance == null) _instance = new MBLocalizationService();
      }
    }

    return _instance;
  }

  private MBBundle getLanguageForCode(String languageCode)
  {
    MBBundle result = null;
    result = _languages.get(languageCode);
    if (result == null)
    {
      result = MBResourceService.getInstance().getBundle(languageCode);
      _languages.put(languageCode, result);
    }

    return result;
  }

  public String getTextForLanguageCode(String key, String languageCode)
  {
    if (getCurrentLanguage().equals(languageCode))
    {
      return getTextForKey(key);
    }

    MBBundle bundle = getLanguageForCode(languageCode);
    String text = bundle.getText(key);

    if (text == null)
    {
      MBLog.w(Constants.APPLICATION_NAME, "No translation defined for key " + key + " using languageCode=" + languageCode);
      text = key;
    }

    return text;
  }

  public String getCurrentLanguage()
  {
    return _currentLanguage;
  }

  public synchronized void setCurrentLanguage(String currentLanguage)
  {
    // If no localeCode was found then we set the default language to nl
    if (currentLanguage == null || currentLanguage.length() <= 0)
    {
      currentLanguage = "nl";
    }

    _currentLanguage = currentLanguage;
    _currentLanguageMap = getLanguageForCode(_currentLanguage);
  }

  public String getTextForKey(String key)
  {
    if (key == null)
    {
      return null;
    }

    MBBundle dict = _currentLanguageMap;
    String text = dict.getText(key);
    if (text == null)
    {
      MBLog.w(Constants.APPLICATION_NAME, "Warning: no translation defined for key '" + key + "' using languageCode="
                                          + getCurrentLanguage());
      // add the missing translation to prevent future warnings
      dict.putText(key, key);
      text = key;
    }

    return text;
  }

  public static String getLocalizedString(String key)
  {
    return getInstance().getTextForKey(key);
  }

  /***
   * @param key Key
   * @param args Objects
   * @return text
   */
  public String getText(String key, Object... args)
  {
    if (key == null)
    {
      return null;
    }

    String text = getTextForKey(key);
    String result = "";
    try
    {
      result = String.format(text, args);
    }
    catch (MissingFormatArgumentException e)
    {
      MBLog.w(Constants.APPLICATION_NAME, e);
      result = text;
    }

    return result;

  }

  public String getLocaleCode()
  {
    if (_localeCode == null)
    {
      _localeCode = MBProperties.getInstance().getValueForProperty("localeCode");
      if (_localeCode == null)
      {
        _localeCode = Locale.getDefault().toString();
      }
    }
    return _localeCode;
  }

  public Locale getLocale()
  {
    String localeCode = getLocaleCode();

    if (localeCode != null && localeCode.length() > 0)
    {
      String[] parts = localeCode.split("_");

      if (parts.length == 1)
      {
        return new Locale(parts[0]);
      }
      else if (parts.length == 2)
      {
        return new Locale(parts[0], parts[1]);
      }

    }

    return null;
  }

}

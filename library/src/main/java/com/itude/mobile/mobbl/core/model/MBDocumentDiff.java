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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.itude.mobile.mobbl.core.util.MBPathUtil;

/**
 * Class that contains the difference between two {@link MBDocument}s
 *
 */
public class MBDocumentDiff
{
  /**
   * A collection with paths for differences in values between document A and B
   */
  private final HashSet<String>     _modified;

  /**
   * A collection with values from document A that are different with the values from document B.
   */
  private final Map<String, String> _aValues;

  /**
   * A collection with values from document B that are different with the values from document A.
   */
  private final Map<String, String> _bValues;

  /**
   * Instantiates a MBDocumentDiff object with two different documents and looks for differences between the two documents
   * 
   * @param newDoc The first document that needs to be compared
   * @param currentDoc The second document that needs to be compared

   */
  public MBDocumentDiff(MBDocument newDoc, MBDocument currentDoc)
  {
    _modified = new HashSet<String>();
    _aValues = new HashMap<String, String>();
    _bValues = new HashMap<String, String>();

    diffA(newDoc, currentDoc);
  }

  private String normalize(String path)
  {
    if (!path.startsWith("/"))
    {
      path = "/" + path;
    }

    return MBPathUtil.normalizedPath(path);
  }

  private void diffA(MBDocument a, MBDocument b)
  {
    HashSet<String> set = new HashSet<String>();
    a.addAllPathsTo(set, "");
    b.addAllPathsTo(set, "");

    for (String changedPath : set)
    {
      String path = normalize(changedPath);
      String valueA = (String) a.getValueForPath(path);
      String valueB = (String) b.getValueForPath(path);

      if ((valueA != null && valueB == null) || (valueA == null && valueB != null))
      {
        // they are different because one is null and the other is not
        _modified.add(path);
      }
      else if (valueA != null && valueB != null && !valueA.equals(valueB))
      {
        _modified.add(path);
        _aValues.put(path, valueA);
        _bValues.put(path, valueB);
      }
    }

  }

  /**
   * Checks if document A and document B are different.
   * 
   * @return true if any value in document A is different from document B. Returns false if the documents are the same.
   */
  public boolean isChanged()
  {
    return !_modified.isEmpty();
  }

  /**
   * Looks for differences in the values between document A and B on the suppied path.
   * 
   * @param path The path that needs to be checked for differences
   * @return true if the value for the given path in document A and B is different (has changed)
   */
  public boolean isChanged(String path)
  {
    return _modified.contains(normalize(path));
  }

  /**
   * Gets a collection with paths to values that are different in document A and B
   * 
   * @return an {@link Set} with paths to values that are different in document A and B
   */
  public Set<String> getPaths()
  {
    return _modified;
  }

  /**
   * Returns the value for path in document A
   * 
   * @param path The path to the element or attribute in document A
   * @return a {@link String} with the value of an attribute or element on the path in document A
   */
  public String valueOfAForPath(String path)
  {
    return _aValues.get(normalize(path));
  }

  /**
   * Returns the value for path in document B
   * 
   * @param path The path to the element or attribute in document B
   * @return a {@link String} with the value of an attribute or element on the path in document B
   */
  public String valueOfBForPath(String path)
  {
    return _bValues.get(normalize(path));
  }

  @Override
  public String toString()
  {
    return _modified.toString();
  }

}

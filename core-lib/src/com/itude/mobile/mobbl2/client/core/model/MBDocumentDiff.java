package com.itude.mobile.mobbl2.client.core.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.itude.mobile.mobbl2.client.core.util.StringUtilities;

public class MBDocumentDiff
{
  private final HashSet<String>     _modified;
  private final Map<String, String> _aValues;
  private final Map<String, String> _bValues;

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

    return StringUtilities.normalizedPath(path);
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

  public boolean isChanged()
  {
    return _modified.size() != 0;
  }

  public boolean isChanged(String path)
  {
    return _modified.contains(normalize(path));
  }

  public Set<String> getPaths()
  {
    return _modified;
  }

  public String valueOfAForPath(String path)
  {
    return _aValues.get(normalize(path));
  }

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

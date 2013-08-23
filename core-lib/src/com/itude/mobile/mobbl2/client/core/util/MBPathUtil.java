package com.itude.mobile.mobbl2.client.core.util;

import java.util.ArrayList;
import java.util.List;

import com.itude.mobile.mobbl2.client.core.util.exceptions.MBInvalidRelativePathException;

public final class MBPathUtil
{
  private MBPathUtil()
  {
  }

  /**
   * Returns a List of path-parts with some light processing.
   * for example the path
   * /a/b/c/////.///d/../e
   * is returned as a list containing
   * a,b,c,e
   * multiple adjacent /-es are ignored
   * a . is removed
   * a .. is interpreted as: pop the previous path part (d in the example above)  
   * 
   * @param path
   * @return
   */
  public static List<String> splitPath(String toSplit)
  {
    // performance tuned implementation of splitPath
    // measurements show this impl takes just 25% compared to the old
    // implementation (splitPathOldImplementation which uses expensive Regular Expressions).
    // note that the AndroidBinckTest project has a testcase that checks splitPath performance
    // against splitPathOldImplementation performance. if splitPath doesn't perform at least
    // 4 times as fast, the test fails.
    List<String> components = new ArrayList<String>();
    int previousPosition = 0;
    int slashPosition;
    while ((slashPosition = toSplit.indexOf('/', previousPosition)) >= 0)
    {
      String component = toSplit.substring(previousPosition, slashPosition);
      previousPosition = slashPosition + 1;

      processPathComponent(component, components, toSplit);
    }
    if (previousPosition < toSplit.length())
    {
      // this happens when the path is something like /a/b/c
      // (no trailing forward slash).
      String component = toSplit.substring(previousPosition);
      processPathComponent(component, components, toSplit);
    }
    return components;
  }

  private static void processPathComponent(String component, List<String> componentsInPath, String completePath)
  {
    if (component.length() == 0 || (component.length() == 1 && component.equals(".")))
    {
      // nothing, ignore this component
    }
    else if (component.length() == 2 && component.equals(".."))
    {
      // pop the previous path component
      if (componentsInPath.size() == 0)
      {
        throw new MBInvalidRelativePathException(completePath);
      }
      componentsInPath.remove(componentsInPath.size() - 1);
    }
    else
    {
      componentsInPath.add(component);
    }
  }

  public static String normalizedPath(String path)
  {
    // try to prevent work in the normal case (the path is already normalized)
    // especially the splitPath method-call is expensive.
    if (path.indexOf('.') < 0 && path.indexOf("//") < 0)
    {
      // remove trailing / if present
      if (path.endsWith("/")) return path.substring(0, path.length() - 1);
      else return path;
    }
    boolean isRelative = !path.startsWith("/");

    StringBuilder result = new StringBuilder();
    for (String component : splitPath(path))
    {
      result.append('/').append(component);
    }

    if (isRelative && result.charAt(0) == '/')
    {
      return result.substring(1);
    }
    else
    {
      return result.toString();
    }
  }
}

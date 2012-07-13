package com.itude.mobile.mobbl2.client.core.util;

import java.security.MessageDigest;
import java.text.CharacterIterator;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Hex;

import android.text.Html;
import android.text.InputType;
import android.text.Spanned;
import android.text.method.NumberKeyListener;
import android.util.Log;

import com.itude.mobile.mobbl2.client.core.services.MBLocalizationService;
import com.itude.mobile.mobbl2.client.core.util.exceptions.MBInvalidRelativePathException;

public final class StringUtilities
{

  private static Locale                           defaultFormattingLocale;

  private static NumberKeyListener                _currencyNumberKeyListener;

  public static final String                      EMPTY           = "";

  private static final ThreadLocal<DecimalFormat> TLFORMATTER3DEC = new ThreadLocal<DecimalFormat>()
                                                                  {
                                                                    @Override
                                                                    protected DecimalFormat initialValue()
                                                                    {
                                                                      DecimalFormat formatter = new DecimalFormat();
                                                                      setupFormatter(formatter, 3);
                                                                      return formatter;
                                                                    }
                                                                  };

  private StringUtilities()
  {
  }

  private static void setupFormatter(DecimalFormat formatter, int numDec)
  {
    formatter.setDecimalFormatSymbols(new DecimalFormatSymbols(getDefaultFormattingLocale()));
    formatter.setMinimumIntegerDigits(1);
    formatter.setMinimumFractionDigits(numDec);
    formatter.setMaximumFractionDigits(numDec);
    formatter.setGroupingUsed(true);
    formatter.setGroupingSize(3);
  }

  public static String stripCharacters(String inputString, String stripCharacters)
  {
    char[] charArray = stripCharacters.toCharArray();

    for (char c : charArray)
    {
      inputString = inputString.replace(String.valueOf(c), "");
    }

    return inputString;
  }

  public static String stripCharacter(String inputString, char stripCharacter)
  {
    return inputString.replaceAll(Pattern.quote(Character.toString(stripCharacter)), "");
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

  /**
   * 
   * @param level
   * @return String of length level spaces
   * 
   */
  public static String getIndentStringWithLevel(int level)
  {
    StringBuffer rt = new StringBuffer(level);
    return appendIndentString(rt, level).toString();
  }

  /**
   * Appends spaces to the supplied StringBuffer, returns the same StringBuffer.
   * 
   * @param appendToMe
   * @param level
   * @return the same StringBuffer given as a param, useful for chaining calls
   */
  public static StringBuffer appendIndentString(StringBuffer appendToMe, int level)
  {
    while (level-- > 0)
      appendToMe.append(' ');

    return appendToMe;
  }

  //returns a string formatted as a number with the original amount of decimals assuming the receiver is a float 
  //WARNING: Only use this method to present data to the screen (BINCKAPPS-32, BINCKMOBILE-35, BINCKMOBILE-113)
  public static String formatNumberWithOriginalNumberOfDecimals(String stringToFormat)
  {

    if (stringToFormat == null || stringToFormat.length() == 0)
    {
      return null;
    }

    String result = null;

    DecimalFormat formatter = new DecimalFormat("#################.####################", new DecimalFormatSymbols(
        getDefaultFormattingLocale()));

    try
    {
      result = formatter.format(Double.parseDouble(stringToFormat));
    }
    catch (Exception e)
    {
      Log.w(Constants.APPLICATION_NAME, "Could not format string " + stringToFormat
                                        + " as number with original number of decimals (StringUtilities)", e);

      return null;
    }

    return result;
  }

  // returns a string formatted as a number with two decimals assuming the receiver is a float string read from XML
  // WARNING: Only use this method to present data to the screen (BINCKAPPS-32, BINCKMOBILE-35, BINCKMOBILE-113)
  public static String formatNumberWithTwoDecimals(String stringToFormat)
  {
    return formatNumberWithDecimals(stringToFormat, 2);
  }

  // returns a string formatted as a number with three decimals assuming the receiver is a float string read from XML
  // WARNING: Only use this method to present data to the screen (BINCKAPPS-32, BINCKMOBILE-35, BINCKMOBILE-113)
  public static String formatNumberWithThreeDecimals(String stringToFormat)
  {
    return formatNumberWithDecimals(stringToFormat, 3);
  }

  /***
   * 
   * @param stringToFormat
   * @param numberOfDecimals can be any number, also negative as the used DecimalFormatter accepts it and makes it 0
   * @return
   */
  public static String formatNumberWithDecimals(String stringToFormat, int numberOfDecimals)
  {
    if (stringToFormat == null || stringToFormat.length() == 0)
    {
      return null;
    }

    String result = null;

    DecimalFormat formatter = new DecimalFormat();
    formatter.setDecimalFormatSymbols(new DecimalFormatSymbols(getDefaultFormattingLocale()));
    formatter.setMinimumIntegerDigits(1);
    formatter.setMinimumFractionDigits(numberOfDecimals);
    formatter.setMaximumFractionDigits(numberOfDecimals);
    formatter.setGroupingUsed(true);
    formatter.setGroupingSize(3);

    result = formatter.format(Double.parseDouble(stringToFormat));

    return result;
  }

  // returns a string formatted as a price with two decimals assuming the receiver is a float string read from XML
  // WARNING: Only use this method to present data to the screen (BINCKAPPS-32, BINCKMOBILE-35, BINCKMOBILE-113)
  public static String formatPriceWithTwoDecimals(String stringToFormat)
  {
    if (stringToFormat == null || stringToFormat.length() == 0)
    {
      return null;
    }

    int numberStart = stringToFormat.indexOf(" ");
    if (numberStart != -1 && (stringToFormat.indexOf("$") > -1 || stringToFormat.indexOf("€") > -1))
    {
      numberStart = Math.max(stringToFormat.indexOf("$"), stringToFormat.indexOf("€"));
    }

    if (numberStart > -1)
    {
      String prefix = stringToFormat.substring(0, numberStart + 1);
      stringToFormat = stringToFormat.substring(numberStart + 1, stringToFormat.length());
      return prefix + formatPriceWithTwoDecimals(Double.parseDouble(stringToFormat));
    }
    return formatPriceWithTwoDecimals(Double.parseDouble(stringToFormat));
  }

  public static String formatPriceWithTwoDecimals(double toFormat)
  {
    DecimalFormat formatter = new DecimalFormat();
    formatter.setDecimalFormatSymbols(new DecimalFormatSymbols(getDefaultFormattingLocale()));
    formatter.setMinimumIntegerDigits(1);
    formatter.setMinimumFractionDigits(2);
    formatter.setMaximumFractionDigits(2);

    formatter.setGroupingUsed(true);
    formatter.setGroupingSize(3);

    return formatter.format(toFormat);
  }

  // returns a string formatted as a price with three decimals assuming the receiver is a float string read from XML
  // WARNING: Only use this method to present data to the screen (BINCKAPPS-32, BINCKMOBILE-35, BINCKMOBILE-113)
  // TODO why is this method not doing anything with the currency sign?
  public static String formatPriceWithThreeDecimals(String stringToFormat)
  {
    if (stringToFormat == null || stringToFormat.length() == 0)
    {
      return null;
    }
    return TLFORMATTER3DEC.get().format(Double.parseDouble(stringToFormat));
  }

  // returns a string formatted as a volume with group separators (eg, 131.224.000) assuming the receiver is an int string read from XML
  // WARNING: Only use this method to present data to the screen (BINCKAPPS-32, BINCKMOBILE-35, BINCKMOBILE-113)
  public static String formatVolume(String stringToFormat)
  {
    if (stringToFormat == null || stringToFormat.length() == 0)
    {
      return null;
    }

    String result = null;

    DecimalFormat formatter = new DecimalFormat();
    formatter.setDecimalFormatSymbols(new DecimalFormatSymbols(getDefaultFormattingLocale()));

    formatter.setGroupingUsed(true);
    formatter.setGroupingSize(3);
    formatter.setMaximumFractionDigits(0);

    result = formatter.format(Double.parseDouble(stringToFormat));

    return result;
  }

  /**
   * returns a string formatted as a percentage with two decimals assuming the receiver is a float string read from XML
   * the receiver's value should be "as displayed", eg for 30%, the receiver should be 30, not 0.3 
   */
  public static String formatPercentageWithTwoDecimals(String stringToFormat)
  {
    return formatPriceWithTwoDecimals(stringToFormat) + "%";
  }

  /**
   * returns a string formatted as a percentage with two decimals assuming the receiver is a float string read from XML
   * the receiver's value should be "as displayed", eg for 30%, the receiver should be 30, not 0.3 
   */
  public static String formatPercentageWithTwoDecimals(double doubleToFormat)
  {
    return formatPriceWithTwoDecimals(doubleToFormat) + "%";
  }

  public static String md5(String stringToHash)
  {
    MessageDigest digest = null;
    try
    {
      digest = java.security.MessageDigest.getInstance("MD5");
      digest.update(stringToHash.getBytes());
      byte[] hash = digest.digest();
      return new String(Hex.encodeHex(hash));
    }
    catch (Exception e)
    {
      Log.w(Constants.APPLICATION_NAME, "Could not create hash of following string: " + stringToHash);
    }

    return null;
  }

  public static void setDefaultFormattingLocale(Locale defaultFormattingLocale)
  {
    StringUtilities.defaultFormattingLocale = defaultFormattingLocale;
    setupFormatter(TLFORMATTER3DEC.get(), 3);
  }

  public static Locale getDefaultFormattingLocale()
  {
    if (defaultFormattingLocale == null)
    {
      defaultFormattingLocale = MBLocalizationService.getInstance().getLocale();
    }

    return defaultFormattingLocale;
  }

  public static Spanned fromHTML(String textToFransform)
  {
    return Html.fromHtml(textToFransform);
  }

  public static String stripHTMLTags(String textToStrip)
  {

    StringBuffer returnText = new StringBuffer(textToStrip.length());

    CharacterIterator iterator = new StringCharacterIterator(textToStrip);

    boolean finished = true;
    boolean started = false;
    for (char ch = iterator.first(); ch != CharacterIterator.DONE; ch = iterator.next())
    {
      if (ch == '<')
      {
        started = true;
      }
      else if (ch == '>')
      {
        started = false;
        finished = true;
      }
      else if (finished && !started)
      {
        returnText.append(ch);
      }

    }

    return returnText.toString().trim();
  }

  /**
   * Capitalizes every word in str 
   */
  public static String capitalize(String str)
  {
    if (str == null || str.length() == 0) return str;

    boolean capitalizeNext = true;
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < str.length(); ++i)
    {
      char ch = str.charAt(i);
      if (capitalizeNext) result.append(Character.toUpperCase(ch));
      else result.append(ch);

      capitalizeNext = Character.isWhitespace(ch);
    }

    return result.toString();
  }

  /**
   * <p>Checks if a String is not empty ("") and not null.</p>
   *
   * <pre>
   * StringUtils.isNotEmpty(null)      = false
   * StringUtils.isNotEmpty("")        = false
   * StringUtils.isNotEmpty(" ")       = true
   * StringUtils.isNotEmpty("wiebe")     = true
   * StringUtils.isNotEmpty("  wiebe  ") = true
   * </pre>
   *
   * @param str  the String to check, may be null
   * @return <code>true</code> if the String is not empty and not null
   */
  public static boolean isNotEmpty(String str)
  {
    return !isEmpty(str);
  }

  /**
   * <p>Checks if a String is empty ("") or null.</p>
   *
   * <pre>
   * StringUtils.isEmpty(null)      = true
   * StringUtils.isEmpty("")        = true
   * StringUtils.isEmpty(" ")       = false
   * StringUtils.isEmpty("wiebe")     = false
   * StringUtils.isEmpty("  wiebe  ") = false
   * </pre>
   *
   * @param str  the String to check, may be null
   * @return <code>true</code> if the String is empty or null
   */
  public static boolean isEmpty(String str)
  {
    return str == null || str.length() == 0;
  }

  /**
   * <p>Checks if a String is whitespace, empty ("") or null.</p>
   *
   * <pre>
   * StringUtils.isBlank(null)      = true
   * StringUtils.isBlank("")        = true
   * StringUtils.isBlank(" ")       = true
   * StringUtils.isBlank("wiebe")     = false
   * StringUtils.isBlank("  wiebe  ") = false
   * </pre>
   *
   * @param str  the String to check, may be null
   * @return <code>true</code> if the String is null, empty or whitespace
   */
  public static boolean isBlank(String str)
  {
    int strLen;
    if (str == null || (strLen = str.length()) == 0)
    {
      return true;
    }
    for (int i = 0; i < strLen; i++)
    {
      if ((Character.isWhitespace(str.charAt(i)) == false))
      {
        return false;
      }
    }
    return true;
  }

  /**
   * <p>Checks if a String is not empty (""), not null and not whitespace only.</p>
   *
   * <pre>
   * StringUtils.isNotBlank(null)      = false
   * StringUtils.isNotBlank("")        = false
   * StringUtils.isNotBlank(" ")       = false
   * StringUtils.isNotBlank("bob")     = true
   * StringUtils.isNotBlank("  bob  ") = true
   * </pre>
   *
   * @param str  the String to check, may be null
   * @return <code>true</code> if the String is
   *  not empty and not null and not whitespace
   */
  public static boolean isNotBlank(String str)
  {
    return !isBlank(str);
  }

  public static NumberKeyListener getCurrencyNumberKeyListener()
  {
    if (_currencyNumberKeyListener == null)
    {
      _currencyNumberKeyListener = new NumberKeyListener()
      {

        public int getInputType()
        {
          return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
        }

        @Override
        protected char[] getAcceptedChars()
        {
          return new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ','};
        }
      };
    }

    return _currencyNumberKeyListener;
  }

  /**
   * <p>Gets the substring after the first occurrence of a separator.
   * The separator is not returned.</p>
   *
   * <p>A <code>null</code> string input will return <code>null</code>.
   * An empty ("") string input will return the empty string.
   * A <code>null</code> separator will return the empty string if the
   * input string is not <code>null</code>.</p>
   *
   * <pre>
   * StringUtils.substringAfter(null, *)      = null
   * StringUtils.substringAfter("", *)        = ""
   * StringUtils.substringAfter(*, null)      = ""
   * StringUtils.substringAfter("abc", "a")   = "bc"
   * StringUtils.substringAfter("abcba", "b") = "cba"
   * StringUtils.substringAfter("abc", "c")   = ""
   * StringUtils.substringAfter("abc", "d")   = ""
   * StringUtils.substringAfter("abc", "")    = "abc"
   * </pre>
   *
   * @param str  the String to get a substring from, may be null
   * @param separator  the String to search for, may be null
   * @return the substring after the first occurrence of the separator,
   *  <code>null</code> if null String input
   * @since 2.0
   */
  public static String substringAfter(String str, String separator)
  {
    if (isEmpty(str))
    {
      return str;
    }
    if (separator == null)
    {
      return EMPTY;
    }
    int pos = str.indexOf(separator);
    if (pos == -1)
    {
      return EMPTY;
    }
    return str.substring(pos + separator.length());
  }

  /**
   * @param value value
   * @return double value
   */
  public static Double getDoubleValue(String value)
  {
    if (isBlank(value))
    {
      throw new NumberFormatException();
    }

    return Double.parseDouble(value.replaceAll(",", "."));
  }

  // Source: http://www.javapractices.com/topic/TopicAction.do?Id=96
  public static String escapeHtml(String html)
  {
    final StringBuilder escapedHtml = new StringBuilder();
    final StringCharacterIterator iterator = new StringCharacterIterator(html);
    char character = iterator.current();
    while (character != CharacterIterator.DONE)
    {
      if (character == '<')
      {
        escapedHtml.append("&lt;");
      }
      else if (character == '>')
      {
        escapedHtml.append("&gt;");
      }
      else if (character == '\"')
      {
        escapedHtml.append("&quot;");
      }
      else if (character == '\'')
      {
        escapedHtml.append("&#039;");
      }
      else if (character == '&')
      {
        escapedHtml.append("&amp;");
      }
      else
      {
        //the char is not a special one
        //add it to the result as is
        escapedHtml.append(character);
      }
      character = iterator.next();
    }
    return escapedHtml.toString();
  }

  /**
   * Gets the String that is nested in between two Strings.
   * Only the first match is returned.
   *
   * A <code>null</code> input String returns <code>null</code>.
   * A <code>null</code> open/close returns <code>null</code> (no match).
   * An empty ("") open and close returns an empty string.
   *
   * <pre>
   * StringUtils.substringBetween("w[ie]be", "[", "]") = "ie"
   * StringUtils.substringBetween(null, *, *)          = null
   * StringUtils.substringBetween(*, null, *)          = null
   * StringUtils.substringBetween(*, *, null)          = null
   * StringUtils.substringBetween("", "", "")          = ""
   * StringUtils.substringBetween("", "", "]")         = null
   * StringUtils.substringBetween("", "[", "]")        = null
   * StringUtils.substringBetween("wiebe", "", "")     = ""
   * StringUtils.substringBetween("wiebe", "w", "e")   = "ieb"
   * StringUtils.substringBetween("wiebewiebe", "w", "w")   = "ieb"
   * </pre>
   *
   * @param str  the String containing the substring, may be null
   * @param open  the String before the substring, may be null
   * @param close  the String after the substring, may be null
   * @return the substring, <code>null</code> if no match
   * @since 2.0
   */
  public static String substringBetween(String str, String open, String close)
  {
    if (str == null || open == null || close == null)
    {
      return null;
    }
    int start = str.indexOf(open);
    if (start != -1)
    {
      int end = str.indexOf(close, start + open.length());
      if (end != -1)
      {
        return str.substring(start + open.length(), end);
      }
    }
    return null;
  }

}

package com.itude.mobile.mobbl2.client.core.util;

import java.security.MessageDigest;
import java.text.CharacterIterator;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import android.text.InputType;
import android.text.method.NumberKeyListener;
import android.util.Log;

import com.itude.mobile.mobbl2.client.core.services.MBLocalizationService;
import com.itude.mobile.mobbl2.client.core.util.exceptions.MBDateParsingException;
import com.itude.mobile.mobbl2.client.core.util.exceptions.MBInvalidRelativePathException;

public class StringUtilities
{

  private static Locale                     defaultFormattingLocale;

  private static NumberKeyListener          _currencyNumberKeyListener;

  private static ThreadLocal<DecimalFormat> TLFormatter3Dec = new ThreadLocal<DecimalFormat>()
                                                            {
                                                              @Override
                                                              protected DecimalFormat initialValue()
                                                              {
                                                                DecimalFormat formatter = new DecimalFormat();
                                                                setupFormatter(formatter, 3);
                                                                return formatter;
                                                              }
                                                            };

  private static void setupFormatter(DecimalFormat formatter, int p_numDec)
  {
    formatter.setDecimalFormatSymbols(new DecimalFormatSymbols(getDefaultFormattingLocale()));
    formatter.setMinimumIntegerDigits(1);
    formatter.setMinimumFractionDigits(p_numDec);
    formatter.setMaximumFractionDigits(p_numDec);
    formatter.setGroupingUsed(true);
    formatter.setGroupingSize(3);
  }

  private static final String                  DEFAULT_DATE_FORMAT    = "yyyy-MM-dd'T'HH:mm:ss";
  private static ThreadLocal<SimpleDateFormat> TLDefaultDateFormatter = new ThreadLocal<SimpleDateFormat>()
                                                                      {
                                                                        @Override
                                                                        protected SimpleDateFormat initialValue()
                                                                        {
                                                                          return new SimpleDateFormat(DEFAULT_DATE_FORMAT);
                                                                        }
                                                                      };

  public static String stripCharacters(String inputString, String stripCharacters)
  {
    char[] charArray = stripCharacters.toCharArray();

    for (char c : charArray)
    {
      inputString = inputString.replaceAll(Pattern.quote(Character.toString(c)), "");
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
  public static List<String> splitPath(String p_toSplit)
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
    while ((slashPosition = p_toSplit.indexOf('/', previousPosition)) >= 0)
    {
      String component = p_toSplit.substring(previousPosition, slashPosition);
      previousPosition = slashPosition + 1;

      processPathComponent(component, components, p_toSplit);
    }
    if (previousPosition < p_toSplit.length())
    {
      // this happens when the path is something like /a/b/c
      // (no trailing forward slash).
      String component = p_toSplit.substring(previousPosition);
      processPathComponent(component, components, p_toSplit);
    }
    return components;
  }

  private static void processPathComponent(String p_component, List<String> p_componentsInPath, String p_completePath)
  {
    if (p_component.length() == 0 || (p_component.length() == 1 && p_component.equals(".")))
    {
      // nothing, ignore this component
    }
    else if (p_component.length() == 2 && p_component.equals(".."))
    {
      // pop the previous path component
      if (p_componentsInPath.size() == 0)
      {
        throw new MBInvalidRelativePathException(p_completePath);
      }
      p_componentsInPath.remove(p_componentsInPath.size() - 1);
    }
    else
    {
      p_componentsInPath.add(p_component);
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
   * @param p_appendToMe
   * @param level
   * @return the same StringBuffer given as a param, useful for chaining calls
   */
  public static StringBuffer appendIndentString(StringBuffer p_appendToMe, int level)
  {
    while (level-- > 0)
      p_appendToMe.append(' ');

    return p_appendToMe;
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

  // Formats the date depending on the current date assuming the receiver is a date string 
  // If the date is equal to the current date, the time is given back as a string
  // If the date is NOT equal to the current date, then a a date is presented back as a string
  public static String formatDateDependingOnCurrentDate(String dateString)
  {
    String result = dateString;
    Date date = dateFromXML(dateString);

    String dateFormatMask = "";

    // We can't just compare two dates, because the time is also compared.
    // Therefore the time is removed and the two dates without time are compared
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);

    Calendar today = Calendar.getInstance();
    today.setTime(new Date());

    if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH)
        && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR))
    {
      dateFormatMask = "HH:mm:ss";
    }
    else
    {
      dateFormatMask = "dd-MM-yy";
    }

    // Format the date
    try
    {
      SimpleDateFormat df = new SimpleDateFormat(dateFormatMask);
      result = df.format(date);

      return result;
    }
    catch (Exception e)
    {
      throw new MBDateParsingException("Could not get format date depending on current date with input string: " + dateString, e);
    }

  }

  public synchronized static Date dateFromXML(String stringToFormat)
  {
    try
    {
      String dateString = stringToFormat.substring(0, 19);
      if (dateString != null) return TLDefaultDateFormatter.get().parse(dateString);
      else return null;
    }
    catch (Exception e)
    {
      throw new MBDateParsingException("Could not parse date from xml value: " + stringToFormat, e);
    }

  }

  public static String dateToString(Date date)
  {
    return dateToStringDefaultFormat(date);
  }

  public static String dateToString(Date date, String format)
  {
    if (isEmpty(format)) return dateToStringDefaultFormat(date);
    SimpleDateFormat df = new SimpleDateFormat(format);

    try
    {
      return df.format(date);
    }
    catch (Exception e)
    {
      throw new MBDateParsingException("Could not convert date to string with input date: " + date, e);
    }

  }

  private static String dateToStringDefaultFormat(Date dateToFormat)
  {
    return TLDefaultDateFormatter.get().format(dateToFormat);
  }

  // returns a string formatted as a number with two decimals assuming the receiver is a float string read from XML
  // WARNING: Only use this method to present data to the screen (BINCKAPPS-32, BINCKMOBILE-35, BINCKMOBILE-113)
  public static String formatNumberWithTwoDecimals(String stringToFormat)
  {

    if (stringToFormat == null || stringToFormat.length() == 0)
    {
      return null;
    }

    String result = null;

    DecimalFormat formatter = new DecimalFormat();
    formatter.setDecimalFormatSymbols(new DecimalFormatSymbols(getDefaultFormattingLocale()));
    formatter.setMinimumIntegerDigits(1);
    formatter.setMinimumFractionDigits(2);
    formatter.setMaximumFractionDigits(2);
    formatter.setGroupingUsed(true);
    formatter.setGroupingSize(3);

    result = formatter.format(Double.parseDouble(stringToFormat));

    return result;
  }

  // returns a string formatted as a number with three decimals assuming the receiver is a float string read from XML
  // WARNING: Only use this method to present data to the screen (BINCKAPPS-32, BINCKMOBILE-35, BINCKMOBILE-113)
  public static String formatNumberWithThreeDecimals(String stringToFormat)
  {
    if (stringToFormat == null || stringToFormat.length() == 0)
    {
      return null;
    }

    String result = null;

    DecimalFormat formatter = new DecimalFormat();
    formatter.setDecimalFormatSymbols(new DecimalFormatSymbols(getDefaultFormattingLocale()));
    formatter.setMinimumIntegerDigits(1);
    formatter.setMinimumFractionDigits(3);
    formatter.setMaximumFractionDigits(3);
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

    stringToFormat.trim();
    int numberStart = -1;
    String prefix = null;

    if (stringToFormat.indexOf(" ") > -1)
    {
      numberStart = stringToFormat.indexOf(" ");
    }
    else if (stringToFormat.indexOf("$") > -1 || stringToFormat.indexOf("€") > -1)
    {
      numberStart = Math.max(stringToFormat.indexOf("$"), stringToFormat.indexOf("€"));
    }

    if (numberStart > -1)
    {
      prefix = stringToFormat.substring(0, numberStart + 1);
      stringToFormat = stringToFormat.substring(numberStart + 1, stringToFormat.length());
    }

    String result = null;

    DecimalFormat formatter = new DecimalFormat();
    formatter.setDecimalFormatSymbols(new DecimalFormatSymbols(getDefaultFormattingLocale()));
    formatter.setMinimumIntegerDigits(1);
    formatter.setMinimumFractionDigits(2);
    formatter.setMaximumFractionDigits(2);

    formatter.setGroupingUsed(true);
    formatter.setGroupingSize(3);

    result = formatter.format(Double.parseDouble(stringToFormat));

    if (numberStart > -1)
    {
      return prefix + result;
    }
    return result;
  }

  // returns a string formatted as a price with three decimals assuming the receiver is a float string read from XML
  // WARNING: Only use this method to present data to the screen (BINCKAPPS-32, BINCKMOBILE-35, BINCKMOBILE-113)
  //
  public static String formatPriceWithThreeDecimals(String stringToFormat)
  {
    if (stringToFormat == null || stringToFormat.length() == 0)
    {
      return null;
    }
    return TLFormatter3Dec.get().format(Double.parseDouble(stringToFormat));
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

  // returns a string formatted as a percentage with two decimals assuming the receiver is a float string read from XML
  // the receiver's value should be "as displayed", eg for 30%, the receiver should be 30, not 0.3
  public static String formatPercentageWithTwoDecimals(String stringToFormat)
  {
    return formatPriceWithTwoDecimals(stringToFormat) + "%";
  }

  public static String md5(String stringToHash)
  {
    MessageDigest digest = null;
    try
    {
      digest = MessageDigest.getInstance("MD5");
      digest.update(stringToHash.getBytes());

      byte[] messageDigest = digest.digest();

      StringBuffer hexString = new StringBuffer();
      for (int i = 0; i < messageDigest.length; i++)
        hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
      return hexString.toString();
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
    setupFormatter(TLFormatter3Dec.get(), 3);
  }

  public static Locale getDefaultFormattingLocale()
  {
    if (defaultFormattingLocale == null)
    {
      defaultFormattingLocale = MBLocalizationService.getInstance().getLocale();
    }

    return defaultFormattingLocale;
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
   * @param value value
   * @return true if {@link String} value is not null and lenght > 0
   */
  public static boolean isNotEmpty(String value)
  {
    return !isEmpty(value);
  }

  /**
   * @param value value
   * @return true if {@link String} value is null or lenght > 0
   */

  public static boolean isEmpty(String value)
  {
    if (value == null || value.length() == 0)
    {
      return true;
    }
    else
    {
      return false;
    }
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

}

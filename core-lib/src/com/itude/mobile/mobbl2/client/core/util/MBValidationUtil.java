package com.itude.mobile.mobbl2.client.core.util;

/**
 * 
 *
 */
public final class MBValidationUtil
{

  private MBValidationUtil()
  {

  }

  public static boolean validateEmail(String value)
  {
    if (StringUtilities.isBlank(value))
    {
      return true;
    }

    if (!StringUtilities.checkPattern("^[0-9a-zA-Z][\\w+_.-]*@\\w[\\w+_.-]*\\.[a-zA-Z]{2,9}$", value))
    {
      return false;
    }

    return true;
  }

  public static boolean validateName(String name)
  {
    boolean validate = true;
    if (StringUtilities.isBlank(name))
    {
      validate = false;
    }
    else if (name.length() < 2)
    {
      validate = false;
    }
    else if (!StringUtilities.checkPattern("^(?![0-9])[a-z A-Z]{1,}$", name))
    {
      validate = false;
    }
    return validate;
  }

  public static boolean validateInitials(String initials)
  {
    boolean validate = true;
    if (StringUtilities.isBlank(initials))
    {
      validate = false;
    }
    else if (!StringUtilities.checkPattern("^(?![0-9])[a-z .A-Z]{1,}$", initials))
    {
      validate = false;
    }
    return validate;
  }

  public static boolean validatePrefix(String prefix)
  {
    boolean validate = true;
    if (StringUtilities.isNotBlank(prefix) && !StringUtilities.checkPattern("^(?![0-9])[a-z A-Z]{1,}$", prefix))
    {
      validate = false;
    }
    return validate;
  }

  public static boolean validateSurname(String surname)
  {
    boolean validate = true;
    if (StringUtilities.isBlank(surname))
    {
      validate = false;
    }
    else if (surname.length() < 2)
    {
      validate = false;
    }
    else if (!StringUtilities.checkPattern("^(?![0-9])[a-zA-Z ]{1,}$", surname))
    {
      validate = false;
    }
    return validate;
  }

  public static boolean validateBirthdate(String birthDate)
  {
    boolean validate = true;
    if (StringUtilities.isBlank(birthDate))
    {
      validate = false;
    }
    return validate;

  }

  public static boolean validateAddress(String address)
  {
    boolean validate = true;
    if (StringUtilities.isBlank(address))
    {
      validate = false;
    }
    else if (address.length() < 2)
    {
      validate = false;
    }
    else if (!StringUtilities.checkPattern("^(?![0-9])[a-zA-Z ]{1,}$", address))
    {
      validate = false;
    }
    return validate;
  }

  public static boolean validateNumber(String houseNumber)
  {
    boolean validate = true;
    if (StringUtilities.isBlank(houseNumber))
    {
      validate = false;
    }
    else if (!StringUtilities.checkPattern("^(?![A-Za-z])[1-9]{1}[0-9]{0,}$", houseNumber))
    {
      validate = false;
    }
    return validate;
  }

  public static boolean validateZipcode(String zipCode)
  {
    boolean validate = true;
    if (StringUtilities.isBlank(zipCode))
    {
      validate = false;
    }
    else if (!StringUtilities.checkPattern("^[0-9]{4}[a-z|A-Z]{2}$", zipCode))
    {
      validate = false;
    }
    return validate;
  }

  public static boolean validateCity(String city)
  {
    boolean validate = true;
    if (StringUtilities.isBlank(city))
    {
      validate = false;
    }
    else if (city.length() < 2)
    {
      validate = false;
    }
    else if (!StringUtilities.checkPattern("^(?![0-9])[a-zA-Z ]{1,}$", city))
    {
      validate = false;
    }
    return validate;
  }

}

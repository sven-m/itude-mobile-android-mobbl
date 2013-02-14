package com.itude.mobile.mobbl2.client.core.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

import com.itude.mobile.android.util.DateUtilities;
import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBAttributeDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDomainDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBFieldDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.exceptions.MBInvalidPathException;
import com.itude.mobile.mobbl2.client.core.controller.MBApplicationFactory;
import com.itude.mobile.mobbl2.client.core.controller.MBViewManager;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.services.MBLocalizationService;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.MBParseUtil;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilderFactory;

public class MBField extends MBComponent
    implements
      OnClickListener,
      OnItemSelectedListener,
      OnCheckedChangeListener,
      TextWatcher,
      OnKeyListener
{
  private static final Pattern  NUMBERPATTERN            = Pattern.compile("\\[[0-9]+\\]");

  private MBAttributeDefinition _attributeDefinition;
  private boolean               _domainDetermined;
  private MBDomainDefinition    _domainDefinition;
  private String                _translatedPath;
  private String                _label;
  private String[]              _labelAttrs;
  private String                _source;
  private String                _dataType;
  private String                _formatMask;
  private String                _alignment;
  private String                _valueIfNil;
  private boolean               _hidden;
  private int                   _width;
  private int                   _height;
  private String                _hint;

  private String                _cachedValue             = null;
  private String                _cachedUntranslatedValue = null;
  private boolean               _cachedValueSet          = false;

  public MBField(MBDefinition definition, MBDocument document, MBComponentContainer parent)
  {
    super(definition, document, parent);

    _attributeDefinition = null;
    _domainDetermined = false;

    MBFieldDefinition fieldDef = (MBFieldDefinition) getDefinition();

    if (fieldDef.getWidth() != null)
    {
      try
      {
        String theWidth = substituteExpressions(fieldDef.getWidth());
        if (theWidth != null && theWidth.length() > 0)
        {
          setWidth(Integer.parseInt(theWidth));
        }
        else
        {
          Log.w(Constants.APPLICATION_NAME, fieldDef.getWidth() + " could not be parsed als int");
        }

      }
      catch (NumberFormatException e)
      {
        Log.d(Constants.APPLICATION_NAME, fieldDef.toString(), e);
      }
    }
    if (fieldDef.getHeight() != null)
    {
      try
      {
        setHeight(Integer.parseInt(substituteExpressions(fieldDef.getHeight())));
      }
      catch (NumberFormatException e)
      {
        Log.d(Constants.APPLICATION_NAME, fieldDef.toString(), e);
      }
    }

    setStyle(substituteExpressions(fieldDef.getStyle()));
    setDataType(substituteExpressions(fieldDef.getDataType()));
    setValueIfNil(substituteExpressions(fieldDef.getValueIfNil()));
    setLabel(substituteExpressions(fieldDef.getLabel()));
    String labelAttrs = fieldDef.getLabelAttrs();
    setLabelAttrs((labelAttrs != null) ? labelAttrs.split(",") : null);
    setSource(substituteExpressions(fieldDef.getSource()));
    setFormatMask(substituteExpressions(fieldDef.getFormatMask()));
    setAlignment(substituteExpressions(fieldDef.getAlignment()));
    setHidden(Boolean.parseBoolean(substituteExpressions(fieldDef.getHidden())));
    setHint(substituteExpressions(fieldDef.getHint()));

    MBApplicationFactory.getInstance().getPageConstructor().onConstructedField(this);
  }

  @Override
  public View buildView()
  {
    return MBViewBuilderFactory.getInstance().getFieldViewBuilder().buildFieldView(this);
  }

  public int getWidth()
  {
    return _width;
  }

  public void setWidth(int width)
  {
    _width = width;
  }

  public int getHeight()
  {
    return _height;
  }

  public void setHeight(int height)
  {
    _height = height;
  }

  public String getLabel()
  {
    if (getLabelAttrs() == null)
    {
      return MBLocalizationService.getInstance().getTextForKey(_label);
    }
    else
    {
      return MBLocalizationService.getInstance().getText(_label, (Object[]) getLabelAttrs());
    }
  }

  public void setLabel(String label)
  {
    _label = label;
  }

  public void setLabelAttrs(String[] labelAttrs)
  {
    if (labelAttrs != null && labelAttrs.length > 0)
    {
      String[] substitutes = new String[labelAttrs.length];
      for (int i = 0; i < substitutes.length; i++)
      {
        String attr = labelAttrs[i];
        String substituded = substituteExpressions(attr);
        substitutes[i] = (substituded == null) ? getValueIfNil() : substituded;
      }
      labelAttrs = substitutes;
    }
    _labelAttrs = labelAttrs;
  }

  public String[] getLabelAttrs()
  {
    return _labelAttrs;
  }

  public String getSource()
  {
    return _source;
  }

  public void setSource(String source)
  {
    _source = source;
  }

  public String getDataType()
  {
    String result = _dataType;
    if (result == null)
    {
      MBDomainDefinition domain = getDomain();
      if (domain != null) result = getDomain().getType();
      if (result == null)
      {
        MBAttributeDefinition ad = getAttributeDefinition();
        if (ad != null) result = getAttributeDefinition().getType();
      }
    }
    return result;
  }

  public boolean isNumeric()
  {
    String tp = getDataType();

    return "int".equals(tp) || "float".equals(tp) || "double".equals(tp);
  }

  public void setDataType(String dataType)
  {
    _dataType = dataType;
  }

  public String getFormatMask()
  {
    return _formatMask;
  }

  public void setFormatMask(String formatMask)
  {
    _formatMask = formatMask;
  }

  public String getAlignment()
  {
    return _alignment;
  }

  public void setAlignment(String alignment)
  {
    _alignment = alignment;
  }

  public String getUntranslatedValueIfNil()
  {
    return _valueIfNil;
  }

  public String getValueIfNil()
  {
    return MBLocalizationService.getInstance().getTextForKey(_valueIfNil);
  }

  public void setValueIfNil(String valueIfNil)
  {
    _valueIfNil = valueIfNil;
  }

  public boolean isHidden()
  {
    return _hidden;
  }

  public void setHidden(boolean hidden)
  {
    _hidden = hidden;
  }

  public String getHint()
  {
    return _hint;
  }

  public void setHint(String hint)
  {
    _hint = hint;
  }

  /**
   * Returns the value of this field, please note this is an EXPENSIVE call.
   * Try not to call it repeatedly, but cache the value.
   * @see #getValuesForDisplay()
   */
  public String getValue()
  {
    calculateValueIfNeeded();
    return _cachedValue;
  }

  public String getUntranslatedValue()
  {
    calculateValueIfNeeded();
    return _cachedUntranslatedValue;
  }

  private void calculateValueIfNeeded()
  {
    if (!_cachedValueSet)
    {
      String result = null;

      if (getDocument() != null)
      {
        Object value = getDocument().getValueForPath(getAbsoluteDataPath());
        if (value instanceof String) result = (String) value;
        else if (value != null) result = value.toString();

        _cachedUntranslatedValue = result;
        // don't use the getter here!
        if (_dataType == null)
        {
          result = MBLocalizationService.getInstance().getTextForKey(result);
        }
      }
      _cachedValue = result;
      _cachedValueSet = true;

    }

  }

  public void setValue(boolean value)
  {
    setValue(value ? Constants.C_TRUE : Constants.C_FALSE);
  }

  public void setValue(String value)
  {
    String path = getAbsoluteDataPath();
    String originalValue = (String) getDocument().getValueForPath(path);

    boolean valueChanged = (value == null && originalValue != null) || (value != null && originalValue == null)
                           || !value.equals(originalValue);

    if (valueChanged && notifyValueWillChange(value, originalValue, path))
    {
      getDocument().setValue(value, path);
      notifyValueChanged(value, originalValue, path);
    }

  }

  public String getPath()
  {
    return ((MBFieldDefinition) getDefinition()).getPath();
  }

  @Override
  public String getType()
  {
    return ((MBFieldDefinition) getDefinition()).getDisplayType();
  }

  public String dataType()
  {
    return null;
  }

  public String getText()
  {
    return MBLocalizationService.getInstance().getTextForKey(((MBFieldDefinition) getDefinition()).getText());
  }

  public String getOutcomeName()
  {
    return ((MBFieldDefinition) getDefinition()).getOutcomeName();
  }

  public boolean required()
  {
    return false;
  }

  public MBDomainDefinition getDomain()
  {
    if (!_domainDetermined)
    {
      MBAttributeDefinition attrDef = getAttributeDefinition();
      if (attrDef != null)
      {
        _domainDefinition = MBMetadataService.getInstance().getDefinitionForDomainName(attrDef.getType(), false);
        _domainDetermined = true;
      }
    }
    return _domainDefinition;
  }

  public MBAttributeDefinition getAttributeDefinition()
  {
    if (_attributeDefinition == null)
    {
      String path = NUMBERPATTERN.matcher(getAbsoluteDataPath()).replaceAll("");
      if (path == null)
      {
        return null;
      }
      try
      {
        _attributeDefinition = getDocument().getDefinition().getAttributeWithPath(path);
      }
      catch (MBInvalidPathException e)
      {
        // Button outcomes do not map to an attribute
        Log.d(Constants.APPLICATION_NAME, "MBField.getAttributeDefinition() with path=" + path
                                          + " does not map to an attribute. Probably an outcomePath for a Button.");
      }
    }

    return _attributeDefinition;
  }

  // This will translate any expression that are part of the path to their actual values
  @Override
  public void translatePath()
  {
    _translatedPath = substituteExpressions(getAbsoluteDataPath());
  }

  @Override
  public String getComponentDataPath()
  {
    String path = ((MBFieldDefinition) getDefinition()).getPath();
    if (path == null || "".equals(path))
    {
      return null;
    }
    return substituteExpressions(path);
  }

  @Override
  public String getAbsoluteDataPath()
  {
    if (_translatedPath != null)
    {
      return _translatedPath;
    }

    return super.getAbsoluteDataPath();
  }

  private String formatValue(String fieldValue)
  {
    boolean fieldValueSameAsNilValue = fieldValue.equals(getValueIfNil());

    final Locale locale = MBLocalizationService.getInstance().getLocale();

    try
    {

      if (getFormatMask() != null && getDataType().equals("dateTime"))
      {
        // Get a date from a xml-dateFormat
        String xmlDate = fieldValue;

        // Formats the date depending on the current date. 
        if (getFormatMask().equals("dateOrTimeDependingOnCurrentDate"))
        {
          fieldValue = DateUtilities.formatDateDependingOnCurrentDate(locale, xmlDate);
        }
        else
        {
          Date date = DateUtilities.dateFromXML(xmlDate);

          SimpleDateFormat df = new SimpleDateFormat(getFormatMask());
          fieldValue = df.format(date);
        }

      }
      else if (!fieldValueSameAsNilValue && getDataType().equals("numberWithTwoDecimals"))
      {
        fieldValue = StringUtil.formatNumberWithTwoDecimals(locale, fieldValue);
      }
      else if (!fieldValueSameAsNilValue && getDataType().equals("numberWithThreeDecimals"))
      {
        fieldValue = StringUtil.formatNumberWithThreeDecimals(locale, fieldValue);
      }
      else if (!fieldValueSameAsNilValue && getDataType().equals("priceWithTwoDecimals"))
      {
        fieldValue = StringUtil.formatPriceWithTwoDecimals(locale, fieldValue);
      }
      else if (!fieldValueSameAsNilValue && getDataType().equals("priceWithThreeDecimals"))
      {
        fieldValue = StringUtil.formatPriceWithThreeDecimals(locale, fieldValue);
      }
      else if (!fieldValueSameAsNilValue && getDataType().equals("priceWithFourDecimals"))
      {
        fieldValue = StringUtil.formatNumberWithDecimals(locale, fieldValue, 4);
      }
      else if (getDataType().equals("volume"))
      {
        fieldValue = StringUtil.formatVolume(locale, fieldValue);
      }
      else if (getDataType().equals("percentageWithTwoDecimals"))
      {
        fieldValue = StringUtil.formatPercentageWithTwoDecimals(locale, fieldValue);
      }

    }
    catch (NumberFormatException nfe)
    {
      throw new NumberFormatException("Unable to format value for field: " + getName());
    }

    // CURRENCY Symbols
    if ("EURO".equals(getStyle()))
    {
      fieldValue = "€ " + fieldValue;
    }

    return fieldValue;
  }

  // Apply a formatmask
  public String getFormattedValue()
  {
    return formatValue(getValue());
  }

  // Returns a path that has indexed expressions evaluated (translated) i.e. something like myelement[someattr='xx'] -> myelement[12]
  // for the current document; where the 12th element is matched
  public String evaluatedDataPath()
  {
    String path = getAbsoluteDataPath();
    if (path != null && path.length() > 0 && path.contains("="))
    {
      // Now translate the index expressions like [someAttr=='x' and someOther=='y'] into [idx]
      // We can only do this if the row that matches the expression does exist!
      List<String> components = new ArrayList<String>();
      String value = (String) getDocument().getValueForPath(path, components);

      // Now glue together the components to make a full path again:
      String result = "";
      for (String part : components)
      {
        if (!part.endsWith(":"))
        {
          result += "/";
        }
        result += part;
      }
      if (value != null)
      {
        return result;
      }

    }

    return path;
  }

  @Override
  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    try
    {

      StringUtil.appendIndentString(appendToMe, level).append("<MBField ").append(attributeAsXml("value", getValue())).append(" ")
          .append(attributeAsXml("path", getAbsoluteDataPath())).append(" ").append(attributeAsXml("style", getStyle())).append(" ")
          .append(attributeAsXml("label", getLabel())).append(" ").append(attributeAsXml("type", getType())).append(" ")
          .append(attributeAsXml("dataType", getDataType())).append(" ").append(attributeAsXml("outcomeName", getOutcomeName()))
          .append(" ").append(attributeAsXml("formatMask", getFormatMask())).append(" ")
          .append(attributeAsXml("alignment", getAlignment())).append(" ").append(attributeAsXml("valueIfNil", getValueIfNil()))
          .append(" ").append(" width='").append(getWidth()).append("' height='").append(getHeight()).append(" hint='").append(getHint())
          .append("'/>\n");
    }
    catch (Exception e)
    {
      // dead code?
      appendToMe.append("<MBField errorInDefinition='" + e.getClass().getSimpleName() + ", " + e.getCause() + "'/>\n");
      Log.d(Constants.APPLICATION_NAME, e.getMessage(), e);
    }

    return appendToMe;
  }

  @Override
  public String toString()
  {
    StringBuffer rt = new StringBuffer();
    return asXmlWithLevel(rt, 0).toString();
  }

  // android.view.View.OnClickListener method
  @Override
  public void onClick(View v)
  {

    // Force the onscreen keyboard to be hidden
    MBViewManager.getInstance().hideSoftKeyBoard(v);

    // When this is being used as a ToggleButton
    if (v instanceof ToggleButton)
    {
      ToggleButton tb = (ToggleButton) v;

      if (tb.isChecked())
      {
        setValue(true);
      }
      else
      {
        setValue(false);
      }
    }
    else
    {
      // When this is being used as a Button
      handleOutcome(getOutcomeName(), getAbsoluteDataPath());
    }
  }

  // OnItemSelectedListener Methods
  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
  {
    if (getDomain() != null && !getDomain().getDomainValidators().isEmpty())
    {
      String value = getDomain().getDomainValidators().get(position).getValue();
      setValue(value);
    }
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent)
  {
  }

  //OnCheckedChangeListener Method
  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
  {
    if (isChecked)
    {
      setValue(true);
    }
    else
    {
      setValue(false);
    }
  }

  @Override
  public void afterTextChanged(Editable s)
  {
  }

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after)
  {
  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count)
  {
    String textFieldValue = s.toString();

    // Representation to the User can be in Dutch (comma for decimal seperator). We need to check this before we store the value
    if (MBLocalizationService.getInstance().getLocaleCode().equals(Constants.C_LOCALE_CODE_DUTCH)
        || MBLocalizationService.getInstance().getLocaleCode().equals(Constants.C_LOCALE_CODE_ITALIAN))
    {
      if (getDataType().equals("double") || getDataType().equals("float"))
      {
        Float doubleValue = MBParseUtil.floatValueDutch(textFieldValue);
        if (doubleValue != null)
        {
          textFieldValue = Float.toString(doubleValue);
        }
      }
    }

    setValue(textFieldValue);
  }

  @Override
  public boolean onKey(View v, int keyCode, KeyEvent event)
  {
    if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK)
    {
      MBViewManager.getInstance().onKeyDown(keyCode, event);
      return true;
    }
    else if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_MENU)
    {
      MBViewManager.getInstance().onMenuKeyDown(keyCode, event, v);
      return true;
    }
    return false;
  }

  /**
   * Optimized method that does all formatting etc, it makes sure
   * not to call expensive methods repeatedly.
   * 
   * @return the value to use for display on a view
   */
  public String getValuesForDisplay()
  {
    // note that getLabel does an expensive localization call
    // therefore we directly check our member variable to determine
    // if label is null
    String value = getValueForDisplay();

    if (_label != null)
    {
      String label = getLabel();
      if (StringUtil.isNotBlank(value))
      {
        return label + " " + value;
      }
      else
      {
        return label;
      }

    }

    // getValue is not a simple getter, so make sure it isn't called
    // unneeded
    if (StringUtil.isNotBlank(value))
    {
      return value;
    }

    if (getValueIfNil() != null) return getValueIfNil();

    return null;
  }

  private String getValueForDisplay()
  {
    //getValue is not a simple getter, so make sure it isn't called
    // unneeded
    String value = getValue();
    if (value != null)
    {
      if (getDataType() != null)
      {
        value = formatValue(value);
      }
    }

    return value;
  }
}

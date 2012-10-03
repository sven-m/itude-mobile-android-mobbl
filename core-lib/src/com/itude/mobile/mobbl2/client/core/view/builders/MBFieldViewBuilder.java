package com.itude.mobile.mobbl2.client.core.view.builders;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils.TruncateAt;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDomainValidatorDefinition;
import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.controller.MBViewManager;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.model.MBDocumentDiff;
import com.itude.mobile.mobbl2.client.core.services.MBLocalizationService;
import com.itude.mobile.mobbl2.client.core.services.MBResourceService;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.DateUtilities;
import com.itude.mobile.mobbl2.client.core.util.MBParseUtil;
import com.itude.mobile.mobbl2.client.core.util.MBScreenUtilities;
import com.itude.mobile.mobbl2.client.core.util.MathUtilities;
import com.itude.mobile.mobbl2.client.core.util.StringUtilities;
import com.itude.mobile.mobbl2.client.core.util.UniqueIntegerGenerator;
import com.itude.mobile.mobbl2.client.core.view.MBDateField;
import com.itude.mobile.mobbl2.client.core.view.MBField;
import com.itude.mobile.mobbl2.client.core.view.MBPanel;

public class MBFieldViewBuilder extends MBViewBuilder
{

  public View buildFieldView(MBField field)
  {
    View view = null;

    if (Constants.C_FIELD_INPUT.equals(field.getType())) view = buildTextField(field);
    else if (Constants.C_FIELD_PASSWORD.equals(field.getType())) view = buildTextField(field);
    else if (Constants.C_FIELD_BUTTON.equals(field.getType())) view = buildButton(field);
    else if (Constants.C_FIELD_IMAGE.equals(field.getType())) view = buildImage(field);
    else if (Constants.C_FIELD_IMAGEBUTTON.equals(field.getType())) view = buildImageButton(field);
    else if (Constants.C_FIELD_LABEL.equals(field.getType())) view = buildLabel(field);
    else if (Constants.C_FIELD_SUBLABEL.equals(field.getType())) view = buildSubLabel(field);
    else if (Constants.C_FIELD_DROPDOWNLIST.equals(field.getType())) view = buildDropdownList(field);
    else if (Constants.C_FIELD_CHECKBOX.equals(field.getType())) view = buildCheckBox(field);
    else if (Constants.C_FIELD_MATRIXTITLE.equals(field.getType())) view = buildMatrixTitle(field);
    else if (Constants.C_FIELD_MATRIXDESCRIPTION.equals(field.getType())) view = buildMatrixDescription(field);
    else if (Constants.C_FIELD_MATRIXCELL.equals(field.getType())) view = buildMatrixCell(field);
    else if (Constants.C_FIELD_TEXT.equals(field.getType())) view = buildTextView(field);
    else if (Constants.C_FIELD_WEB.equals(field.getType())) view = buildWebView(field);
    else if (Constants.C_FIELD_DATE.equals(field.getType())) view = buildDateOrTimeView(field, Constants.C_FIELD_DATE);
    else if (Constants.C_FIELD_TIME.equals(field.getType())) view = buildDateOrTimeView(field, Constants.C_FIELD_TIME);
    else
    {
      Log.w(Constants.APPLICATION_NAME, "MBFieldViewBuilder.buildFieldView(): Failed to build unsupported view type " + field.getType());
    }
    field.attachView(view);
    return view;
  }

  public View buildButton(MBField field)
  {
    MarginLayoutParams buttonParams = new MarginLayoutParams(MarginLayoutParams.WRAP_CONTENT, MarginLayoutParams.WRAP_CONTENT);

    buttonParams.setMargins(MBScreenUtilities.FIVE, 0, MBScreenUtilities.FIVE, 0);
    Button button = new Button(MBApplicationController.getInstance().getBaseContext());
    button.setLayoutParams(buttonParams);

    String defaultValue = field.getLabel();

    String path = field.getPath();
    if (StringUtilities.isNotBlank(path))
    {
      String fieldValue = field.getValue();
      if (StringUtilities.isNotBlank(fieldValue))
      {
        defaultValue = fieldValue;
      }
      else if (StringUtilities.isNotBlank(field.getValueIfNil()))
      {
        defaultValue = field.getValueIfNil();
      }
    }
    button.setText(defaultValue);
    button.setOnClickListener(field);
    button.setOnKeyListener(field);

    String source = field.getSource();
    if (source != null)
    {
      Drawable drawable = MBResourceService.getInstance().getImageByID(source);
      button.setBackgroundDrawable(drawable);
    }
    else
    {
      getStyleHandler().styleButton(button, field);
    }
    return button;
  }

  public View buildImage(MBField field)
  {
    String source = field.getSource();
    String path = field.getPath();
    if (StringUtilities.isBlank(source) && StringUtilities.isBlank(path))
    {
      Log.w(Constants.APPLICATION_NAME, "Source or Path is null or empty for field");
      return null;
    }

    ImageView image = new ImageView(MBApplicationController.getInstance().getBaseContext());
    if (StringUtilities.isNotBlank(field.getOutcomeName()))
    {
      image.setOnClickListener(field);
    }

    Drawable drawable = null;
    if (StringUtilities.isNotBlank(source))
    {
      drawable = MBResourceService.getInstance().getImageByID(source);
    }
    else
    {
      drawable = MBResourceService.getInstance().getImageByURL(field.getFormattedValue());
    }
    image.setBackgroundDrawable(drawable);

    getStyleHandler().styleImage(image);
    getStyleHandler().styleImage(image, field.getStyle());

    return image;

  }

  public View buildImageButton(MBField field)
  {
    String source = field.getSource();
    if (StringUtilities.isBlank(source))
    {
      Log.w(Constants.APPLICATION_NAME, "Source is null or empty for field");
      return null;
    }

    ImageButton button = new ImageButton(MBApplicationController.getInstance().getBaseContext());
    button.setOnClickListener(field);
    button.setOnKeyListener(field);

    Drawable drawable = MBResourceService.getInstance().getImageByID(source);
    button.setBackgroundDrawable(drawable);

    return button;
  }

  public View buildLabel(MBField field)
  {
    String value = field.getValuesForDisplay();

    TextView label = buildTextViewWithValue(value);
    label.setSingleLine(true);
    label.setEllipsize(TruncateAt.END);
    getStyleHandler().styleLabel(label, field);

    return label;
  }

  public View buildMatrixDescription(MBField field)
  {
    String value = field.getValuesForDisplay();

    // Title TextView
    TextView label = buildTextViewWithValue(value);

    getStyleHandler().styleMatrixRowDescription(label, field);

    return label;
  }

  public View buildMatrixTitle(MBField field)
  {
    String value = field.getValuesForDisplay();

    // Title TextView
    TextView label = buildTextViewWithValue(value);

    // Decide which styling to apply
    if (((MBPanel) field.getParent()).getType().equals(Constants.C_MATRIXHEADER))
    {
      getStyleHandler().styleMatrixHeaderTitle(label);
    }
    else if (((MBPanel) field.getParent()).getType().equals(Constants.C_MATRIXROW))
    {
      getStyleHandler().styleMatrixRowTitle(label, field);
    }

    // Make sure only 1 line is visible when creating the title
    label.setSingleLine(true);

    return label;
  }

  public View buildMatrixCell(MBField field)
  {
    String value = field.getValuesForDisplay();

    // Title TextView
    TextView label = buildTextViewWithValue(value);
    label.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1));
    label.setSingleLine();

    // default styling
    getStyleHandler().styleMatrixCell(field, label);

    MBPanel parent = (MBPanel) field.getFirstParentOfKind(MBPanel.class);
    if (parent != null && !parent.isDiffableMaster()) parent = parent.getFirstParentPanelWithType("MATRIX");
    if (parent != null && !parent.isDiffableMaster()) parent = null;

    MBDocumentDiff documentDiff = field.getPage().getDocumentDiff();

    if (parent != null && field.isDiffablePrimary() || field.isDiffableSecondary())
    {
      double styleDelta = 0;
      double styleValue = 0;

      String primaryPath = parent.getDiffablePrimaryPath();
      String primaryValueString = field.getDocument().getValueForPath(primaryPath);
      Double primaryValue = MBParseUtil.doubleValueDutch(primaryValueString);

      if (primaryPath != null)
      {
        if (documentDiff == null || !documentDiff.isChanged() || !documentDiff.isChanged(primaryPath))
        {
          String markerValueString = field.getDocument().getValueForPath(parent.getDiffableMarkerPath());
          Double markerValue = MBParseUtil.doubleValueDutch(markerValueString);

          if (primaryValue != null && markerValue != null && !Double.isNaN(primaryValue) && !Double.isNaN(markerValue))
          {
            styleValue = MathUtilities.truncate(primaryValue - markerValue);
          }
        }
        else
        {
          String valueOfBForPath = documentDiff.valueOfBForPath(primaryPath);

          Double valueB = MBParseUtil.doubleValueDutch(valueOfBForPath);

          if (primaryValue != null && valueB != null && !Double.isNaN(primaryValue) && !Double.isNaN(valueB))
          {
            styleDelta = MathUtilities.truncate(primaryValue - valueB);
          }
        }
      }
      getStyleHandler().styleChangedValue(label, styleValue, styleDelta);
    }

    return label;
  }

  private TextView buildTextViewWithValue(String value)
  {
    return buildTextViewWithValue(value, false);
  }

  private TextView buildTextViewWithValue(String value, boolean isHtml)
  {
    TextView label = new TextView(MBApplicationController.getInstance().getBaseContext());
    if (value == null)
    {
      // If the value is null we don't want it to be parsed as HTML since that will break the application
      label.setText("");
    }
    else
    {
      label.setEllipsize(TruncateAt.END);

      if (isHtml)
      {
        label.setText(Html.fromHtml(value));
      }
      else
      {
        label.setText(value);
      }
    }

    getStyleHandler().styleLabel(label, null);

    return label;
  }

  public View buildSubLabel(MBField field)
  {
    String value = field.getValuesForDisplay();

    TextView label = buildTextViewWithValue(value);
    getStyleHandler().styleSubLabel(label);
    getStyleHandler().styleSubLabel(label, field.getStyle());

    return label;
  }

  public View buildTextField(MBField field)
  {
    Context context = MBApplicationController.getInstance().getBaseContext();

    EditText inputField = new EditText(context);

    // Default inputfield should be single lined

    String hint = field.getHint();
    if (StringUtilities.isNotBlank(hint))
    {
      getStyleHandler().styleHint(inputField);
      inputField.setHint(MBLocalizationService.getInstance().getTextForKey(hint));
      // http://code.google.com/p/android/issues/detail?id=7252
      inputField.setEllipsize(TruncateAt.END);
    }

    inputField.setSingleLine();
    inputField.setOnKeyListener(field);
    getStyleHandler().styleInputfieldBackgroundWithName(inputField, null);

    inputField.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1));
    String defaultValue = "";
    if (field.getPath() != null)
    {
      String fieldValue = field.getValue();
      if (fieldValue != null)
      {
        defaultValue = fieldValue;
      }
      else if (field.getValueIfNil() != null)
      {
        defaultValue = field.getValueIfNil();
      }
    }

    // Set type of value (so different keyboard will be shown)
    if (field.getDataType() != null && field.getDataType().equals(Constants.C_FIELD_DATATYPE_INT))
    {
      inputField.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);

      try
      {
        Integer.parseInt(defaultValue);
        inputField.setText(defaultValue);
      }
      catch (Exception e)
      {
        Log.w(Constants.APPLICATION_NAME, "Inputfield with type \"" + field.getDataType() + "\" cannot have the value \"" + defaultValue
                                          + "\"", e);
      }
    }
    else if (field.getDataType() != null
             && (field.getDataType().equals(Constants.C_FIELD_DATATYPE_DOUBLE) || field.getDataType()
                 .equals(Constants.C_FIELD_DATATYPE_FLOAT)))
    {
      inputField.setKeyListener(StringUtilities.getCurrencyNumberKeyListener());

      try
      {
        Double.parseDouble(defaultValue);
        inputField.setText(defaultValue);
      }
      catch (Exception e)
      {
        Log.w(Constants.APPLICATION_NAME, "Inputfield with type \"" + field.getDataType() + "\" cannot have the value \"" + defaultValue
                                          + "\"", e);
      }

      // Depending on the localeCode-settings in the applicationProperties, we want to display a comma or a dot as decimal seperator for floats and doubles
      if (MBLocalizationService.getInstance().getLocaleCode() != null
          && (MBLocalizationService.getInstance().getLocaleCode().equals(Constants.C_LOCALE_CODE_DUTCH) || MBLocalizationService
              .getInstance().getLocaleCode().equals(Constants.C_LOCALE_CODE_ITALIAN)))
      {

        if (inputField.getText().toString().length() > 0)
        {
          String textFieldText = StringUtilities.formatNumberWithOriginalNumberOfDecimals(inputField.getText().toString());
          inputField.setText(textFieldText);
        }

      }

    }
    else
    {
      inputField.setText(defaultValue);
    }

    // Add TextChangedListener to EditText so changes will be saved to the document
    inputField.addTextChangedListener(field);

    // Set type of field visualization
    if (field.getType() != null && field.getType().equals(Constants.C_FIELD_PASSWORD))
    {
      inputField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
      inputField.setTransformationMethod(new PasswordTransformationMethod());
    }

    getStyleHandler().styleTextfield(inputField, field);

    if (field.getLabel() != null && field.getLabel().length() > 0)
    {
      inputField.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 50));

      LinearLayout labelLayout = new LinearLayout(context);
      labelLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
      labelLayout.setOrientation(LinearLayout.HORIZONTAL);

      TextView label = buildTextViewWithValue(field.getLabel());
      getStyleHandler().styleLabel(label, field);
      label.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 50));

      labelLayout.addView(label);
      labelLayout.addView(inputField);

      return labelLayout;
    }

    return inputField;
  }

  public View buildDropdownList(final MBField field)
  {
    Context context = MBApplicationController.getInstance().getViewManager();

    int selected = -1;

    Spinner dropdownList = new Spinner(context);
    dropdownList.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1));

    getStyleHandler().styleSpinner(dropdownList);

    ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(context, android.R.layout.simple_spinner_item)
    {

      @Override
      public View getView(int position, View convertView, ViewGroup parent)
      {
        View view = super.getView(position, convertView, parent);
        if (view instanceof TextView)
        {
          TextView textView = (TextView) view;
          getStyleHandler().styleLabel(textView, null);
        }
        return view;
      }
    };

    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    String fieldValue = field.getValue();
    if (field.getDomain() != null)
    {
      for (int i = 0; i < field.getDomain().getDomainValidators().size(); i++)
      {
        MBDomainValidatorDefinition domDef = field.getDomain().getDomainValidators().get(i);
        adapter.add(MBLocalizationService.getInstance().getTextForKey(domDef.getTitle()));

        String domDefValue = domDef.getValue();
        if ((fieldValue != null && fieldValue.equals(domDefValue))
            || (fieldValue == null && field.getValueIfNil() != null && field.getValueIfNil().equals(domDefValue)))
        {
          selected = i;
        }
      }
    }

    dropdownList.setAdapter(adapter);

    if (selected > -1)
    {
      dropdownList.setSelection(selected);
    }

    dropdownList.setOnItemSelectedListener(field);
    dropdownList.setOnKeyListener(field);

    if (field.getLabel() != null && field.getLabel().length() > 0)
    {
      dropdownList.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 50));

      LinearLayout labelLayout = new LinearLayout(context);
      labelLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
      labelLayout.setOrientation(LinearLayout.HORIZONTAL);

      TextView label = buildTextViewWithValue(field.getLabel());
      label.setText(field.getLabel());
      label.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 50));
      getStyleHandler().styleLabel(label, field);

      labelLayout.addView(label);
      labelLayout.addView(dropdownList);

      return labelLayout;
    }

    return dropdownList;
  }

  private View buildDateOrTimeView(final MBField field, final String type)
  {
    final Context context = MBApplicationController.getInstance().getBaseContext();

    final MBStyleHandler styleHandler = getStyleHandler();

    final MBDocument doc = field.getDocument();
    final String path = field.getPath();

    final MBDateField df = new MBDateField();

    // Create our container which will fill the whole width
    LinearLayout container = new LinearLayout(context);
    container.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
    container.setGravity(Gravity.CENTER_VERTICAL);

    // Add our label (if one exists)
    TextView label = buildTextViewWithValue(field.getLabel());
    label.setGravity(Gravity.CENTER_VERTICAL);
    label.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 50));

    styleHandler.styleLabel(label, field);

    final TextView value = new TextView(context);
    value.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 50));
    value.setGravity(Gravity.CENTER_VERTICAL);

    // Find out if we have previously set a time
    String dateTimeString = doc.getValueForPath(path);
    String valueLabelText = "";

    String nillValue = field.getValueIfNil();
    if (StringUtilities.isNotBlank(nillValue))
    {
      valueLabelText = field.getValueIfNil();
    }

    if (StringUtilities.isNotBlank(dateTimeString))
    {
      df.setTime(dateTimeString);
      valueLabelText = DateUtilities.dateToString(df.getCalender().getTime(), field.getFormatMask());
    }

    if (StringUtilities.isNotBlank(valueLabelText))
    {
      value.setText(valueLabelText);
    }

    styleHandler.styleDateOrTimeSelectorValue(value, field);

    String source = field.getSource();
    if (StringUtilities.isNotBlank(source))
    {
      Drawable drawable = MBResourceService.getInstance().getImageByID(source);
      value.setBackgroundDrawable(drawable);
    }

    value.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {

        if (Constants.C_FIELD_TIME.equals(type))
        {
          // Time
          final TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener()
          {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute)
            {
              df.setTime(hourOfDay, minute);

              field.setValue(DateUtilities.dateToString(df.getCalender().getTime()));

              // Update our label
              value.setText(DateUtilities.dateToString(df.getCalender().getTime(), field.getFormatMask()));
            }
          };
          TimePickerDialog timePickerDialog = new TimePickerDialog(MBViewManager.getInstance(), listener, df
              .getHourOfDay(), df.getMinute(), true);

          timePickerDialog.setTitle(field.getLabel());
          styleHandler.styleTimePickerDialog(timePickerDialog, field);

          timePickerDialog.show();
        }
        else
        {
          final DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener()
          {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
              df.setDate(year, monthOfYear, dayOfMonth);

              field.setValue(DateUtilities.dateToString(df.getCalender().getTime()));

              // Update our label
              value.setText(DateUtilities.dateToString(df.getCalender().getTime(), field.getFormatMask()));

            }
          };

          DatePickerDialog datePickerDialog = new DatePickerDialog(MBViewManager.getInstance(), listener, df.getYear(),
              df.getMonth(), df.getDay());
          datePickerDialog.setTitle(field.getLabel());
          styleHandler.styleDatePickerDialog(datePickerDialog, field);
          styleHandler.styleDatePickerDialog(datePickerDialog, value, field);

          datePickerDialog.show();
        }
      }
    });

    container.addView(label);
    container.addView(value);

    return container;
  }

  public View buildCheckBox(MBField field)
  {
    String value = field.getValue();
    String valueIfNil = field.getValueIfNil();
    boolean checked = false;

    if ((value != null && value.equalsIgnoreCase("TRUE")) || (valueIfNil != null && valueIfNil.equalsIgnoreCase("TRUE")))
    {
      checked = true;
    }

    Context context = MBApplicationController.getInstance().getBaseContext();

    final CheckBox checkBox = new CheckBox(context);
    checkBox.setId(UniqueIntegerGenerator.getId());
    checkBox.setChecked(checked);
    checkBox.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    checkBox.setOnCheckedChangeListener(field);
    checkBox.setOnKeyListener(field);

    getStyleHandler().styleCheckBox(checkBox);

    RelativeLayout container = new RelativeLayout(context);
    RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
        RelativeLayout.LayoutParams.WRAP_CONTENT);
    container.setLayoutParams(rlParams);
    container.setOnClickListener(new OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        checkBox.toggle();
      }
    });

    if (field.getLabel() != null && field.getLabel().length() > 0)
    {
      RelativeLayout.LayoutParams cbParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
          RelativeLayout.LayoutParams.WRAP_CONTENT);
      cbParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
      checkBox.setLayoutParams(cbParams);

      RelativeLayout.LayoutParams labelParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
          RelativeLayout.LayoutParams.WRAP_CONTENT);
      labelParams.addRule(RelativeLayout.LEFT_OF, checkBox.getId());
      labelParams.addRule(RelativeLayout.CENTER_VERTICAL);

      TextView label = buildTextViewWithValue(field.getLabel());
      label.setLayoutParams(labelParams);
      getStyleHandler().styleLabel(label, field);

      container.addView(label);
    }

    container.addView(checkBox);

    return container;
  }

  public View buildTextView(MBField field)
  {
    String value = field.getValuesForDisplay();

    // Title TextView
    TextView returnView = buildTextViewWithValue(value, true);
    returnView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    returnView.setEllipsize(null);

    if (field.getAlignment() != null)
    {
      if (field.getAlignment().equals(Constants.C_ALIGNMENT_RIGHT))
      {
        returnView.setGravity(Gravity.RIGHT);
      }
      else if (field.getAlignment().equals(Constants.C_ALIGNMENT_LEFT))
      {
        returnView.setGravity(Gravity.LEFT);
      }
      else if (field.getAlignment().equals(Constants.C_ALIGNMENT_CENTER_VERTICAL))
      {
        returnView.setGravity(Gravity.CENTER_VERTICAL);
      }
      else if (field.getAlignment().equals(Constants.C_ALIGNMENT_CENTER))
      {
        returnView.setGravity(Gravity.CENTER);
      }
    }

    getStyleHandler().styleTextView(returnView, field);

    return returnView;
  }

  private View buildWebView(MBField field)
  {
    WebView webView = new WebView(MBApplicationController.getInstance().getViewManager());
    webView.setScrollContainer(false);

    if (StringUtilities.isNotBlank(field.getSource()))
    {
      webView.setOnTouchListener(new OnTouchListener()
      {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
          return true;
        }
      });

      String url = MBResourceService.getInstance().getUrlById(field.getSource());
      webView.loadUrl(url);
    }
    else
    {
      webView.loadDataWithBaseURL(null, field.getValuesForDisplay(), null, "UTF-8", null);

    }
    getStyleHandler().styleWebView(webView, field);

    return webView;
  }
}
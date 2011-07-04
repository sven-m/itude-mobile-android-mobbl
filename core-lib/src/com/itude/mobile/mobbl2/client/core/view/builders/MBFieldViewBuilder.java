package com.itude.mobile.mobbl2.client.core.view.builders;

import android.content.Context;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils.TruncateAt;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDomainValidatorDefinition;
import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.model.MBDocumentDiff;
import com.itude.mobile.mobbl2.client.core.services.MBLocalizationService;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.MBParseUtil;
import com.itude.mobile.mobbl2.client.core.util.MBScreenUtilities;
import com.itude.mobile.mobbl2.client.core.util.MathUtilities;
import com.itude.mobile.mobbl2.client.core.util.StringUtilities;
import com.itude.mobile.mobbl2.client.core.util.UniqueIntegerGenerator;
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
    else if (Constants.C_FIELD_LABEL.equals(field.getType())) view = buildLabel(field);
    else if (Constants.C_FIELD_SUBLABEL.equals(field.getType())) view = buildSubLabel(field);
    else if (Constants.C_FIELD_DROPDOWNLIST.equals(field.getType())) view = buildDropdownList(field);
    else if (Constants.C_FIELD_CHECKBOX.equals(field.getType())) view = buildCheckBox(field);
    else if (Constants.C_FIELD_MATRIXTITLE.equals(field.getType())) view = buildMatrixTitle(field);
    else if (Constants.C_FIELD_MATRIXCELL.equals(field.getType())) view = buildMatrixCell(field);
    else if (Constants.C_FIELD_TEXT.equals(field.getType())) view = buildTextView(field);
    else
    {
      Log.w("MOBBL", "MBFieldViewBuilder.buildFieldView(): Failed to build unsupported view type " + field.getType());
    }

    return view;
  }

  public View buildButton(MBField field)
  {
    MarginLayoutParams buttonParams = new MarginLayoutParams(MarginLayoutParams.WRAP_CONTENT, MarginLayoutParams.WRAP_CONTENT);

    buttonParams.setMargins(MBScreenUtilities.FIVE, 0, MBScreenUtilities.FIVE, 0);
    Button button = new Button(MBApplicationController.getInstance().getBaseContext());
    button.setLayoutParams(buttonParams);
    button.setText(field.getLabel());
    button.setOnClickListener(field);
    button.setOnKeyListener(field);
    getStyleHandler().styleButton(button, field);
    return button;
  }

  public View buildLabel(MBField field)
  {
    String value = field.getValueForDisplay();

    TextView label = buildTextViewWithValue(value);
    label.setSingleLine(true);
    label.setEllipsize(TruncateAt.END);
    getStyleHandler().styleLabel(label, field);

    return label;
  }

  public View buildMatrixTitle(MBField field)
  {
    String value = field.getValueForDisplay();

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
    String value = field.getValueForDisplay();

    // Title TextView
    TextView label = buildTextViewWithValue(value);
    label.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1));
    label.setSingleLine();

    // default styling
    getStyleHandler().styleMatrixCell(field, label);

    MBPanel parent = (MBPanel) field.getFirstParentOfKind(MBPanel.class);
    if (parent != null && !parent.isDiffableMaster()) parent = parent.getFirstParentPanelWithType("MATRIX");
    if (parent != null && !parent.isDiffableMaster()) parent = null;

    if (parent != null && (field.isDiffablePrimary() || field.isDiffableSecondary()))
    {
      // apply style based on diff
      getStyleHandler().styleDiffableValue(label, parent.getDiffablePrimaryDelta());
    }
    else return label;

    MBDocumentDiff documentDiff = field.getPage().getDocumentDiff();
    String path = field.evaluatedDataPath();

    //FIXME, werkt nog niet goed heeft last van DataTypes
    //Mark the delta (<0, 0, >0) of the cell's value
    if (documentDiff != null && path != null && documentDiff.isChanged(path))
    {

      String valueA = documentDiff.valueOfAForPath(path);
      String valueB = documentDiff.valueOfBForPath(path);

      if (valueA != null && valueB != null)
      {
        Double a = MBParseUtil.doubleValueDutch(valueA);
        Double b = MBParseUtil.doubleValueDutch(valueB);

        if (a != null && b != null && !Double.isNaN(a) && !Double.isNaN(b))
        {
          double delta = MathUtilities.truncate(a - b);
          getStyleHandler().styleChangedValue(label, a, delta);
        }
      }
      else
      {
        Log.w("MOBBL",
              "MBFieldViewBuilder.buildMatrixCell(): Failed to calculate delta because documentDiff.valueOfAForPath return null for path "
                  + path);
      }
    }

    return label;
  }

  private TextView buildTextViewWithValue(String value)
  {
    return buildTextViewWithValue(value, false);
  }

  private TextView buildTextViewWithValue(String value, boolean p_isHtml)
  {
    TextView label = new TextView(MBApplicationController.getInstance().getBaseContext());
    label.setEllipsize(TruncateAt.END);
    if (p_isHtml) label.setText(Html.fromHtml(value));
    else label.setText(value);

    getStyleHandler().styleLabel(label, null);

    return label;
  }

  public View buildSubLabel(MBField field)
  {
    String value = field.getValueForDisplay();

    TextView label = buildTextViewWithValue(value);
    getStyleHandler().styleSubLabel(label);

    return label;
  }

  public View buildTextField(MBField field)
  {
    Context context = MBApplicationController.getInstance().getBaseContext();

    EditText inputField = new EditText(context);
    //    EditText inputField = new EditText(context);
    // Add TextWatcher to EditText so changes will be saved to the document
    inputField.addTextChangedListener(field);
    inputField.setOnKeyListener(field);

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
             && (field.getDataType().equals(Constants.C_FIELD_DATATYPE_double) || field.getDataType()
                 .equals(Constants.C_FIELD_DATATYPE_float)))
    {
      inputField.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);

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
          && MBLocalizationService.getInstance().getLocaleCode().equals(Constants.C_LOCALE_CODE_DUTCH))
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

    // Set type of field visualization
    if (field.getType() != null && field.getType().equals(Constants.C_FIELD_PASSWORD))
    {
      inputField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
      inputField.setTransformationMethod(new PasswordTransformationMethod());
    }

    getStyleHandler().styleTextfield(inputField, field);

    if (field.getLabel() != null && field.getLabel().length() > 0)
    {
      inputField.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 40));

      LinearLayout labelLayout = new LinearLayout(context);
      labelLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
      labelLayout.setOrientation(LinearLayout.HORIZONTAL);

      TextView label = buildTextViewWithValue(field.getLabel());
      getStyleHandler().styleLabel(label, field);
      label.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 60));

      labelLayout.addView(label);
      labelLayout.addView(inputField);

      return labelLayout;
    }

    return inputField;
  }

  public View buildDropdownList(MBField field)
  {
    Context context = MBApplicationController.getInstance().getViewManager();

    int selected = -1;

    Spinner dropdownList = new Spinner(context);
    dropdownList.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1));
    dropdownList.setOnItemSelectedListener(field);
    dropdownList.setOnKeyListener(field);

    ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(context, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    String fieldValue = field.getValue();
    for (int i = 0; i < field.getDomain().getDomainValidators().size(); i++)
    {
      MBDomainValidatorDefinition domDef = field.getDomain().getDomainValidators().get(i);
      adapter.add(domDef.getTitle());

      if ((fieldValue != null && domDef.getValue() != null && fieldValue.equals(domDef.getValue()))
          || (field.getValueIfNil() != null && domDef.getValue() != null && field.getValueIfNil().equals(domDef.getValue())))
      {
        selected = i;
      }
    }

    dropdownList.setAdapter(adapter);

    if (selected > -1)
    {
      dropdownList.setSelection(selected);
    }

    if (field.getLabel() != null && field.getLabel().length() > 0)
    {
      dropdownList.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 40));

      LinearLayout labelLayout = new LinearLayout(context);
      labelLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
      labelLayout.setOrientation(LinearLayout.HORIZONTAL);

      TextView label = buildTextViewWithValue(field.getLabel());
      label.setText(field.getLabel());
      label.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 60));
      getStyleHandler().styleLabel(label, field);

      labelLayout.addView(label);
      labelLayout.addView(dropdownList);

      return labelLayout;
    }

    return dropdownList;
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

    RelativeLayout container = new RelativeLayout(context);
    RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
        RelativeLayout.LayoutParams.WRAP_CONTENT);
    container.setLayoutParams(rlParams);
    container.setOnClickListener(new OnClickListener()
    {
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
    String value = field.getValueForDisplay();

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

}

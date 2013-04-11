package com.itude.mobile.mobbl2.client.core.view.builders;

import android.view.View;

import com.itude.mobile.android.util.AssertUtil;
import com.itude.mobile.mobbl2.client.core.MBException;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.view.MBField;
import com.itude.mobile.mobbl2.client.core.view.builders.field.ButtonFieldBuilder;
import com.itude.mobile.mobbl2.client.core.view.builders.field.CheckboxFieldBuilder;
import com.itude.mobile.mobbl2.client.core.view.builders.field.DateFieldBuilder;
import com.itude.mobile.mobbl2.client.core.view.builders.field.DropdownListFieldBuilder;
import com.itude.mobile.mobbl2.client.core.view.builders.field.ImageButtonFieldBuilder;
import com.itude.mobile.mobbl2.client.core.view.builders.field.ImageFieldBuilder;
import com.itude.mobile.mobbl2.client.core.view.builders.field.InputFieldBuilder;
import com.itude.mobile.mobbl2.client.core.view.builders.field.LabelFieldBuilder;
import com.itude.mobile.mobbl2.client.core.view.builders.field.MatrixCellFieldBuilder;
import com.itude.mobile.mobbl2.client.core.view.builders.field.MatrixDescriptionFieldBuilder;
import com.itude.mobile.mobbl2.client.core.view.builders.field.MatrixTitleFieldBuilder;
import com.itude.mobile.mobbl2.client.core.view.builders.field.PasswordFieldBuilder;
import com.itude.mobile.mobbl2.client.core.view.builders.field.SublabelFieldBuilder;
import com.itude.mobile.mobbl2.client.core.view.builders.field.TextFieldBuilder;
import com.itude.mobile.mobbl2.client.core.view.builders.field.TimeFieldBuilder;
import com.itude.mobile.mobbl2.client.core.view.builders.field.WebFieldBuilder;

public class MBFieldViewBuilder extends MBViewBuilder
{

  private final MBBuilderRegistry<MBField, Builder, String> _builders;

  public MBFieldViewBuilder()
  {
    _builders = new MBBuilderRegistry<MBField, MBFieldViewBuilder.Builder, String>();
    registerBuilders();
  }

  private void registerBuilders()
  {
    _builders.registerBuilder(Constants.C_FIELD_INPUT, new InputFieldBuilder());
    _builders.registerBuilder(Constants.C_FIELD_PASSWORD, new PasswordFieldBuilder());
    _builders.registerBuilder(Constants.C_FIELD_BUTTON, new ButtonFieldBuilder());
    _builders.registerBuilder(Constants.C_FIELD_IMAGE, new ImageFieldBuilder());
    _builders.registerBuilder(Constants.C_FIELD_IMAGEBUTTON, new ImageButtonFieldBuilder());
    _builders.registerBuilder(Constants.C_FIELD_LABEL, new LabelFieldBuilder());
    _builders.registerBuilder(Constants.C_FIELD_SUBLABEL, new SublabelFieldBuilder());
    _builders.registerBuilder(Constants.C_FIELD_DROPDOWNLIST, new DropdownListFieldBuilder());
    _builders.registerBuilder(Constants.C_FIELD_CHECKBOX, new CheckboxFieldBuilder());
    _builders.registerBuilder(Constants.C_FIELD_MATRIXTITLE, new MatrixTitleFieldBuilder());
    _builders.registerBuilder(Constants.C_FIELD_MATRIXDESCRIPTION, new MatrixDescriptionFieldBuilder());
    _builders.registerBuilder(Constants.C_FIELD_MATRIXCELL, new MatrixCellFieldBuilder());
    _builders.registerBuilder(Constants.C_FIELD_TEXT, new TextFieldBuilder());
    _builders.registerBuilder(Constants.C_FIELD_WEB, new WebFieldBuilder());
    _builders.registerBuilder(Constants.C_FIELD_DATE, new DateFieldBuilder());
    _builders.registerBuilder(Constants.C_FIELD_TIME, new TimeFieldBuilder());

  }

  public void registerBuilder(String type, Builder builder)
  {
    _builders.registerBuilder(type, builder);
  }

  public void registerBuilder(String type, String style, Builder builder)
  {
    _builders.registerBuilder(type, style, builder);
  }

  public View buildFieldView(MBField field)
  {
    AssertUtil.notNull("field", field);
    Builder builder = _builders.getBuilder(field.getType(), field.getStyle());
    if (builder == null) throw new MBException("No field builder found for field " + field);

    View view = builder.buildField(field);
    if (view != null) field.attachView(view);
    return view;
  }

  ///////   

  public static interface Builder
  {
    public View buildField(MBField field);
  }

}
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
package com.itude.mobile.mobbl.core.view.builders;

import android.view.View;

import com.itude.mobile.android.util.AssertUtil;
import com.itude.mobile.mobbl.core.MBException;
import com.itude.mobile.mobbl.core.util.MBConstants;
import com.itude.mobile.mobbl.core.view.MBField;
import com.itude.mobile.mobbl.core.view.builders.field.ButtonFieldBuilder;
import com.itude.mobile.mobbl.core.view.builders.field.CheckboxFieldBuilder;
import com.itude.mobile.mobbl.core.view.builders.field.DateFieldBuilder;
import com.itude.mobile.mobbl.core.view.builders.field.DropdownListFieldBuilder;
import com.itude.mobile.mobbl.core.view.builders.field.ImageButtonFieldBuilder;
import com.itude.mobile.mobbl.core.view.builders.field.ImageFieldBuilder;
import com.itude.mobile.mobbl.core.view.builders.field.InputFieldBuilder;
import com.itude.mobile.mobbl.core.view.builders.field.LabelFieldBuilder;
import com.itude.mobile.mobbl.core.view.builders.field.MatrixCellFieldBuilder;
import com.itude.mobile.mobbl.core.view.builders.field.MatrixDescriptionFieldBuilder;
import com.itude.mobile.mobbl.core.view.builders.field.MatrixTitleFieldBuilder;
import com.itude.mobile.mobbl.core.view.builders.field.PasswordFieldBuilder;
import com.itude.mobile.mobbl.core.view.builders.field.SublabelFieldBuilder;
import com.itude.mobile.mobbl.core.view.builders.field.TextFieldBuilder;
import com.itude.mobile.mobbl.core.view.builders.field.TimeFieldBuilder;
import com.itude.mobile.mobbl.core.view.builders.field.WebFieldBuilder;

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
    _builders.registerBuilder(MBConstants.C_FIELD_INPUT, new InputFieldBuilder());
    _builders.registerBuilder(MBConstants.C_FIELD_PASSWORD, new PasswordFieldBuilder());
    _builders.registerBuilder(MBConstants.C_FIELD_BUTTON, new ButtonFieldBuilder());
    _builders.registerBuilder(MBConstants.C_FIELD_IMAGE, new ImageFieldBuilder());
    _builders.registerBuilder(MBConstants.C_FIELD_IMAGEBUTTON, new ImageButtonFieldBuilder());
    _builders.registerBuilder(MBConstants.C_FIELD_LABEL, new LabelFieldBuilder());
    _builders.registerBuilder(MBConstants.C_FIELD_SUBLABEL, new SublabelFieldBuilder());
    _builders.registerBuilder(MBConstants.C_FIELD_DROPDOWNLIST, new DropdownListFieldBuilder());
    _builders.registerBuilder(MBConstants.C_FIELD_CHECKBOX, new CheckboxFieldBuilder());
    _builders.registerBuilder(MBConstants.C_FIELD_MATRIXTITLE, new MatrixTitleFieldBuilder());
    _builders.registerBuilder(MBConstants.C_FIELD_MATRIXDESCRIPTION, new MatrixDescriptionFieldBuilder());
    _builders.registerBuilder(MBConstants.C_FIELD_MATRIXCELL, new MatrixCellFieldBuilder());
    _builders.registerBuilder(MBConstants.C_FIELD_TEXT, new TextFieldBuilder());
    _builders.registerBuilder(MBConstants.C_FIELD_WEB, new WebFieldBuilder());
    _builders.registerBuilder(MBConstants.C_FIELD_DATE, new DateFieldBuilder());
    _builders.registerBuilder(MBConstants.C_FIELD_TIME, new TimeFieldBuilder());

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

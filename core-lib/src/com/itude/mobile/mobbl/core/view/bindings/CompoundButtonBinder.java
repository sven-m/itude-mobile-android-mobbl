package com.itude.mobile.mobbl.core.view.bindings;

import android.widget.CompoundButton;

import com.itude.mobile.mobbl.core.util.MBParseUtil;
import com.itude.mobile.mobbl.core.view.MBField;

public class CompoundButtonBinder extends SingleViewBinder<CompoundButton, MBField>
{

  protected CompoundButtonBinder(int id)
  {
    super(id);
  }

  public static CompoundButtonBinder getInstance(int id)
  {
    return new CompoundButtonBinder(id);
  }

  @Override
  protected void bindSingleView(CompoundButton view, MBField component)
  {
    Boolean value = MBParseUtil.strictBooleanValue(component.getUntranslatedValue());
    boolean valueIfNil = MBParseUtil.booleanValue(component.getUntranslatedValueIfNil());
    boolean checked = false;

    if ((value != null && value) || (value == null && valueIfNil))
    {
      checked = true;
    }

    // remove any earlier listeners, in case this shows up in a ListView, and the view
    // gets recycled

    view.setOnCheckedChangeListener(component);
    view.setOnKeyListener(component);

    view.setChecked(checked);
  }

}

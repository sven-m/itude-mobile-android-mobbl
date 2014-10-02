package com.itude.mobile.mobbl.core.view.bindings;

import android.view.View;
import android.widget.CompoundButton;

import com.itude.mobile.mobbl.core.util.MBParseUtil;
import com.itude.mobile.mobbl.core.view.MBField;

public class CompoundButtonBinder extends BaseViewBinder
{
  private final int id;

  protected CompoundButtonBinder(int id)
  {
    this.id = id;
  }

  public static CompoundButtonBinder getInstance(int id)
  {
    return new CompoundButtonBinder(id);
  }

  @Override
  protected View bindSpecificView(BuildState state)
  {
    CompoundButton button = (CompoundButton) state.parent.findViewById(id);

    if (button != null)
    {
      MBField field = (MBField) state.component;

      Boolean value = MBParseUtil.strictBooleanValue(field.getUntranslatedValue());
      boolean valueIfNil = MBParseUtil.booleanValue(field.getUntranslatedValueIfNil());
      boolean checked = false;

      if ((value != null && value) || (value == null && valueIfNil))
      {
        checked = true;
      }

      // remove any earlier listeners, in case this shows up in a ListView, and the view
      // gets recycled

      button.setOnCheckedChangeListener(field);
      button.setOnKeyListener(field);

      button.setChecked(checked);
    }

    return button;

  }

}

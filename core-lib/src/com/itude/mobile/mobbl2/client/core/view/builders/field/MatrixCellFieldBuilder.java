package com.itude.mobile.mobbl2.client.core.view.builders.field;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.itude.mobile.mobbl2.client.core.model.MBDocumentDiff;
import com.itude.mobile.mobbl2.client.core.util.MBParseUtil;
import com.itude.mobile.mobbl2.client.core.util.MathUtilities;
import com.itude.mobile.mobbl2.client.core.view.MBField;
import com.itude.mobile.mobbl2.client.core.view.MBPanel;

public class MatrixCellFieldBuilder extends MBBaseFieldBuilder
{

  @Override
  public View buildField(MBField field)
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

}

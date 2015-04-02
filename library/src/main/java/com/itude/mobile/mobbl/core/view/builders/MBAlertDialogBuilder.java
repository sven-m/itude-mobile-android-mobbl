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

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.itude.mobile.mobbl.core.controller.MBApplicationController;
import com.itude.mobile.mobbl.core.controller.MBOutcome;
import com.itude.mobile.mobbl.core.controller.MBViewManager;
import com.itude.mobile.mobbl.core.model.MBDocument;
import com.itude.mobile.mobbl.core.util.MBConstants;
import com.itude.mobile.mobbl.core.view.MBAlert;
import com.itude.mobile.mobbl.core.view.MBComponent;
import com.itude.mobile.mobbl.core.view.MBField;

public class MBAlertDialogBuilder {

    private static final String C_FIELD_BUTTON_STYLE_NEGATIVE = "NEGATIVE"; // iOS: Cancel Button
    private static final String C_FIELD_BUTTON_STYLE_POSITIVE = "POSITIVE"; // iOS: Other Button
    private static final String C_FIELD_BUTTON_STYLE_OTHER = "OTHER";   // iOS: Other Button

    public AlertDialog buildAlertDialog(MBAlert alert) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MBViewManager.getInstance());
        builder.setTitle(alert.getTitle());

        // Build all the children
        for (MBComponent child : alert.getChildren()) {
            if (child instanceof MBField) {
                MBField field = (MBField) child;

                if (MBConstants.C_FIELD_TEXT.equals(field.getType())) {
                    builder.setMessage(field.getValuesForDisplay());
                } else if (MBConstants.C_FIELD_BUTTON.equals(field.getType())) {
                    String text = field.getLabel();
                    if (C_FIELD_BUTTON_STYLE_NEGATIVE.equals(field.getStyle())) {
                        builder.setNegativeButton(text, createOnClickListener(field, alert.getDocument()));
                    } else if (C_FIELD_BUTTON_STYLE_POSITIVE.equals(field.getStyle())) {
                        builder.setPositiveButton(text, createOnClickListener(field, alert.getDocument()));
                    } else if (C_FIELD_BUTTON_STYLE_OTHER.equals(field.getStyle())) {
                        builder.setNeutralButton(text, createOnClickListener(field, alert.getDocument()));
                    }
                }

            }
        }

        return builder.create();
    }

    private DialogInterface.OnClickListener createOnClickListener(final MBField field, final MBDocument document) {

        if (field.getOutcomeName() != null) {
            return new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    MBOutcome oc = new MBOutcome();
                    oc.setOutcomeName(field.getOutcomeName());
                    oc.setDocument(document);
                    oc.setPath(field.getPath());
                    MBApplicationController.getInstance().handleOutcome(oc);
                }
            };
        }

        return null;
    }

}

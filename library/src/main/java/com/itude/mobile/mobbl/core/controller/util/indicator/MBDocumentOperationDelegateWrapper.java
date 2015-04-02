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
package com.itude.mobile.mobbl.core.controller.util.indicator;

import com.itude.mobile.android.util.AssertUtil;
import com.itude.mobile.mobbl.core.model.MBDocument;
import com.itude.mobile.mobbl.core.services.operation.MBDocumentOperationDelegate;

/**
 * {@link MBDocumentOperationDelegate} wrapper class to handle document operations including the use of a indicator
 */
public class MBDocumentOperationDelegateWrapper implements MBDocumentOperationDelegate {
    final MBDocumentOperationDelegate _actualDelegate;
    final MBIndicator _indicator;

    /**
     * Constructor so you handle a document operator including a indicator.
     *
     * @param actualDelegate {@link MBDocumentOperationDelegate}
     * @param indicator      {@link MBIndicator}
     */
    public MBDocumentOperationDelegateWrapper(MBDocumentOperationDelegate actualDelegate, MBIndicator indicator) {
        AssertUtil.notNull("actualDelegate", actualDelegate);
        AssertUtil.notNull("indicator", indicator);
        _actualDelegate = actualDelegate;
        _indicator = indicator;
    }

    /**
     * @see com.itude.mobile.mobbl.core.services.operation.MBDocumentOperationDelegate#processResult(com.itude.mobile.mobbl.core.model.MBDocument)
     */
    @Override
    public void processResult(MBDocument document) {

        try {
            _actualDelegate.processResult(document);
        } finally {
            _indicator.release();
        }

    }

    /**
     * @see com.itude.mobile.mobbl.core.services.operation.MBDocumentOperationDelegate#processException(java.lang.Exception)
     */
    @Override
    public void processException(Exception e) {
        try {
            _actualDelegate.processException(e);
        } finally {
            _indicator.release();
        }
    }

}

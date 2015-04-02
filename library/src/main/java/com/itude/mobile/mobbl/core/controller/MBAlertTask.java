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
package com.itude.mobile.mobbl.core.controller;

import com.itude.mobile.mobbl.core.configuration.mvc.MBAlertDefinition;

/**
 * {@link MBOutcomeTask} class describing a alert task
 */
public class MBAlertTask extends MBOutcomeTask {
    private final MBAlertDefinition _alertDefinition;

    public MBAlertTask(MBOutcomeTaskManager manager, MBAlertDefinition alertDefinition) {
        super(manager);
        _alertDefinition = alertDefinition;
    }

    public MBAlertDefinition getAlertDefinition() {
        return _alertDefinition;
    }

    @Override
    protected Threading getThreading() {
        return Threading.UI;
    }

    @Override
    protected void execute() {
        final MBApplicationController applicationController = MBApplicationController.getInstance();
        applicationController.prepareAlert(new MBOutcome(getOutcome()), getAlertDefinition().getName(),
                applicationController.getBackStackEnabled());

    }

}

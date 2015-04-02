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

import com.itude.mobile.mobbl.core.model.MBDocument;

/**
 * Interface for an action. Typical use is to influence the flow of navigation between screens during authentication sequences or purchase flows.
 */
public interface MBAction {
    /**
     * Execute an action.
     *
     * @param document {@link MBDocument}
     * @param path     path
     * @return {@link MBOutcome} to be executed
     */
    public MBOutcome execute(MBDocument document, String path);

}

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

import java.util.LinkedList;
import java.util.Queue;

/**
 * Manager to handle all outcome related functionality
 */
public class MBOutcomeTaskManager {

    private final Queue<MBOutcomeTask<?>> _tasks;
    private final MBOutcome _outcome;

    public MBOutcomeTaskManager(MBOutcome outcome) {
        _outcome = outcome;
        _tasks = new LinkedList<MBOutcomeTask<?>>();
    }

    public void run() {
        MBOutcomeTask<?> task = _tasks.poll();
        if (task != null) task.start();
    }

    public void addTask(MBOutcomeTask<?> task) {
        _tasks.add(task);
    }

    public MBOutcome getOutcome() {
        return _outcome;
    }

    public void finished(MBOutcomeTask<?> task) {
        run();
    }
}

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

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.itude.mobile.android.util.AssertUtil;
import com.itude.mobile.android.util.ComparisonUtil;
import com.itude.mobile.android.util.log.MBLog;
import com.itude.mobile.mobbl.core.configuration.mvc.MBDialogDefinition;
import com.itude.mobile.mobbl.core.controller.util.MBBaseLifecycleListener;
import com.itude.mobile.mobbl.core.controller.util.MBBasicViewController;
import com.itude.mobile.mobbl.core.services.MBMetadataService;
import com.itude.mobile.mobbl.core.util.threads.MBThread;
import com.itude.mobile.mobbl.core.util.threads.exception.MBInterruptedException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manager to handle all dialog related functionality
 */
public class MBDialogManager extends MBBaseLifecycleListener {
    public static interface MBDialogChangeListener {
        public void onDialogSelected(String name);
    }

    private final List<String> _sortedDialogNames;
    private final Map<String, MBDialogController> _controllerMap;
    private final Map<String, MBPageStackController> _pageStackControllers;
    private String _activeDialog;
    private final Activity _activity;
    private final List<MBDialogChangeListener> _listeners;
    private String _toActivate;

    public MBDialogManager(Activity activity) {
        _activity = activity;
        _sortedDialogNames = new ArrayList<String>();
        _listeners = new ArrayList<MBDialogChangeListener>();
        _controllerMap = new HashMap<String, MBDialogController>();
        _pageStackControllers = new HashMap<String, MBPageStackController>();
    }

    ///////////////// Lifecycle events ////////////

    @Override
    public void onCreate() {
        super.onCreate();

        build();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActiveDialog() != null) getActiveDialog().addOnBackStackChangedListener();
    }

    @Override
    public void onPause() {
        super.onPause();

        MBDialogController dc = getActiveDialog();
        if (dc != null) dc.removeOnBackStackChangedListenerOfCurrentDialog();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // signal the DialogControllers that we are closing down
        for (MBDialogController controller : _controllerMap.values())
            controller.shutdown();

    }

    public void onConfigurationChanged(final Configuration newConfig) {
        MBThread thread = new MBThread() {
            @Override
            public void runMethod() throws MBInterruptedException {
                for (MBDialogController dialog : getDialogs())
                    dialog.handleOrientationChange(newConfig);
            }
        };

        _activity.runOnUiThread(thread);
    }

    /////////// Properties /////

    public MBDialogController getActiveDialog() {
        checkEnqueued();
        return getDialog(_activeDialog);
    }

    @Deprecated
    public Collection<MBDialogController> getDialogs() {
        return _controllerMap.values();
    }

    public List<String> getSortedDialogNames() {
        return _sortedDialogNames;
    }

    public MBDialogController getDialog(String name) {
        return _controllerMap.get(name);
    }

    public void addDialogChangeListener(MBDialogChangeListener listener) {
        _listeners.add(listener);
    }

    public void removeDialogChangeListener(MBDialogChangeListener listener) {
        _listeners.remove(listener);
    }

    ///////////////

    public void reset() {
        _activeDialog = null;
        _activity.runOnUiThread(new MBThread() {
            @Override
            public void runMethod() throws MBInterruptedException {
                clear();
                while (((FragmentActivity) _activity).getSupportFragmentManager().popBackStackImmediate()) {
                    //noop
                }

                build();

            }
        });

    }

    ////////////// Actual management ////////

    private void clear() {
        for (MBDialogController controller : _controllerMap.values())
            for (MBBasicViewController bvc : controller.getAllFragments())
                bvc.handleOnLeavingWindow();

        _sortedDialogNames.clear();
        _controllerMap.clear();
        _activeDialog = null;
        _toActivate = null;
    }

    private void build() {
        List<MBDialogDefinition> dialogs = MBMetadataService.getInstance().getDialogs();

        for (MBDialogDefinition dialog : dialogs)
            if (dialog.isPreConditionValid()) {
                createDialog(dialog);
            }
    }

    public void activateHome() {
        MBDialogDefinition homeDialogDefinition = MBMetadataService.getInstance().getHomeDialogDefinition();
        MBViewManager.getInstance().activateDialogWithName(homeDialogDefinition.getName());
    }

    boolean activateDialog(String dialogName) {
        if (ComparisonUtil.safeEquals(_activeDialog, dialogName)) return false;

        MBDialogController dialog = _controllerMap.get(dialogName);
        if (dialog == null)
            return false;// throw new MBException("No dialog " + dialogName + " found!");

        // since inner dialog names map to the encompassing dialogcontroller,
        // it is possible the same controller is being activated, while having a
        // different name
        MBDialogController current = getActiveDialog();
        if (current == dialog) return false;

        if (current != null) {
            MBLog.d("MBDialogManager", "Switching from " + current.getName() + "...");
            current.deactivate(dialog.getDecorator().maintainPreviousStack());
            current.handleAllOnLeavingWindow();
        }

        for (MBDialogChangeListener listener : _listeners)
            listener.onDialogSelected(dialogName);

        MBLog.d("MBDialogManager", "Switching to " + dialogName + "...");
        dialog.activate();
        _activeDialog = dialogName;
        _toActivate = null;
        dialog.handleAllOnWindowActivated();
        return true;
    }

    void activatePageStack(String pageStackName) {
        MBPageStackController controller = _pageStackControllers.get(pageStackName);
        AssertUtil.notNull("controller", controller);

        activateDialog(controller.getParent().getName());
    }

    void deactivateDialog(String dialogName) {
        if (!ComparisonUtil.safeEquals(_activeDialog, dialogName)) return;

        // can't deactivate home dialog, since we have nothing to switch to
        MBDialogDefinition homeDialogDefinition = MBMetadataService.getInstance().getHomeDialogDefinition();
        if (homeDialogDefinition.getName().equals(dialogName)) return;

        MBDialogController current = getActiveDialog();
        if (current.getDecorator().handlesOwnDismiss()) {
            current.deactivate(false);
            current.getDecorator().hide();
        } else activateHome();
    }

    private void createDialog(MBDialogDefinition definition) {
        MBDialogController controller = MBApplicationFactory.getInstance().createDialogController();
        String name = definition.getName();
        controller.init(name, null);
        _controllerMap.put(name, controller);
        _pageStackControllers.putAll(controller.getPageStacks());
        _sortedDialogNames.add(name);
    }

    public void removeDialog(String dialogName) {
        MBDialogController activeDialog = getActiveDialog();
        if (activeDialog != null) {
            MBBasicViewController fragment = activeDialog.findFragment(dialogName);
            if (fragment != null) {
                View root = fragment.getView();
                if (root != null) {
                    ViewParent parent = root.getParent();
                    if (parent instanceof FrameLayout) {
                        final FrameLayout fragmentContainer = (FrameLayout) parent;
                        _activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fragmentContainer.removeAllViews();
                            }
                        });
                    }
                }
            }
        }
    }

    public MBPageStackController getPageStack(String dialogName) {
        return _pageStackControllers.get(dialogName);
    }

    public void enqueueDialog(String dialog) {
        _activeDialog = null;
        _toActivate = dialog;

        new Handler(Looper.getMainLooper()).post(new MBThread() {
            @Override
            public void runMethod() throws MBInterruptedException {
                checkEnqueued();
            }
        });
    }

    private void checkEnqueued() {
        if (_activeDialog == null && _toActivate != null) {
            String toActivate = _toActivate;
            _toActivate = null;
            activateDialog(toActivate);
        }
    }
}

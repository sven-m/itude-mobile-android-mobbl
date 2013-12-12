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
package com.itude.mobile.mobbl2.client.core.controller;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDialogDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDialogGroupDefinition;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.view.MBOutcomeListenerProtocol;

/**
 * @author Coen Houtman
 *
 */
public class MBOutcomeHandler extends Handler
{
  private final LinkedList<WeakReference<MBOutcomeListenerProtocol>> _outcomeListeners = new LinkedList<WeakReference<MBOutcomeListenerProtocol>>();

  @Override
  public void handleMessage(Message msg)
  {
    Log.d(Constants.APPLICATION_NAME, "MBOutcomeHandler.handleMessage(): " + msg.what);
    if (msg.what == Constants.C_MESSAGE_INITIAL_OUTCOMES_FINISHED)
    {
      MBApplicationController.getInstance().finishedInitialOutcomes();
      return;
    }
    MBOutcome outcome = msg.getData().getParcelable("outcome");
    boolean throwException = msg.getData().getBoolean("throwException", true);

    new MBOutcomeRunner(outcome, throwException).handle();
  }

  public void handleOutcomeSynchronously(MBOutcome outcome, boolean throwException)
  {
    outcome.setNoBackgroundProcessing(true);
    new MBOutcomeRunner(outcome, throwException).handle();

  }

  /***
   * In case of:
   *  1. a split dialog; 
   *  2. a page must always put either left or right; and
   *  3. that page can be used in multiple dialogs
   *  
   *  Instead of defining the specific dialog name, either LEFT or RIGHT can be defined as the target dialog.
   *  The page will be displayed in either the left or right part of the active dialog.
   *  
   *  Example outcome definition:
   *  <Outcome origin="*" name="OUTCOME-page_winnerslosers_overview" action="PAGE-page_winnerslosers_overview" transferDocument="TRUE" dialog="LEFT"/>
   *  
   * @param dialogName
   * @return the dialog name to place the page in
   */
  static String resolveDialogName(String dialogName)
  {
    if (!"RIGHT".equals(dialogName) && !"LEFT".equals(dialogName))
    {
      return dialogName;
    }

    String newDialogName = null;

    String activeDialogName = MBApplicationController.getInstance().activeDialogName();
    MBDialogDefinition activeDialogDef = MBMetadataService.getInstance().getDefinitionForDialogName(activeDialogName);

    if (activeDialogDef.isGroup())
    {
      MBDialogGroupDefinition activeDialogGroupDef = (MBDialogGroupDefinition) activeDialogDef;
      List<MBDialogDefinition> children = activeDialogGroupDef.getChildren();

      MBDialogDefinition dialogDef = null;
      if ("RIGHT".equals(dialogName))
      {
        dialogDef = children.get(children.size() - 1);
      }
      else if ("LEFT".equals(dialogName))
      {
        dialogDef = children.get(0);
      }

      if (dialogDef != null)
      {
        newDialogName = dialogDef.getName();

        Log.d(Constants.APPLICATION_NAME, "Dialog name '" + dialogName + "' resolved to '" + newDialogName + "'");
      }
    }

    return newDialogName;
  }

  public void registerOutcomeListener(MBOutcomeListenerProtocol listener)
  {
    synchronized (_outcomeListeners)
    {
      for (Iterator<WeakReference<MBOutcomeListenerProtocol>> it = _outcomeListeners.iterator(); it.hasNext();)
      {
        WeakReference<MBOutcomeListenerProtocol> ref = it.next();
        MBOutcomeListenerProtocol prot = ref.get();
        if (prot == null) it.remove();
        else if (prot.equals(listener)) return;
      }
      _outcomeListeners.add(new WeakReference<MBOutcomeListenerProtocol>(listener));
    }
  }

  public void unregisterOutcomeListener(MBOutcomeListenerProtocol listener)
  {
    synchronized (_outcomeListeners)
    {
      for (Iterator<WeakReference<MBOutcomeListenerProtocol>> it = _outcomeListeners.iterator(); it.hasNext();)
      {
        WeakReference<MBOutcomeListenerProtocol> ref = it.next();
        MBOutcomeListenerProtocol prot = ref.get();
        if (prot == null) it.remove();
        else if (prot.equals(listener)) it.remove();
      }
    }
  }

  public List<MBOutcomeListenerProtocol> getOutcomeListeners()
  {
    synchronized (_outcomeListeners)
    {
      List<MBOutcomeListenerProtocol> list = new ArrayList<MBOutcomeListenerProtocol>(_outcomeListeners.size());
      for (Iterator<WeakReference<MBOutcomeListenerProtocol>> it = _outcomeListeners.iterator(); it.hasNext();)
      {
        WeakReference<MBOutcomeListenerProtocol> ref = it.next();
        MBOutcomeListenerProtocol prot = ref.get();
        if (prot == null) it.remove();
        else list.add(prot);
      }
      return list;
    }
  }
}

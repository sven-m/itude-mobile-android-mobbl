package com.itude.mobile.mobbl2.client.core.view.components.slidingmenu;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDialogDefinition;
import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.controller.MBViewManager;
import com.itude.mobile.mobbl2.client.core.controller.util.MBBasicViewController;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;
import com.itude.mobile.mobbl2.client.core.services.MBResourceService;
import com.itude.mobile.mobbl2.client.core.view.builders.MBStyleHandler;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilderFactory;

public class MBDefaultSlidingMenu extends MBBasicViewController
{
  @Override
  protected ViewGroup buildInitialView(LayoutInflater inflater)
  {
    Context context = MBApplicationController.getInstance().getBaseContext();

    LinearLayout menu = new LinearLayout(context);
    MBStyleHandler styleHandler = MBViewBuilderFactory.getInstance().getStyleHandler();
    styleHandler.styleSlidingMenuContainer(menu);

    for (final String dialogName : MBViewManager.getInstance().getSortedDialogNames())
    {
      final MBDialogDefinition dialogDefinition = MBMetadataService.getInstance().getDefinitionForDialogName(dialogName);

      if (dialogDefinition.isShowAsMenu())
      {
        MBSlidingMenuItem menuItem = new MBSlidingMenuItem(MBViewManager.getInstance());
        if (dialogDefinition.getIcon() != null)
        {
          menuItem.setIcon(MBResourceService.getInstance().getImageByID(dialogDefinition.getIcon()));
        }

        setSlidingMenuItemText(dialogDefinition, menuItem);

        menuItem.setOnClickListener(new OnClickListener()
        {
          @Override
          public void onClick(View v)
          {
            MBViewManager.getInstance().activateOrCreateDialogWithID(dialogName.hashCode());
          }
        });

        menu.addView(menuItem);
      }
    }

    return menu;
  }

  private void setSlidingMenuItemText(MBDialogDefinition dialogDefinition, MBSlidingMenuItem menuItem)
  {
    String title;

    if (MBViewManager.getInstance().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
    {
      title = dialogDefinition.getTitlePortrait();
    }
    else
    {
      title = dialogDefinition.getTitle();
    }

    if (StringUtil.isNotBlank(title))
    {
      menuItem.setText(title);
    }
  }

}

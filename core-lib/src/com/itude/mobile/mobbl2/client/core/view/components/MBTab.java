package com.itude.mobile.mobbl2.client.core.view.components;

import java.util.List;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDialogDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDomainDefinition;
import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.controller.MBOutcome;
import com.itude.mobile.mobbl2.client.core.services.MBLocalizationService;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;
import com.itude.mobile.mobbl2.client.core.util.StringUtilities;
import com.itude.mobile.mobbl2.client.core.util.UniqueIntegerGenerator;
import com.itude.mobile.mobbl2.client.core.view.builders.MBStyleHandler;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilderFactory;
import com.itude.mobile.mobbl2.client.core.view.listeners.MBTabListenerI;

@TargetApi(11)
public class MBTab extends RelativeLayout implements OnClickListener, OnItemClickListener, OnItemSelectedListener
{
  private int                    _tabId;
  private MBTabBar               _tabBar                 = null;
  private ImageView              _icon                   = null;
  private TextView               _textView               = null;
  private MBTabListenerI         _listener               = null;

  private View                   _leftSpacer;
  private View                   _rightSpacer;
  private LinearLayout           _content;

  private boolean                _isDropDown             = false;
  private MBTabSpinnerAdapter       _adapter                = null;
  private Drawable               _selectedBackground     = null;
  private ListPopupWindow        _dropDownWindow         = null;
  private OnItemSelectedListener _onItemSelectedListener = null;

  public MBTab(Context context)
  {
    super(context);

    setFocusable(true);
    setClickable(true);
    setOnClickListener(this);

    MBStyleHandler styleHandler = MBViewBuilderFactory.getInstance().getStyleHandler();
    styleHandler.styleTab(this);

    setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.FILL_PARENT));

    RelativeLayout.LayoutParams leftSpacerParams = new RelativeLayout.LayoutParams(0, 0);
    leftSpacerParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

    _leftSpacer = new View(context);
    _leftSpacer.setId(UniqueIntegerGenerator.getId());
    _leftSpacer.setLayoutParams(leftSpacerParams);

    RelativeLayout.LayoutParams contentParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    contentParams.addRule(RelativeLayout.RIGHT_OF, _leftSpacer.getId());
    contentParams.addRule(RelativeLayout.CENTER_VERTICAL);

    _content = new LinearLayout(context);
    _content.setId(UniqueIntegerGenerator.getId());
    _content.setLayoutParams(contentParams);
    _content.setOrientation(LinearLayout.HORIZONTAL);
    _content.setGravity(Gravity.CENTER);

    _icon = new ImageView(context);
    _icon.setId(UniqueIntegerGenerator.getId());

    _textView = new TextView(context);
    _textView.setId(UniqueIntegerGenerator.getId());
    _textView.setSingleLine();

    styleHandler.styleTabText(_textView);

    _content.addView(_icon);
    _content.addView(_textView);

    RelativeLayout.LayoutParams rightSpacerParams = new RelativeLayout.LayoutParams(0, 0);
    rightSpacerParams.addRule(RelativeLayout.RIGHT_OF, _content.getId());

    _rightSpacer = new View(context);
    _rightSpacer.setLayoutParams(rightSpacerParams);

    addView(_leftSpacer);
    addView(_content);
    addView(_rightSpacer);

    _onItemSelectedListener = this;
  }

  void select()
  {
    if (_isDropDown)
    {
      _content.setBackgroundDrawable(_selectedBackground);
    }

    setSelected(true);

    if (_listener != null)
    {
      _listener.onTabSelected(this);
    }
  }

  void unselect()
  {
    setSelected(false);

    if (_listener != null)
    {
      _listener.onTabUnselected(this);
    }

    if (_isDropDown)
    {
      _content.setBackgroundDrawable(null);
    }
  }

  void reselect()
  {
    if (_isDropDown)
    {
      if (_dropDownWindow == null)
      {
        _dropDownWindow = new ListPopupWindow(getContext());
        _dropDownWindow.setAdapter(_adapter);
        _dropDownWindow.setAnchorView(this);
        _dropDownWindow.setOnItemClickListener(this);
      }
      _dropDownWindow.show();
      ListView listView = _dropDownWindow.getListView();

      listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
      _dropDownWindow.setSelection(_adapter.getSelectedElement());
    }
    else if (_listener != null)
    {
      _listener.onTabReselected(this);
    }
  }

  public MBTab setIcon(Drawable drawable)
  {
    _icon.setImageDrawable(drawable);
    return this;
  }

  public MBTab setText(String text)
  {
    if (StringUtilities.isNotBlank(text))
    {
      _textView.setText(MBLocalizationService.getInstance().getTextForKey(text));
    }

    return this;
  }

  MBTabListenerI getListener()
  {
    return _listener;
  }

  public MBTab setListener(MBTabListenerI listener)
  {
    _listener = listener;

    return this;
  }

  public MBTab setTabId(int tabId)
  {
    _tabId = tabId;
    return this;
  }

  public int getTabId()
  {
    return _tabId;
  }

  void setTabBar(MBTabBar tabBar)
  {
    _tabBar = tabBar;
  }

  public MBTab setLeftPadding(int pixels)
  {
    _leftSpacer.getLayoutParams().width = pixels;
    return this;
  }

  public MBTab setRightPadding(int pixels)
  {
    _leftSpacer.getLayoutParams().width = pixels;
    return this;
  }

  public MBTab setAdapter(MBTabSpinnerAdapter adapter)
  {
    if (adapter == null)
    {
      _isDropDown = false;
    }
    else
    {
      _isDropDown = true;
    }

    _adapter = adapter;
    return this;
  }

  public MBTabSpinnerAdapter getAdapter()
  {
    return _adapter;
  }

  public MBTab setSelectedBackground(Drawable selectedBackground)
  {
    _selectedBackground = selectedBackground;
    return this;
  }

  public MBTab setDropDownOnItemSelectedListener(OnItemSelectedListener listener)
  {
    _onItemSelectedListener = listener;
    return this;
  }

  @Override
  public void onClick(View view)
  {
    _tabBar.selectTab(this, true);
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id)
  {
    if (_onItemSelectedListener != null)
    {
      _onItemSelectedListener.onItemSelected(parent, view, position, id);
    }

    _adapter.setSelectedElement(position);
    _dropDownWindow.dismiss();
    view.setSelected(true);

  }

  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
  {
    //    if (position == _adapter.getSelectedElement())
    //    {
    //      return;
    //    }

    MBDialogDefinition dialogDef = null;

    List<MBDialogDefinition> dialogs = MBMetadataService.getInstance().getDialogs();
    for (int i = 0; i < dialogs.size() && dialogDef == null; i++)
    {
      MBDialogDefinition dialog = dialogs.get(i);
      if (dialog.getName().hashCode() == _tabId)
      {
        dialogDef = dialog;
      }
    }

    if (dialogDef != null)
    {
      MBDomainDefinition domainDef = MBMetadataService.getInstance().getDefinitionForDomainName(dialogDef.getDomain());
      String value = domainDef.getDomainValidators().get(position).getValue();

      if (value != null)
      {
        MBOutcome outcome = new MBOutcome(value, null);
        outcome.setOriginName(dialogDef.getName());
        MBApplicationController.getInstance().handleOutcome(outcome);
      }
    }
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent)
  {

  }
}

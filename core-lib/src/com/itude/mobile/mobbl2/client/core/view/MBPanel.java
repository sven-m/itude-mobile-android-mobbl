package com.itude.mobile.mobbl2.client.core.view;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBPanelDefinition;
import com.itude.mobile.mobbl2.client.core.controller.MBViewManager.MBViewState;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.services.MBLocalizationService;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.MBDevice;
import com.itude.mobile.mobbl2.client.core.util.StringUtilities;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilderFactory;

public class MBPanel extends MBComponentContainer implements OnClickListener
{
  private String  _type;
  private String  _title;
  private int     _width;
  private int     _height;
  private String  _outcomeName;
  private String  _path;
  private String  _translatedPath;
  private String  _mode;
  private boolean _focused;

  private boolean _childrenDeletable     = false;
  private boolean _childrenDraggable     = false;
  private boolean _childrenSelectable    = false;
  private boolean _childrenClickable     = false;
  private boolean _childrenLongClickable = false;

  private String  _diffableMarkerPath    = null;
  private String  _diffablePrimaryPath   = null;
  private boolean _diffableMaster        = false;

  public MBPanel(MBPanelDefinition definition, MBDocument document, MBComponentContainer parent)
  {
    this(definition, document, parent, true);
  }

  public MBPanel(MBPanelDefinition definition, MBDocument document, MBComponentContainer parent, boolean buildViewStructure)
  {
    super(definition, document, parent);
    setTitle(definition.getTitle());
    setType(definition.getType());
    setWidth(definition.getWidth());
    setHeight(definition.getHeight());
    setOutcomeName(substituteExpressions(definition.getOutcomeName()));
    setPath(definition.getPath());
    setMode(definition.getMode());
    parsePermissions(definition.getPermissions());
    setFocused(definition.isFocused());

    if (buildViewStructure)
    {
      buildChildren(definition, document, parent);
      if (Constants.C_MATRIX.equals(_type) || Constants.C_MATRIXROW.equals(_type))
      {
        processDiffResponsibility();
      }
    }
  }

  final protected void buildChildren(MBPanelDefinition definition, MBDocument document, MBComponentContainer parent)
  {
    for (MBDefinition def : definition.getChildren())
    {
      String parentAbsoluteDataPath = null;
      if (parent != null)
      {
        parentAbsoluteDataPath = parent.getAbsoluteDataPath();
      }

      if (def.isPreConditionValid(document, parentAbsoluteDataPath))
      {
        addChild(MBComponentFactory.getComponentFromDefinition(def, document, this));
      }
    }
  }

  private void processDiffResponsibility()
  {
    if (getDiffableMarkerPath() == null || getDiffablePrimaryPath() == null)
    {
      if (!Constants.C_MATRIX.equals(getType()))
      {
        // assume inter-row diffables so make the matrix panel the master and move diffable knowledge to the parent
        setDiffableMaster(false);
        MBPanel parent = getFirstParentPanelWithType(Constants.C_MATRIX);
        if (parent == null)
        {
          parent = getFirstParentPanelWithType(Constants.C_EDITABLEMATRIX);
        }
        parent.setDiffableMaster(true);
        if (getDiffableMarkerPath() != null)
        {
          parent.setDiffableMarkerPath(getDiffableMarkerPath());
        }

        if (getDiffablePrimaryPath() != null)
        {
          parent.setDiffablePrimaryPath(getDiffablePrimaryPath());
        }
      }
    }
    else
    {
      setDiffableMaster(true);
    }
  }

  public String getType()
  {
    return _type;
  }

  public void setType(String type)
  {
    _type = type;
  }

  public String getTitle()
  {
    String result = _title;

    if (_title != null)
    {
      result = _title;
    }
    else
    {
      MBPanelDefinition definition = (MBPanelDefinition) getDefinition();
      if (definition.getTitle() != null)
      {
        result = definition.getTitle();
      }
      else if (definition.getTitlePath() != null)
      {
        String path = definition.getTitlePath();
        if (!path.startsWith("/"))
        {
          path = getAbsoluteDataPath() + "/" + path;
        }

        result = (String) getDocument().getValueForPath(path);
        //        return result;
      }
    }

    return MBLocalizationService.getInstance().getTextForKey(result);
  }

  //This will translate any expression that are part of the path to their actual values
  @Override
  public void translatePath()
  {
    _translatedPath = substituteExpressions(getAbsoluteDataPath());
    super.translatePath();
  }

  @Override
  public String getAbsoluteDataPath()
  {
    if (_translatedPath != null)
    {
      return _translatedPath;
    }

    return super.getAbsoluteDataPath();
  }

  @Override
  public ViewGroup buildViewWithMaxBounds(MBViewState viewState)
  {
    return MBViewBuilderFactory.getInstance().getPanelViewBuilder().buildPanelView(this, viewState);
  }

  @Override
  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    StringUtilities.appendIndentString(appendToMe, level)//
        .append("<MBPanel ")//
        .append(attributeAsXml("type", _type))//
        .append(" ")//
        .append(attributeAsXml("title", _title))//
        .append(" ")//
        .append(attributeAsXml("width", _width))//
        .append(" ")//
        .append(attributeAsXml("height", _height))//
        .append(" ")//
        .append(attributeAsXml("outcomeName", getOutcomeName()))//
        .append(" ")//
        .append(attributeAsXml("focused", isFocused()))//
        .append(" ")//
        .append(attributeAsXml("mode", getMode()))//
        .append(" ")//
        .append(attributeAsXml("path", getPath()))//
        .append(">\n");

    childrenAsXmlWithLevel(appendToMe, level + 2);
    return StringUtilities.appendIndentString(appendToMe, level).append("</MBPanel>\n");
  }

  @Override
  public String toString()
  {
    StringBuffer rt = new StringBuffer();
    return asXmlWithLevel(rt, 0).toString();
  }

  public void setTitle(String title)
  {
    _title = title;
  }

  public int getWidth()
  {
    return _width;
  }

  public void setWidth(int width)
  {
    _width = width;
  }

  public int getHeight()
  {
    return _height;
  }

  public void setHeight(int height)
  {
    _height = height;
  }

  public void setOutcomeName(String outcomeName)
  {
    _outcomeName = outcomeName;
  }

  public String getOutcomeName()
  {
    return _outcomeName;
  }

  public void setPath(String path)
  {
    _path = path;
  }

  public String getPath()
  {
    return _path;
  }

  public void rebuild()
  {
    getChildren().clear();
    MBPanelDefinition panelDef = (MBPanelDefinition) getDefinition();
    for (MBDefinition def : panelDef.getChildren())
    {
      String absoluteDataPath = null;

      if (getParent() != null)
      {
        absoluteDataPath = getParent().getAbsoluteDataPath();
      }

      if (def.isPreConditionValid(getDocument(), absoluteDataPath))
      {
        addChild(MBComponentFactory.getComponentFromDefinition(def, getDocument(), this));
      }
    }
  }

  @Override
  public int getLeftInset()
  {
    if (getType().equals("LIST") || getType().equals("MATRIX"))
    {
      return 10;
    }
    else
    {
      return super.getLeftInset();
    }
  }

  @Override
  public int getBottomInset()
  {
    if (getType().equals("LIST") || getType().equals("MATRIX"))
    {
      return 10;
    }
    else
    {
      return super.getBottomInset();
    }
  }

  @Override
  public int getRightInset()
  {
    if (getType().equals("LIST") || getType().equals("MATRIX"))
    {
      return 10;
    }
    else
    {
      return super.getRightInset();
    }
  }

  @Override
  public int getTopInset()
  {
    if (getType().equals("LIST") || getType().equals("MATRIX"))
    {
      return 0;
    }
    else
    {
      return super.getTopInset();
    }
  }

  public String getMode()
  {
    return _mode;
  }

  public void setMode(String mode)
  {
    _mode = mode;
  }

  public boolean isFocused()
  {
    return _focused;
  }

  private void setFocused(boolean focused)
  {
    _focused = focused;
  }

  public void parsePermissions(String permissions)
  {
    if (permissions != null)
    {
      String[] permissionList = permissions.split("\\|");
      for (String permission : permissionList)
      {
        if (permission.equals(Constants.C_EDITABLEMATRIX_PERMISSION_DELETE))
        {
          _childrenDeletable = true;
        }
        else if (permission.equals(Constants.C_EDITABLEMATRIX_PERMISSION_DRAGGABLE))
        {
          _childrenDraggable = true;
        }
        else if (permission.equals(Constants.C_EDITABLEMATRIX_PERMISSION_SELECTABLE))
        {
          _childrenSelectable = true;
        }
        else if (permission.equals(Constants.C_EDITABLEMATRIX_PERMISSION_CLICKABLE))
        {
          _childrenClickable = true;
        }
        else if (permission.equals(Constants.C_EDITABLEMATRIX_PERMISSION_LONGCLICKABLE))
        {
          _childrenLongClickable = true;
        }
      }
    }

  }

  public boolean isChildrenDeletable()
  {
    return _childrenDeletable;
  }

  public boolean isChildrenDraggable()
  {
    return _childrenDraggable;
  }

  public boolean isChildrenSelectable()
  {
    return _childrenSelectable;
  }

  public boolean isChildrenClickable()
  {
    return _childrenClickable;
  }

  public boolean isChildrenLongClickable()
  {
    return _childrenLongClickable;
  }

  public void setDiffableMaster(boolean diffableMaster)
  {
    _diffableMaster = diffableMaster;
  }

  public boolean isDiffableMaster()
  {
    return _diffableMaster;
  }

  public void setDiffableMarkerPath(String diffableMarkerPath)
  {
    _diffableMarkerPath = diffableMarkerPath;
  }

  public String getDiffableMarkerPath()
  {
    return _diffableMarkerPath;
  }

  public void setDiffablePrimaryPath(String diffablePrimaryPath)
  {
    _diffablePrimaryPath = diffablePrimaryPath;
  }

  public String getDiffablePrimaryPath()
  {
    return _diffablePrimaryPath;
  }

  // android.view.View.OnClickListener method

  @Override
  public void onClick(View v)
  {
    if (MBDevice.getInstance().isTablet())
    {
      View selectedView = getPage().getSelectedView();

      if (selectedView != null)
      {
        selectedView.setSelected(false);
      }
      getPage().setSelectedView(v);
      v.setSelected(true);
    }

    if (getPath() != null)
    {
      handleOutcome(getOutcomeName(), getAbsoluteDataPath() + "/" + getPath());
    }
    else
    {
      handleOutcome(getOutcomeName(), getAbsoluteDataPath());
    }
  }

}

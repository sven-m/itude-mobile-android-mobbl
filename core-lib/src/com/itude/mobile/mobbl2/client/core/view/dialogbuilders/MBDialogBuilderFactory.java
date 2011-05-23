package com.itude.mobile.mobbl2.client.core.view.dialogbuilders;

public class MBDialogBuilderFactory
{
  private static MBDialogBuilderFactory _instance;

  private MBSingleDialogBuilder         _singleDialogBuilder;
  private MBSplitDialogBuilder          _splitDialogBuilder;

  private MBDialogBuilderFactory()
  {
    setSingleDialogBuilder(new MBSingleDialogBuilder());
    setSplitDialogBuilder(new MBSplitDialogBuilder());
  }

  public static MBDialogBuilderFactory getInstance()
  {
    if (_instance == null)
    {
      synchronized (MBDialogBuilderFactory.class)
      {
        if (_instance == null) _instance = new MBDialogBuilderFactory();
      }
    }

    return _instance;
  }

  public void setSingleDialogBuilder(MBSingleDialogBuilder singleDialogBuilder)
  {
    _singleDialogBuilder = singleDialogBuilder;
  }

  public MBSingleDialogBuilder getSingleDialogBuilder()
  {
    return _singleDialogBuilder;
  }

  public void setSplitDialogBuilder(MBSplitDialogBuilder splitDialogBuilder)
  {
    _splitDialogBuilder = splitDialogBuilder;
  }

  public MBSplitDialogBuilder getSplitDialogBuilder()
  {
    return _splitDialogBuilder;
  }
}

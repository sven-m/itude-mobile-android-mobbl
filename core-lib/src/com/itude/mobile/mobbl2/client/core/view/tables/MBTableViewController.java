package com.itude.mobile.mobbl2.client.core.view.tables;

import java.util.List;

import com.itude.mobile.mobbl2.client.core.view.MBPage;

public class MBTableViewController extends Object
{
  private List           _sections;
  private MBPage         _page;

  // allows subclasses to attach behaviour to a field.-(void)
  // fieldWasSelected:(MBField *)field;
  public Object initWebView()
  {
    return null;
  }

  public List getSections()
  {
    return _sections;
  }

  public void setSections(List sections)
  {
    _sections = sections;
  }

  public MBPage getPage()
  {
    return _page;
  }

  public void setPage(MBPage page)
  {
    _page = page;
  }

}

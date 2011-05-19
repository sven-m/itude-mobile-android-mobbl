package com.itude.mobile.mobbl2.client.core.controller.background;

import android.os.AsyncTask;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;

public class MBApplicationControllerBackgroundRunner extends AsyncTask<Object[], int[], Object[]>
{

  MBApplicationController _controller = null;

  public MBApplicationController getController()
  {
    return _controller;
  }

  public void setController(MBApplicationController controller)
  {
    _controller = controller;
  }

  @Override
  protected void onPostExecute(Object[] result)
  {
    // TODO Auto-generated method stub
    super.onPostExecute(result);
  }

  @Override
  protected Object[] doInBackground(Object[]... params)
  {
    // TODO Auto-generated method stub
    return null;
  }

}

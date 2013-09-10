package com.itude.mobile.mobbl2.client.core.controller.background;

import android.os.AsyncTask;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;

public class MBApplicationControllerBackgroundRunner<Result> extends AsyncTask<Object[], int[], Result>
{

  private MBApplicationController _controller = null;

  public MBApplicationController getController()
  {
    return _controller;
  }

  public void setController(MBApplicationController controller)
  {
    _controller = controller;
  }

  @Override
  protected void onPostExecute(Result result)
  {
    super.onPostExecute(result);
  }

  @Override
  protected Result doInBackground(Object[]... params)
  {
    return null;
  }

}

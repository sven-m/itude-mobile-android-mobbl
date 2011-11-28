package com.itude.mobile.mobbl2.client.core;

import android.test.suitebuilder.TestSuiteBuilder;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
* A test suite containing all tests for MorseFlash.
*/
public class AllTests extends TestSuite
{

  public static Test suite()
  {
    return new TestSuiteBuilder(AllTests.class).includeAllPackagesUnderHere().build();
  }
}

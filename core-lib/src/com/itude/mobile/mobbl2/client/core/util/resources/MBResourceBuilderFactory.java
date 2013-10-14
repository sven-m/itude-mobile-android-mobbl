/*
 * (C) Copyright ItudeMobile.
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
package com.itude.mobile.mobbl2.client.core.util.resources;

public class MBResourceBuilderFactory
{
  private static MBResourceBuilderFactory _instance;

  private MBResourceBuilder               _resourceBuilder;
  private MBBundleBuilder                 _bundleResourceBuilder;

  private MBResourceBuilderFactory()
  {
    _resourceBuilder = new MBResourceBuilder();
    _bundleResourceBuilder = new MBBundleBuilder();
  }

  public static MBResourceBuilderFactory getInstance()
  {
    if (_instance == null)
    {
      _instance = new MBResourceBuilderFactory();
    }

    return _instance;
  }

  public MBResourceBuilder getResourceBuilder()
  {
    return _resourceBuilder;
  }

  public void setResourceBuilder(MBResourceBuilder resourceBuilder)
  {
    _resourceBuilder = resourceBuilder;
  }

  public MBBundleBuilder getBundleResourceBuilder()
  {
    return _bundleResourceBuilder;
  }

  public void setBundleResourceBuilder(MBBundleBuilder bundleResourceBuilder)
  {
    _bundleResourceBuilder = bundleResourceBuilder;
  }
}

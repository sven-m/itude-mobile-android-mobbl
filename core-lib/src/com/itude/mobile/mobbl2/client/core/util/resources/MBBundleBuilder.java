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

import com.itude.mobile.android.util.AssertUtil;
import com.itude.mobile.mobbl2.client.core.MBException;
import com.itude.mobile.mobbl2.client.core.view.MBBundle;
import com.itude.mobile.mobbl2.client.core.view.builders.MBBuilderRegistry;

public class MBBundleBuilder
{
  private final MBBuilderRegistry<MBBundle, Builder, Object> _builders;

  public MBBundleBuilder()
  {
    _builders = new MBBuilderRegistry<MBBundle, MBBundleBuilder.Builder, Object>();
    registerBuilders();
  }

  private void registerBuilders()
  {
    _builders.registerBuilder(null, new MBLanguageBundleBuilder());
  }

  public void registerBuilder(String type, Builder builder)
  {
    _builders.registerBuilder(type, builder);
  }

  public void registerBuilder(String type, String style, Builder builder)
  {
    _builders.registerBuilder(type, style, builder);
  }

  public MBBundle buildBundle(MBBundle bundle)
  {
    AssertUtil.notNull("bundle", bundle);
    Builder builder = _builders.getBuilder(bundle.getClass());
    if (builder == null) throw new MBException("No bundle builder found for bundle " + bundle);

    return builder.buildBundle(bundle);
  }

  //////////////

  public static interface Builder
  {
    public MBBundle buildBundle(MBBundle resource);
  }
}

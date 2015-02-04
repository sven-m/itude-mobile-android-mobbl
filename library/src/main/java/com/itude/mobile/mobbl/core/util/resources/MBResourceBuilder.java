/*
 * (C) Copyright Itude Mobile B.V., The Netherlands
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
package com.itude.mobile.mobbl.core.util.resources;

import com.itude.mobile.android.util.AssertUtil;
import com.itude.mobile.mobbl.core.MBException;
import com.itude.mobile.mobbl.core.view.MBResource;
import com.itude.mobile.mobbl.core.view.builders.MBBuilderRegistry;

public class MBResourceBuilder
{
  private final MBBuilderRegistry<MBResource, Builder<?>, String> _builders;

  public MBResourceBuilder()
  {
    _builders = new MBBuilderRegistry<MBResource, MBResourceBuilder.Builder<?>, String>();
    registerBuilders();
  }

  private void registerBuilders()
  {
    _builders.registerBuilder("IMAGE", new MBImageResourceBuilder());
    _builders.registerBuilder("STATEDIMAGE", new MBStatedImageResourceBuilder());
    _builders.registerBuilder("STATEDIMAGE", "COLOR", new MBColorStatedResourceBuilder());
    _builders.registerBuilder("STATEDIMAGE", "RADIOGROUP", new MBRadioGroupStatedResourceBuilder());
    _builders.registerBuilder("REMOTE_IMAGE", new MBRemoteImageResourceBuilder());
    _builders.registerBuilder("LAYEREDIMAGE", new MBLayeredImageResourceBuilder());
    _builders.registerBuilder("COLOR", new MBColorResourceBuilder());
  }

  public void registerBuilder(String type, Builder<?> builder)
  {
    _builders.registerBuilder(type, builder);
  }

  public void registerBuilder(String type, String style, Builder<?> builder)
  {
    _builders.registerBuilder(type, style, builder);
  }

  public <T> T buildResource(MBResource resource)
  {
    AssertUtil.notNull("resource", resource);
    Builder<?> builder = _builders.getBuilder(resource.getType(), resource.getViewType());
    if (builder == null) throw new MBException("No resource builder found for resource " + resource);

    return (T) builder.buildResource(resource);
  }

  //////////////

  public static interface Builder<T>
  {
    public T buildResource(MBResource resource);
  }
}

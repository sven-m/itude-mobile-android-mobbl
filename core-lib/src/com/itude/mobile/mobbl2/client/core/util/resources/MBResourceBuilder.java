package com.itude.mobile.mobbl2.client.core.util.resources;

import com.itude.mobile.android.util.AssertUtil;
import com.itude.mobile.mobbl2.client.core.MBException;
import com.itude.mobile.mobbl2.client.core.view.MBResource;
import com.itude.mobile.mobbl2.client.core.view.builders.MBBuilderRegistry;

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
    _builders.registerBuilder("REMOTE_IMAGE", new MBRemoteImageResourceBuilder());
    _builders.registerBuilder("LAYEREDIMAGE", new MBLayeredImageResourceBuilder());
    _builders.registerBuilder("COLOR", new MBColorResourceBuilder());
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

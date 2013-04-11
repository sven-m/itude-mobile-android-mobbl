package com.itude.mobile.mobbl2.client.core.util.resources;

import com.itude.mobile.android.util.AssertUtil;
import com.itude.mobile.mobbl2.client.core.MBException;
import com.itude.mobile.mobbl2.client.core.view.MBColorResource;
import com.itude.mobile.mobbl2.client.core.view.MBImageResource;
import com.itude.mobile.mobbl2.client.core.view.MBResource;
import com.itude.mobile.mobbl2.client.core.view.builders.MBBuilderRegistry;

public class MBResourceBuilder
{
  private final MBBuilderRegistry<MBResource, Builder<?>, Class<? extends MBResource>> _builders;

  public MBResourceBuilder()
  {
    _builders = new MBBuilderRegistry<MBResource, MBResourceBuilder.Builder<?>, Class<? extends MBResource>>();
    registerBuilders();
  }

  private void registerBuilders()
  {
    _builders.registerBuilder(MBImageResource.class, new MBImageResourceBuilder());
    _builders.registerBuilder(MBRemoteImageResource.class, new MBRemoteImageResourceBuilder());
    _builders.registerBuilder(MBColorResource.class, new MBColorResourceBuilder());
  }

  public <T> T buildResource(MBResource resource)
  {
    AssertUtil.notNull("resource", resource);
    Builder<?> builder = _builders.getBuilder(resource.getClass());
    if (builder == null) throw new MBException("No resource builder found for resource " + resource);

    return (T) builder.buildResource(resource);
  }

  //////////////

  public static interface Builder<T>
  {
    public T buildResource(MBResource resource);
  }
}

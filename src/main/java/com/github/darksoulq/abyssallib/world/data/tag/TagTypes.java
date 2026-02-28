package com.github.darksoulq.abyssallib.world.data.tag;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.server.registry.object.Holder;
import com.github.darksoulq.abyssallib.world.data.tag.impl.BlockTag;
import com.github.darksoulq.abyssallib.world.data.tag.impl.ItemTag;

public class TagTypes {
    public static final DeferredRegistry<TagType<?, ?>> TAG_TYPES = DeferredRegistry.create(Registries.TAG_TYPES, AbyssalLib.PLUGIN_ID);

    public static final Holder<TagType<?, ?>> ITEM = TAG_TYPES.register("item", id -> ItemTag.TYPE);
    public static final Holder<TagType<?, ?>> BLOCK = TAG_TYPES.register("block", id -> BlockTag.TYPE);
}
package com.github.darksoulq.abyssallib.world.data.tag;

import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.server.registry.object.Holder;

public class BuiltinTags {
    public static DeferredRegistry<ItemTag> TAGS = DeferredRegistry.create(Registries.ITEM_TAGS, "abyssallib");

    public static Holder<ItemTag> AXES = TAGS.register("axes", (id) -> new ItemTag("axes"));
    public static Holder<ItemTag> PICKAXES = TAGS.register("pickaxes", (id) -> new ItemTag("pickaxes"));
    public static Holder<ItemTag> SHOVELS = TAGS.register("shovels", (id) -> new ItemTag("pickaxes"));
    public static Holder<ItemTag> HOES = TAGS.register("hoes", (id) -> new ItemTag("hoes"));
    public static Holder<ItemTag> SWORDS = TAGS.register("swords", (id) -> new ItemTag("swords"));
}

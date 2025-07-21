package com.github.darksoulq.abyssallib.world.level.data.tag;

import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.server.registry.object.DeferredObject;

public class BuiltinTags {
    public static DeferredRegistry<ItemTag> TAGS = DeferredRegistry.create(Registries.ITEM_TAGS, "abyssallib");

    public static DeferredObject<ItemTag> AXES = TAGS.register("axes", (id) -> new ItemTag("axes"));
    public static DeferredObject<ItemTag> PICKAXES = TAGS.register("pickaxes", (id) -> new ItemTag("pickaxes"));
    public static DeferredObject<ItemTag> SHOVELS = TAGS.register("shovels", (id) -> new ItemTag("pickaxes"));
    public static DeferredObject<ItemTag> HOES = TAGS.register("hoes", (id) -> new ItemTag("hoes"));
    public static DeferredObject<ItemTag> SWORDS = TAGS.register("swords", (id) -> new ItemTag("swords"));
}

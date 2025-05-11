package me.darksoul.abyssallib.tags;

import me.darksoul.abyssallib.registry.Registry;
import me.darksoul.abyssallib.util.ResourceLocation;

public class TagRegistry<T> extends Registry<Tag<T>> {

    public Tag<T> createTag(ResourceLocation id) {
        Tag<T> tag = new Tag<>(id.toString());
        register(id.toString(), (unused) -> tag);
        return tag;
    }

    public void addToTag(ResourceLocation tagId, ResourceLocation entryId) {
        Tag<T> tag = get(tagId.toString());
        if (tag == null) {
            tag = createTag(tagId);
        }
        tag.add(entryId.toString());
    }

    public boolean isTagged(String tagId, String entryId) {
        Tag<T> tag = get(tagId);
        return tag != null && tag.contains(entryId);
    }
}

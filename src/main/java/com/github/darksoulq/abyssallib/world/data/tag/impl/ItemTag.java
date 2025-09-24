package com.github.darksoulq.abyssallib.world.data.tag.impl;

import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.bridge.ItemBridge;
import com.github.darksoulq.abyssallib.world.data.tag.Tag;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class ItemTag extends Tag<ItemStack> {
    public ItemTag(Identifier id) {
        super(id);
    }

    @Override
    public void add(ItemStack value) {
        values.add(ItemBridge.getIdAsString(value));
    }

    @Override
    public boolean contains(ItemStack value) {
        if (values.contains(ItemBridge.getIdAsString(value))) return true;
        for (Tag<ItemStack> tag : included) {
            if (!tag.getValues().contains(ItemBridge.getIdAsString(value))) continue;
            return true;
        }
        return false;
    }

    @Override
    public Set<ItemStack> getAll() {
        Set<ItemStack> all = new HashSet<>(values.stream().map(ItemBridge::get).toList());
        included.forEach(t ->
                all.addAll(t.getValues().stream().map(ItemBridge::get).toList()));
        return all;
    }
}

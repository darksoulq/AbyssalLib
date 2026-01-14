package com.github.darksoulq.abyssallib.world.data.tag.impl;

import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.data.tag.Tag;
import com.github.darksoulq.abyssallib.world.item.ItemPredicate;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class ItemTag extends Tag<ItemPredicate, ItemStack> {
    public ItemTag(Identifier id) {
        super(id);
    }

    @Override
    public boolean contains(ItemStack value) {
        for (ItemPredicate predicate : values) {
            if (predicate.test(value)) return true;
        }
        for (Tag<ItemPredicate, ItemStack> includedTag : included) {
            if (includedTag.contains(value)) return true;
        }
        return false;
    }

    @Override
    public Set<ItemPredicate> getAll() {
        Set<ItemPredicate> all = new HashSet<>(values);
        for (Tag<ItemPredicate, ItemStack> t : included) {
            all.addAll(t.getAll());
        }
        return all;
    }
}
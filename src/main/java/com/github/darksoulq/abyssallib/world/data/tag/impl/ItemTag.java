package com.github.darksoulq.abyssallib.world.data.tag.impl;

import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.data.tag.Tag;
import com.github.darksoulq.abyssallib.world.item.ItemPredicate;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of a {@link Tag} for {@link ItemStack}s, using {@link ItemPredicate} entries.
 */
public class ItemTag extends Tag<ItemPredicate, ItemStack> {
    /**
     * @param id The tag identifier.
     */
    public ItemTag(Identifier id) {
        super(id);
    }

    /**
     * Checks if the given ItemStack satisfies any predicate in this tag or included tags.
     *
     * @param value The {@link ItemStack} to test.
     * @return {@code true} if the stack matches.
     */
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

    /**
     * Flattens all predicates from this and included tags.
     *
     * @return A {@link Set} of all applicable {@link ItemPredicate}s.
     */
    @Override
    public Set<ItemPredicate> getAll() {
        Set<ItemPredicate> all = new HashSet<>(values);
        for (Tag<ItemPredicate, ItemStack> t : included) {
            all.addAll(t.getAll());
        }
        return all;
    }
}
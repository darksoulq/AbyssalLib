package com.github.darksoulq.abyssallib.world.item.component;

import org.bukkit.inventory.ItemStack;

/**
 * A marker interface for {@link DataComponent}s that have a direct counterpart in vanilla Minecraft.
 * <p>
 * Components implementing this interface are responsible for synchronizing their values
 * directly with the NMS/Bukkit {@link ItemStack} data component system, ensuring that
 * changes are reflected in the actual game client.
 * </p>
 */
public interface Vanilla {

    /**
     * Applies the value of this component to the specified {@link ItemStack}.
     * <p>
     * This method typically calls {@link ItemStack#setData} using the appropriate
     * vanilla {@link io.papermc.paper.datacomponent.DataComponentType}.
     * </p>
     *
     * @param stack The {@link ItemStack} to modify.
     */
    void apply(ItemStack stack);

    /**
     * Removes this specific vanilla component from the {@link ItemStack}.
     * <p>
     * This effectively unsets the data on the stack, reverting it to its
     * default state for that specific vanilla component type.
     * </p>
     *
     * @param stack The {@link ItemStack} to modify.
     */
    void remove(ItemStack stack);
}
package com.github.darksoulq.abyssallib.server.translation;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@FunctionalInterface
public interface ClientItemModifier {
    boolean modify(ItemStack item, Player player);
}
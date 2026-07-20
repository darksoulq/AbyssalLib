package com.github.darksoulq.abyssallib.world.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public record ClickContext(Player player, ClickType clickType, AbstractContainerMenu menu, Slot slot, ItemStack cursor) { }
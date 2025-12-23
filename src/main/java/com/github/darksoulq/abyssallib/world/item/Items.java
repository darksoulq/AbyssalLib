package com.github.darksoulq.abyssallib.world.item;

import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.server.event.ClickType;
import com.github.darksoulq.abyssallib.server.event.InventoryClickType;
import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.server.registry.object.Holder;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.PlayerInventory;

public class Items {
    public static final DeferredRegistry<Item> ITEMS = DeferredRegistry.create(Registries.ITEMS, "abyssallib");

    public static final Holder<Item> INVISIBLE_ITEM = ITEMS.register("invisible", (id) -> new Item(id, Material.STICK));
    public static final Holder<Item> BACKWARD = ITEMS.register("backward", (id) -> new Item(id, Material.STICK));
    public static final Holder<Item> FORWARD = ITEMS.register("forward", (id) -> new Item(id, Material.STICK));
}

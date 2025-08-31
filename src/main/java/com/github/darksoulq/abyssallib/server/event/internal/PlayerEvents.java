package com.github.darksoulq.abyssallib.server.event.internal;

import com.github.darksoulq.abyssallib.server.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.server.packet.PacketInterceptor;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.entity.data.EntityAttributes;
import com.github.darksoulq.abyssallib.world.item.Item;
import io.papermc.paper.event.player.PlayerPickBlockEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class PlayerEvents {

    @SubscribeEvent
    public void onJoin(PlayerJoinEvent event) {
        PacketInterceptor.inject(event.getPlayer());
        EntityAttributes.of(event.getPlayer()).load();
    }

    @SubscribeEvent
    public void onLeave(PlayerQuitEvent event) {
        PacketInterceptor.uninject(event.getPlayer());
    }

    @SubscribeEvent
    public void onPick(PlayerPickBlockEvent event) {
        if (CustomBlock.from(event.getBlock()) != null) {
            event.setCancelled(true);
            Item item = CustomBlock.asItem(CustomBlock.from(event.getBlock()));
            if (item == null) return;
            ItemStack stack = item.getStack().clone();
            HashMap<Integer, ItemStack> remaining = event.getPlayer().getInventory().addItem(stack);
            if (!remaining.isEmpty()) {
                stack = stack.clone();
                stack.setAmount(remaining.values().stream().toList().getFirst().getAmount());
                event.getPlayer().getInventory().setItem(EquipmentSlot.HAND,
                        stack);
            }
        }
    }

    private boolean isBlockSolidBelow(Player player) {
        Location loc = player.getLocation().clone().subtract(0, 0.1, 0);
        Block blockBelow = loc.getBlock();
        return blockBelow.getType().isSolid();
    }

    private boolean isWaterBlock(Block block) {
        Material type = block.getType();
        return type == Material.WATER || type == Material.KELP_PLANT || type == Material.SEAGRASS;
    }
}

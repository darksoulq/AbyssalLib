package com.github.darksoulq.abyssallib.server.event.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.util.TextUtil;
import com.github.darksoulq.abyssallib.server.event.EventBus;
import com.github.darksoulq.abyssallib.server.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.server.event.custom.block.BlockInteractionEvent;
import com.github.darksoulq.abyssallib.server.event.custom.server.PacketReceiveEvent;
import com.github.darksoulq.abyssallib.server.packet.PacketIO;
import com.github.darksoulq.abyssallib.server.packet.PacketInterceptor;
import com.github.darksoulq.abyssallib.server.translation.internal.PacketTranslator;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.data.statistic.PlayerStatistics;
import com.github.darksoulq.abyssallib.world.entity.data.EntityAttributes;
import com.github.darksoulq.abyssallib.world.item.Item;
import io.papermc.paper.event.player.PlayerPickBlockEvent;
import net.kyori.adventure.text.Component;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundRecipeBookAddPacket;
import net.minecraft.network.protocol.game.ClientboundRecipeBookRemovePacket;
import net.minecraft.network.protocol.game.ClientboundRecipeBookSettingsPacket;
import net.minecraft.network.protocol.game.GamePacketTypes;
import net.minecraft.stats.RecipeBookSettings;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public class PlayerEvents {
    @SubscribeEvent(ignoreCancelled = false)
    public void onInteract(PlayerInteractEvent event) {
        CustomBlock block = CustomBlock.resolve(event.getClickedBlock());
        if (block == null || event.getPlayer().isSneaking()) return;
        BlockInteractionEvent be = EventBus.post(new BlockInteractionEvent(
            event.getPlayer(),
            block,
            event.getBlockFace(),
            event.getInteractionPoint(),
            event.getAction(),
            event.getItem()
        ));
        if (be.isCancelled()) event.setCancelled(true);
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onJoin(PlayerJoinEvent event) {
        PacketInterceptor.inject(event.getPlayer());
        EntityAttributes.of(event.getPlayer());
        PlayerStatistics.of(event.getPlayer());
        if (AbyssalLib.PERMISSION_MANAGER != null) {
            AbyssalLib.PERMISSION_MANAGER.handleJoin(event.getPlayer());
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onLeave(PlayerQuitEvent event) {
        PacketInterceptor.uninject(event.getPlayer());
        if (AbyssalLib.PERMISSION_MANAGER != null) {
            AbyssalLib.PERMISSION_MANAGER.handleQuit(event.getPlayer());
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onPick(PlayerPickBlockEvent event) {
        CustomBlock block = CustomBlock.resolve(event.getBlock());
        if (block != null) {
            event.setCancelled(true);
            Item item = CustomBlock.asItem(block);
            if (item == null) return;
            ItemStack stack = item.getStack().clone();
            HashMap<Integer, ItemStack> remaining = event.getPlayer().getInventory().addItem(stack);
            if (!remaining.isEmpty()) {
                stack = stack.clone();
                stack.setAmount(remaining.values().stream().toList().getFirst().getAmount());
                event.getPlayer().getInventory().setItem(EquipmentSlot.HAND, stack);
            }
        }
    }
}
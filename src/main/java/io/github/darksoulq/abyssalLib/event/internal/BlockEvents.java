package io.github.darksoulq.abyssalLib.event.internal;

import io.github.darksoulq.abyssalLib.block.Block;
import io.github.darksoulq.abyssalLib.block.BlockManager;
import io.github.darksoulq.abyssalLib.event.SubscribeEvent;
import io.github.darksoulq.abyssalLib.event.context.block.BlockBreakContext;
import io.github.darksoulq.abyssalLib.event.context.block.BlockInteractContext;
import io.github.darksoulq.abyssalLib.event.context.block.BlockPlaceContext;
import io.github.darksoulq.abyssalLib.event.custom.AbyssalBlockBreakEvent;
import io.github.darksoulq.abyssalLib.event.custom.AbyssalBlockInteractEvent;
import io.github.darksoulq.abyssalLib.event.custom.AbyssalBlockPlaceEvent;
import io.github.darksoulq.abyssalLib.item.Item;
import io.github.darksoulq.abyssalLib.loot.LootTable;
import io.github.darksoulq.abyssalLib.registry.BuiltinRegistries;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Random;

public class BlockEvents {
    @SubscribeEvent
    public void onBlockPlace(BlockPlaceEvent event) {
        for (Block block : BuiltinRegistries.BLOCKS.getAll()) {
            if (block.blockItem() == null) continue;
            if (block.blockItem() == Item.from(event.getItemInHand())) {
                AbyssalBlockPlaceEvent breakEvent = new AbyssalBlockPlaceEvent(event.getPlayer(), block, event.getItemInHand());
                Bukkit.getPluginManager().callEvent(breakEvent);
                if (breakEvent.isCancelled()) {
                    event.setCancelled(true);
                    return;
                }
                block.place(new BlockPlaceContext(
                        event.getBlock(),
                        event.getPlayer(),
                        Item.from(event.getItemInHand()),
                        event.getHand(),
                        event
                ));
            }
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = Block.from(event.getBlock());
        AbyssalBlockBreakEvent breakEvent = new AbyssalBlockBreakEvent(event.getPlayer(), block);
        Bukkit.getPluginManager().callEvent(breakEvent);
        if (breakEvent.isCancelled()) {
            event.setCancelled(true);
            return;
        }
        Location loc = event.getBlock().getLocation();
        if (block == null) return;
        Item item = block.blockItem();
        if (item == null) return;
        LootTable lootTable = block.lootTable();
        event.setDropItems(false);
        if (!(event.getPlayer().getGameMode() == GameMode.CREATIVE)) {
            event.setExpToDrop(block.exp());
            if (lootTable == null) {
                loc.getWorld().dropItem(loc, item);
            } else {
                lootTable.generateLoot(new Random()).forEach(stack -> {
                    loc.getWorld().dropItem(loc, item);
                });
            }
        }
        block.onBreak(new BlockBreakContext(
                event.getPlayer(),
                event.getBlock(),
                event
        ));
        BlockManager.INSTANCE.removeBlockAt(event.getBlock().getLocation());
    }

    @SubscribeEvent
    public void onBlockInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (Block.from(event.getClickedBlock()) != null) {
            if (event.getAction().isRightClick()) {
                event.setCancelled(true);
            }
            AbyssalBlockInteractEvent breakEvent = new AbyssalBlockInteractEvent(
                    event.getPlayer(),
                    Block.from(event.getClickedBlock()),
                    event.getBlockFace(),
                    event.getInteractionPoint(),
                    event.getAction(),
                    event.getItem());
            Bukkit.getPluginManager().callEvent(breakEvent);
            if (breakEvent.isCancelled()) {
                event.setCancelled(true);
                return;
            }
            Block.from(event.getClickedBlock()).onInteract(new BlockInteractContext(
                    event.getPlayer(),
                    event.getClickedBlock(),
                    event.getBlockFace(),
                    event.getInteractionPoint(),
                    event.getAction(),
                    event.getHand(),
                    event.getItem(),
                    event
            ));
        }
    }

    @SubscribeEvent
    public void onEntityMove(EntityMoveEvent event) {
        Block block = Block.from(event.getTo().clone().add(0, -1, 0).getBlock());
        if (block != null && event.hasChangedBlock()) {
            block.onStep(event.getEntity());
        }
    }

    @SubscribeEvent
    public void onPlayerMove(PlayerMoveEvent event) {
        Block block = Block.from(event.getTo().clone().add(0, -1, 0).getBlock());
        if (block != null && event.hasChangedBlock()) {
            block.onStep(event.getPlayer());
        }
    }

    @SubscribeEvent
    public void onExplode(BlockExplodeEvent event) {
        for (org.bukkit.block.Block bukkitBlock : event.blockList()) {
            Block block = Block.from(bukkitBlock);
            if (block == null) continue;
            block.onExplode();
        }
    }

    @SubscribeEvent
    public void onExplode(EntityExplodeEvent event) {
        for (org.bukkit.block.Block bukkitBlock : event.blockList()) {
            Block block = Block.from(bukkitBlock);
            if (block == null) continue;
            block.onExplode();
        }
    }

    @SubscribeEvent
    public void onProjectileHit(ProjectileHitEvent event) {
        Block block = Block.from(event.getHitBlock());
        if (block == null) return;
        block.onProjectileHit();
    }
}

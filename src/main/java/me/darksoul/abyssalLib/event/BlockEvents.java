package me.darksoul.abyssalLib.event;

import me.darksoul.abyssalLib.block.Block;
import me.darksoul.abyssalLib.block.BlockManager;
import me.darksoul.abyssalLib.event.context.BlockBreakContext;
import me.darksoul.abyssalLib.event.context.BlockInteractContext;
import me.darksoul.abyssalLib.event.context.BlockPlaceContext;
import me.darksoul.abyssalLib.event.custom.AbyssalBlockBreakEvent;
import me.darksoul.abyssalLib.event.custom.AbyssalBlockInteractEvent;
import me.darksoul.abyssalLib.event.custom.AbyssalBlockPlaceEvent;
import me.darksoul.abyssalLib.item.Item;
import me.darksoul.abyssalLib.loot.LootTable;
import me.darksoul.abyssalLib.registry.BuiltinRegistries;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

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
}

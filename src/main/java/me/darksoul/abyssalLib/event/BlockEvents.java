package me.darksoul.abyssalLib.event;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import me.darksoul.abyssalLib.block.Block;
import me.darksoul.abyssalLib.block.BlockManager;
import me.darksoul.abyssalLib.event.context.BlockBreakContext;
import me.darksoul.abyssalLib.event.context.BlockInteractContext;
import me.darksoul.abyssalLib.event.context.BlockPlaceContext;
import me.darksoul.abyssalLib.item.Item;
import me.darksoul.abyssalLib.loot.LootTable;
import me.darksoul.abyssalLib.registry.BuiltinRegistries;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.Event;
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
            event.setUseInteractedBlock(Event.Result.DENY);
            event.setUseItemInHand(Event.Result.DENY);
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

        if (event.getPlayer().getGameMode() == GameMode.CREATIVE && event.getAction().isLeftClick()) return;
        event.setCancelled(true);
    }
}

package com.github.darksoulq.abyssallib.event.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.block.Block;
import com.github.darksoulq.abyssallib.block.BlockManager;
import com.github.darksoulq.abyssallib.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.event.context.block.*;
import com.github.darksoulq.abyssallib.event.custom.block.AbyssalBlockBreakEvent;
import com.github.darksoulq.abyssallib.event.custom.block.AbyssalBlockInteractEvent;
import com.github.darksoulq.abyssallib.event.custom.block.AbyssalBlockPlaceEvent;
import com.github.darksoulq.abyssallib.item.Item;
import com.github.darksoulq.abyssallib.loot.LootContext;
import com.github.darksoulq.abyssallib.loot.LootTable;
import com.github.darksoulq.abyssallib.registry.BuiltinRegistries;
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
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BlockEvents {
    @SubscribeEvent
    public void onBlockPlace(BlockPlaceEvent event) {
        for (Block block : BuiltinRegistries.BLOCKS.getAll()) {
            if (Block.asItem(block) != null ) {
                if (Block.asItem(block) == Item.from(event.getItemInHand())) {
                    AbyssalBlockPlaceEvent placeEvent = new AbyssalBlockPlaceEvent(event.getPlayer(), block, event.getItemInHand());
                    Bukkit.getPluginManager().callEvent(placeEvent);
                    if (placeEvent.isCancelled()) {
                        event.setCancelled(true);
                        return;
                    }
                    block.place(new BlockPlaceContext(event));
                    AbyssalLib.getInstance().getLogger().info(BlockManager.INSTANCE.blockCache().toString());
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = Block.from(event.getBlock());
        if (block != null) {
            AbyssalBlockBreakEvent breakEvent = new AbyssalBlockBreakEvent(event.getPlayer(), block);
            Bukkit.getPluginManager().callEvent(breakEvent);
            if (breakEvent.isCancelled()) {
                event.setCancelled(true);
                return;
            }
            Location loc = event.getBlock().getLocation();
            Item item = Block.asItem(block);
            LootTable lootTable = block.lootTable();
            event.setDropItems(false);
            if (!(event.getPlayer().getGameMode() == GameMode.CREATIVE)) {
                event.setExpToDrop(block.exp());
                if (lootTable == null && item != null) {
                    loc.getWorld().dropItem(loc, item.stack());
                } else if (lootTable != null) {
                    LootContext context = new LootContext();
                    List<ItemStack> drops = lootTable.generate(context);
                    for (ItemStack drop : drops) {
                        loc.getWorld().dropItem(loc, drop);
                    }
                }
            }
            block.onBreak(new BlockBreakContext(event));
            BlockManager.INSTANCE.removeBlockAt(loc);
        }
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
            Block.from(event.getClickedBlock()).onInteract(new BlockInteractContext(event));
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
            ExplodeContext ctx = new BlockExplodeContext(event);
            block.onExplode(ctx);
            if (!ctx.shouldExplode()) {
                event.blockList().remove(bukkitBlock);
            }
        }
    }

    @SubscribeEvent
    public void onExplode(EntityExplodeEvent event) {
        for (org.bukkit.block.Block bukkitBlock : event.blockList()) {
            Block block = Block.from(bukkitBlock);
            if (block == null) continue;
            ExplodeContext ctx = new EntityExplodeContext(event);
            block.onExplode(ctx);
            if (!ctx.shouldExplode()) {
                event.blockList().remove(bukkitBlock);
            }
        }
    }

    @SubscribeEvent
    public void onProjectileHit(ProjectileHitEvent event) {
        Block block = Block.from(event.getHitBlock());
        if (block == null) return;
        block.onProjectileHit();
    }
}

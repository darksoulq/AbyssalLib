package com.github.darksoulq.abyssallib.server.event.internal;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.server.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.server.event.custom.block.AbyssalBlockBreakEvent;
import com.github.darksoulq.abyssallib.server.event.custom.block.AbyssalBlockPlaceEvent;
import com.github.darksoulq.abyssallib.server.registry.BuiltinRegistries;
import com.github.darksoulq.abyssallib.world.level.block.Block;
import com.github.darksoulq.abyssallib.world.level.block.BlockManager;
import com.github.darksoulq.abyssallib.world.level.data.loot.LootContext;
import com.github.darksoulq.abyssallib.world.level.data.loot.LootTable;
import com.github.darksoulq.abyssallib.world.level.item.Item;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BlockEvents {

    @SubscribeEvent
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack handItem = event.getItemInHand();

        for (Block block : BuiltinRegistries.BLOCKS.getAll().values()) {
            Item blockItem = Block.asItem(block);
            if (blockItem == null) continue;

            if (blockItem.equals(Item.from(handItem))) {
                AbyssalBlockPlaceEvent placeEvent = new AbyssalBlockPlaceEvent(event.getPlayer(), block, handItem);
                AbyssalLib.EVENT_BUS.post(placeEvent);
                if (placeEvent.isCancelled()) {
                    event.setCancelled(true);
                    return;
                }
                ActionResult result = block.onPlaced(event.getPlayer(), event.getBlock().getLocation(),
                        handItem);
                if (result == ActionResult.CANCEL) {
                    event.setCancelled(true);
                    return;
                }

                block.place(event.getBlock());
                return;
            }
        }
        event.setCancelled(true);
    }

    @SubscribeEvent
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = Block.from(event.getBlock());
        if (block == null) return;

        AbyssalBlockBreakEvent breakEvent = new AbyssalBlockBreakEvent(event.getPlayer(), block);
        Bukkit.getPluginManager().callEvent(breakEvent);
        if (breakEvent.isCancelled()) {
            event.setCancelled(true);
            return;
        }
        ActionResult result = block.onBreak(event.getPlayer(), event.getBlock().getLocation(),
                event.getPlayer().getActiveItem());
        if (result == ActionResult.CANCEL) {
            event.setCancelled(true);
            return;
        }

        Location loc = event.getBlock().getLocation();
        Item blockItem = Block.asItem(block);
        LootTable lootTable = block.lootTable();

        event.setDropItems(false);

        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setExpToDrop(block.exp());
            if (lootTable == null) {
                if (blockItem != null) {
                    loc.getWorld().dropItem(loc, blockItem.stack());
                }
            } else {
                LootContext context = new LootContext();
                List<ItemStack> drops = lootTable.generate(context);
                for (ItemStack drop : drops) {
                    loc.getWorld().dropItem(loc, drop);
                }
            }
        }
        BlockManager.INSTANCE.remove(loc);
    }

    @SubscribeEvent
    public void onEntityMove(EntityMoveEvent event) {
        if (!event.hasChangedBlock()) return;

        Block block = Block.from(event.getTo().clone().add(0, -1, 0).getBlock());
        if (block != null) {
            if (event.getEntity().getFallDistance() > 1) {
                block.onLanded(event.getEntity());
            } else {
                block.onSteppedOn(event.getEntity());
            }
        }
    }

    @SubscribeEvent
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!event.hasChangedBlock()) return;

        Block block = Block.from(event.getTo().clone().add(0, -1, 0).getBlock());
        if (block != null) {
            if (event.getPlayer().getFallDistance() > 1) {
                block.onLanded(event.getPlayer());
            } else {
                block.onSteppedOn(event.getPlayer());
            }
        }
    }

    @SubscribeEvent
    public void onBlockExplode(BlockExplodeEvent event) {
        Iterator<org.bukkit.block.Block> it = event.blockList().iterator();
        while (it.hasNext()) {
            org.bukkit.block.Block bukkitBlock = it.next();
            Block block = Block.from(bukkitBlock);
            if (block == null) continue;

            ActionResult result = block.onDestroyedByExplosion(null, event.getBlock());
            if (result == ActionResult.CANCEL) {
                it.remove();
            }
        }
    }

    @SubscribeEvent
    public void onEntityExplode(EntityExplodeEvent event) {
        Iterator<org.bukkit.block.Block> it = event.blockList().iterator();
        while (it.hasNext()) {
            org.bukkit.block.Block bukkitBlock = it.next();
            Block block = Block.from(bukkitBlock);
            if (block == null) continue;

            ActionResult result = block.onDestroyedByExplosion(event.getEntity(), null);
            if (result == ActionResult.CANCEL) {
                it.remove();
            }
        }
    }

    @SubscribeEvent
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getHitBlock() == null) return;

        Block block = Block.from(event.getHitBlock());
        if (block != null) {
            block.onProjectileHit(event.getEntity());
        }
    }

    @SubscribeEvent
    public void onBlockRedstone(BlockRedstoneEvent event) {
        Block block = Block.from(event.getBlock());
        if (block == null) return;
        event.setNewCurrent(event.getOldCurrent());
    }

    @SubscribeEvent
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Block block = Block.from(event.getBlock());
        if (block == null) return;
        if (block.allowPhysics) return;
        event.setCancelled(true);
    }

    @SubscribeEvent
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for (org.bukkit.block.Block bukkitBlock : event.getBlocks()) {
            Block block = Block.from(bukkitBlock);
            if (block != null) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @SubscribeEvent
    public void onPistonRetract(BlockPistonRetractEvent event) {
        for (org.bukkit.block.Block bukkitBlock : event.getBlocks()) {
            Block block = Block.from(bukkitBlock);
            if (block != null) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @SubscribeEvent
    public void onBlockBurn(BlockBurnEvent event) {
        if (Block.from(event.getBlock()) != null) event.setCancelled(true);
    }

    @SubscribeEvent
    public void onBlockFade(BlockFadeEvent event) {
        if (Block.from(event.getBlock()) != null) event.setCancelled(true);
    }

    @SubscribeEvent
    public void onBlockForm(BlockFormEvent event) {
        if (Block.from(event.getBlock()) != null) event.setCancelled(true);
    }

    @SubscribeEvent
    public void onBlockGrow(BlockGrowEvent event) {
        if (Block.from(event.getBlock()) != null) event.setCancelled(true);
    }

    @SubscribeEvent
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (Block.from(event.getBlock()) != null) event.setCancelled(true);
    }

    @SubscribeEvent
    public void onBlockSpread(BlockSpreadEvent event) {
        if (Block.from(event.getBlock()) != null) event.setCancelled(true);
    }

    @SubscribeEvent
    public void onLeavesDecay(LeavesDecayEvent event) {
        if (Block.from(event.getBlock()) != null) event.setCancelled(true);
    }

    @SubscribeEvent
    public void onSpongeAbsorb(SpongeAbsorbEvent event) {
        if (Block.from(event.getBlock()) != null) event.setCancelled(true);
    }

    @SubscribeEvent
    public void onSignChange(SignChangeEvent event) {
        if (Block.from(event.getBlock()) != null) event.setCancelled(true);
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEndEvent event) {
        for (Block block : BlockManager.INSTANCE.blocks.values()) {
            if (block.entity() != null) {
                block.entity().serverTick();
                if (ThreadLocalRandom.current().nextFloat() < 0.1f) {
                    block.entity().randomTick();
                }
            }
        }
    }
}

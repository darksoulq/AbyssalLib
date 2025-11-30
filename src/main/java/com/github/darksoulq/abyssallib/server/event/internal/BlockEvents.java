package com.github.darksoulq.abyssallib.server.event.internal;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.server.event.EventBus;
import com.github.darksoulq.abyssallib.server.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.server.event.custom.block.BlockBrokenEvent;
import com.github.darksoulq.abyssallib.server.event.custom.block.BlockPlacedEvent;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.block.BlockProperties;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.block.internal.BlockManager;
import com.github.darksoulq.abyssallib.world.data.loot.LootContext;
import com.github.darksoulq.abyssallib.world.data.loot.LootTable;
import com.github.darksoulq.abyssallib.world.item.Item;
import com.github.darksoulq.abyssallib.world.item.component.builtin.BlockItem;
import com.github.darksoulq.abyssallib.world.util.BlockPersistentData;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BlockEvents {
    @SubscribeEvent(ignoreCancelled = false)
    public void onChunkLoad(ChunkLoadEvent event) {
        if (event.isNewChunk()) return;
        List<CustomBlock> blocks = BlockManager.getBlocksInChunk(event.getChunk());
        if (blocks.isEmpty()) return;
        for (CustomBlock block : blocks) {
            BlockManager.INACTIVE_BLOCKS.remove(block);
            if (!BlockManager.ACTIVE_BLOCKS.contains(block)) {
                BlockManager.ACTIVE_BLOCKS.add(block);
                block.onLoad();
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onChunkUnload(ChunkUnloadEvent event) {
        List<CustomBlock> blocks = BlockManager.getBlocksInChunk(event.getChunk());
        if (blocks.isEmpty()) return;
        for (CustomBlock block : blocks) {
            BlockManager.ACTIVE_BLOCKS.remove(block);
            if (!BlockManager.INACTIVE_BLOCKS.contains(block)) {
                BlockManager.INACTIVE_BLOCKS.add(block);
                block.onUnLoad();
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack handItem = event.getItemInHand();
        Item heldItem = Item.resolve(handItem);
        Location loc = event.getBlock().getLocation();
        if (heldItem == null) return;
        if (!heldItem.hasData(BlockItem.class)) {
            event.setCancelled(true);
            return;
        }
        Identifier blockId = (Identifier) heldItem.getData(BlockItem.class).value;
        CustomBlock block = Registries.BLOCKS.get(blockId.toString()).clone();
        if (block == null) return;
        block.place(event.getBlock(), false);
        BlockPlacedEvent placeEvent = EventBus.post(new BlockPlacedEvent(
                event.getPlayer(),
                block,
                handItem
        ));
        ActionResult result = block.onPlaced(event.getPlayer(), loc,
                handItem);
        if (!result.equals(ActionResult.CANCEL) && !placeEvent.isCancelled()) return;
        BlockManager.remove(loc, block);
        loc.getBlock().setType(Material.AIR);
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onBlockBreak(BlockBreakEvent event) {
        CustomBlock block = CustomBlock.from(event.getBlock());
        if (block == null) return;

        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();
        ItemStack stack = player.getInventory().getItemInMainHand();

        BlockProperties props = block.properties;
        boolean silkTouch = props.requireSilkTouch && stack.containsEnchantment(Enchantment.SILK_TOUCH);
        boolean allowFortune = props.allowFortune;
        int fortuneLevel = allowFortune ? stack.getEnchantmentLevel(Enchantment.FORTUNE) : 0;

        BlockBrokenEvent breakEvent = EventBus.post(new BlockBrokenEvent(player, block, fortuneLevel));
        if (breakEvent.isCancelled()) {
            event.setCancelled(true);
            return;
        }

        if (block.onBreak(player, loc, stack) == ActionResult.CANCEL) {
            event.setCancelled(true);
            return;
        }

        event.setDropItems(false);

        if (player.getGameMode() != GameMode.CREATIVE) {
            dropBlockLoot(loc, block, stack, breakEvent, silkTouch, fortuneLevel);
            event.setExpToDrop(block.getExpToDrop(player, fortuneLevel, silkTouch));
        }

        BlockManager.remove(loc, block);
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onEntityMove(EntityMoveEvent event) {
        if (!event.hasChangedBlock()) return;

        CustomBlock block = CustomBlock.from(event.getTo().clone().add(0, -1, 0).getBlock());
        if (block == null) return;
        if (event.getEntity().getFallDistance() > 1) {
            block.onLanded(event.getEntity());
        } else {
            block.onSteppedOn(event.getEntity());
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!event.hasChangedBlock()) return;

        CustomBlock block = CustomBlock.from(event.getTo().clone().add(0, -1, 0).getBlock());
        if (block == null) return;
        if (event.getPlayer().getFallDistance() > 1) {
            block.onLanded(event.getPlayer());
        } else {
            block.onSteppedOn(event.getPlayer());
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onBlockExplode(BlockExplodeEvent event) {
        Iterator<org.bukkit.block.Block> it = event.blockList().iterator();
        while (it.hasNext()) {
            org.bukkit.block.Block bukkitBlock = it.next();
            CustomBlock block = CustomBlock.from(bukkitBlock);
            if (block == null) continue;

            ActionResult result = block.onDestroyedByExplosion(null, event.getBlock());
            if (result == ActionResult.CANCEL) {
                it.remove();
            } else {
                BlockBrokenEvent breakEvent = EventBus.post(new BlockBrokenEvent(null, block, 0));
                if (breakEvent.isCancelled()) {
                    it.remove();
                    return;
                }
                dropBlockLoot(block.getLocation(), block, new ItemStack(Material.AIR),
                        breakEvent, false, 0);
                BlockManager.remove(block.getLocation(), block);
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onEntityExplode(EntityExplodeEvent event) {
        Iterator<org.bukkit.block.Block> it = event.blockList().iterator();
        while (it.hasNext()) {
            org.bukkit.block.Block bukkitBlock = it.next();
            CustomBlock block = CustomBlock.from(bukkitBlock);
            if (block == null) continue;

            ActionResult result = block.onDestroyedByExplosion(event.getEntity(), null);
            if (result == ActionResult.CANCEL) {
                it.remove();
            } else {
                BlockBrokenEvent breakEvent = EventBus.post(new BlockBrokenEvent(null, block, 0));
                if (breakEvent.isCancelled()) {
                    it.remove();
                    return;
                }
                dropBlockLoot(block.getLocation(), block, new ItemStack(Material.AIR),
                        breakEvent, false, 0);
                BlockManager.remove(block.getLocation(), block);
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getHitBlock() == null) return;

        CustomBlock block = CustomBlock.from(event.getHitBlock());
        if (block == null) return;
        block.onProjectileHit(event.getEntity());
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onBlockRedstone(BlockRedstoneEvent event) {
        CustomBlock block = CustomBlock.from(event.getBlock());
        if (block == null) return;
        int oldCurrent = event.getOldCurrent();
        int newCurrent = event.getNewCurrent();
        int finalCurrent = block.onRedstone(oldCurrent, newCurrent);
        event.setNewCurrent(finalCurrent);
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        CustomBlock block = CustomBlock.from(event.getBlock());
        if (block == null) return;
        if (block.allowPhysics) return;
        event.setCancelled(true);
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for (org.bukkit.block.Block bukkitBlock : event.getBlocks()) {
            CustomBlock block = CustomBlock.from(bukkitBlock);
            if (block != null) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        for (org.bukkit.block.Block bukkitBlock : event.getBlocks()) {
            CustomBlock block = CustomBlock.from(bukkitBlock);
            if (block != null) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onBlockBurn(BlockBurnEvent event) {
        if (CustomBlock.from(event.getBlock()) != null) event.setCancelled(true);
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onBlockFade(BlockFadeEvent event) {
        if (CustomBlock.from(event.getBlock()) != null) event.setCancelled(true);
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onBlockForm(BlockFormEvent event) {
        if (CustomBlock.from(event.getBlock()) != null) event.setCancelled(true);
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onBlockGrow(BlockGrowEvent event) {
        if (CustomBlock.from(event.getBlock()) != null) event.setCancelled(true);
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (CustomBlock.from(event.getBlock()) != null) event.setCancelled(true);
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onBlockSpread(BlockSpreadEvent event) {
        if (CustomBlock.from(event.getBlock()) != null) event.setCancelled(true);
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onLeavesDecay(LeavesDecayEvent event) {
        if (CustomBlock.from(event.getBlock()) != null) event.setCancelled(true);
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onSpongeAbsorb(SpongeAbsorbEvent event) {
        if (CustomBlock.from(event.getBlock()) != null) event.setCancelled(true);
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onSignChange(SignChangeEvent event) {
        if (CustomBlock.from(event.getBlock()) != null) event.setCancelled(true);
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onServerTick(ServerTickEndEvent event) {
        for (CustomBlock block : BlockManager.ACTIVE_BLOCKS) {
            if (block.getEntity() != null) {
                block.getEntity().serverTick();
                if (ThreadLocalRandom.current().nextFloat() < 0.001f) {
                    block.getEntity().randomTick();
                }
            }
        }
    }

    public static void dropBlockLoot(Location loc, CustomBlock block, ItemStack tool, BlockBrokenEvent breakEvent, boolean silkTouch, int fortuneLevel) {
        BlockProperties props = block.properties;

        World world = loc.getWorld();
        if (world == null) return;

        if (!breakEvent.getBaseDrops() && breakEvent.getNewDrops() != null) {
            for (ItemStack drop : breakEvent.getNewDrops()) {
                world.dropItemNaturally(loc, drop);
            }
            return;
        }

        if (breakEvent.getBaseDrops()) {
            LootTable lootTable = block.getLootTable();
            if (lootTable != null) {
                LootContext context = new LootContext(fortuneLevel);
                List<ItemStack> drops = lootTable.generate(context);
                for (ItemStack drop : drops) {
                    world.dropItemNaturally(loc, drop);
                }
                return;
            }

            if (props.requireSilkTouch && silkTouch) {
                Item blockItem = CustomBlock.asItem(block);
                if (blockItem != null) {
                    world.dropItemNaturally(loc, blockItem.clone().getStack().clone());
                }
                return;
            }

            if (!props.requireSilkTouch) {
                Item blockItem = CustomBlock.asItem(block);
                if (blockItem != null) {
                    world.dropItemNaturally(loc, blockItem.clone().getStack().clone());
                }
            }
        }
    }

    // Vanilla Blocks
    @SubscribeEvent(ignoreCancelled = false, priority = EventPriority.HIGHEST)
    public void onBlockBreakPDC(BlockBreakEvent event) {
        if (!event.isCancelled()) BlockPersistentData.remove(event.getBlock());
    }
    @SubscribeEvent(ignoreCancelled = false, priority = EventPriority.HIGHEST)
    public void onBlockFadePDC(BlockFadeEvent event) {
        if (!event.isCancelled()) BlockPersistentData.remove(event.getBlock());
    }
    @SubscribeEvent(ignoreCancelled = false, priority = EventPriority.HIGHEST)
    public void onBlockExplodePDC(BlockExplodeEvent event) {
        if (!event.isCancelled()) {
            BlockPersistentData.remove(event.getExplodedBlockState().getBlock());
            if (event.getExplosionResult() == ExplosionResult.DESTROY || event.getExplosionResult() == ExplosionResult.DESTROY_WITH_DECAY) {
                event.blockList().forEach(BlockPersistentData::remove);
            }
        }
    }
    @SubscribeEvent(ignoreCancelled = false, priority = EventPriority.HIGHEST)
    public void onBlockBurnPDC(BlockBurnEvent event) {
        if (!event.isCancelled()) BlockPersistentData.remove(event.getBlock());
    }
}

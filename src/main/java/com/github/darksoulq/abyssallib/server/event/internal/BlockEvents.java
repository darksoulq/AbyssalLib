package com.github.darksoulq.abyssallib.server.event.internal;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.server.event.EventBus;
import com.github.darksoulq.abyssallib.server.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.server.event.custom.block.BlockBrokenEvent;
import com.github.darksoulq.abyssallib.server.event.custom.block.BlockInteractionEvent;
import com.github.darksoulq.abyssallib.server.event.custom.block.BlockPlacedEvent;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.server.util.TaskUtil;
import com.github.darksoulq.abyssallib.world.block.BlockProperties;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.block.internal.BlockManager;
import com.github.darksoulq.abyssallib.world.block.internal.structure.StructureBlock;
import com.github.darksoulq.abyssallib.world.block.internal.structure.StructureBlockEntity;
import com.github.darksoulq.abyssallib.world.block.internal.structure.StructureBlockMenu;
import com.github.darksoulq.abyssallib.world.data.loot.LootContext;
import com.github.darksoulq.abyssallib.world.data.loot.LootTable;
import com.github.darksoulq.abyssallib.world.item.Item;
import com.github.darksoulq.abyssallib.world.item.component.builtin.BlockItem;
import com.github.darksoulq.abyssallib.world.util.BlockPersistentData;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BlockEvents {

    @SubscribeEvent(ignoreCancelled = false)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getClickedBlock() == null) return;

        CustomBlock block = CustomBlock.from(event.getClickedBlock());
        if (block == null) return;

        BlockInteractionEvent interactionEvent = new BlockInteractionEvent(
            event.getPlayer(),
            block,
            event.getBlockFace(),
            event.getInteractionPoint(),
            event.getAction(),
            event.getItem()
        );
        EventBus.post(interactionEvent);

        if (interactionEvent.isCancelled()) {
            event.setCancelled(true);
            return;
        }

        ActionResult result = block.onInteract(interactionEvent);
        if (result == ActionResult.CANCEL) {
            event.setCancelled(true);
        }
    }

    @SubscribeEvent
    public void onInteractStructure(BlockInteractionEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!(e.getBlock() instanceof StructureBlock)) return;

        if (e.getBlock().getEntity() instanceof StructureBlockEntity sbe) {
            e.setCancelled(true);
            TaskUtil.delayedTask(AbyssalLib.getInstance(), 2, () ->  new StructureBlockMenu(sbe).open(e.getPlayer()));
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onChunkLoad(ChunkLoadEvent event) {
        if (event.isNewChunk()) return;
        List<CustomBlock> blocks = BlockManager.getBlocksInChunk(event.getChunk());
        if (blocks.isEmpty()) return;
        for (CustomBlock block : blocks) {
            BlockManager.ACTIVE_BLOCKS.add(block.getLocation());
            block.onLoad();
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onChunkUnload(ChunkUnloadEvent event) {
        List<CustomBlock> blocks = BlockManager.getBlocksInChunk(event.getChunk());
        if (blocks.isEmpty()) return;
        for (CustomBlock block : blocks) {
            BlockManager.ACTIVE_BLOCKS.remove(block.getLocation());
            block.onUnLoad();
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onBlockDamage(BlockDamageEvent event) {
        CustomBlock block = CustomBlock.from(event.getBlock());
        if (block == null) return;

        if (block.properties.hardness < 0) {
            event.setCancelled(true);
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack handItem = event.getItemInHand();
        Item heldItem = Item.resolve(handItem);
        Location loc = event.getBlock().getLocation();
        if (heldItem == null) return;
        if (!heldItem.hasData(BlockItem.TYPE)) {
            event.setCancelled(true);
            return;
        }
        Identifier blockId = heldItem.getData(BlockItem.TYPE).getValue();
        CustomBlock block = Registries.BLOCKS.get(blockId.toString());
        if (block == null) return;

        CustomBlock instance = block.clone();
        instance.place(event.getBlock(), false);
        BlockPlacedEvent placeEvent = EventBus.post(new BlockPlacedEvent(event.getPlayer(), instance, handItem));

        if (placeEvent.isCancelled()) {
            event.setCancelled(true);
            BlockManager.remove(loc);
            return;
        }

        ActionResult result = instance.onPlaced(event.getPlayer(), loc, handItem);
        if (result == ActionResult.CANCEL) {
            event.setCancelled(true);
            BlockManager.remove(loc);
            loc.getBlock().setType(Material.AIR);
        }
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

        BlockManager.remove(loc);
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onBlockFertilize(BlockFertilizeEvent event) {
        CustomBlock block = CustomBlock.from(event.getBlock());
        if (block == null) return;

        if (block.onBoneMeal(event.getPlayer()) == ActionResult.CANCEL) {
            event.setCancelled(true);
        }
    }

    @SubscribeEvent(ignoreCancelled = false, priority = EventPriority.HIGHEST)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        handlePiston(event, event.getBlocks(), event.getDirection());
    }

    @SubscribeEvent(ignoreCancelled = false, priority = EventPriority.HIGHEST)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        handlePiston(event, event.getBlocks(), event.getDirection());
    }

    private void handlePiston(BlockEvent event, List<Block> blocks, BlockFace direction) {
        if (event instanceof Cancellable cancellable && cancellable.isCancelled()) return;

        for (Block b : blocks) {
            CustomBlock cb = CustomBlock.from(b);
            if (cb != null) {
                if (cb.getEntity() != null) {
                    if (event instanceof Cancellable c) c.setCancelled(true);
                    return;
                }

                if (cb.onPistonMove(direction) == ActionResult.CANCEL) {
                    if (event instanceof Cancellable c) c.setCancelled(true);
                    return;
                }

                BlockProperties.PistonReaction reaction = cb.properties.pistonReaction;
                switch (reaction) {
                    case BLOCK -> {
                        if (event instanceof Cancellable c) c.setCancelled(true);
                        return;
                    }
                    case DESTROY -> {
                        BlockBrokenEvent breakEvent = EventBus.post(new BlockBrokenEvent(null, cb, 0));
                        dropBlockLoot(cb.getLocation(), cb, new ItemStack(Material.AIR), breakEvent, false, 0);
                        BlockManager.remove(cb.getLocation());
                        b.setType(Material.AIR);
                    }
                    case MOVE -> {
                        if (event instanceof Cancellable c) c.setCancelled(false);
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPistonExtendMonitor(BlockPistonExtendEvent event) {
        updateMovedBlocks(event.getBlocks(), event.getDirection());
    }

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPistonRetractMonitor(BlockPistonRetractEvent event) {
        updateMovedBlocks(event.getBlocks(), event.getDirection());
    }

    private void updateMovedBlocks(List<Block> blocks, BlockFace direction) {
        List<CustomBlock> movingBlocks = new ArrayList<>();

        for (Block b : blocks) {
            CustomBlock cb = CustomBlock.from(b);
            if (cb != null && cb.properties.pistonReaction == BlockProperties.PistonReaction.MOVE) {
                movingBlocks.add(cb);
            }
        }

        if (movingBlocks.isEmpty()) return;

        for (CustomBlock cb : movingBlocks) {
            BlockManager.remove(cb.getLocation());
        }

        for (CustomBlock cb : movingBlocks) {
            Location newLoc = cb.getLocation().add(direction.getModX(), direction.getModY(), direction.getModZ());
            cb.setLocation(newLoc);
            BlockManager.register(cb);
        }
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
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        CustomBlock block = CustomBlock.from(event.getBlock());
        if (block != null) {
            event.setCancelled(true);
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onBlockExplode(BlockExplodeEvent event) {
        Iterator<Block> it = event.blockList().iterator();
        while (it.hasNext()) {
            Block bukkitBlock = it.next();
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
                dropBlockLoot(block.getLocation(), block, new ItemStack(Material.AIR), breakEvent, false, 0);
                BlockManager.remove(block);
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onEntityExplode(EntityExplodeEvent event) {
        Iterator<Block> it = event.blockList().iterator();
        while (it.hasNext()) {
            Block bukkitBlock = it.next();
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
                dropBlockLoot(block.getLocation(), block, new ItemStack(Material.AIR), breakEvent, false, 0);
                BlockManager.remove(block);
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getHitBlock() == null) return;

        CustomBlock block = CustomBlock.from(event.getHitBlock());
        if (block == null) return;
        if (block.onProjectileHit(event.getEntity()) == ActionResult.CANCEL) event.setCancelled(true);
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

        if (!block.properties.allowPhysics) {
            event.setCancelled(true);
            return;
        }

        ActionResult result = block.onNeighborUpdate(event.getSourceBlock());
        if (result == ActionResult.CANCEL) {
            event.setCancelled(true);
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onBlockBurn(BlockBurnEvent event) {
        CustomBlock block = CustomBlock.from(event.getBlock());
        if (block != null) {
            if (!block.properties.isFlammable) {
                event.setCancelled(true);
            } else {
                if (block.onIgnite(BlockIgniteEvent.IgniteCause.SPREAD, null, event.getIgnitingBlock()) == ActionResult.CANCEL) {
                    event.setCancelled(true);
                    return;
                }
                BlockManager.remove(block.getLocation());
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onBlockFade(BlockFadeEvent event) {
        CustomBlock block = CustomBlock.from(event.getBlock());
        if (block != null) {
            if (block.onFade(event.getBlock(), event.getNewState()) == ActionResult.CANCEL) {
                event.setCancelled(true);
            } else {
                BlockManager.remove(block.getLocation());
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onBlockForm(BlockFormEvent event) {
        CustomBlock block = CustomBlock.from(event.getBlock());
        if (block != null) {
            if (block.onForm(event.getBlock(), event.getNewState()) == ActionResult.CANCEL) {
                event.setCancelled(true);
            } else {
                BlockManager.remove(block.getLocation());
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onBlockGrow(BlockGrowEvent event) {
        CustomBlock block = CustomBlock.from(event.getBlock());
        if (block != null) {
            if (block.onGrow(event.getBlock(), event.getNewState()) == ActionResult.CANCEL) {
                event.setCancelled(true);
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onBlockIgnite(BlockIgniteEvent event) {
        CustomBlock block = CustomBlock.from(event.getBlock());
        if (block != null) {
            if (!block.properties.isFlammable) {
                event.setCancelled(true);
            } else {
                if (block.onIgnite(event.getCause(), event.getIgnitingEntity(), event.getIgnitingBlock()) == ActionResult.CANCEL) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onBlockSpread(BlockSpreadEvent event) {
        CustomBlock block = CustomBlock.from(event.getBlock());
        if (block != null) {
            if (block.onSpread(event.getBlock(), event.getSource(), event.getNewState()) == ActionResult.CANCEL) {
                event.setCancelled(true);
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onLeavesDecay(LeavesDecayEvent event) {
        CustomBlock block = CustomBlock.from(event.getBlock());
        if (block != null) {
            if (block.onLeavesDecay() == ActionResult.CANCEL) {
                event.setCancelled(true);
            } else {
                BlockManager.remove(block.getLocation());
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onSpongeAbsorb(SpongeAbsorbEvent event) {
        CustomBlock block = CustomBlock.from(event.getBlock());
        if (block != null) {
            if (block.onSpongeAbsorb(event.getBlocks()) == ActionResult.CANCEL) {
                event.setCancelled(true);
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onSignChange(SignChangeEvent event) {
        CustomBlock block = CustomBlock.from(event.getBlock());
        if (block != null) {
            if (block.onSignChange(event.getPlayer(), event.getSide()) == ActionResult.CANCEL) {
                event.setCancelled(true);
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onServerTick(ServerTickEndEvent event) {
        for (Location loc : BlockManager.ACTIVE_BLOCKS) {
            CustomBlock block = CustomBlock.from(loc.getBlock());
            if (block == null) {
                return;
            }
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
            LootTable lootTable = block.getDrops();
            if (lootTable != null) {
                LootContext context = LootContext.builder(loc)
                    .tool(tool)
                    .luck(fortuneLevel)
                    .build();
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
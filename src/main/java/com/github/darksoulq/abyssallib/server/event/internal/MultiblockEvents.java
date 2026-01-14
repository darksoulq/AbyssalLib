package com.github.darksoulq.abyssallib.server.event.internal;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.server.event.EventBus;
import com.github.darksoulq.abyssallib.server.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.server.event.custom.multiblock.MultiblockBreakEvent;
import com.github.darksoulq.abyssallib.server.event.custom.multiblock.MultiblockInteractionEvent;
import com.github.darksoulq.abyssallib.server.event.custom.multiblock.MultiblockPlaceEvent;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.multiblock.Multiblock;
import com.github.darksoulq.abyssallib.world.multiblock.internal.MultiblockManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MultiblockEvents {
    @SubscribeEvent(ignoreCancelled = false)
    public void onChunkLoad(ChunkLoadEvent event) {
        if (event.isNewChunk()) return;
        List<Multiblock> blocks = MultiblockManager.getMultiblocksInChunk(event.getChunk());
        if (blocks.isEmpty()) return;
        for (Multiblock block : blocks) {
            MultiblockManager.loadMultiblock(block);
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onChunkUnload(ChunkUnloadEvent event) {
        List<Multiblock> blocks = MultiblockManager.getMultiblocksInChunk(event.getChunk());
        if (blocks.isEmpty()) return;
        for (Multiblock block : blocks) {
            MultiblockManager.unloadMultiblock(block);
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        Action action = event.getAction();
        Block clicked = event.getClickedBlock();
        Player player = event.getPlayer();
        Multiblock existing = MultiblockManager.getAt(clicked.getLocation());

        if (existing != null) {
            MultiblockInteractionEvent inter = EventBus.post(new MultiblockInteractionEvent(
                player,
                existing,
                event.getBlockFace(),
                clicked.getLocation(),
                action,
                player.getInventory().getItemInMainHand()
            ));
            if (inter.isCancelled()) event.setCancelled(true);
            return;
        }

        if (action == Action.RIGHT_CLICK_BLOCK) {
            if (player.isSneaking()) return;
            for (String id : Registries.MULTIBLOCKS.getAll().keySet()) {
                Multiblock proto = Registries.MULTIBLOCKS.get(id);
                if (proto == null) continue;
                if (!proto.getTriggerChoice().matches(clicked)) continue;

                Location trigger = clicked.getLocation();
                Multiblock clone = proto.clone();
                if (!clone.matchesLayout(trigger)) {
                    continue;
                }
                MultiblockPlaceEvent placeEvent = EventBus.post(new MultiblockPlaceEvent(player, clone, trigger, player.getInventory().getItemInMainHand()));
                if (placeEvent.isCancelled()) {
                    event.setCancelled(true);
                    return;
                }
                ActionResult result = clone.onConstruct(player, clone, player.getInventory().getItemInMainHand());
                if (result == ActionResult.CANCEL) {
                    event.setCancelled(true);
                    return;
                }

                try {
                    clone.place(trigger, false);
                } catch (Throwable t) {
                    AbyssalLib.getInstance().getLogger().warning("Failed to place multiblock: " + t.getMessage());
                    t.printStackTrace();
                    event.setCancelled(true);
                    return;
                }
                return;
            }
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockBreakEvent event) {
        Multiblock mb = MultiblockManager.getAt(event.getBlock().getLocation());
        if (mb == null) return;
        MultiblockBreakEvent e = EventBus.post(new MultiblockBreakEvent(event.getPlayer(), mb, event.getPlayer().getInventory().getItemInMainHand()));
        if (e.isCancelled()) {
            event.setCancelled(true);
            return;
        }
        if (mb.onBreak(event.getPlayer(), mb, event.getPlayer().getInventory().getItemInMainHand()) == ActionResult.CANCEL) {
            event.setCancelled(true);
            return;
        }
        MultiblockManager.remove(mb);
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onBlockExplode(BlockExplodeEvent event) {
        Iterator<org.bukkit.block.Block> it = event.blockList().iterator();
        while (it.hasNext()) {
            Block b = it.next();
            Multiblock mb = MultiblockManager.getAt(b.getLocation());
            if (mb == null) continue;
            MultiblockBreakEvent broken = EventBus.post(new MultiblockBreakEvent(event.getBlock(), mb));
            if (broken.isCancelled()) {
                it.remove();
                return;
            }
            if (MultiblockManager.isPartOfMultiblock(b.getLocation())) {
                MultiblockManager.remove(mb);
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onEntityExplode(EntityExplodeEvent event) {
        Iterator<org.bukkit.block.Block> it = event.blockList().iterator();
        while (it.hasNext()) {
            org.bukkit.block.Block b = it.next();
            Multiblock mb = MultiblockManager.getAt(b.getLocation());
            if (mb == null) continue;
            MultiblockBreakEvent broken = EventBus.post(new MultiblockBreakEvent(event.getEntity(), mb));
            if (broken.isCancelled()) {
                it.remove();
                return;
            }
            if (MultiblockManager.isPartOfMultiblock(b.getLocation())) {
                MultiblockManager.remove(mb);
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getHitBlock() == null) return;
        Multiblock mb = MultiblockManager.getAt(event.getHitBlock().getLocation());
        if (mb == null) return;
        if (mb.onProjectileHit(event.getEntity()) == ActionResult.CANCEL) event.setCancelled(true);
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onBlockRedstone(BlockRedstoneEvent event) {
        Multiblock mb = MultiblockManager.getAt(event.getBlock().getLocation());
        if (mb == null) return;
        int oldCurrent = event.getOldCurrent();
        int newCurrent = event.getNewCurrent();
        int finalCurrent = mb.onRedstone(oldCurrent, newCurrent);
        event.setNewCurrent(finalCurrent);
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Multiblock mb = MultiblockManager.getAt(event.getBlock().getLocation());
        if (mb != null) event.setCancelled(true);
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for (Block bukkitBlock : event.getBlocks()) {
            if (MultiblockManager.isPartOfMultiblock(bukkitBlock.getLocation())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        for (Block bukkitBlock : event.getBlocks()) {
            if (MultiblockManager.isPartOfMultiblock(bukkitBlock.getLocation())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onServerTick(ServerTickEndEvent event) {
        for (Multiblock mb : MultiblockManager.getTickingMultiblocks()) {
            if (mb == null) continue;
            if (mb.getEntity() != null) {
                mb.getEntity().serverTick();
                if (ThreadLocalRandom.current().nextFloat() < 0.001f) mb.getEntity().randomTick();
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onBlockBurn(BlockBurnEvent event) {
        if (Multiblock.from(event.getBlock()) != null) event.setCancelled(true);
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onBlockFade(BlockFadeEvent event) {
        if (Multiblock.from(event.getBlock()) != null) event.setCancelled(true);
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onBlockForm(BlockFormEvent event) {
        if (Multiblock.from(event.getBlock()) != null) event.setCancelled(true);
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onBlockGrow(BlockGrowEvent event) {
        if (Multiblock.from(event.getBlock()) != null) event.setCancelled(true);
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (Multiblock.from(event.getBlock()) != null) event.setCancelled(true);
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onBlockSpread(BlockSpreadEvent event) {
        if (Multiblock.from(event.getBlock()) != null) event.setCancelled(true);
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onLeavesDecay(LeavesDecayEvent event) {
        if (Multiblock.from(event.getBlock()) != null) event.setCancelled(true);
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onSpongeAbsorb(SpongeAbsorbEvent event) {
        if (Multiblock.from(event.getBlock()) != null) event.setCancelled(true);
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onSignChange(SignChangeEvent event) {
        if (Multiblock.from(event.getBlock()) != null) event.setCancelled(true);
    }
}

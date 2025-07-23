package com.github.darksoulq.abyssallib.server.event.internal;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.server.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.server.event.custom.block.BlockBrokenEvent;
import com.github.darksoulq.abyssallib.server.event.custom.block.BlockInteractionEvent;
import com.github.darksoulq.abyssallib.server.event.custom.block.BlockPlacedEvent;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.level.block.Block;
import com.github.darksoulq.abyssallib.world.level.block.BlockProperties;
import com.github.darksoulq.abyssallib.world.level.block.internal.BlockManager;
import com.github.darksoulq.abyssallib.world.level.data.CTag;
import com.github.darksoulq.abyssallib.world.level.data.loot.LootContext;
import com.github.darksoulq.abyssallib.world.level.data.loot.LootTable;
import com.github.darksoulq.abyssallib.world.level.item.Item;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BlockEvents {

    @SubscribeEvent
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack handItem = event.getItemInHand();
        Item heldItem = Item.from(handItem);
        Location loc = event.getBlock().getLocation();
        if (heldItem == null) return;
        CTag data = heldItem.getData();

        for (Block block : Registries.BLOCKS.getAll().values()) {
            block.place(event.getBlock(), false);
            if (data.has("BlockItem") && data.getString("BlockItem").get().equals(block.getId().toString())) {
                BlockPlacedEvent placeEvent = AbyssalLib.EVENT_BUS.post(new BlockPlacedEvent(
                        event.getPlayer(),
                        block,
                        handItem
                ));
                ActionResult result = block.onPlaced(event.getPlayer(), loc,
                        handItem);
                if (result == ActionResult.CANCEL || placeEvent.isCancelled()) {
                    BlockManager.remove(loc);
                    loc.getBlock().setType(Material.AIR);
                    return;
                }
                return;
            }
        }
        event.setCancelled(true);
    }

    @SubscribeEvent
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = Block.from(event.getBlock());
        if (block == null) return;

        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();
        ItemStack stack = player.getInventory().getItemInMainHand();

        BlockProperties props = block.properties;
        boolean silkTouch = props.requireSilkTouch && stack.containsEnchantment(Enchantment.SILK_TOUCH);
        boolean allowFortune = props.allowFortune;
        int fortuneLevel = allowFortune ? stack.getEnchantmentLevel(Enchantment.FORTUNE) : 0;

        BlockBrokenEvent breakEvent = AbyssalLib.EVENT_BUS
                .post(new BlockBrokenEvent(player, block, fortuneLevel));
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

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE && event.getAction().isLeftClick()) return;
        org.bukkit.block.Block bukkitBlock = event.getClickedBlock();
        if (bukkitBlock == null) return;

        ItemStack handItem = event.getItem();
        Item heldItem = Item.from(handItem);
        if (heldItem == null) return;

        Block clickedBlock = Block.from(bukkitBlock);
        if (clickedBlock != null) {
            BlockInteractionEvent blockEvent = AbyssalLib.EVENT_BUS.post(new BlockInteractionEvent(
                    event.getPlayer(),
                    clickedBlock,
                    event.getBlockFace(),
                    event.getInteractionPoint(),
                    event.getAction(),
                    handItem
            ));

            if (blockEvent.isCancelled()) {
                event.setCancelled(true);
            }
        }
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
            } else {
                BlockBrokenEvent breakEvent = AbyssalLib.EVENT_BUS
                        .post(new BlockBrokenEvent(null, block, 0));
                if (breakEvent.isCancelled()) {
                    it.remove();
                    return;
                }
                dropBlockLoot(block.getLocation(), block, new ItemStack(Material.AIR),
                        breakEvent, false, 0);
                BlockManager.remove(block.getLocation());
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
            } else {
                BlockBrokenEvent breakEvent = AbyssalLib.EVENT_BUS
                        .post(new BlockBrokenEvent(null, block, 0));
                if (breakEvent.isCancelled()) {
                    it.remove();
                    return;
                }
                dropBlockLoot(block.getLocation(), block, new ItemStack(Material.AIR),
                        breakEvent, false, 0);
                BlockManager.remove(block.getLocation());
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
        for (Block block : BlockManager.blocks.values()) {
            if (block.getEntity() != null) {
                block.getEntity().serverTick();
                if (ThreadLocalRandom.current().nextFloat() < 0.001f) {
                    block.getEntity().randomTick();
                }
            }
        }
    }

    public static void dropBlockLoot(Location loc, Block block, ItemStack tool, BlockBrokenEvent breakEvent, boolean silkTouch, int fortuneLevel) {
        BlockProperties props = block.properties;

        if (!props.isCorrectTool(tool)) return;

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
                Item blockItem = Block.asItem(block);
                if (blockItem != null) {
                    world.dropItemNaturally(loc, blockItem.clone().getStack().clone());
                }
                return;
            }

            if (!props.requireSilkTouch) {
                Item blockItem = Block.asItem(block);
                if (blockItem != null) {
                    world.dropItemNaturally(loc, blockItem.clone().getStack().clone());
                }
            }
        }
    }

}

package com.github.darksoulq.abyssallib.server.event.internal;

import com.destroystokyo.paper.event.inventory.PrepareResultEvent;
import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.server.event.ClickType;
import com.github.darksoulq.abyssallib.server.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.server.event.context.item.AnvilContext;
import com.github.darksoulq.abyssallib.world.level.item.Item;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;

import java.util.Iterator;

public class ItemEvents {

    @SubscribeEvent
    public void onBlockMine(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Item item = Item.from(event.getPlayer().getActiveItem());
        if (item != null) {
            ActionResult result = item.postMine(block, player);
            if (result == ActionResult.CANCEL) {
                event.setCancelled(true);
            }
        }
    }

    @SubscribeEvent
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity source = event.getDamager();
        Entity target = event.getEntity();
        if (source instanceof LivingEntity lSource) {
            Item item = Item.from(lSource.getActiveItem());
            if (item != null) {
                ActionResult result = item.postHit(lSource, target);
                if (result == ActionResult.CANCEL) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();
        ItemStack stack = event.getItem();
        Item item = Item.from(stack);
        if (item != null) {
            if (block != null && (event.getAction() == Action.LEFT_CLICK_BLOCK ||
                    event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
                ActionResult result = item.useOnBlock(event.getAction() == Action.LEFT_CLICK_AIR ? ClickType.LEFT_CLICK : ClickType.RIGHT_CLICK,
                        block, event.getBlockFace(), player, event.getHand());
                if (result == ActionResult.CANCEL) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onUseEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = player.getInventory().getItem(event.getHand());
        Item item = Item.from(stack);
        if (item != null) {
            ActionResult result = item.useOnEntity(ClickType.RIGHT_CLICK, player, event.getHand(), event.getRightClicked());
            if (result == ActionResult.CANCEL) {
                event.setCancelled(true);
            }
        }
    }

    @SubscribeEvent
    public void onAnvilCombine(PrepareAnvilEvent event) {
        ItemStack[] stacks = event.getInventory().getContents();
        for (ItemStack stack : stacks) {
            Item item = Item.from(stack);
            if (item != null) {
                ActionResult result = item.onAnvilPrepare(new AnvilContext(event));
                if (result == ActionResult.CANCEL) {
                    event.getInventory().setResult(null);
                }
                break;
            }
        }
    }

    @SubscribeEvent
    public void onCrafted(CraftItemEvent event) {
        Player player = (Player) event.getWhoClicked();
        Item item = Item.from(event.getRecipe().getResult());
        if (item != null) {
            item.onCraft(player);
        }
    }

    @SubscribeEvent
    public void onSlotChange(PlayerInventorySlotChangeEvent event) {
        Item item = Item.from(event.getNewItemStack());
        if (item != null) {
            event.setShouldTriggerAdvancements(false);
        }
    }

    @SubscribeEvent
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        CraftingInventory inventory = event.getInventory();
        ItemStack[] contents = inventory.getContents();

        if (inventory.getRecipe() == null) return;
        boolean isCustom = true;
        for (Iterator<Recipe> it = Bukkit.recipeIterator(); it.hasNext(); ) {
            Recipe recipe = it.next();
            if (recipe instanceof Keyed keyed && "minecraft".equals(keyed.getKey().getNamespace())) {
                isCustom = false;
                break;
            }
        }
        if (isCustom) return;

        for (ItemStack item : contents) {
            if (Item.from(item) != null) {
                inventory.setResult(null);
                return;
            }
        }
    }

    @SubscribeEvent
    public void onPrepareSmithing(PrepareSmithingEvent event) {
        SmithingInventory inventory = event.getInventory();
        ItemStack a = inventory.getInputTemplate();
        ItemStack b = inventory.getInputEquipment();
        ItemStack c = inventory.getInputMineral();

        if (inventory.getRecipe() == null) return;
        boolean isCustom = true;
        for (Iterator<Recipe> it = Bukkit.recipeIterator(); it.hasNext(); ) {
            Recipe recipe = it.next();
            if (recipe instanceof Keyed keyed && "minecraft".equals(keyed.getKey().getNamespace())) {
                isCustom = false;
                break;
            }
        }
        if (isCustom) return;

        if (Item.from(a) != null || Item.from(b) != null || Item.from(c) != null) {
            inventory.setResult(null);
        }
    }

    @SubscribeEvent
    public void onFurnaceBurn(FurnaceBurnEvent event) {
        ItemStack source = event.getFuel();
        Block block = event.getBlock();

        BlockState furnace = block.getState();
        FurnaceInventory inv = (FurnaceInventory) ((Container) furnace).getInventory();
        ItemStack input = inv.getSmelting();

        if (Item.from(input) != null || Item.from(source) != null) {
            event.setCancelled(true);
        }
    }

    @SubscribeEvent
    public void onCampfireCook(BlockCookEvent event) {
        ItemStack a = event.getSource();

        if (event.getRecipe() == null) return;
        boolean isCustom = true;
        for (Iterator<Recipe> it = Bukkit.recipeIterator(); it.hasNext(); ) {
            Recipe recipe = it.next();
            if (recipe instanceof Keyed keyed && "minecraft".equals(keyed.getKey().getNamespace())) {
                isCustom = false;
                break;
            }
        }
        if (isCustom) return;

        if (Item.from(a) != null) {
            event.setCancelled(true);
        }
    }

    @SubscribeEvent
    public void onLoomPrepareEvent(PrepareResultEvent event) {
        InventoryView view = event.getView();
        if (view.getTopInventory().getType() != InventoryType.LOOM) return;

        Inventory inv = view.getTopInventory();
        ItemStack base = inv.getItem(0);
        ItemStack dye = inv.getItem(1);
        ItemStack pattern = inv.getItem(2);

        if (Item.from(base) != null || Item.from(dye) != null || Item.from(pattern) != null) {
            event.setResult(null);
        }
    }
}

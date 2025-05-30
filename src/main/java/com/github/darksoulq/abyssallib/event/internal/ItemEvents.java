package com.github.darksoulq.abyssallib.event.internal;

import com.destroystokyo.paper.event.inventory.PrepareResultEvent;
import com.github.darksoulq.abyssallib.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.event.context.item.AnvilContext;
import com.github.darksoulq.abyssallib.event.context.item.ItemUseContext;
import com.github.darksoulq.abyssallib.item.Item;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;

import java.util.Iterator;
import java.util.Random;

public class ItemEvents {
    private final Random random = new Random();

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = player.getInventory().getItem(event.getHand());
        Item item = Item.from(stack);
        if (item != null) {
            item.onInteract(new ItemUseContext(event));
        }
    }

    @SubscribeEvent
    public void onUseEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = player.getInventory().getItem(event.getHand());
        Item item = Item.from(stack);
        if (item != null) {
            item.onUseEntity(new ItemUseContext(event));
        }
    }

    @SubscribeEvent
    public void onAnvilCombine(PrepareAnvilEvent event) {
        ItemStack[] stacks = event.getInventory().getContents();
        for (ItemStack stack : stacks) {
            Item item = Item.from(stack);
            if (item != null) {
                event.getInventory().setResult(null);
                item.onAnvilPrepare(new AnvilContext(event));
                break;
            }
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

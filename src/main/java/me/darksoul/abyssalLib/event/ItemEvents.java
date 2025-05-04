package me.darksoul.abyssalLib.event;

import com.destroystokyo.paper.event.inventory.PrepareResultEvent;
import me.darksoul.abyssalLib.event.context.AnvilContext;
import me.darksoul.abyssalLib.event.context.ItemUseContext;
import me.darksoul.abyssalLib.item.Item;
import me.darksoul.abyssalLib.registry.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.block.CampfireStartEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;

import java.util.HashSet;
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
            ItemUseContext ctx = new ItemUseContext(
                    player,
                    item,
                    event.getHand(),
                    event.getInteractionPoint(),
                    event.getClickedBlock(),
                    null,
                    event
            );
            item.onInteract(ctx);
        }
    }

    @SubscribeEvent
    public void onUseEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = player.getInventory().getItem(event.getHand());
        Item item = Item.from(stack);
        if (item != null) {
            ItemUseContext ctx = new ItemUseContext(
                    player,
                    item,
                    event.getHand(),
                    null,
                    null,
                    event.getRightClicked(),
                    event
            );
            item.onUseEntity(ctx);
        }
    }

    @SubscribeEvent
    public void onAnvilCombine(PrepareAnvilEvent event) {
        Player player = (Player) event.getView().getPlayer();

        ItemStack[] stacks = event.getInventory().getContents();
        for (ItemStack stack : stacks) {
            Item item = Item.from(stack);
            if (item != null) {
                event.getInventory().setResult(null);
                AnvilContext ctx = new AnvilContext(
                        player,
                        event.getView().getTopInventory().getFirstItem(),
                        event.getView().getTopInventory().getSecondItem(),
                        event.getView().getTopInventory().getResult(),
                        event.getView().getRenameText(),
                        event.getView().getRepairCost(),
                        event
                );
                item.onAnvilPrepare(ctx);
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

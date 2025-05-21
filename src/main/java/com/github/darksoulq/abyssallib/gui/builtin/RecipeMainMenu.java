package com.github.darksoulq.abyssallib.gui.builtin;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.gui.impl.SearchGui;
import com.github.darksoulq.abyssallib.gui.slot.ButtonSlot;
import com.github.darksoulq.abyssallib.recipe.Recipe;
import com.github.darksoulq.abyssallib.recipe.impl.*;
import com.github.darksoulq.abyssallib.registry.BuiltinRegistries;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RecipeMainMenu extends SearchGui {
    private static final int ITEMS_PER_PAGE = 27;

    private final List<Map.Entry<String, Recipe>> recipes = new ArrayList<>();
    private final Map<Player, Integer> currentPages = new HashMap<>();

    public RecipeMainMenu() {
        super(Component.text("Recipes Viewer"));
        recipes.addAll(BuiltinRegistries.RECIPES.getMap().entrySet());
    }

    @Override
    public void _init(Player player) {
        currentPages.putIfAbsent(player, 0);
        fillMain(player);
    }

    private void fillMain(Player player) {
        int currentPage = currentPages.getOrDefault(player, 0);
        String search = text(player) == null ? "" : text(player).toLowerCase();
        List<Map.Entry<String, Recipe>> filtered = new ArrayList<>();

        for (Map.Entry<String, Recipe> entry : recipes) {
            String id = entry.getKey().toLowerCase();
            Recipe recipe = entry.getValue();
            ItemStack result = resulOf(recipe);
            if (result == null) continue;

            String displayName = result.hasItemMeta() && result.getItemMeta().hasDisplayName()
                    ? displayNameOf(result.getItemMeta().displayName())
                    : result.getType().name().toLowerCase();

            if (search.startsWith("@")) {
                String modid = search.substring(1);
                if (id.split(":")[0].contains(modid)) {
                    filtered.add(entry);
                }
            } else {
                if (displayName.contains(search)) {
                    filtered.add(entry);
                }
            }
        }

        int totalPages = (int) Math.ceil((double) filtered.size() / ITEMS_PER_PAGE);
        currentPage = Math.min(currentPage, totalPages - 1);
        if (currentPage < 0) currentPage = 0;
        currentPages.put(player, currentPage);

        int startIndex = currentPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, filtered.size());

        inventory(player, Type.BOTTOM).clear();

        List<Map.Entry<String, Recipe>> pageItems = filtered.subList(startIndex, endIndex);

        for (int i = 0; i < pageItems.size(); i++) {
            Map.Entry<String, Recipe> entry = pageItems.get(i);
            String id = entry.getKey();
            Recipe recipe = entry.getValue();

            ItemStack icon = resulOf(recipe);

            slot(Type.BOTTOM, new ButtonSlot(i + 9, icon, ctx -> {
                restoreBottomMenu(ctx.player);
                AbyssalLib.GUI_MANAGER.openGui(player, new RecipeViewer(id));
            }));
        }

        slot(Type.BOTTOM, new ButtonSlot(0, named(Material.ARROW, "Previous Page"), ctx -> {
            int page = currentPages.getOrDefault(ctx.player, 0);
            if (page > 0) {
                currentPages.put(ctx.player, page - 1);
                fillMain(ctx.player);
            }
        }));

        slot(Type.BOTTOM, new ButtonSlot(8, named(Material.ARROW, "Next Page"), ctx -> {
            int page = currentPages.getOrDefault(ctx.player, 0);
            if (page < totalPages - 1) {
                currentPages.put(ctx.player, page + 1);
                fillMain(ctx.player);
            }
        }));
    }

    @Override
    public void onTick() {
        for (Player viewer : viewers()) {
            fillMain(viewer);
        }
    }

    private ItemStack resulOf(Recipe recipe) {
        if (recipe instanceof ShapedRecipeImpl r) return r.result;
        if (recipe instanceof ShapelessRecipeImpl r) return r.result;
        if (recipe instanceof SmeltingRecipeImpl r) return r.result;
        if (recipe instanceof SmithingRecipeImpl r) return r.result;
        if (recipe instanceof StonecuttingRecipeImpl r) return r.result;
        if (recipe instanceof CampfireRecipeImpl r) return r.result;
        return null;
    }

    private String displayNameOf(Component component) {
        if (component instanceof TranslatableComponent tc) {
            return tc.key().toLowerCase();
        }
        return component.toString().toLowerCase();
    }

    private ItemStack named(Material material, String name) {
        ItemStack item = new ItemStack(material);
        item.editMeta(meta -> meta.displayName(Component.text(name)));
        return item;
    }
}

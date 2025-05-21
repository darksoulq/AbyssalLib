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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeMainMenu extends SearchGui {
    private Map<Player, Integer> currentPages = new HashMap<>();
    private static final int ITEMS_PER_PAGE = 27;

    private final List<Map.Entry<String, Recipe>> recipes = new ArrayList<>();

    public RecipeMainMenu() {
        super(Component.text("Recipes Viewer"));
        recipes.addAll(BuiltinRegistries.RECIPES.getMap().entrySet());
    }

    @Override
    public void _init(Player player) {
        fillMain(player);
        currentPages.put(player, 0);
    }

    private void fillMain(Player player) {
        String search = text(player) == null ? "" : text(player).toLowerCase();
        List<Map.Entry<String, Recipe>> filtered = new ArrayList<>();

        for (Map.Entry<String, Recipe> entry : recipes) {
            String id = entry.getKey().toLowerCase();
            Recipe recipe = entry.getValue();
            ItemStack result = resulOf(recipe);
            String displayName = result.hasItemMeta() && result.getItemMeta().hasItemName()
                    ? ((TranslatableComponent) result.getItemMeta().itemName()).key().toLowerCase()
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
        currentPages.put(player, Math.min(currentPages.get(player) != null ? currentPages.get(player) : 0, totalPages - 1));
        if (currentPages.get(player) < 0) currentPages.put(player, 0);

        int startIndex = currentPages.get(player) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, filtered.size());

        List<Map.Entry<String, Recipe>> pageItems = filtered.subList(startIndex, endIndex);

        for (int i = 0; i < pageItems.size(); i++) {
            Map.Entry<String, Recipe> entry = pageItems.get(i);
            String id = entry.getKey();
            Recipe recipe = entry.getValue();

            ItemStack icon = resulOf(recipe);

            slot(player, Type.BOTTOM, new ButtonSlot(i + 9, icon, ctx -> {
                restoreBottomMenu(player);
                AbyssalLib.GUI_MANAGER.openGui(player, new RecipeViewer(id));
            }));
        }

        slot(player, Type.BOTTOM, new ButtonSlot(0, named(Material.ARROW, "Previous Page"), ctx -> {
            if (currentPages.get(player) > 0) {
                currentPages.put(player, currentPages.get(player) - 1);
            }
        }));

        slot(player, Type.BOTTOM, new ButtonSlot(8, named(Material.ARROW, "Next Page"), ctx -> {
            if (currentPages.get(player) < totalPages - 1) {
                currentPages.put(player, currentPages.get(player) + 1);
            }
        }));
    }

    @Override
    public void onTick(Player player) {
        fillMain(player);
    }

    private ItemStack resulOf(Recipe recipe) {
        if (recipe instanceof ShapedRecipeImpl recipe1) {
            return recipe1.result;
        } else if (recipe instanceof ShapelessRecipeImpl recipe1) {
            return recipe1.result;
        } else if (recipe instanceof SmeltingRecipeImpl recipe1) {
            return recipe1.result;
        } else if (recipe instanceof  SmithingRecipeImpl recipe1) {
            return recipe1.result;
        } else if (recipe instanceof StonecuttingRecipeImpl recipe1) {
            return recipe1.result;
        } else if (recipe instanceof CampfireRecipeImpl recipe1) {
            return recipe1.result;
        }
        return null;
    }

    private ItemStack named(Material material, String name) {
        ItemStack item = new ItemStack(material);
        item.editMeta(meta -> meta.displayName(Component.text(name)));
        return item;
    }
}

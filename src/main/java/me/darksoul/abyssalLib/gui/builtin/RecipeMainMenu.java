package me.darksoul.abyssalLib.gui.builtin;

import me.darksoul.abyssalLib.AbyssalLib;
import me.darksoul.abyssalLib.gui.SearchGui;
import me.darksoul.abyssalLib.gui.slot.ButtonSlot;
import me.darksoul.abyssalLib.recipe.Recipe;
import me.darksoul.abyssalLib.recipe.impl.*;
import me.darksoul.abyssalLib.registry.BuiltinRegistries;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecipeMainMenu extends SearchGui {
    private int currentPage = 0;
    private static final int ITEMS_PER_PAGE = 27;

    private final List<Map.Entry<String, Recipe>> recipes = new ArrayList<>();

    public RecipeMainMenu(Player player) {
        super(player, Component.text("Recipes Viewer"));
        recipes.addAll(BuiltinRegistries.RECIPES.getMap().entrySet());
    }

    @Override
    public void init(Player player) {
        fillMain();
    }

    private void fillMain() {
        String search = text() == null ? "" : text().toLowerCase();
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
        currentPage = Math.min(currentPage, totalPages - 1);
        if (currentPage < 0) currentPage = 0;

        int startIndex = currentPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, filtered.size());

        List<Map.Entry<String, Recipe>> pageItems = filtered.subList(startIndex, endIndex);

        for (int i = 0; i < pageItems.size(); i++) {
            Map.Entry<String, Recipe> entry = pageItems.get(i);
            String id = entry.getKey();
            Recipe recipe = entry.getValue();

            ItemStack icon = resulOf(recipe);

            slot(new ButtonSlot(i + 9, icon, ctx -> {
                AbyssalLib.GUI_MANAGER.openGui(new RecipeViewer(ctx.player(), id));
                restoreBottomMenu();
            }));
        }

        slot(new ButtonSlot(0, named(Material.ARROW, "Previous Page"), ctx -> {
            if (currentPage > 0) {
                currentPage--;
            }
        }));

        slot(new ButtonSlot(8, named(Material.ARROW, "Next Page"), ctx -> {
            if (currentPage < totalPages - 1) {
                currentPage++;
            }
        }));
    }

    @Override
    public void onTick() {
        slots.clear();
        inventory().getBottomInventory().clear();
        fillMain();
        if (dirtyDraw()) {
            drawPartial();
        } else {
            draw();
        }
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

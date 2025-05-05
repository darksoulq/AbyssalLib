package me.darksoul.abyssalLib.gui.builtin;

import me.darksoul.abyssalLib.AbyssalLib;
import me.darksoul.abyssalLib.gui.ChestGui;
import me.darksoul.abyssalLib.gui.slot.ButtonSlot;
import me.darksoul.abyssalLib.gui.slot.StaticSlot;
import me.darksoul.abyssalLib.recipe.*;
import me.darksoul.abyssalLib.recipe.impl.*;
import me.darksoul.abyssalLib.registry.BuiltinRegistries;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RecipeViewer extends ChestGui {
    private String selectedRecipeId = "";

    public RecipeViewer(Player player, String recipeId) {
        super(player, Component.text("Recipe - " + recipeId), 6);
        selectedRecipeId = recipeId;
    }

    @Override
    public void init(Player player) {
        fillGui(player);
    }

    private void fillGui(Player player) {
        slots.clear();
        inventory().getTopInventory().clear();

        fillRecipe();
    }

    private void fillRecipe() {
        Recipe recipe = BuiltinRegistries.RECIPES.get(selectedRecipeId);
        if (recipe == null) return;

        if (recipe instanceof ShapedRecipeImpl shaped) {
            for (int y = 0; y < shaped.getHeight(); y++) {
                for (int x = 0; x < shaped.getWidth(); x++) {
                    ItemStack ingredient = shaped.getIngredient(x + y * shaped.getWidth());
                    if (ingredient != null) {
                        slot(new StaticSlot(x + y * 9 + 10, ingredient));
                    }
                }
            }
            slot(new StaticSlot(24, shaped.result));
        } else if (recipe instanceof ShapelessRecipeImpl shapeless) {
            List<ItemStack> inputs = shapeless.ingredients;
            for (int i = 0; i < inputs.size(); i++) {
                int x = i % 3;
                int y = i / 3;
                if (y < 3) {
                    int slotIndex = x + y * 9 + 10;
                    slot(new StaticSlot(slotIndex, inputs.get(i)));
                }
            }
            slot(new StaticSlot(24, shapeless.result));
        } else if (recipe instanceof SmeltingRecipeImpl smelting) {
            slot(new StaticSlot(20, smelting.input));
            slot(new StaticSlot(24, smelting.result));
        } else if (recipe instanceof CampfireRecipeImpl campfire) {
            slot(new StaticSlot(20, campfire.input));
            slot(new StaticSlot(24, campfire.result));
        } else if (recipe instanceof SmithingRecipeImpl smithing) {
            slot(new StaticSlot(19, smithing.base));
            slot(new StaticSlot(20, smithing.addition));
            slot(new StaticSlot(24, smithing.result));
        } else if (recipe instanceof StonecuttingRecipeImpl stonecutting) {
            slot(new StaticSlot(20, stonecutting.input));
            slot(new StaticSlot(24, stonecutting.result));
        }

        slot(new ButtonSlot(49, named(Material.AIR, "Back"), ctx -> {
            AbyssalLib.GUI_MANAGER.openGui(new RecipeMainMenu(ctx.player()));
        }));
    }

    private ItemStack named(Material material, String name) {
        ItemStack item = new ItemStack(material);
        item.editMeta(meta -> meta.displayName(Component.text(name)));
        return item;
    }
}

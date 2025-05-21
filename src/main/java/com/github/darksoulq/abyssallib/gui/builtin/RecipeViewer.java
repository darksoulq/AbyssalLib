package com.github.darksoulq.abyssallib.gui.builtin;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.gui.impl.ChestGui;
import com.github.darksoulq.abyssallib.gui.slot.ButtonSlot;
import com.github.darksoulq.abyssallib.gui.slot.StaticSlot;
import com.github.darksoulq.abyssallib.recipe.Recipe;
import com.github.darksoulq.abyssallib.recipe.impl.*;
import com.github.darksoulq.abyssallib.registry.BuiltinRegistries;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class RecipeViewer extends ChestGui {
    private final String selectedRecipeId;

    public RecipeViewer(String recipeId) {
        super(Component.text("Recipe - " + recipeId), 6);
        this.selectedRecipeId = recipeId;
    }

    @Override
    public void init(Player player) {
        fillGui(player);
    }

    private void fillGui(Player player) {
        sharedSlots.TOP.clear();
        inventory(player, Type.TOP).clear();

        Recipe recipe = BuiltinRegistries.RECIPES.get(selectedRecipeId);
        if (recipe == null) return;

        if (recipe instanceof ShapedRecipeImpl shaped) {
            for (int y = 0; y < shaped.getHeight(); y++) {
                for (int x = 0; x < shaped.getWidth(); x++) {
                    ItemStack ingredient = shaped.getIngredient(x + y * shaped.getWidth());
                    if (ingredient != null) {
                        slot(Type.TOP, new StaticSlot(x + y * 9 + 10, ingredient));
                    }
                }
            }
            slot(Type.TOP, new StaticSlot(24, shaped.result));
        } else if (recipe instanceof ShapelessRecipeImpl shapeless) {
            List<ItemStack> inputs = shapeless.ingredients;
            for (int i = 0; i < inputs.size(); i++) {
                int x = i % 3;
                int y = i / 3;
                if (y < 3) {
                    int slotIndex = x + y * 9 + 10;
                    slot(player, Type.TOP, new StaticSlot(slotIndex, inputs.get(i)));
                }
            }
            slot(player, Type.TOP, new StaticSlot(24, shapeless.result));
        } else if (recipe instanceof SmeltingRecipeImpl smelting) {
            slot(player, Type.TOP, new StaticSlot(20, smelting.input));
            slot(player, Type.TOP, new StaticSlot(24, smelting.result));
        } else if (recipe instanceof CampfireRecipeImpl campfire) {
            slot(player, Type.TOP, new StaticSlot(20, campfire.input));
            slot(player, Type.TOP, new StaticSlot(24, campfire.result));
        } else if (recipe instanceof SmithingRecipeImpl smithing) {
            slot(player, Type.TOP, new StaticSlot(19, smithing.base));
            slot(player, Type.TOP, new StaticSlot(20, smithing.addition));
            slot(player, Type.TOP, new StaticSlot(24, smithing.result));
        } else if (recipe instanceof StonecuttingRecipeImpl stonecutting) {
            slot(player, Type.TOP, new StaticSlot(20, stonecutting.input));
            slot(player, Type.TOP, new StaticSlot(24, stonecutting.result));
        }

        slot(player, Type.TOP, new ButtonSlot(49, named(Material.AIR, "Back"), ctx -> {
            AbyssalLib.GUI_MANAGER.openGui(ctx.player, new RecipeMainMenu());
        }));
    }

    @Override
    public boolean enableHandling(Type type) {
        return !type.equals(Type.BOTTOM);
    }

    private ItemStack named(Material material, String name) {
        ItemStack item = new ItemStack(material);
        item.editMeta(meta -> meta.displayName(Component.text(name)));
        return item;
    }
}

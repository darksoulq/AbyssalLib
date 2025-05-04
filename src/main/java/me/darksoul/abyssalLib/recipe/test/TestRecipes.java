package me.darksoul.abyssalLib.recipe.test;

import me.darksoul.abyssalLib.AbyssalLib;
import me.darksoul.abyssalLib.recipe.Recipe;
import me.darksoul.abyssalLib.recipe.ShapedRecipeImpl;
import me.darksoul.abyssalLib.registry.BuiltinRegistries;
import me.darksoul.abyssalLib.registry.DeferredRegistry;
import me.darksoul.abyssalLib.registry.object.DeferredObject;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TestRecipes {
    public static final DeferredRegistry<Recipe> RECIPES = DeferredRegistry.create(BuiltinRegistries.RECIPES, AbyssalLib.MODID);

    public static DeferredObject<Recipe> MAGIC_WAND = RECIPES.register("magic_wand", (name, id) -> new ShapedRecipeImpl(id,
            BuiltinRegistries.ITEMS.get("abyssallib:magic_wand"), " L ", " S ", " S ")
            .define('L', new ItemStack(Material.LAPIS_LAZULI))
            .define('S', new ItemStack(Material.STICK)));
}

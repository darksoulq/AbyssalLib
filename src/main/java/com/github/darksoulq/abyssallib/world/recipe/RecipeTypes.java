package com.github.darksoulq.abyssallib.world.recipe;

import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.recipe.type.*;

public class RecipeTypes {
    public static final DeferredRegistry<RecipeType<?>> RECIPE_TYPES = DeferredRegistry.create(Registries.RECIPE_TYPES, "minecraft");

    public static final RecipeType<?> SHAPED_RECIPE = RECIPE_TYPES.register("shaped", _ -> CustomShapedRecipe.TYPE);
    public static final RecipeType<?> SHAPELESS_RECIPE = RECIPE_TYPES.register("shapeless", _ -> CustomShapelessRecipe.TYPE);
    public static final RecipeType<?> TRANSMUTE_RECIPE = RECIPE_TYPES.register("transmute", _ -> CustomTransmuteRecipe.TYPE);
    public static final RecipeType<?> FURNACE_RECIPE = RECIPE_TYPES.register("furnace", _ -> CustomFurnaceRecipe.TYPE);
    public static final RecipeType<?> BLASTING_RECIPE = RECIPE_TYPES.register("blasting", _ -> CustomBlastingRecipe.TYPE);
    public static final RecipeType<?> SMOKING_RECIPE = RECIPE_TYPES.register("smoking", _ -> CustomSmokingRecipe.TYPE);
    public static final RecipeType<?> CAMPFIRE_RECIPE = RECIPE_TYPES.register("campfire", _ -> CustomCampfireRecipe.TYPE);
    public static final RecipeType<?> STONECUTTING_RECIPE = RECIPE_TYPES.register("stonecutting", _ -> CustomStonecuttingRecipe.TYPE);
    public static final RecipeType<?> SMITHING_TRANSFORM_RECIPE = RECIPE_TYPES.register("smithing_transform", _ -> CustomSmithingTransformRecipe.TYPE);
    public static final RecipeType<?> POTION_MIX = RECIPE_TYPES.register("potion_mix", _ -> CustomPotionMix.TYPE);
}

package com.github.darksoulq.abyssallib.world.recipe;

import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.recipe.type.*;

public class RecipeTypes {
    public static final DeferredRegistry<RecipeType<?>> RECIPE_TYPES = DeferredRegistry.create(Registries.RECIPE_TYPES, "minecraft");

    public static final RecipeType<?> SHAPED_RECIPE = RECIPE_TYPES.register("shaped", ignored -> CustomShapedRecipe.TYPE);
    public static final RecipeType<?> SHAPELESS_RECIPE = RECIPE_TYPES.register("shapeless", ignored -> CustomShapelessRecipe.TYPE);
    public static final RecipeType<?> TRANSMUTE_RECIPE = RECIPE_TYPES.register("transmute", ignored -> CustomTransmuteRecipe.TYPE);
    public static final RecipeType<?> FURNACE_RECIPE = RECIPE_TYPES.register("furnace", ignored -> CustomFurnaceRecipe.TYPE);
    public static final RecipeType<?> BLASTING_RECIPE = RECIPE_TYPES.register("blasting", ignored -> CustomBlastingRecipe.TYPE);
    public static final RecipeType<?> SMOKING_RECIPE = RECIPE_TYPES.register("smoking", ignored -> CustomSmokingRecipe.TYPE);
    public static final RecipeType<?> CAMPFIRE_RECIPE = RECIPE_TYPES.register("campfire", ignored -> CustomCampfireRecipe.TYPE);
    public static final RecipeType<?> STONECUTTING_RECIPE = RECIPE_TYPES.register("stonecutting", ignored -> CustomStonecuttingRecipe.TYPE);
    public static final RecipeType<?> SMITHING_TRANSFORM_RECIPE = RECIPE_TYPES.register("smithing_transform", ignored -> CustomSmithingTransformRecipe.TYPE);
    public static final RecipeType<?> POTION_MIX = RECIPE_TYPES.register("potion_mix", ignored -> CustomPotionMix.TYPE);
}

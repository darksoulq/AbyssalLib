package com.github.darksoulq.abyssallib.bootstrap;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.util.FileUtils;
import com.github.darksoulq.abyssallib.world.recipe.RecipeLoader;

import java.io.File;

public final class RecipeSetup {

    public static void init(AbyssalLib plugin) {
        File recipeFolder = new File(plugin.getDataFolder(), "recipes");
        FileUtils.createDirectories(recipeFolder);

        int loaded = RecipeLoader.loadFolder(recipeFolder);
        AbyssalLib.LOGGER.info("Loaded " + loaded + " Recipes");
    }
}
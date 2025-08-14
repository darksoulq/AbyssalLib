package com.github.darksoulq.abyssallib.util;

import com.github.darksoulq.abyssallib.world.level.block.internal.BlockManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class TextUtil {
    /**
     * Gson instance configured with registered type adapters for JSON (de)serialization.
     */
    public static Gson GSON;

    public static void buildGson() {
        GsonBuilder builder = new GsonBuilder()
                .serializeNulls()
                .excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT)
                .setPrettyPrinting();

        GSON = builder.create();
    }
}

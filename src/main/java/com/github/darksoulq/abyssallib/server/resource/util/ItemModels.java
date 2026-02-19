package com.github.darksoulq.abyssallib.server.resource.util;

import com.github.darksoulq.abyssallib.server.resource.Namespace;
import com.github.darksoulq.abyssallib.server.resource.asset.Model;
import com.github.darksoulq.abyssallib.server.resource.asset.Texture;

/**
 * Static factory utility for generating common Minecraft item model configurations.
 * <p>
 * This class provides methods to create standard {@link Model} assets using
 * vanilla parent templates such as {@code item/generated} or {@code item/handheld}.
 */
public final class ItemModels {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private ItemModels() {}

    /**
     * Creates a standard "generated" item model (flat item).
     * <p>
     * Typically used for simple items like materials, food, or GUI icons.
     *
     * @param ns     The {@link Namespace} to register the model in.
     * @param name   The name of the model file (without .json).
     * @param layer0 The base {@link Texture} to apply to the model.
     * @return A configured {@link Model} instance.
     */
    public static Model generated(Namespace ns, String name, Texture layer0) {
        return ns.model(name, false)
            .parent("minecraft:item/generated")
            .texture("layer0", layer0);
    }

    /**
     * Creates a "handheld" item model.
     * <p>
     * Handheld models are rotated in the player's hand to look like a tool or weapon.
     *
     * @param ns     The {@link Namespace} to register the model in.
     * @param name   The name of the model file.
     * @param layer0 The base {@link Texture} for the tool/weapon.
     * @return A configured {@link Model} instance with the handheld parent.
     */
    public static Model handheld(Namespace ns, String name, Texture layer0) {
        return ns.model(name, false)
            .parent("minecraft:item/handheld")
            .texture("layer0", layer0);
    }

    /**
     * Creates a "handheld_rod" item model.
     * <p>
     * Similar to handheld, but specifically used for fishing rods or similar tools.
     *
     * @param ns     The {@link Namespace} to register the model in.
     * @param name   The name of the model file.
     * @param layer0 The base {@link Texture}.
     * @return A configured {@link Model} instance.
     */
    public static Model handheldRod(Namespace ns, String name, Texture layer0) {
        return ns.model(name, false)
            .parent("minecraft:item/handheld_rod")
            .texture("layer0", layer0);
    }

    /**
     * Creates a model based on the vanilla spawn egg template.
     * <p>
     * Note: This template typically relies on color tints to distinguish between mobs.
     *
     * @param ns   The {@link Namespace} to register the model in.
     * @param name The name of the model file.
     * @return A configured {@link Model} instance with the spawn egg parent.
     */
    public static Model spawnEgg(Namespace ns, String name) {
        return ns.model(name, false)
            .parent("minecraft:item/template_spawn_egg");
    }

    /**
     * Creates a layered "generated" model with multiple texture overlays.
     * <p>
     * Useful for items with colorable layers, such as leather armor or potions.
     * Textures are assigned to {@code layer0}, {@code layer1}, etc., in order.
     *
     * @param ns     The {@link Namespace} to register the model in.
     * @param name   The name of the model file.
     * @param layers A vararg array of {@link Texture} assets.
     * @return A configured {@link Model} instance.
     */
    public static Model layered(Namespace ns, String name, Texture... layers) {
        Model model = ns.model(name, false)
            .parent("minecraft:item/generated");
        for (int i = 0; i < layers.length; i++) {
            model.texture("layer" + i, layers[i]);
        }
        return model;
    }

    /**
     * Creates a layered "handheld" model.
     * <p>
     * Combines multi-texture layering with the handheld rotation logic.
     *
     * @param ns     The {@link Namespace} to register the model in.
     * @param name   The name of the model file.
     * @param layers A vararg array of {@link Texture} assets.
     * @return A configured {@link Model} instance.
     */
    public static Model handheldLayered(Namespace ns, String name, Texture... layers) {
        Model model = ns.model(name, false)
            .parent("minecraft:item/handheld");
        for (int i = 0; i < layers.length; i++) {
            model.texture("layer" + i, layers[i]);
        }
        return model;
    }

    /**
     * Creates a custom model using a specific parent template.
     *
     * @param ns     The {@link Namespace} to register the model in.
     * @param name   The name of the model file.
     * @param parent The namespaced ID of the parent model (e.g., "minecraft:item/bow").
     * @param layer0 The base {@link Texture} to apply.
     * @return A configured {@link Model} instance.
     */
    public static Model template(Namespace ns, String name, String parent, Texture layer0) {
        return ns.model(name, false)
            .parent(parent)
            .texture("layer0", layer0);
    }
}
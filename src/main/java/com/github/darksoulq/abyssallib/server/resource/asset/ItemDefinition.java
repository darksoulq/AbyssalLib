package com.github.darksoulq.abyssallib.server.resource.asset;

import com.github.darksoulq.abyssallib.server.resource.asset.definition.Selector;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.plugin.Plugin;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class ItemDefinition implements Asset {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final String namespace;
    private final String id;

    private final byte[] rawData;

    private Selector selector;
    private boolean handAnimationOnSwap;
    private boolean oversizedInGui;
    private double swapAnimationScale;

    public ItemDefinition(Plugin plugin, String namespace, String id) {
        this.namespace = namespace;
        this.id = id;
        String path = "resourcepack/" + namespace + "/items/" + id + ".json";
        try (InputStream in = plugin.getResource(path)) {
            if (in == null) throw new RuntimeException("ItemDefinition not found: " + path);
            this.rawData = in.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load item definition", e);
        }
    }

    public ItemDefinition(String namespace, String id, byte[] data) {
        this.namespace = namespace;
        this.id = id;
        this.rawData = data;
    }

    public ItemDefinition(String namespace, String id, Selector selector, boolean handAnimationOnSwap, boolean oversizedInGui, double swapAnimationScale) {
        this.namespace = namespace;
        this.id = id;
        this.rawData = null;
        this.selector = selector;
        this.handAnimationOnSwap = handAnimationOnSwap;
        this.oversizedInGui = oversizedInGui;
        this.swapAnimationScale = swapAnimationScale;
    }

    public String file() {
        return namespace + ':' + id;
    }

    @Override
    public void emit(Map<String, byte[]> files) {
        String path = "assets/" + namespace + "/items/" + id + ".json";
        if (rawData != null) {
            files.put(path, rawData);
            return;
        }

        Map<String, Object> root = new LinkedHashMap<>();
        root.put("model", selector.toJson());
        root.put("hand_animation_on_swap", handAnimationOnSwap);
        root.put("oversized_in_gui", oversizedInGui);
        root.put("swap_animation_scale", swapAnimationScale);

        files.put(path, GSON.toJson(root).getBytes(StandardCharsets.UTF_8));
    }
}
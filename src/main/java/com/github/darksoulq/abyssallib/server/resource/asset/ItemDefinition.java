package com.github.darksoulq.abyssallib.server.resource.asset;

import com.github.darksoulq.abyssallib.server.resource.asset.definition.Selector;
import com.google.gson.GsonBuilder;
import org.bukkit.plugin.Plugin;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class ItemDefinition implements Asset {
    private final String namespace;
    private final String id;
    private final byte[] json;

    public ItemDefinition(Plugin plugin, String namespace, String id) {
        this.namespace = namespace;
        this.id = id;
        try (InputStream in = plugin.getResource("resourcepack/" + namespace + "/items/" + id + ".json")) {
            if (in == null) throw new RuntimeException("ItemDefinition not found");
            this.json = in.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load item definition", e);
        }
    }

    public ItemDefinition(String namespace, String id, Selector selector) {
        this(namespace, id, selector, true, false);
    }
    public ItemDefinition(String namespace, String id, Selector selector, boolean handAnimationOnSwap) {
        this(namespace, id, selector, handAnimationOnSwap, false);
    }
    public ItemDefinition(String namespace, String id, Selector selector, boolean handAnimationOnSwap, boolean oversizedInGui) {
        this.namespace = namespace;
        this.id = id;
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("model", selector.toJson());
        root.put("hand_animation_on_swap", handAnimationOnSwap);
        root.put("oversized_in_gui", oversizedInGui);
        this.json = new GsonBuilder().setPrettyPrinting().create()
                .toJson(root).getBytes(StandardCharsets.UTF_8);
    }

    public String file() {
        return namespace + ':' + id;
    }

    @Override
    public void emit(Map<String, byte[]> files) {
        files.put("assets/" + namespace + "/items/" + id + ".json", json);
    }
}
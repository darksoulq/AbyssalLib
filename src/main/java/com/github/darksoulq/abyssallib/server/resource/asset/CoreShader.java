package com.github.darksoulq.abyssallib.server.resource.asset;

import org.bukkit.plugin.Plugin;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class CoreShader implements Asset {
    private final String namespace;
    private final String name;
    private final byte[] vsh;
    private final byte[] fsh;
    private final byte[] json;

    public CoreShader(Plugin plugin, String namespace, String name) {
        this.namespace = namespace;
        this.name = name;
        this.vsh = load(plugin, namespace, name, ".vsh");
        this.fsh = load(plugin, namespace, name, ".fsh");
        this.json = load(plugin, namespace, name, ".json");
    }

    public CoreShader(String namespace, String name, byte[] vsh, byte[] fsh, byte[] json) {
        this.namespace = namespace;
        this.name = name;
        this.vsh = vsh;
        this.fsh = fsh;
        this.json = json;
    }

    public CoreShader(String namespace, String name, String vsh, String fsh, String json) {
        this(
            namespace, 
            name, 
            vsh != null ? vsh.getBytes(StandardCharsets.UTF_8) : null, 
            fsh != null ? fsh.getBytes(StandardCharsets.UTF_8) : null, 
            json != null ? json.getBytes(StandardCharsets.UTF_8) : null
        );
    }

    private byte[] load(Plugin plugin, String namespace, String name, String ext) {
        String path = "resourcepack/" + namespace + "/shaders/core/" + name + ext;
        try (InputStream in = plugin.getResource(path)) {
            if (in == null) return null;
            return in.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load core shader file: " + path, e);
        }
    }

    @Override
    public void emit(Map<String, byte[]> files) {
        String basePath = "assets/" + namespace + "/shaders/core/" + name;
        if (vsh != null) files.put(basePath + ".vsh", vsh);
        if (fsh != null) files.put(basePath + ".fsh", fsh);
        if (json != null) files.put(basePath + ".json", json);
    }
}
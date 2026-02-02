package com.github.darksoulq.abyssallib.server.resource.asset;

import org.bukkit.plugin.Plugin;

import java.io.InputStream;
import java.util.Map;

public class Texture implements Asset {

    private final String namespace;
    private final String path;
    private final byte[] data;

    public Texture(Plugin plugin, String namespace, String path) {
        this.namespace = namespace;
        this.path = path;
        String file = "resourcepack/" + namespace + "/textures/" + path + ".png";
        try (InputStream in = plugin.getResource(file)) {
            if (in == null) throw new RuntimeException("Texture not found: " + file);
            this.data = in.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load texture: " + file, e);
        }
    }

    public Texture(String namespace, String path, byte[] data) {
        this.namespace = namespace;
        this.path = path;
        this.data = data;
    }

    public String file() {
        return namespace + ':' + path;
    }

    public byte[] data() {
        return data;
    }

    @Override
    public void emit(Map<String, byte[]> files) {
        files.put("assets/" + namespace + "/textures/" + path + ".png", data);
    }
}
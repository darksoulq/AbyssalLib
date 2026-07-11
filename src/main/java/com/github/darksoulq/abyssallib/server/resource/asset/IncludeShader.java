package com.github.darksoulq.abyssallib.server.resource.asset;

import org.bukkit.plugin.Plugin;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class IncludeShader implements Asset {
    private final String namespace;
    private final String name;
    private final byte[] glsl;

    public IncludeShader(Plugin plugin, String namespace, String name) {
        this.namespace = namespace;
        this.name = name;
        this.glsl = load(plugin, namespace, name, ".glsl");
    }

    public IncludeShader(String namespace, String name, byte[] glsl) {
        this.namespace = namespace;
        this.name = name;
        this.glsl = glsl;
    }

    public IncludeShader(String namespace, String name, String glsl) {
        this(
            namespace,
            name,
            glsl != null ? glsl.getBytes(StandardCharsets.UTF_8) : null
        );
    }

    private byte[] load(Plugin plugin, String namespace, String name, String ext) {
        String path = "resourcepack/" + namespace + "/shaders/include/" + name + ext;
        try (InputStream in = plugin.getResource(path)) {
            if (in == null) return null;
            return in.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void emit(Map<String, byte[]> files) {
        if (glsl != null) {
            files.put("assets/" + namespace + "/shaders/include/" + name + ".glsl", glsl);
        }
    }
}
package com.github.darksoulq.abyssallib.server.resource.asset;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Sounds implements Asset {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final String namespace;
    private final Plugin plugin;
    private final Map<String, Sound> sounds = new LinkedHashMap<>();

    public Sounds(Plugin plugin, String namespace) {
        this.plugin = plugin;
        this.namespace = namespace;
    }

    public Sound sound(String name) {
        String path = "resourcepack/" + namespace + "/sounds/" + name + ".ogg";
        try (InputStream in = plugin.getResource(path)) {
            if (in == null) throw new RuntimeException("Sound file not found: " + path);
            byte[] data = in.readAllBytes();
            return sound(name, data);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load sound: " + name, e);
        }
    }

    public Sound sound(String name, byte[] data) {
        Sound sound = new Sound(namespace, name, data);
        sounds.put(name, sound);
        return sound;
    }

    @Override
    public void emit(Map<String, byte[]> files) {
        JsonObject root = new JsonObject();

        for (Map.Entry<String, Sound> entry : sounds.entrySet()) {
            entry.getValue().emit(files);
            root.add(namespace + "." + entry.getKey(), entry.getValue().toJson());
        }

        files.put("assets/" + namespace + "/sounds.json", GSON.toJson(root).getBytes(StandardCharsets.UTF_8));
    }

    public static class Sound {
        private final String namespace;
        private final String name;
        private final byte[] data;

        private String category = "master";
        private String subtitle;
        private boolean replace = false;
        private final List<SoundEntry> entries = new ArrayList<>();

        public Sound(String namespace, String name, byte[] data) {
            this.namespace = namespace;
            this.name = name;
            this.data = data;
            this.entries.add(new SoundEntry(namespace + ":sounds/" + name));
        }

        public Sound category(org.bukkit.SoundCategory category) {
            this.category = category.name().toLowerCase();
            return this;
        }

        public Sound subtitle(String subtitle) {
            this.subtitle = subtitle;
            return this;
        }

        public Sound replace(boolean replace) {
            this.replace = replace;
            return this;
        }

        public Sound stream(boolean stream) {
            if (!entries.isEmpty()) entries.get(0).stream = stream;
            return this;
        }

        public Sound pitch(float pitch) {
            if (!entries.isEmpty()) entries.get(0).pitch = pitch;
            return this;
        }

        public Sound volume(float volume) {
            if (!entries.isEmpty()) entries.get(0).volume = volume;
            return this;
        }

        public Sound attenuationDistance(int distance) {
            if (!entries.isEmpty()) entries.get(0).attenuationDistance = distance;
            return this;
        }

        public Sound preload(boolean preload) {
            if (!entries.isEmpty()) entries.get(0).preload = preload;
            return this;
        }

        public void play(Player player) {
            play(player, 1f, 1f);
        }

        public void play(Player player, float volume, float pitch) {
            player.playSound(player.getLocation(), namespace + ":" + namespace + "." + name, org.bukkit.SoundCategory.valueOf(category.toUpperCase()), volume, pitch);
        }

        public void play(Location location) {
            play(location, 1f, 1f);
        }

        public void play(Location location, float volume, float pitch) {
            if (location.getWorld() == null) return;
            location.getWorld().playSound(location, namespace + ":" + namespace + "." + name, org.bukkit.SoundCategory.valueOf(category.toUpperCase()), volume, pitch);
        }

        public String id() {
            return namespace + ":" + namespace + "." + name;
        }

        protected void emit(Map<String, byte[]> files) {
            files.put("assets/" + namespace + "/sounds/" + name + ".ogg", data);
        }

        protected JsonObject toJson() {
            JsonObject obj = new JsonObject();
            if (category != null) obj.addProperty("category", category);
            if (subtitle != null) obj.addProperty("subtitle", subtitle);
            if (replace) obj.addProperty("replace", true);

            JsonArray soundsArr = new JsonArray();
            for (SoundEntry e : entries) {
                soundsArr.add(e.toJson());
            }
            obj.add("sounds", soundsArr);
            return obj;
        }

        private static class SoundEntry {
            String name;
            Float volume;
            Float pitch;
            Float weight;
            Boolean stream;
            Integer attenuationDistance;
            Boolean preload;
            String type;

            SoundEntry(String name) {
                this.name = name;
            }

            JsonObject toJson() {
                JsonObject o = new JsonObject();
                o.addProperty("name", name);
                if (volume != null && volume != 1.0f) o.addProperty("volume", volume);
                if (pitch != null && pitch != 1.0f) o.addProperty("pitch", pitch);
                if (weight != null && weight != 1.0f) o.addProperty("weight", weight);
                if (stream != null && stream) o.addProperty("stream", true);
                if (attenuationDistance != null) o.addProperty("attenuation_distance", attenuationDistance);
                if (preload != null && preload) o.addProperty("preload", true);
                if (type != null) o.addProperty("type", type);
                return o;
            }
        }
    }
}
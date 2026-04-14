package com.github.darksoulq.abyssallib.server.resource.asset.definition;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

public interface AtlasSource {
    JsonObject toJson();

    class Directory implements AtlasSource {
        private final String source;
        private final String prefix;

        public Directory(String source, String prefix) {
            this.source = source;
            this.prefix = prefix;
        }

        @Override
        public JsonObject toJson() {
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "minecraft:directory");
            obj.addProperty("source", source);
            obj.addProperty("prefix", prefix);
            return obj;
        }
    }

    class Single implements AtlasSource {
        private final String resource;
        private final String sprite;

        public Single(String resource) {
            this.resource = resource;
            this.sprite = null;
        }

        public Single(String resource, String sprite) {
            this.resource = resource;
            this.sprite = sprite;
        }

        @Override
        public JsonObject toJson() {
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "minecraft:single");
            obj.addProperty("resource", resource);
            if (sprite != null) obj.addProperty("sprite", sprite);
            return obj;
        }
    }

    class PalettedPermutations implements AtlasSource {
        private final List<String> textures;
        private final String paletteKey;
        private final Map<String, String> permutations;

        public PalettedPermutations(List<String> textures, String paletteKey, Map<String, String> permutations) {
            this.textures = textures;
            this.paletteKey = paletteKey;
            this.permutations = permutations;
        }

        @Override
        public JsonObject toJson() {
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "minecraft:paletted_permutations");

            JsonArray texArray = new JsonArray();
            for (String t : textures) texArray.add(t);
            obj.add("textures", texArray);

            obj.addProperty("palette_key", paletteKey);

            JsonObject perms = new JsonObject();
            for (Map.Entry<String, String> e : permutations.entrySet()) {
                perms.addProperty(e.getKey(), e.getValue());
            }
            obj.add("permutations", perms);

            return obj;
        }
    }
}
package com.github.darksoulq.abyssallib.server.resource.asset.definition;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface Tint {
    String id();

    Map<String, Object> toJson();

    class Constant implements Tint {
        private String type = "int";
        private int value = 0;
        private int[] values = new int[3]; // max three

        public Constant(String type) {
            if (!"int".equals(type) && !"list".equals(type)) {
                throw new IllegalArgumentException(type + " is not a valid type!");
            }
            this.type = type;
        }

        public Constant value(int value) {
            this.value = value;
            return this;
        }

        public Constant values(int[] values) {
            this.values = values;
            return this;
        }

        @Override
        public String id() {
            return "minecraft:constant";
        }

        @Override
        public Map<String, Object> toJson() {
            Map<String, Object> json = new LinkedHashMap<>();
            json.put("type", id());
            json.put("value", "int".equals(type) ? value : List.of(values));
            return json;
        }
    }
    class CustomModelData implements Tint {
        public String type = "int";
        public int index;
        public int value = 0;
        public int[] values = new int[3]; // max three

        public CustomModelData(String type, int index) {
            if (!"int".equals(type) && !"list".equals(type)) {
                throw new IllegalArgumentException(type + " is not a valid type!");
            }
            this.type = type;
            this.index = index;
        }

        public CustomModelData value(int value) {
            this.value = value;
            return this;
        }

        public CustomModelData values(int[] values) {
            this.values = values;
            return this;
        }

        @Override
        public String id() {
            return "minecraft:custom_model_data";
        }

        @Override
        public Map<String, Object> toJson() {
            Map<String, Object> json = new LinkedHashMap<>();
            json.put("type", id());
            json.put("index", index);
            json.put("value", "int".equals(type) ? value : List.of(values));
            return json;
        }
    }
    class Dye implements Tint {
        public String type = "int";
        public int value = 0;
        public int[] values = new int[3]; // max three

        public Dye(String type) {
            if (!"int".equals(type) && !"list".equals(type)) {
                throw new IllegalArgumentException(type + " is not a valid type!");
            }
            this.type = type;
        }

        public Dye value(int value) {
            this.value = value;
            return this;
        }

        public Dye values(int[] values) {
            this.values = values;
            return this;
        }

        @Override
        public String id() {
            return "minecraft:dye";
        }

        @Override
        public Map<String, Object> toJson() {
            Map<String, Object> json = new LinkedHashMap<>();
            json.put("type", id());
            json.put("value", "int".equals(type) ? value : List.of(values));
            return json;
        }
    }
    class Firework implements Tint {
        public String type = "int";
        public int value = 0;
        public int[] values = new int[3]; // max three

        public Firework(String type) {
            if (!"int".equals(type) && !"list".equals(type)) {
                throw new IllegalArgumentException(type + " is not a valid type!");
            }
            this.type = type;
        }

        public Firework value(int value) {
            this.value = value;
            return this;
        }

        public Firework values(int[] values) {
            this.values = values;
            return this;
        }

        @Override
        public String id() {
            return "minecraft:firework";
        }

        @Override
        public Map<String, Object> toJson() {
            Map<String, Object> json = new LinkedHashMap<>();
            json.put("type", id());
            json.put("value", "int".equals(type) ? value : List.of(values));
            return json;
        }
    }
    class MapColor implements Tint {
        public String type = "int";
        public int value = 0;
        public int[] values = new int[3]; // max three

        public MapColor(String type) {
            if (!"int".equals(type) && !"list".equals(type)) {
                throw new IllegalArgumentException(type + " is not a valid type!");
            }
            this.type = type;
        }

        public MapColor value(int value) {
            this.value = value;
            return this;
        }

        public MapColor values(int[] values) {
            this.values = values;
            return this;
        }

        @Override
        public String id() {
            return "minecraft:map_color";
        }

        @Override
        public Map<String, Object> toJson() {
            Map<String, Object> json = new LinkedHashMap<>();
            json.put("type", id());
            json.put("value", "int".equals(type) ? value : List.of(values));
            return json;
        }
    }
    class Potion implements Tint {
        public String type = "int";
        public int value = 0;
        public int[] values = new int[3]; // max three

        public Potion(String type) {
            if (!"int".equals(type) && !"list".equals(type)) {
                throw new IllegalArgumentException(type + " is not a valid type!");
            }
            this.type = type;
        }

        public Potion value(int value) {
            this.value = value;
            return this;
        }

        public Potion values(int[] values) {
            this.values = values;
            return this;
        }

        @Override
        public String id() {
            return "minecraft:potion";
        }

        @Override
        public Map<String, Object> toJson() {
            Map<String, Object> json = new LinkedHashMap<>();
            json.put("type", id());
            json.put("value", "int".equals(type) ? value : List.of(values));
            return json;
        }
    }
    class Team implements Tint {
        public String type = "int";
        public int value = 0;
        public int[] values = new int[3]; // max three

        public Team(String type) {
            if (!"int".equals(type) && !"list".equals(type)) {
                throw new IllegalArgumentException(type + " is not a valid type!");
            }
            this.type = type;
        }

        public Team value(int value) {
            this.value = value;
            return this;
        }

        public Team values(int[] values) {
            this.values = values;
            return this;
        }

        @Override
        public String id() {
            return "minecraft:team";
        }

        @Override
        public Map<String, Object> toJson() {
            Map<String, Object> json = new LinkedHashMap<>();
            json.put("type", id());
            json.put("value", "int".equals(type) ? value : List.of(values));
            return json;
        }
    }
    class Grass implements Tint {
        public int temperature = 0;
        public int downfall = 0;

        public Grass(int temperature, int downfall) {
            this.temperature = temperature;
            this.downfall = downfall;
        }

        @Override
        public String id() {
            return "minecraft:grass";
        }

        @Override
        public Map<String, Object> toJson() {
            Map<String, Object> json = new LinkedHashMap<>();
            json.put("type", id());
            json.put("temperature", temperature);
            json.put("downfall", downfall);
            return json;
        }
    }
}
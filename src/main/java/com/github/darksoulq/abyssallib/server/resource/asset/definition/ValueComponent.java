package com.github.darksoulq.abyssallib.server.resource.asset.definition;

import com.github.darksoulq.abyssallib.common.util.Identifier;
import org.bukkit.attribute.Attribute;

import java.util.*;

public interface ValueComponent {
    String id();
    Object toJson();

    class AttributeModifiers implements ValueComponent {
        private final List<Contains> contains = new LinkedList<>();
        private final List<Count> counts = new LinkedList<>();
        private final Integer min, max;

        private AttributeModifiers(List<Contains> contains, List<Count> counts, Integer min, Integer max) {
            this.contains.addAll(contains);
            this.counts.addAll(counts);
            this.min = min;
            this.max = max;
        }

        @Override
        public String id() {
            return "minecraft:attribute_modifiers";
        }

        @Override
        public Map<String, Object> toJson() {
            Map<String, Object> json = new HashMap<>();
            Map<String, Object> modifiers = new HashMap<>();
            if (!contains.isEmpty()) modifiers.put("contains", contains.stream().map(Contains::toJson).toList());
            if (!counts.isEmpty()) modifiers.put("count", counts.stream().map(Count::toJson).toList());

            json.put("modifiers", modifiers);
            if (min != null) {
                if (max != null) {
                    Map<String, Integer> size = new HashMap<>();
                    size.put("min", min);
                    size.put("max", max);
                    json.put("size", size);
                } else json.put("size", min);
            }
            return json;
        }

        public static class Builder {
            private final List<Contains> contains = new LinkedList<>();
            private final List<Count> counts = new LinkedList<>();
            private Integer min, max = null;

            public Builder contains(Contains contains) {
                this.contains.add(contains);
                return this;
            }
            public Builder contains(List<Contains> contains) {
                this.contains.addAll(contains);
                return this;
            }
            public Builder contains(Contains... contains) {
                this.contains.addAll(Arrays.stream(contains).toList());
                return this;
            }

            public Builder count(Count count) {
                this.counts.add(count);
                return this;
            }
            public Builder counts(List<Count> counts) {
                this.counts.addAll(counts);
                return this;
            }
            public Builder counts(Count... counts) {
                this.counts.addAll(Arrays.stream(counts).toList());
                return this;
            }

            public Builder size(int size) {
                this.min = size;
                return this;
            }
            public Builder size(int min, int max) {
                this.min = min;
                this.max = max;
                return this;
            }

            public AttributeModifiers build() {
                return new AttributeModifiers(contains, counts, min, max);
            }
        }

        public record Contains(List<Attribute> attributes, Identifier id, Integer min, Integer max, Operation operation, Slot slot) {
            public static Builder builder() {
                return new Builder();
            }

            public Map<String, Object> toJson() {
                Map<String, Object> json = new HashMap<>();
                json.put("attribute", attributes.size() > 1 ? attributes.stream().map(a -> a.key().toString()).toList() : attributes.getFirst().key().toString());
                json.put("id", id.toString());
                if (min != null) {
                    if (max != null) {
                        Map<String, Integer> amount = new HashMap<>();
                        amount.put("min", min);
                        amount.put("max", max);
                        json.put("amount", amount);
                    } else json.put("amount", min);
                }
                if (operation != null) json.put("operation", operation.toString().toLowerCase());
                if (slot != null) json.put("slot", slot.toString().toLowerCase());
                return json;
            }

            public static class Builder {
                private final Set<Attribute> attributes = new HashSet<>();
                private Identifier id = null;
                private Integer min, max = null;
                private Operation operation = null;
                private Slot slot = null;

                public Builder attribute(Attribute attribute) {
                    attributes.add(attribute);
                    return this;
                }
                public Builder attributes(Set<Attribute> attributes) {
                    this.attributes.addAll(attributes);
                    return this;
                }
                public Builder attributes(Attribute... attributes) {
                    this.attributes.addAll(Arrays.stream(attributes).toList());
                    return this;
                }

                public Builder id(Identifier id) {
                    this.id = id;
                    return this;
                }

                public Builder size(int size) {
                    this.min = size;
                    return this;
                }
                public Builder size(int min, int max) {
                    this.min = min;
                    this.max = max;
                    return this;
                }

                public Builder operation(Operation operation) {
                    this.operation = operation;
                    return this;
                }
                public Builder slot(Slot slot) {
                    this.slot = slot;
                    return this;
                }

                public Contains build() {
                    if (attributes.isEmpty()) throw new IllegalStateException("Missing attributes");
                    return new Contains(attributes.stream().toList(), id, min, max, operation, slot);
                }
            }
        }
        public record Count(Contains contains, Integer min, Integer max) {
            public Map<String, Object> toJson() {
                Map<String, Object> json = new HashMap<>();
                json.put("test", contains.toJson());
                if (min != null) {
                    if (max != null) {
                        Map<String, Integer> amount = new HashMap<>();
                        amount.put("min", min);
                        amount.put("max", max);
                        json.put("count", amount);
                    } else json.put("count", min);
                }
                return json;
            }

            public static class Builder {
                private Contains contains;
                Integer min, max = null;

                public Builder test(Contains contains) {
                    this.contains = contains;
                    return this;
                }
                public Builder count(int count) {
                    this.min = count;
                    return this;
                }
                public Builder count(int min, int max) {
                    this.min = min;
                    this.max = max;
                    return this;
                }
                public Count build() {
                    if (contains == null || min == null) throw new IllegalStateException("Missing test or count");
                    return new Count(contains, min, max);
                }
            }
        }

        public enum Operation {
            ADD_VALUE, ADD_MULTIPLIED_BASE, ADD_MULTIPLIED_TOTAL
        }
        public enum Slot {
            MAIN_HAND, OFF_HAND, HEAD, CHEST, LEGS, FEET, HAND, ARMOR, ANY, BODY, SADDLE
        }
    }
    class CustomData implements ValueComponent {
        private final String value;
        private final Map<String, CustomDataValue> values = new HashMap<>();

        public CustomData(Map<String, CustomDataValue> values) {
            this.values.putAll(values);
            this.value = null;
        }
        public CustomData(String value) {
            this.value = value;
        }

        @Override
        public String id() {
            return "minecraft:custom_data";
        }

        @Override
        public Object toJson() {
            Map<String, Object> json = new HashMap<>();
            if (value != null) return value;
            else {
                values.forEach((k, v) -> {
                    json.put(k, v.toJson());
                });
                return json;
            }
        }

        public static class Factory {
            public static BooleanValue bool(boolean bool) {
                return new BooleanValue(bool);
            }
            public static DoubleValue dbl(double dbl) {
                return new DoubleValue(dbl);
            }
            public static FloatValue flt(float flt) {
                return new FloatValue(flt);
            }
            public static IntegerValue integer(int integer) {
                return new IntegerValue(integer);
            }
            public static StringValue string(String string) {
                return new StringValue(string);
            }
            public static ListValue list(List<CustomDataValue> values) {
                return new ListValue(values);
            }
            public static ListValue list(CustomDataValue... values) {
                return new ListValue(Arrays.stream(values).toList());
            }
            public static StructBuilder struct() {
                return new StructBuilder();
            }

            public static class StructBuilder {
                private final Map<String, CustomDataValue> values = new HashMap<>();

                public StructBuilder put(String key, CustomDataValue value) {
                    values.put(key, value);
                    return this;
                }
                public StructValue build() {
                    return new StructValue(values);
                }
            }
        }

        public interface CustomDataValue {
            Object toJson();
        }
        public record BooleanValue(boolean value) implements CustomDataValue {
            @Override
            public Object toJson() {
                return value;
            }
        }
        public record DoubleValue(double value) implements CustomDataValue {
            @Override
            public Object toJson() {
                return value;
            }
        }
        public record FloatValue(float value) implements CustomDataValue {
            @Override
            public Object toJson() {
                return value;
            }
        }
        public record IntegerValue(int value) implements CustomDataValue {
            @Override
            public Object toJson() {
                return value;
            }
        }
        public record StringValue(String value) implements CustomDataValue {
            @Override
            public Object toJson() {
                return value;
            }
        }
        public record ListValue(List<CustomDataValue> value) implements CustomDataValue {
            @Override
            public List<Object> toJson() {
                return List.of(value.stream().map(CustomDataValue::toJson));
            }
        }
        public record StructValue(Map<String, CustomDataValue> values) implements CustomDataValue {
            @Override
            public Object toJson() {
                Map<String, Object> json = new LinkedHashMap<>();
                values.forEach((k, v) -> json.put(k, v.toJson()));
                return json;
            }
        }
    }
    class Damage implements ValueComponent {
        private final Integer minDmg, maxDmg, minDur, maxDur;

        private Damage(Integer minDmg, Integer maxDmg, Integer minDur, Integer maxDur) {
            this.minDmg = minDmg;
            this.maxDmg = maxDmg;
            this.minDur = minDur;
            this.maxDur = maxDur;
        }

        @Override
        public String id() {
            return "minecraft:damage";
        }

        @Override
        public Object toJson() {
            Map<String, Object> json = new HashMap<>();
            if (minDmg != null) {
                if (maxDmg != null) {
                    Map<String, Object> damage = new HashMap<>();
                    damage.put("min", minDmg);
                    damage.put("max", maxDmg);
                    json.put("damage", damage);
                } else json.put("damage", minDmg);
            }
            if (minDur != null) {
                if (maxDur != null) {
                    Map<String, Object> durability = new HashMap<>();
                    durability.put("min", minDur);
                    durability.put("max", maxDur);
                    json.put("damage", durability);
                } else json.put("damage", minDur);
            }
            return json;
        }

        public static class Builder {
            private Integer minDmg, maxDmg, minDur, maxDur;

            public Builder damage(int damage) {
                this.minDmg = damage;
                return this;
            }
            public Builder damage(int min, int max) {
                this.minDmg = min;
                this.maxDmg = max;
                return this;
            }

            public Builder durability(int durability) {
                this.minDur = durability;
                return this;
            }
            public Builder durability(int min, int max) {
                this.minDur = min;
                this.maxDmg = max;
                return this;
            }

            public Damage build() {
                return new Damage(minDmg, maxDmg, minDur, maxDur);
            }
        }
    }
}

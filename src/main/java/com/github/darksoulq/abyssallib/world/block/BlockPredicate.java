package com.github.darksoulq.abyssallib.world.block;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.darksoulq.abyssallib.common.serialization.BlockInfo;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.util.Condition;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;

import java.util.*;
import java.util.function.Predicate;

/**
 * A sophisticated predicate used to evaluate and filter {@link BlockInfo} instances.
 * This class allows for complex conditional checks against a block's identity,
 * visual states, custom block entity properties, vanilla NBT data, and evaluation
 * of nested predicates.
 */
public class BlockPredicate implements Predicate<BlockInfo> {

    /**
     * A shared ObjectMapper instance for translating raw data into Jackson JsonNodes.
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Internal codec responsible for serializing and deserializing individual JSON map entries
     * used within the predicate's block state, property, and NBT conditions.
     */
    private static final Codec<Map.Entry<String, JsonNode>> JSON_ENTRY_CODEC = new Codec<>() {
        /**
         * Decodes a JSON map entry from a serialized format.
         *
         * @param <D>
         * The dynamic data type.
         * @param ops
         * The dynamic operations logic provider.
         * @param input
         * The raw serialized input data.
         * @return
         * The decoded map entry containing a String key and a {@link JsonNode} value.
         * @throws CodecException
         * If the map is malformed or empty.
         */
        @Override
        public <D> Map.Entry<String, JsonNode> decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected Map for entry"));
            if (map.isEmpty()) {
                throw new CodecException("Empty map entry");
            }

            Map.Entry<D, D> entry = map.entrySet().iterator().next();
            String key = Codecs.STRING.decode(ops, entry.getKey());
            JsonNode valueNode = MAPPER.valueToTree(entry.getValue());

            return Map.entry(key, valueNode);
        }

        /**
         * Encodes a JSON map entry into a serialized format.
         *
         * @param <D>
         * The dynamic data type.
         * @param ops
         * The dynamic operations logic provider.
         * @param value
         * The map entry to serialize.
         * @return
         * The encoded data map.
         * @throws CodecException
         * If the inner JSON node fails to encode.
         */
        @Override
        public <D> D encode(DynamicOps<D> ops, Map.Entry<String, JsonNode> value) throws CodecException {
            return ops.createMap(Map.of(
                ops.createString(value.getKey()),
                encodeNode(ops, value.getValue())
            ));
        }

        /**
         * Recursively encodes a Jackson JsonNode into the target dynamic data format.
         *
         * @param <D>
         * The dynamic data type.
         * @param ops
         * The dynamic operations logic provider.
         * @param node
         * The {@link JsonNode} to encode.
         * @return
         * The encoded dynamic data object.
         * @throws CodecException
         * If a nested node fails to serialize.
         */
        @SuppressWarnings("unchecked")
        private <D> D encodeNode(DynamicOps<D> ops, JsonNode node) throws CodecException {
            if (node.isObject()) {
                Map<D, D> map = new HashMap<>();
                node.fields().forEachRemaining(e -> {
                    try {
                        map.put(ops.createString(e.getKey()), encodeNode(ops, e.getValue()));
                    } catch (CodecException ex) {
                        throw new RuntimeException(ex);
                    }
                });
                return ops.createMap(map);
            } else if (node.isArray()) {
                List<D> list = new ArrayList<>();
                node.elements().forEachRemaining(e -> {
                    try {
                        list.add(encodeNode(ops, e));
                    } catch (CodecException ex) {
                        throw new RuntimeException(ex);
                    }
                });
                return ops.createList(list);
            } else if (node.isNumber()) {
                if (node.isFloatingPointNumber()) {
                    return (D) Codecs.FLOAT.encode(ops, node.floatValue());
                } else {
                    return (D) Codecs.INT.encode(ops, node.intValue());
                }
            } else if (node.isBoolean()) {
                return (D) Codecs.BOOLEAN.encode(ops, node.booleanValue());
            } else {
                return ops.createString(node.asText());
            }
        }
    };

    /**
     * The primary codec used to serialize and deserialize entire {@link BlockPredicate} instances.
     * Supports resolving registered predicates by their String ID or parsing full nested objects.
     */
    public static final Codec<BlockPredicate> CODEC = new Codec<>() {
        /**
         * Decodes a BlockPredicate from a serialized format.
         *
         * @param <D>
         * The dynamic data type.
         * @param ops
         * The dynamic operations logic provider.
         * @param input
         * The raw serialized input data.
         * @return
         * The decoded {@link BlockPredicate} instance.
         * @throws CodecException
         * If the payload is malformed or references an unknown registered predicate.
         */
        @Override
        public <D> BlockPredicate decode(DynamicOps<D> ops, D input) throws CodecException {
            if (ops.getStringValue(input).isPresent()) {
                String idStr = ops.getStringValue(input).get();
                BlockPredicate registered = Registries.BLOCK_PREDICATES.get(idStr);
                if (registered == null) {
                    throw new CodecException("Unknown block predicate: " + idStr);
                }
                return registered;
            }

            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected Map"));

            Key id = null;
            List<Condition<Map.Entry<String, JsonNode>>> states = new ArrayList<>();
            List<Condition<Map.Entry<String, JsonNode>>> properties = new ArrayList<>();
            List<Condition<Map.Entry<String, JsonNode>>> nbt = new ArrayList<>();
            List<Condition<BlockPredicate>> predicates = new ArrayList<>();

            if (map.containsKey(ops.createString("id"))) {
                id = Codecs.KEY.decode(ops, map.get(ops.createString("id")));
            }
            if (map.containsKey(ops.createString("states"))) {
                states = Condition.codec(JSON_ENTRY_CODEC).list().decode(ops, map.get(ops.createString("states")));
            }
            if (map.containsKey(ops.createString("properties"))) {
                properties = Condition.codec(JSON_ENTRY_CODEC).list().decode(ops, map.get(ops.createString("properties")));
            }
            if (map.containsKey(ops.createString("nbt"))) {
                nbt = Condition.codec(JSON_ENTRY_CODEC).list().decode(ops, map.get(ops.createString("nbt")));
            }
            if (map.containsKey(ops.createString("predicates"))) {
                predicates = Condition.codec(this).list().decode(ops, map.get(ops.createString("predicates")));
            }

            return new BlockPredicate(id, states, properties, nbt, predicates);
        }

        /**
         * Encodes a BlockPredicate into a serialized format.
         *
         * @param <D>
         * The dynamic data type.
         * @param ops
         * The dynamic operations logic provider.
         * @param value
         * The {@link BlockPredicate} to serialize.
         * @return
         * The encoded data, either as a reference string or a complete definition map.
         * @throws CodecException
         * If internal encoding operations fail.
         */
        @Override
        public <D> D encode(DynamicOps<D> ops, BlockPredicate value) throws CodecException {
            if (Registries.BLOCK_PREDICATES != null && Registries.BLOCK_PREDICATES.getAll().containsValue(value)) {
                return ops.createString(Registries.BLOCK_PREDICATES.getId(value));
            }

            Map<D, D> map = new HashMap<>();
            if (value.id != null) {
                map.put(ops.createString("id"), Codecs.KEY.encode(ops, value.id));
            }
            if (!value.states.isEmpty()) {
                map.put(ops.createString("states"), Condition.codec(JSON_ENTRY_CODEC).list().encode(ops, value.states));
            }
            if (!value.properties.isEmpty()) {
                map.put(ops.createString("properties"), Condition.codec(JSON_ENTRY_CODEC).list().encode(ops, value.properties));
            }
            if (!value.nbt.isEmpty()) {
                map.put(ops.createString("nbt"), Condition.codec(JSON_ENTRY_CODEC).list().encode(ops, value.nbt));
            }
            if (!value.predicates.isEmpty()) {
                map.put(ops.createString("predicates"), Condition.codec(this).list().encode(ops, value.predicates));
            }
            return ops.createMap(map);
        }
    };

    /** The specific base identity Key the block must match, if any. */
    private final Key id;

    /** A list of conditions checking for exact matches against block visual states. */
    private final List<Condition<Map.Entry<String, JsonNode>>> states;

    /** A list of conditions checking for exact matches against custom block properties. */
    private final List<Condition<Map.Entry<String, JsonNode>>> properties;

    /** A list of conditions checking for exact matches against vanilla NBT data. */
    private final List<Condition<Map.Entry<String, JsonNode>>> nbt;

    /** A list of nested predicate conditions to evaluate against the block. */
    private final List<Condition<BlockPredicate>> predicates;

    /**
     * Constructs a new BlockPredicate with the specified conditional rules.
     *
     * @param id
     * The base block Key that must match (nullable for any block).
     * @param states
     * The list of state-matching JSON data conditions.
     * @param properties
     * The list of property-matching JSON data conditions.
     * @param nbt
     * The list of NBT-matching JSON data conditions.
     * @param predicates
     * The list of nested predicate conditions.
     */
    public BlockPredicate(Key id, List<Condition<Map.Entry<String, JsonNode>>> states, List<Condition<Map.Entry<String, JsonNode>>> properties,
                          List<Condition<Map.Entry<String, JsonNode>>> nbt, List<Condition<BlockPredicate>> predicates) {
        this.id = id;
        this.states = states;
        this.properties = properties;
        this.nbt = nbt;
        this.predicates = predicates;
    }

    /**
     * Creates a new fluent builder instance for constructing a BlockPredicate.
     *
     * @return
     * A new {@link Builder} instance.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Compares this predicate against another object for logical equivalence.
     *
     * @param o
     * The object to compare.
     * @return
     * True if the object is an identical BlockPredicate.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BlockPredicate that)) {
            return false;
        }
        return Objects.equals(id, that.id) &&
            Objects.equals(states, that.states) &&
            Objects.equals(properties, that.properties) &&
            Objects.equals(nbt, that.nbt) &&
            Objects.equals(predicates, that.predicates);
    }

    /**
     * Generates a hash code based on the predicate's underlying conditions.
     *
     * @return
     * The integer hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, states, properties, nbt, predicates);
    }

    /**
     * Evaluates a {@link BlockInfo} against all configured conditions within this predicate.
     *
     * @param info
     * The {@link BlockInfo} to test.
     * @return
     * True if the block satisfies all identity, data, and nested rules; false otherwise.
     */
    @Override
    public boolean test(BlockInfo info) {
        if (info == null) {
            return false;
        }

        if (id != null) {
            String targetId = id.asString();
            String blockId = info.getAsString();
            if (!targetId.equals(blockId)) {
                return false;
            }
        }

        if (!checkNodes(info.states(), states)) {
            return false;
        }
        if (!checkNodes(info.properties(), properties)) {
            return false;
        }
        if (!checkNodes(info.nbt(), nbt)) {
            return false;
        }

        for (Condition<BlockPredicate> condition : predicates) {
            if (!condition.test(sub -> sub.test(info))) {
                return false;
            }
        }

        return true;
    }

    /**
     * Internal helper method to evaluate a parsed JSON ObjectNode against a list of data conditions.
     * Handles fuzzy matching between numeric and textual representations.
     *
     * @param node
     * The root {@link ObjectNode} representing a subset of the block's data.
     * @param conditions
     * The list of conditions to evaluate against the node.
     * @return
     * True if the JSON node satisfies all data conditions, false otherwise.
     */
    private boolean checkNodes(ObjectNode node, List<Condition<Map.Entry<String, JsonNode>>> conditions) {
        for (Condition<Map.Entry<String, JsonNode>> condition : conditions) {
            if (!condition.test(entry -> {
                if (node == null || !node.has(entry.getKey())) {
                    return false;
                }
                JsonNode actual = node.get(entry.getKey());

                if (actual.isTextual() && entry.getValue().isNumber()) {
                    return actual.asText().equals(entry.getValue().asText());
                } else if (actual.isNumber() && entry.getValue().isTextual()) {
                    return actual.asText().equals(entry.getValue().asText());
                }

                return actual.equals(entry.getValue());
            })) {
                return false;
            }
        }
        return true;
    }

    /**
     * A fluent builder pattern class designed to easily construct {@link BlockPredicate} instances.
     */
    public static class Builder {
        private Key id = null;
        private final List<Condition<Map.Entry<String, JsonNode>>> states = new ArrayList<>();
        private final List<Condition<Map.Entry<String, JsonNode>>> properties = new ArrayList<>();
        private final List<Condition<Map.Entry<String, JsonNode>>> nbt = new ArrayList<>();
        private final List<Condition<BlockPredicate>> predicates = new ArrayList<>();

        /**
         * Mandates that the block matches a specific identity Key.
         *
         * @param id
         * The {@link Key} to match.
         * @return
         * This builder instance.
         */
        public Builder id(Key id) {
            this.id = id;
            return this;
        }

        /**
         * Mandates that the block matches a specific vanilla material.
         *
         * @param material
         * The {@link Material} to match.
         * @return
         * This builder instance.
         */
        public Builder material(Material material) {
            this.id = Key.key(Key.MINECRAFT_NAMESPACE, material.name().toLowerCase(Locale.ROOT));
            return this;
        }

        /**
         * Adds an exact value-matching condition against the block's state data.
         *
         * @param condition
         * The {@link Condition} wrapping a JSON node entry.
         * @return
         * This builder instance.
         */
        public Builder state(Condition<Map.Entry<String, JsonNode>> condition) {
            this.states.add(condition);
            return this;
        }

        /**
         * Adds an exact value-matching condition against the block's custom properties.
         *
         * @param condition
         * The {@link Condition} wrapping a JSON node entry.
         * @return
         * This builder instance.
         */
        public Builder property(Condition<Map.Entry<String, JsonNode>> condition) {
            this.properties.add(condition);
            return this;
        }

        /**
         * Adds an exact value-matching condition against the block's NBT data.
         *
         * @param condition
         * The {@link Condition} wrapping a JSON node entry.
         * @return
         * This builder instance.
         */
        public Builder nbt(Condition<Map.Entry<String, JsonNode>> condition) {
            this.nbt.add(condition);
            return this;
        }

        /**
         * Adds a nested predicate condition.
         *
         * @param condition
         * The {@link Condition} wrapping a nested block predicate.
         * @return
         * This builder instance.
         */
        public Builder check(Condition<BlockPredicate> condition) {
            this.predicates.add(condition);
            return this;
        }

        /**
         * Requires an exact match for a specific key-value pair within the block's states.
         *
         * @param key
         * The state key to inspect.
         * @param value
         * The expected value object to match against.
         * @return
         * This builder instance.
         */
        public Builder state(String key, Object value) {
            return state(Condition.one(Map.entry(key, MAPPER.valueToTree(value))));
        }

        /**
         * Requires an exact match for a specific key-value pair within the block's properties.
         *
         * @param key
         * The property key to inspect.
         * @param value
         * The expected value object to match against.
         * @return
         * This builder instance.
         */
        public Builder property(String key, Object value) {
            return property(Condition.one(Map.entry(key, MAPPER.valueToTree(value))));
        }

        /**
         * Requires an exact match for a specific key-value pair within the block's NBT.
         *
         * @param key
         * The NBT key to inspect.
         * @param value
         * The expected value object to match against.
         * @return
         * This builder instance.
         */
        public Builder nbt(String key, Object value) {
            return nbt(Condition.one(Map.entry(key, MAPPER.valueToTree(value))));
        }

        /**
         * Evaluates a sub-predicate against the block.
         *
         * @param predicate
         * The nested {@link BlockPredicate} to test.
         * @return
         * This builder instance.
         */
        public Builder check(BlockPredicate predicate) {
            return check(Condition.one(predicate));
        }

        /**
         * Requires the block's states to match at least one of the provided key-value data entries.
         *
         * @param entries
         * The varargs array of map entries to check.
         * @return
         * This builder instance.
         */
        @SafeVarargs
        public final Builder stateAny(Map.Entry<String, Object>... entries) {
            List<Condition<Map.Entry<String, JsonNode>>> list = new ArrayList<>();
            for (Map.Entry<String, Object> e : entries) {
                list.add(Condition.one(Map.entry(e.getKey(), MAPPER.valueToTree(e.getValue()))));
            }
            return state(Condition.anyOf(list));
        }

        /**
         * Requires the block's properties to match at least one of the provided key-value data entries.
         *
         * @param entries
         * The varargs array of map entries to check.
         * @return
         * This builder instance.
         */
        @SafeVarargs
        public final Builder propertyAny(Map.Entry<String, Object>... entries) {
            List<Condition<Map.Entry<String, JsonNode>>> list = new ArrayList<>();
            for (Map.Entry<String, Object> e : entries) {
                list.add(Condition.one(Map.entry(e.getKey(), MAPPER.valueToTree(e.getValue()))));
            }
            return property(Condition.anyOf(list));
        }

        /**
         * Requires the block's NBT to match at least one of the provided key-value data entries.
         *
         * @param entries
         * The varargs array of map entries to check.
         * @return
         * This builder instance.
         */
        @SafeVarargs
        public final Builder nbtAny(Map.Entry<String, Object>... entries) {
            List<Condition<Map.Entry<String, JsonNode>>> list = new ArrayList<>();
            for (Map.Entry<String, Object> e : entries) {
                list.add(Condition.one(Map.entry(e.getKey(), MAPPER.valueToTree(e.getValue()))));
            }
            return nbt(Condition.anyOf(list));
        }

        /**
         * Requires the block to satisfy at least one of the provided sub-predicates.
         *
         * @param predicates
         * The varargs array of nested {@link BlockPredicate}s to check.
         * @return
         * This builder instance.
         */
        public Builder checkAny(BlockPredicate... predicates) {
            return check(Condition.anyOf(Arrays.stream(predicates).map(Condition::one).toList()));
        }

        /**
         * Finalizes construction and returns the built BlockPredicate.
         *
         * @return
         * The configured {@link BlockPredicate} instance.
         */
        public BlockPredicate build() {
            return new BlockPredicate(id, states, properties, nbt, predicates);
        }
    }
}
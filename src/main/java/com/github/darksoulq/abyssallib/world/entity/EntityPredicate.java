package com.github.darksoulq.abyssallib.world.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.SavedEntity;
import com.github.darksoulq.abyssallib.common.util.Condition;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import net.kyori.adventure.key.Key;

import java.util.*;
import java.util.function.Predicate;

/**
 * A predicate used to evaluate and filter {@link SavedEntity} instances based on their identity
 * and raw serialized data. This class allows for complex conditional checks against an entity's
 * JSON-like data structure, identity keys, and nested predicate logic.
 */
public class EntityPredicate implements Predicate<SavedEntity> {

    /**
     * A shared ObjectMapper instance for translating raw data into Jackson JsonNodes.
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Internal codec responsible for serializing and deserializing individual JSON map entries
     * used within the predicate's data conditions.
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
     * The primary codec used to serialize and deserialize entire {@link EntityPredicate} instances.
     * Supports resolving registered predicates by their String ID or parsing full nested objects.
     */
    public static final Codec<EntityPredicate> CODEC = new Codec<>() {
        /**
         * Decodes an EntityPredicate from a serialized format.
         *
         * @param <D>
         * The dynamic data type.
         * @param ops
         * The dynamic operations logic provider.
         * @param input
         * The raw serialized input data.
         * @return
         * The decoded {@link EntityPredicate} instance.
         * @throws CodecException
         * If the payload is malformed or references an unknown registered predicate.
         */
        @Override
        public <D> EntityPredicate decode(DynamicOps<D> ops, D input) throws CodecException {
            if (ops.getStringValue(input).isPresent()) {
                String idStr = ops.getStringValue(input).get();
                EntityPredicate registered = Registries.ENTITY_PREDICATES.get(idStr);
                if (registered == null) {
                    throw new CodecException("Unknown entity predicate: " + idStr);
                }
                return registered;
            }

            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected Map"));

            Key id = null;
            List<Condition<Map.Entry<String, JsonNode>>> data = new ArrayList<>();
            List<Condition<EntityPredicate>> predicates = new ArrayList<>();

            if (map.containsKey(ops.createString("id"))) {
                id = Codecs.KEY.decode(ops, map.get(ops.createString("id")));
            }
            if (map.containsKey(ops.createString("data"))) {
                data = Condition.codec(JSON_ENTRY_CODEC).list().decode(ops, map.get(ops.createString("data")));
            }
            if (map.containsKey(ops.createString("predicates"))) {
                predicates = Condition.codec(this).list().decode(ops, map.get(ops.createString("predicates")));
            }

            return new EntityPredicate(id, data, predicates);
        }

        /**
         * Encodes an EntityPredicate into a serialized format.
         *
         * @param <D>
         * The dynamic data type.
         * @param ops
         * The dynamic operations logic provider.
         * @param value
         * The {@link EntityPredicate} to serialize.
         * @return
         * The encoded data, either as a reference string or a complete definition map.
         * @throws CodecException
         * If internal encoding operations fail.
         */
        @Override
        public <D> D encode(DynamicOps<D> ops, EntityPredicate value) throws CodecException {
            if (Registries.ENTITY_PREDICATES != null && Registries.ENTITY_PREDICATES.getAll().containsValue(value)) {
                return ops.createString(Registries.ENTITY_PREDICATES.getId(value));
            }

            Map<D, D> map = new HashMap<>();
            if (value.id != null) {
                map.put(ops.createString("id"), Codecs.KEY.encode(ops, value.id));
            }
            if (!value.data.isEmpty()) {
                map.put(ops.createString("data"), Condition.codec(JSON_ENTRY_CODEC).list().encode(ops, value.data));
            }
            if (!value.predicates.isEmpty()) {
                map.put(ops.createString("predicates"), Condition.codec(this).list().encode(ops, value.predicates));
            }
            return ops.createMap(map);
        }
    };

    /** The specific identity Key the entity must match, if defined. */
    private final Key id;

    /** A list of conditions checking for exact data matches within the entity's JSON structure. */
    private final List<Condition<Map.Entry<String, JsonNode>>> data;

    /** A list of nested predicate conditions to evaluate against the entity. */
    private final List<Condition<EntityPredicate>> predicates;

    /**
     * Constructs a new EntityPredicate with the specified conditional rules.
     *
     * @param id
     * The identity Key that must match (nullable for any entity).
     * @param data
     * The list of specific value-matching JSON data conditions.
     * @param predicates
     * The list of nested predicate conditions.
     */
    public EntityPredicate(Key id, List<Condition<Map.Entry<String, JsonNode>>> data, List<Condition<EntityPredicate>> predicates) {
        this.id = id;
        this.data = data;
        this.predicates = predicates;
    }

    /**
     * Creates a new fluent builder instance for constructing an EntityPredicate.
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
     * True if the object is an identical EntityPredicate.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EntityPredicate that)) {
            return false;
        }
        return Objects.equals(id, that.id) &&
            Objects.equals(data, that.data) &&
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
        return Objects.hash(id, data, predicates);
    }

    /**
     * Evaluates a {@link SavedEntity} against all configured conditions within this predicate.
     *
     * @param info
     * The {@link SavedEntity} to test.
     * @return
     * True if the entity satisfies all identity, data, and nested rules; false otherwise.
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean test(SavedEntity info) {
        if (info == null) {
            return false;
        }

        if (id != null) {
            String targetId = id.asString();
            String entityId = info.getType().right().isEmpty()
                ? info.getType().right().get().getId().asString()
                : "minecraft:" + info.getType().left().get().name().toLowerCase(Locale.ROOT);

            if (!targetId.equals(entityId)) {
                return false;
            }
        }

        if (!data.isEmpty()) {
            Object rawData = info.getRawData();
            if (!(rawData instanceof Map)) {
                return false;
            }

            Map<Object, Object> rawMap = (Map<Object, Object>) rawData;
            JsonNode jsonNode = MAPPER.valueToTree(rawMap);

            if (!checkNodes(jsonNode, data)) {
                return false;
            }
        }

        for (Condition<EntityPredicate> condition : predicates) {
            if (!condition.test(sub -> sub.test(info))) {
                return false;
            }
        }

        return true;
    }

    /**
     * Internal helper method to evaluate a parsed JSON node against a list of data conditions.
     * Handles fuzzy matching between numeric and textual representations.
     *
     * @param node
     * The root {@link JsonNode} representing the entity's data.
     * @param conditions
     * The list of conditions to evaluate against the node.
     * @return
     * True if the JSON node satisfies all data conditions, false otherwise.
     */
    private boolean checkNodes(JsonNode node, List<Condition<Map.Entry<String, JsonNode>>> conditions) {
        if (node == null || !node.isObject()) {
            return false;
        }

        for (Condition<Map.Entry<String, JsonNode>> condition : conditions) {
            if (!condition.test(entry -> {
                if (!node.has(entry.getKey())) {
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
     * A fluent builder pattern class designed to easily construct {@link EntityPredicate} instances.
     */
    public static class Builder {
        private Key id = null;
        private final List<Condition<Map.Entry<String, JsonNode>>> data = new ArrayList<>();
        private final List<Condition<EntityPredicate>> predicates = new ArrayList<>();

        /**
         * Mandates that the entity matches a specific identity Key.
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
         * Adds an exact value-matching condition against the entity's serialized JSON data.
         *
         * @param condition
         * The {@link Condition} wrapping a JSON node entry.
         * @return
         * This builder instance.
         */
        public Builder data(Condition<Map.Entry<String, JsonNode>> condition) {
            this.data.add(condition);
            return this;
        }

        /**
         * Adds a nested predicate condition.
         *
         * @param condition
         * The {@link Condition} wrapping a nested entity predicate.
         * @return
         * This builder instance.
         */
        public Builder check(Condition<EntityPredicate> condition) {
            this.predicates.add(condition);
            return this;
        }

        /**
         * Requires an exact match for a specific key-value pair within the entity's data.
         *
         * @param key
         * The JSON key to inspect.
         * @param value
         * The expected value object to match against.
         * @return
         * This builder instance.
         */
        public Builder data(String key, Object value) {
            return data(Condition.one(Map.entry(key, MAPPER.valueToTree(value))));
        }

        /**
         * Evaluates a sub-predicate against the entity.
         *
         * @param predicate
         * The nested {@link EntityPredicate} to test.
         * @return
         * This builder instance.
         */
        public Builder check(EntityPredicate predicate) {
            return check(Condition.one(predicate));
        }

        /**
         * Requires the entity to match at least one of the provided key-value data entries.
         *
         * @param entries
         * The varargs array of map entries to check.
         * @return
         * This builder instance.
         */
        @SafeVarargs
        public final Builder dataAny(Map.Entry<String, Object>... entries) {
            List<Condition<Map.Entry<String, JsonNode>>> list = new ArrayList<>();
            for (Map.Entry<String, Object> e : entries) {
                list.add(Condition.one(Map.entry(e.getKey(), MAPPER.valueToTree(e.getValue()))));
            }
            return data(Condition.anyOf(list));
        }

        /**
         * Requires the entity to satisfy at least one of the provided sub-predicates.
         *
         * @param predicates
         * The varargs array of nested {@link EntityPredicate}s to check.
         * @return
         * This builder instance.
         */
        public Builder checkAny(EntityPredicate... predicates) {
            return check(Condition.anyOf(Arrays.stream(predicates).map(Condition::one).toList()));
        }

        /**
         * Finalizes construction and returns the built EntityPredicate.
         *
         * @return
         * The configured {@link EntityPredicate} instance.
         */
        public EntityPredicate build() {
            return new EntityPredicate(id, data, predicates);
        }
    }
}
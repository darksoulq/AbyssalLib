package com.github.darksoulq.abyssallib.common.serialization.ops;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;

import java.util.*;

/**
 * {@link DynamicOps} implementation for {@link JsonNode}.
 * <p>
 * Provides methods to encode and decode primitive types, lists, and maps into JSON using Jackson.
 * Supports optional extraction of primitive values and structured objects.
 * <p>
 * This is a singleton implementation; use {@link #INSTANCE}.
 */
public class JsonOps extends DynamicOps<JsonNode> {

    /** Singleton instance of {@link JsonOps}. */
    public static final JsonOps INSTANCE = new JsonOps();

    /** Private constructor to enforce singleton usage. */
    private JsonOps() {}

    /** {@inheritDoc} Encodes a string as a {@link TextNode}. */
    @Override
    public JsonNode createString(String value) {
        return TextNode.valueOf(value);
    }

    /** {@inheritDoc} Encodes an int as an {@link IntNode}. */
    @Override
    public JsonNode createInt(int value) {
        return IntNode.valueOf(value);
    }

    /** {@inheritDoc} Encodes a long as a {@link LongNode}. */
    @Override
    public JsonNode createLong(long value) {
        return LongNode.valueOf(value);
    }

    /** {@inheritDoc} Encodes a float as a {@link FloatNode}. */
    @Override
    public JsonNode createFloat(float value) {
        return FloatNode.valueOf(value);
    }

    /** {@inheritDoc} Encodes a double as a {@link DoubleNode}. */
    @Override
    public JsonNode createDouble(double value) {
        return DoubleNode.valueOf(value);
    }

    /** {@inheritDoc} Encodes a boolean as a {@link BooleanNode}. */
    @Override
    public JsonNode createBoolean(boolean value) {
        return BooleanNode.valueOf(value);
    }

    /** {@inheritDoc} Encodes a list of {@link JsonNode}s as an {@link ArrayNode}. */
    @Override
    public JsonNode createList(List<JsonNode> elements) {
        ArrayNode array = JsonNodeFactory.instance.arrayNode();
        for (JsonNode elem : elements) array.add(elem);
        return array;
    }

    /** {@inheritDoc} Encodes a map of {@link JsonNode}s as an {@link ObjectNode}, using the key's text representation. */
    @Override
    public JsonNode createMap(Map<JsonNode, JsonNode> map) {
        ObjectNode obj = JsonNodeFactory.instance.objectNode();
        for (Map.Entry<JsonNode, JsonNode> entry : map.entrySet()) {
            obj.set(entry.getKey().asText(), entry.getValue());
        }
        return obj;
    }

    /** {@inheritDoc} Extracts a string value from a {@link TextNode}, if present. */
    @Override
    public Optional<String> getStringValue(JsonNode input) {
        return input.isTextual() ? Optional.of(input.asText()) : Optional.empty();
    }

    /** {@inheritDoc} Extracts an int value from an {@link IntNode}, if present. */
    @Override
    public Optional<Integer> getIntValue(JsonNode input) {
        return input.isNumber() ? Optional.of(input.asInt()) : Optional.empty();
    }

    /** {@inheritDoc} Extracts a long value from a {@link LongNode}, if present. */
    @Override
    public Optional<Long> getLongValue(JsonNode input) {
        return input.isNumber() ? Optional.of(input.asLong()) : Optional.empty();
    }

    /** {@inheritDoc} Extracts a float value from a {@link FloatNode}, if present. */
    @Override
    public Optional<Float> getFloatValue(JsonNode input) {
        return input.isNumber() ? Optional.of(input.floatValue()) : Optional.empty();
    }

    /** {@inheritDoc} Extracts a double value from a {@link DoubleNode}, if present. */
    @Override
    public Optional<Double> getDoubleValue(JsonNode input) {
        return input.isNumber() ? Optional.of(input.doubleValue()) : Optional.empty();
    }

    /** {@inheritDoc} Extracts a boolean value from a {@link BooleanNode}, if present. */
    @Override
    public Optional<Boolean> getBooleanValue(JsonNode input) {
        return input.isBoolean() ? Optional.of(input.asBoolean()) : Optional.empty();
    }

    /** {@inheritDoc} Extracts a list of {@link JsonNode}s from an {@link ArrayNode}, if present. */
    @Override
    public Optional<List<JsonNode>> getList(JsonNode input) {
        if (!input.isArray()) return Optional.empty();
        List<JsonNode> list = new ArrayList<>();
        input.forEach(list::add);
        return Optional.of(list);
    }

    /** {@inheritDoc} Extracts a map of {@link JsonNode}s from an {@link ObjectNode}, if present. */
    @Override
    public Optional<Map<JsonNode, JsonNode>> getMap(JsonNode input) {
        if (!input.isObject()) return Optional.empty();
        Map<JsonNode, JsonNode> map = new LinkedHashMap<>();
        input.fields().forEachRemaining(entry -> map.put(TextNode.valueOf(entry.getKey()), entry.getValue()));
        return Optional.of(map);
    }

    /** {@inheritDoc} Returns a {@link NullNode} representing empty JSON. */
    @Override
    public JsonNode empty() {
        return NullNode.instance;
    }
}

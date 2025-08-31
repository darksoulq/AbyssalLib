package com.github.darksoulq.abyssallib.common.serialization.ops;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;

import java.util.*;

public class JsonOps extends DynamicOps<JsonNode> {

    public static final JsonOps INSTANCE = new JsonOps();

    private JsonOps() {}

    @Override
    public JsonNode createString(String value) {
        return TextNode.valueOf(value);
    }

    @Override
    public JsonNode createInt(int value) {
        return IntNode.valueOf(value);
    }

    @Override
    public JsonNode createLong(long value) {
        return LongNode.valueOf(value);
    }

    @Override
    public JsonNode createFloat(float value) {
        return FloatNode.valueOf(value);
    }

    @Override
    public JsonNode createDouble(double value) {
        return DoubleNode.valueOf(value);
    }

    @Override
    public JsonNode createBoolean(boolean value) {
        return BooleanNode.valueOf(value);
    }

    @Override
    public JsonNode createList(List<JsonNode> elements) {
        ArrayNode array = JsonNodeFactory.instance.arrayNode();
        for (JsonNode elem : elements) array.add(elem);
        return array;
    }

    @Override
    public JsonNode createMap(Map<JsonNode, JsonNode> map) {
        ObjectNode obj = JsonNodeFactory.instance.objectNode();
        for (Map.Entry<JsonNode, JsonNode> entry : map.entrySet()) {
            obj.set(entry.getKey().asText(), entry.getValue());
        }
        return obj;
    }

    @Override
    public Optional<String> getStringValue(JsonNode input) {
        return input.isTextual() ? Optional.of(input.asText()) : Optional.empty();
    }

    @Override
    public Optional<Integer> getIntValue(JsonNode input) {
        return input.isInt() ? Optional.of(input.asInt()) : Optional.empty();
    }

    @Override
    public Optional<Long> getLongValue(JsonNode input) {
        return input.isLong() ? Optional.of(input.asLong()) : Optional.empty();
    }

    @Override
    public Optional<Float> getFloatValue(JsonNode input) {
        return input.isFloat() ? Optional.of(input.floatValue()) : Optional.empty();
    }

    @Override
    public Optional<Double> getDoubleValue(JsonNode input) {
        return input.isDouble() ? Optional.of(input.doubleValue()) : Optional.empty();
    }

    @Override
    public Optional<Boolean> getBooleanValue(JsonNode input) {
        return input.isBoolean() ? Optional.of(input.asBoolean()) : Optional.empty();
    }

    @Override
    public Optional<List<JsonNode>> getList(JsonNode input) {
        if (!input.isArray()) return Optional.empty();
        List<JsonNode> list = new ArrayList<>();
        input.forEach(list::add);
        return Optional.of(list);
    }

    @Override
    public Optional<Map<JsonNode, JsonNode>> getMap(JsonNode input) {
        if (!input.isObject()) return Optional.empty();
        Map<JsonNode, JsonNode> map = new LinkedHashMap<>();
        input.fields().forEachRemaining(entry -> map.put(TextNode.valueOf(entry.getKey()), entry.getValue()));
        return Optional.of(map);
    }

    @Override
    public JsonNode empty() {
        return NullNode.instance;
    }
}

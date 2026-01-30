package com.github.darksoulq.abyssallib.common.serialization.ops;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;

import java.util.*;

/**
 * An implementation of {@link DynamicOps} for Jackson's {@link JsonNode} tree model.
 * This class allows {@link com.github.darksoulq.abyssallib.common.serialization.Codec}s
 * to serialize and deserialize Java objects to and from JSON structures.
 */
public class JsonOps extends DynamicOps<JsonNode> {

    /** Singleton instance of JsonOps to avoid unnecessary allocations. */
    public static final JsonOps INSTANCE = new JsonOps();

    /** Private constructor for singleton pattern. */
    private JsonOps() {}

    /**
     * Creates a {@link TextNode} from a String.
     * @param value The string value.
     * @return A JSON text node.
     */
    @Override
    public JsonNode createString(String value) {
        return TextNode.valueOf(value);
    }

    /**
     * Creates an {@link IntNode} from an integer.
     * @param value The int value.
     * @return A JSON integer node.
     */
    @Override
    public JsonNode createInt(int value) {
        return IntNode.valueOf(value);
    }

    /**
     * Creates a {@link LongNode} from a long.
     * @param value The long value.
     * @return A JSON long node.
     */
    @Override
    public JsonNode createLong(long value) {
        return LongNode.valueOf(value);
    }

    /**
     * Creates a {@link FloatNode} from a float.
     * @param value The float value.
     * @return A JSON float node.
     */
    @Override
    public JsonNode createFloat(float value) {
        return FloatNode.valueOf(value);
    }

    /**
     * Creates a {@link DoubleNode} from a double.
     * @param value The double value.
     * @return A JSON double node.
     */
    @Override
    public JsonNode createDouble(double value) {
        return DoubleNode.valueOf(value);
    }

    /**
     * Creates a {@link BooleanNode} from a boolean.
     * @param value The boolean value.
     * @return A JSON boolean node.
     */
    @Override
    public JsonNode createBoolean(boolean value) {
        return BooleanNode.valueOf(value);
    }

    /**
     * Creates an {@link ArrayNode} from a list of JSON nodes.
     * @param elements The list of nodes to include.
     * @return A JSON array node.
     */
    @Override
    public JsonNode createList(List<JsonNode> elements) {
        ArrayNode array = JsonNodeFactory.instance.arrayNode();
        for (JsonNode elem : elements) array.add(elem);
        return array;
    }

    /**
     * Creates an {@link ObjectNode} from a map of JSON nodes.
     * Keys are converted to strings via {@link JsonNode#asText()}.
     * @param map The map of key-value nodes.
     * @return A JSON object node.
     */
    @Override
    public JsonNode createMap(Map<JsonNode, JsonNode> map) {
        ObjectNode obj = JsonNodeFactory.instance.objectNode();
        for (Map.Entry<JsonNode, JsonNode> entry : map.entrySet()) {
            obj.set(entry.getKey().asText(), entry.getValue());
        }
        return obj;
    }

    /**
     * Extracts a String from a JSON node if it is textual.
     * @param input The JSON node.
     * @return An Optional containing the string, or empty.
     */
    @Override
    public Optional<String> getStringValue(JsonNode input) {
        return input.isTextual() ? Optional.of(input.asText()) : Optional.empty();
    }

    /**
     * Extracts an Integer from a JSON node if it is a number.
     * @param input The JSON node.
     * @return An Optional containing the integer, or empty.
     */
    @Override
    public Optional<Integer> getIntValue(JsonNode input) {
        return input.isNumber() ? Optional.of(input.asInt()) : Optional.empty();
    }

    /**
     * Extracts a Long from a JSON node if it is a number.
     * @param input The JSON node.
     * @return An Optional containing the long, or empty.
     */
    @Override
    public Optional<Long> getLongValue(JsonNode input) {
        return input.isNumber() ? Optional.of(input.asLong()) : Optional.empty();
    }

    /**
     * Extracts a Float from a JSON node if it is a number.
     * @param input The JSON node.
     * @return An Optional containing the float, or empty.
     */
    @Override
    public Optional<Float> getFloatValue(JsonNode input) {
        return input.isNumber() ? Optional.of(input.floatValue()) : Optional.empty();
    }

    /**
     * Extracts a Double from a JSON node if it is a number.
     * @param input The JSON node.
     * @return An Optional containing the double, or empty.
     */
    @Override
    public Optional<Double> getDoubleValue(JsonNode input) {
        return input.isNumber() ? Optional.of(input.doubleValue()) : Optional.empty();
    }

    /**
     * Extracts a Boolean from a JSON node if it is a boolean.
     * @param input The JSON node.
     * @return An Optional containing the boolean, or empty.
     */
    @Override
    public Optional<Boolean> getBooleanValue(JsonNode input) {
        return input.isBoolean() ? Optional.of(input.asBoolean()) : Optional.empty();
    }

    /**
     * Converts a JSON array node into a List of nodes.
     * @param input The JSON node.
     * @return An Optional containing the list, or empty if not an array.
     */
    @Override
    public Optional<List<JsonNode>> getList(JsonNode input) {
        if (!input.isArray()) return Optional.empty();
        List<JsonNode> list = new ArrayList<>();
        input.forEach(list::add);
        return Optional.of(list);
    }

    /**
     * Converts a JSON object node into a Map of nodes.
     * Keys are wrapped in {@link TextNode}s.
     * @param input The JSON node.
     * @return An Optional containing the map, or empty if not an object.
     */
    @Override
    public Optional<Map<JsonNode, JsonNode>> getMap(JsonNode input) {
        if (!input.isObject()) return Optional.empty();
        Map<JsonNode, JsonNode> map = new LinkedHashMap<>();
        input.fields().forEachRemaining(entry -> map.put(TextNode.valueOf(entry.getKey()), entry.getValue()));
        return Optional.of(map);
    }

    /**
     * Returns the representation of a null value in JSON.
     * @return {@link NullNode#instance}.
     */
    @Override
    public JsonNode empty() {
        return NullNode.instance;
    }
}
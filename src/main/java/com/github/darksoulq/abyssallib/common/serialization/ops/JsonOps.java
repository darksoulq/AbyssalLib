package com.github.darksoulq.abyssallib.common.serialization.ops;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.*;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;

import java.util.*;

/**
 * A {@link DynamicOps} implementation backed by Jackson's {@link JsonNode} tree model.
 * <p>
 * This implementation allows codecs to serialize and deserialize structured data
 * into standard JSON representations using Jackson's immutable tree API.
 */
public class JsonOps extends DynamicOps<JsonNode> {

    /**
     * Singleton instance of {@code JsonOps}.
     */
    public static final JsonOps INSTANCE = new JsonOps();

    /**
     * Underlying Jackson object mapper used for JSON operations.
     */
    public final ObjectMapper mapper = new JsonMapper();

    /**
     * Creates a new {@code JsonOps} instance.
     */
    private JsonOps() {
    }

    /**
     * Creates a JSON string node.
     *
     * @param value the string value
     * @return a {@link TextNode} containing the value
     */
    @Override
    public JsonNode createString(String value) {
        return TextNode.valueOf(value);
    }

    /**
     * Creates a JSON numeric node representing a byte.
     *
     * @param value the byte value
     * @return an {@link IntNode}
     */
    @Override
    public JsonNode createByte(byte value) {
        return IntNode.valueOf(value);
    }

    /**
     * Creates a JSON numeric node representing a short.
     *
     * @param value the short value
     * @return an {@link IntNode}
     */
    @Override
    public JsonNode createShort(short value) {
        return IntNode.valueOf(value);
    }

    /**
     * Creates a JSON numeric node representing an int.
     *
     * @param value the int value
     * @return an {@link IntNode}
     */
    @Override
    public JsonNode createInt(int value) {
        return IntNode.valueOf(value);
    }

    /**
     * Creates a JSON numeric node representing a long.
     *
     * @param value the long value
     * @return a {@link LongNode}
     */
    @Override
    public JsonNode createLong(long value) {
        return LongNode.valueOf(value);
    }

    /**
     * Creates a JSON numeric node representing a float.
     *
     * @param value the float value
     * @return a {@link FloatNode}
     */
    @Override
    public JsonNode createFloat(float value) {
        return FloatNode.valueOf(value);
    }

    /**
     * Creates a JSON numeric node representing a double.
     *
     * @param value the double value
     * @return a {@link DoubleNode}
     */
    @Override
    public JsonNode createDouble(double value) {
        return DoubleNode.valueOf(value);
    }

    /**
     * Creates a JSON boolean node.
     *
     * @param value the boolean value
     * @return a {@link BooleanNode}
     */
    @Override
    public JsonNode createBoolean(boolean value) {
        return BooleanNode.valueOf(value);
    }

    /**
     * Creates a JSON array node from a list of elements.
     *
     * @param elements the list of JSON nodes
     * @return an {@link ArrayNode}
     */
    @Override
    public JsonNode createList(List<JsonNode> elements) {
        ArrayNode array = JsonNodeFactory.instance.arrayNode();
        for (JsonNode elem : elements) array.add(elem);
        return array;
    }

    /**
     * Creates a JSON object node from a map of key-value pairs.
     * <p>
     * Keys are converted to strings using {@link JsonNode#asText()}.
     *
     * @param map the map of JSON nodes
     * @return an {@link ObjectNode}
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
     * Extracts a string value from a JSON node if it is textual.
     *
     * @param input the JSON node
     * @return the string value if present
     */
    @Override
    public Optional<String> getStringValue(JsonNode input) {
        return input.isTextual() ? Optional.of(input.asText()) : Optional.empty();
    }

    /**
     * Extracts a numeric value from a JSON node if it is numeric.
     *
     * @param input the JSON node
     * @return the number value if present
     */
    @Override
    public Optional<Number> getNumberValue(JsonNode input) {
        return input.isNumber() ? Optional.of(input.numberValue()) : Optional.empty();
    }

    /**
     * Extracts an integer value from a JSON node if it is numeric.
     *
     * @param input the JSON node
     * @return the integer value if present
     */
    @Override
    public Optional<Integer> getIntValue(JsonNode input) {
        return input.isNumber() ? Optional.of(input.asInt()) : Optional.empty();
    }

    /**
     * Extracts a long value from a JSON node if it is numeric.
     *
     * @param input the JSON node
     * @return the long value if present
     */
    @Override
    public Optional<Long> getLongValue(JsonNode input) {
        return input.isNumber() ? Optional.of(input.asLong()) : Optional.empty();
    }

    /**
     * Extracts a float value from a JSON node if it is numeric.
     *
     * @param input the JSON node
     * @return the float value if present
     */
    @Override
    public Optional<Float> getFloatValue(JsonNode input) {
        return input.isNumber() ? Optional.of(input.floatValue()) : Optional.empty();
    }

    /**
     * Extracts a double value from a JSON node if it is numeric.
     *
     * @param input the JSON node
     * @return the double value if present
     */
    @Override
    public Optional<Double> getDoubleValue(JsonNode input) {
        return input.isNumber() ? Optional.of(input.doubleValue()) : Optional.empty();
    }

    /**
     * Extracts a boolean value from a JSON node if it is boolean.
     *
     * @param input the JSON node
     * @return the boolean value if present
     */
    @Override
    public Optional<Boolean> getBooleanValue(JsonNode input) {
        return input.isBoolean() ? Optional.of(input.asBoolean()) : Optional.empty();
    }

    /**
     * Converts a JSON array node into a list of JSON nodes.
     *
     * @param input the JSON node
     * @return the list of elements if the node is an array
     */
    @Override
    public Optional<List<JsonNode>> getList(JsonNode input) {
        if (!input.isArray()) return Optional.empty();
        List<JsonNode> list = new ArrayList<>();
        input.forEach(list::add);
        return Optional.of(list);
    }

    /**
     * Converts a JSON object node into a map of JSON nodes.
     *
     * @param input the JSON node
     * @return the map of entries if the node is an object
     */
    @Override
    public Optional<Map<JsonNode, JsonNode>> getMap(JsonNode input) {
        if (!input.isObject()) return Optional.empty();
        Map<JsonNode, JsonNode> map = new LinkedHashMap<>();
        input.fields().forEachRemaining(entry ->
            map.put(TextNode.valueOf(entry.getKey()), entry.getValue())
        );
        return Optional.of(map);
    }

    /**
     * Returns the keys of a JSON object node.
     *
     * @param input the JSON node
     * @return an iterable of field names if the node is an object
     */
    @Override
    public Optional<Iterable<String>> getKeys(JsonNode input) {
        return input.isObject() ? Optional.of(input::fieldNames) : Optional.empty();
    }

    /**
     * Returns the number of elements in a JSON container node.
     *
     * @param input the JSON node
     * @return the size if the node is a container
     */
    @Override
    public OptionalInt size(JsonNode input) {
        return input.isContainerNode() ? OptionalInt.of(input.size()) : OptionalInt.empty();
    }

    /**
     * Creates a deep copy of the JSON node.
     *
     * @param input the JSON node
     * @return a deep-copied node
     */
    @Override
    public JsonNode copy(JsonNode input) {
        return input.deepCopy();
    }

    /**
     * Returns the representation of an empty JSON value.
     *
     * @return {@link NullNode#instance}
     */
    @Override
    public JsonNode empty() {
        return NullNode.instance;
    }
}
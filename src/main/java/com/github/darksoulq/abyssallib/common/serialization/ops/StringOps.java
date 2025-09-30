package com.github.darksoulq.abyssallib.common.serialization.ops;

import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;

import java.util.*;
import java.util.stream.Collectors;

/**
 * {@link DynamicOps} implementation for {@link String}-based serialization.
 * <p>
 * Provides encoding and decoding of primitive types, lists, and maps into a simple string format.
 * Strings are quoted with escaped quotes, numeric types have suffixes for long, float, and double.
 * Lists are represented as comma-separated values in square brackets, and maps as key:value pairs in curly braces.
 * <p>
 * Singleton implementation; use {@link #INSTANCE}.
 */
public class StringOps extends DynamicOps<String> {

    /** Singleton instance of {@link StringOps}. */
    public static final StringOps INSTANCE = new StringOps();

    /** Private constructor to enforce singleton usage. */
    private StringOps() {}

    /** {@inheritDoc} Encodes a string with quotes and escaped inner quotes. */
    @Override
    public String createString(String value) {
        return "\"" + value.replace("\"", "\\\"") + "\"";
    }

    /** {@inheritDoc} Encodes an int as a string. */
    @Override
    public String createInt(int value) {
        return Integer.toString(value);
    }

    /** {@inheritDoc} Encodes a long as a string with suffix "L". */
    @Override
    public String createLong(long value) {
        return value + "L";
    }

    /** {@inheritDoc} Encodes a float as a string with suffix "f". */
    @Override
    public String createFloat(float value) {
        return value + "f";
    }

    /** {@inheritDoc} Encodes a double as a string with suffix "d". */
    @Override
    public String createDouble(double value) {
        return value + "d";
    }

    /** {@inheritDoc} Encodes a boolean as a string "true" or "false". */
    @Override
    public String createBoolean(boolean value) {
        return Boolean.toString(value);
    }

    /** {@inheritDoc} Encodes a list of strings as a comma-separated string within square brackets. */
    @Override
    public String createList(List<String> elements) {
        return elements.stream().collect(Collectors.joining(",", "[", "]"));
    }

    /** {@inheritDoc} Encodes a map as comma-separated key:value pairs within curly braces. */
    @Override
    public String createMap(Map<String, String> map) {
        return map.entrySet()
                .stream()
                .map(e -> e.getKey() + ":" + e.getValue())
                .collect(Collectors.joining(",", "{", "}"));
    }

    /** {@inheritDoc} Decodes a quoted string, unescaping inner quotes. */
    @Override
    public Optional<String> getStringValue(String input) {
        if (input.startsWith("\"") && input.endsWith("\"")) {
            return Optional.of(input.substring(1, input.length() - 1).replace("\\\"", "\""));
        }
        return Optional.empty();
    }

    /** {@inheritDoc} Decodes an integer value, ignoring values with suffixes L, f, d. */
    @Override
    public Optional<Integer> getIntValue(String input) {
        try {
            if (input.endsWith("L") || input.endsWith("f") || input.endsWith("d")) return Optional.empty();
            return Optional.of(Integer.parseInt(input));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /** {@inheritDoc} Decodes a long value from a string ending with "L". */
    @Override
    public Optional<Long> getLongValue(String input) {
        try {
            if (input.endsWith("L")) {
                return Optional.of(Long.parseLong(input.substring(0, input.length() - 1)));
            }
            return Optional.empty();
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /** {@inheritDoc} Decodes a float value from a string ending with "f". */
    @Override
    public Optional<Float> getFloatValue(String input) {
        try {
            if (input.endsWith("f")) {
                return Optional.of(Float.parseFloat(input.substring(0, input.length() - 1)));
            }
            return Optional.empty();
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /** {@inheritDoc} Decodes a double value from a string ending with "d". */
    @Override
    public Optional<Double> getDoubleValue(String input) {
        try {
            if (input.endsWith("d")) {
                return Optional.of(Double.parseDouble(input.substring(0, input.length() - 1)));
            }
            return Optional.empty();
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /** {@inheritDoc} Decodes a boolean value from "true" or "false". */
    @Override
    public Optional<Boolean> getBooleanValue(String input) {
        if ("true".equals(input)) return Optional.of(true);
        if ("false".equals(input)) return Optional.of(false);
        return Optional.empty();
    }

    /** {@inheritDoc} Decodes a list from a string in square brackets. */
    @Override
    public Optional<List<String>> getList(String input) {
        if (!input.startsWith("[") || !input.endsWith("]")) return Optional.empty();
        String inner = input.substring(1, input.length() - 1);
        if (inner.isEmpty()) return Optional.of(new ArrayList<>());
        List<String> elements = Arrays.asList(inner.split(","));
        return Optional.of(elements);
    }

    /** {@inheritDoc} Decodes a map from a string in curly braces with key:value pairs. */
    @Override
    public Optional<Map<String, String>> getMap(String input) {
        if (!input.startsWith("{") || !input.endsWith("}")) return Optional.empty();
        String inner = input.substring(1, input.length() - 1);
        if (inner.isEmpty()) return Optional.of(new LinkedHashMap<>());
        Map<String, String> map = new LinkedHashMap<>();
        String[] entries = inner.split(",");
        for (String e : entries) {
            String[] kv = e.split(":", 2);
            if (kv.length == 2) {
                map.put(kv[0], kv[1]);
            }
        }
        return Optional.of(map);
    }

    /** {@inheritDoc} Returns an empty string. */
    @Override
    public String empty() {
        return "";
    }
}

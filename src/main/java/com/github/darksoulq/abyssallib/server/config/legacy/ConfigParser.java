package com.github.darksoulq.abyssallib.server.config.legacy;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.List;

/**
 * A utility class that handles the parsing and serialization of configuration values.
 * It provides methods to convert JSON elements to Java objects and vice versa,
 * with support for various types including primitives, lists, and custom types.
 * Additionally, it includes helpers for parsing type-specific strings (e.g., "10i" for integers, "10L" for longs).
 */
public class ConfigParser {

    /**
     * A helper method to parse a JSON element into its corresponding Java object representation.
     *
     * @param element The JSON element to parse.
     * @return The parsed Java object (could be a primitive, a List, or a String).
     */
    public static Object parseValue(JsonElement element) {
        if (element.isJsonPrimitive()) {
            JsonPrimitive prim = element.getAsJsonPrimitive();
            if (prim.isBoolean()) return prim.getAsBoolean();
            if (prim.isNumber()) {
                double d = prim.getAsDouble();
                return (d % 1 == 0) ? (int) d : d;
            }
            if (prim.isString()) {
                String raw = prim.getAsString();
                return parseTypedString(raw);
            }
        } else if (element.isJsonArray()) {
            List<Object> list = new ArrayList<>();
            for (JsonElement el : element.getAsJsonArray()) {
                list.add(parseValue(el));
            }
            return list;
        } else if (element.isJsonNull()) {
            return null;
        }

        return element.toString(); // fallback
    }

    /**
     * Serializes a given Java object into its corresponding JSON element representation.
     *
     * @param value The Java object to serialize.
     * @return The serialized JSON element.
     */
    public static JsonElement serializeValue(Object value) {
        if (value instanceof Integer i) return new JsonPrimitive(i + "i");
        if (value instanceof Long l) return new JsonPrimitive(l + "L");
        if (value instanceof Float f) return new JsonPrimitive(f + "f");
        if (value instanceof Double d) return new JsonPrimitive(d);
        if (value instanceof Boolean b) return new JsonPrimitive(b);
        if (value instanceof String s) return new JsonPrimitive(s);
        if (value instanceof List<?> list) {
            JsonArray array = new JsonArray();
            for (Object obj : list) {
                JsonElement el = serializeValue(obj);
                if (el != null) array.add(el);
            }
            return array;
        }
        return null;
    }

    /**
     * Parses a comma-separated list of values into a list of objects, based on the type of a default list.
     *
     * @param input The input string representing the list.
     * @param defaultList The list used as a type hint for parsing.
     * @return A list of parsed objects.
     */
    public static List<Object> parseList(String input, List<?> defaultList) {
        List<Object> result = new ArrayList<>();
        String[] parts = input.split(",");

        Object hint = defaultList.isEmpty() ? null : defaultList.get(0);

        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.isEmpty()) continue;

            Object parsed;
            if (hint instanceof Integer) {
                parsed = Integer.parseInt(trimmed);
            } else if (hint instanceof Long) {
                parsed = Long.parseLong(trimmed);
            } else if (hint instanceof Float) {
                parsed = Float.parseFloat(trimmed);
            } else if (hint instanceof Double) {
                parsed = Double.parseDouble(trimmed);
            } else if (hint instanceof Boolean) {
                parsed = Boolean.parseBoolean(trimmed);
            } else if (hint instanceof String) {
                parsed = trimmed;
            } else {
                parsed = parseTypedString(trimmed);
            }

            result.add(parsed);
        }

        return result;
    }

    /**
     * Attempts to parse a string into its respective typed value based on suffixes (e.g., 'i' for integer, 'L' for long).
     *
     * @param raw The raw string to parse.
     * @return The parsed value, or the original string if no matching suffix is found.
     */
    public static Object parseTypedString(String raw) {
        if (raw.endsWith("i")) {
            try { return Integer.parseInt(raw.substring(0, raw.length() - 1)); } catch (NumberFormatException ignored) {}
        } else if (raw.endsWith("L")) {
            try { return Long.parseLong(raw.substring(0, raw.length() - 1)); } catch (NumberFormatException ignored) {}
        } else if (raw.endsWith("f")) {
            try { return Float.parseFloat(raw.substring(0, raw.length() - 1)); } catch (NumberFormatException ignored) {}
        } else if (raw.endsWith("d")) {
            try { return Double.parseDouble(raw.substring(0, raw.length() - 1)); } catch (NumberFormatException ignored) {}
        }

        return raw;
    }
}

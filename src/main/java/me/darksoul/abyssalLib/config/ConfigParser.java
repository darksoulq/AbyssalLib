package me.darksoul.abyssalLib.config;

import com.google.gson.*;

import java.util.*;

public class ConfigParser {

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

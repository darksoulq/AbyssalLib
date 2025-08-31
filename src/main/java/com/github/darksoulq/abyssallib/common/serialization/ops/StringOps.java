package com.github.darksoulq.abyssallib.common.serialization.ops;

import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;

import java.util.*;
import java.util.stream.Collectors;

public class StringOps extends DynamicOps<String> {

    public static final StringOps INSTANCE = new StringOps();

    private StringOps() {}

    @Override
    public String createString(String value) {
        return "\"" + value.replace("\"", "\\\"") + "\"";
    }

    @Override
    public String createInt(int value) {
        return Integer.toString(value);
    }

    @Override
    public String createLong(long value) {
        return value + "L";
    }

    @Override
    public String createFloat(float value) {
        return value + "f";
    }

    @Override
    public String createDouble(double value) {
        return value + "d";
    }

    @Override
    public String createBoolean(boolean value) {
        return Boolean.toString(value);
    }

    @Override
    public String createList(List<String> elements) {
        return elements.stream().collect(Collectors.joining(",", "[", "]"));
    }

    @Override
    public String createMap(Map<String, String> map) {
        return map.entrySet()
                .stream()
                .map(e -> e.getKey() + ":" + e.getValue())
                .collect(Collectors.joining(",", "{", "}"));
    }

    @Override
    public Optional<String> getStringValue(String input) {
        if (input.startsWith("\"") && input.endsWith("\"")) {
            return Optional.of(input.substring(1, input.length() - 1).replace("\\\"", "\""));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Integer> getIntValue(String input) {
        try {
            if (input.endsWith("L") || input.endsWith("f") || input.endsWith("d")) return Optional.empty();
            return Optional.of(Integer.parseInt(input));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

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

    @Override
    public Optional<Boolean> getBooleanValue(String input) {
        if ("true".equals(input)) return Optional.of(true);
        if ("false".equals(input)) return Optional.of(false);
        return Optional.empty();
    }

    @Override
    public Optional<List<String>> getList(String input) {
        if (!input.startsWith("[") || !input.endsWith("]")) return Optional.empty();
        String inner = input.substring(1, input.length() - 1);
        if (inner.isEmpty()) return Optional.of(new ArrayList<>());
        List<String> elements = Arrays.asList(inner.split(","));
        return Optional.of(elements);
    }

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

    @Override
    public String empty() {
        return "";
    }
}

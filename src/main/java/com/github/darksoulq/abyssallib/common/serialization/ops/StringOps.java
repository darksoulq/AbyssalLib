package com.github.darksoulq.abyssallib.common.serialization.ops;

import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;

import java.util.*;
import java.util.stream.Collectors;

/**
 * {@link DynamicOps} implementation for {@link String}-based serialization.
 */
public final class StringOps extends DynamicOps<String> {

    public static final StringOps INSTANCE = new StringOps();

    private StringOps() {}

    @Override
    public String createString(String value) {
        return "\"" + value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"") + "\"";
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
        return elements.stream()
            .collect(Collectors.joining(",", "[", "]"));
    }

    @Override
    public String createMap(Map<String, String> map) {
        StringBuilder sb = new StringBuilder("{");
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, String> e = it.next();
            sb.append(e.getKey())
                .append(":")
                .append(e.getValue());

            if (it.hasNext()) {
                sb.append(",");
            }
        }

        sb.append("}");
        return sb.toString();
    }

    @Override
    public Optional<String> getStringValue(String input) {
        if (input.length() >= 2 && input.startsWith("\"") && input.endsWith("\"")) {
            String inner = input.substring(1, input.length() - 1);
            return Optional.of(inner
                .replace("\\\"", "\"")
                .replace("\\\\", "\\"));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Integer> getIntValue(String input) {
        if (isQuoted(input)) return Optional.empty();
        try {
            if (input.endsWith("L") || input.endsWith("f") || input.endsWith("d")) {
                return Optional.empty();
            }
            return Optional.of(Integer.parseInt(input));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Long> getLongValue(String input) {
        if (isQuoted(input)) return Optional.empty();
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
        if (isQuoted(input)) return Optional.empty();
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
        if (isQuoted(input)) return Optional.empty();
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
        if ("true".equalsIgnoreCase(input)) return Optional.of(true);
        if ("false".equalsIgnoreCase(input)) return Optional.of(false);
        return Optional.empty();
    }

    @Override
    public Optional<List<String>> getList(String input) {
        if (!input.startsWith("[") || !input.endsWith("]")) return Optional.empty();
        return Optional.of(splitTopLevel(input.substring(1, input.length() - 1)));
    }

    @Override
    public Optional<Map<String, String>> getMap(String input) {
        if (!input.startsWith("{") || !input.endsWith("}")) return Optional.empty();

        String inner = input.substring(1, input.length() - 1);
        Map<String, String> map = new LinkedHashMap<>();

        for (String entry : splitTopLevel(inner)) {
            int idx = entry.indexOf(':');
            if (idx > 0) {
                map.put(entry.substring(0, idx), entry.substring(idx + 1));
            }
        }
        return Optional.of(map);
    }

    @Override
    public String empty() {
        return "";
    }

    private static boolean isQuoted(String s) {
        return s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"");
    }

    /**
     * Splits a comma-separated string while respecting nesting and quotes.
     */
    private static List<String> splitTopLevel(String input) {
        List<String> result = new ArrayList<>();
        if (input.isEmpty()) return result;

        int depth = 0;
        boolean quoted = false;
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c == '"' && (i == 0 || input.charAt(i - 1) != '\\')) {
                quoted = !quoted;
            }

            if (!quoted) {
                if (c == '[' || c == '{') depth++;
                else if (c == ']' || c == '}') depth--;
                else if (c == ',' && depth == 0) {
                    result.add(current.toString());
                    current.setLength(0);
                    continue;
                }
            }

            current.append(c);
        }

        result.add(current.toString());
        return result;
    }
}

package com.github.darksoulq.abyssallib.common.serialization.ops;

import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import java.util.*;
import java.util.stream.Collectors;

/**
 * An implementation of {@link DynamicOps} that serializes data into a custom string-based format.
 * <p>
 * This format utilizes Java-style literal suffixes for numeric types and specialized
 * brackets for collections:
 * <ul>
 * <li><b>Strings:</b> Quoted with backslash escaping ({@code "text"})</li>
 * <li><b>Longs:</b> Suffix {@code L} ({@code 100L})</li>
 * <li><b>Floats:</b> Suffix {@code f} ({@code 1.5f})</li>
 * <li><b>Doubles:</b> Suffix {@code d} ({@code 2.0d})</li>
 * <li><b>Lists:</b> Wrapped in {@code []} and comma-separated</li>
 * <li><b>Maps:</b> Wrapped in {@code {}} with {@code key:value} pairs</li>
 * </ul>
 */
public final class StringOps extends DynamicOps<String> {

    /** The singleton instance of StringOps. */
    public static final StringOps INSTANCE = new StringOps();

    /** Private constructor to enforce the singleton pattern. */
    private StringOps() {}

    /**
     * Creates a quoted string with escaped backslashes and quotes.
     * @param value The raw string.
     * @return The escaped and quoted string.
     */
    @Override
    public String createString(String value) {
        return "\"" + value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"") + "\"";
    }

    /**
     * Converts an integer to its string representation.
     * @param value The integer value.
     * @return The string form of the integer.
     */
    @Override
    public String createInt(int value) {
        return Integer.toString(value);
    }

    /**
     * Converts a long to a string with an 'L' suffix.
     * @param value The long value.
     * @return The suffixed string.
     */
    @Override
    public String createLong(long value) {
        return value + "L";
    }

    /**
     * Converts a float to a string with an 'f' suffix.
     * @param value The float value.
     * @return The suffixed string.
     */
    @Override
    public String createFloat(float value) {
        return value + "f";
    }

    /**
     * Converts a double to a string with a 'd' suffix.
     * @param value The double value.
     * @return The suffixed string.
     */
    @Override
    public String createDouble(double value) {
        return value + "d";
    }

    /**
     * Converts a boolean to its string representation.
     * @param value The boolean value.
     * @return "true" or "false".
     */
    @Override
    public String createBoolean(boolean value) {
        return Boolean.toString(value);
    }

    /**
     * Joins a list of strings into a bracketed, comma-separated string.
     * @param elements The list of serialized elements.
     * @return A string formatted as {@code [elem1,elem2]}.
     */
    @Override
    public String createList(List<String> elements) {
        return elements.stream()
            .collect(Collectors.joining(",", "[", "]"));
    }

    /**
     * Formats a map into a curly-bracketed string of key-value pairs.
     * @param map The map of serialized keys and values.
     * @return A string formatted as {@code {key1:val1,key2:val2}}.
     */
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

    /**
     * Attempts to unquote and unescape a string.
     * @param input The serialized string.
     * @return Optional containing the raw string if correctly quoted.
     */
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

    /**
     * Parses an integer if no type suffixes are present.
     * @param input The serialized input.
     * @return Optional containing the integer if parsing succeeds.
     */
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

    /**
     * Parses a long by checking for the 'L' suffix.
     * @param input The serialized input.
     * @return Optional containing the long.
     */
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

    /**
     * Parses a float by checking for the 'f' suffix.
     * @param input The serialized input.
     * @return Optional containing the float.
     */
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

    /**
     * Parses a double by checking for the 'd' suffix.
     * @param input The serialized input.
     * @return Optional containing the double.
     */
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

    /**
     * Parses a boolean value.
     * @param input The string to check.
     * @return Optional containing true/false if valid.
     */
    @Override
    public Optional<Boolean> getBooleanValue(String input) {
        if ("true".equalsIgnoreCase(input)) return Optional.of(true);
        if ("false".equalsIgnoreCase(input)) return Optional.of(false);
        return Optional.empty();
    }

    /**
     * Parses a list by stripping brackets and splitting content at the top level.
     * @param input The serialized list string.
     * @return Optional list of serialized element strings.
     */
    @Override
    public Optional<List<String>> getList(String input) {
        if (!input.startsWith("[") || !input.endsWith("]")) return Optional.empty();
        return Optional.of(splitTopLevel(input.substring(1, input.length() - 1)));
    }

    /**
     * Parses a map by stripping brackets and splitting entries at the top level.
     * @param input The serialized map string.
     * @return Optional map of serialized key-value strings.
     */
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

    /**
     * @return An empty string representation.
     */
    @Override
    public String empty() {
        return "";
    }

    /**
     * Helper to determine if a string is wrapped in quotes.
     * @param s The string to test.
     * @return True if quoted.
     */
    private static boolean isQuoted(String s) {
        return s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"");
    }

    /**
     * Utility to split a string by commas, but only at the current nesting depth.
     * It ignores commas inside quotes or nested brackets/braces.
     *
     * @param input The inner content of a list or map.
     * @return A list of split segments.
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
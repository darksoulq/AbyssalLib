package com.github.darksoulq.abyssallib.common.serialization.ops;

import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A {@link DynamicOps} implementation that serializes values into a compact
 * string-based format.
 * <p>
 * The format encodes primitives using type suffixes and structures using
 * bracketed notation:
 * <ul>
 * <li>Strings: {@code "text"} with escaping</li>
 * <li>Bytes: {@code 1b}</li>
 * <li>Shorts: {@code 1s}</li>
 * <li>Integers: {@code 1}</li>
 * <li>Longs: {@code 1L}</li>
 * <li>Floats: {@code 1.0f}</li>
 * <li>Doubles: {@code 1.0d}</li>
 * <li>Lists: {@code [a,b,c]}</li>
 * <li>Maps: {@code {k:v,k2:v2}}</li>
 * </ul>
 */
public final class StringOps extends DynamicOps<String> {

    /** Singleton instance. */
    public static final StringOps INSTANCE = new StringOps();

    /** Private constructor for singleton usage. */
    private StringOps() {}

    /**
     * Serializes a string with escaping and quotes.
     *
     * @param value input string
     * @return quoted and escaped string
     */
    @Override
    public String createString(String value) {
        return "\"" + value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"") + "\"";
    }

    /**
     * Serializes a byte with {@code b} suffix.
     *
     * @param value byte value
     * @return encoded string
     */
    @Override
    public String createByte(byte value) {
        return value + "b";
    }

    /**
     * Serializes a short with {@code s} suffix.
     *
     * @param value short value
     * @return encoded string
     */
    @Override
    public String createShort(short value) {
        return value + "s";
    }

    /**
     * Serializes an integer as a plain numeric string.
     *
     * @param value integer value
     * @return encoded string
     */
    @Override
    public String createInt(int value) {
        return Integer.toString(value);
    }

    /**
     * Serializes a long with {@code L} suffix.
     *
     * @param value long value
     * @return encoded string
     */
    @Override
    public String createLong(long value) {
        return value + "L";
    }

    /**
     * Serializes a float with {@code f} suffix.
     *
     * @param value float value
     * @return encoded string
     */
    @Override
    public String createFloat(float value) {
        return value + "f";
    }

    /**
     * Serializes a double with {@code d} suffix.
     *
     * @param value double value
     * @return encoded string
     */
    @Override
    public String createDouble(double value) {
        return value + "d";
    }

    /**
     * Serializes a boolean value.
     *
     * @param value boolean value
     * @return {@code true} or {@code false}
     */
    @Override
    public String createBoolean(boolean value) {
        return Boolean.toString(value);
    }

    /**
     * Serializes a list into a comma-separated bracketed structure.
     *
     * @param elements encoded elements
     * @return list string
     */
    @Override
    public String createList(List<String> elements) {
        return elements.stream()
            .collect(Collectors.joining(",", "[", "]"));
    }

    /**
     * Serializes a map into a comma-separated key-value structure.
     *
     * @param map encoded map entries
     * @return map string
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

            if (it.hasNext()) sb.append(",");
        }

        sb.append("}");
        return sb.toString();
    }

    /**
     * Parses a quoted string and unescapes characters.
     *
     * @param input encoded string
     * @return decoded value if valid
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
     * Parses a numeric value using optional type suffixes.
     *
     * @param input encoded input
     * @return parsed number if valid
     */
    @Override
    public Optional<Number> getNumberValue(String input) {
        if (isQuoted(input)) return Optional.empty();
        try {
            if (input.endsWith("b")) return Optional.of(Byte.parseByte(input.substring(0, input.length() - 1)));
            if (input.endsWith("s")) return Optional.of(Short.parseShort(input.substring(0, input.length() - 1)));
            if (input.endsWith("L")) return Optional.of(Long.parseLong(input.substring(0, input.length() - 1)));
            if (input.endsWith("f")) return Optional.of(Float.parseFloat(input.substring(0, input.length() - 1)));
            if (input.endsWith("d")) return Optional.of(Double.parseDouble(input.substring(0, input.length() - 1)));
            return Optional.of(Integer.parseInt(input));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Parses an integer value if no type suffix is present.
     *
     * @param input encoded input
     * @return integer value if valid
     */
    @Override
    public Optional<Integer> getIntValue(String input) {
        if (isQuoted(input)) return Optional.empty();
        try {
            if (input.endsWith("L") || input.endsWith("f") || input.endsWith("d")
                || input.endsWith("b") || input.endsWith("s")) {
                return Optional.empty();
            }
            return Optional.of(Integer.parseInt(input));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Parses a long value with {@code L} suffix.
     *
     * @param input encoded input
     * @return long value if valid
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
     * Parses a float value with {@code f} suffix.
     *
     * @param input encoded input
     * @return float value if valid
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
     * Parses a double value with {@code d} suffix.
     *
     * @param input encoded input
     * @return double value if valid
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
     *
     * @param input encoded input
     * @return boolean if valid
     */
    @Override
    public Optional<Boolean> getBooleanValue(String input) {
        if ("true".equalsIgnoreCase(input)) return Optional.of(true);
        if ("false".equalsIgnoreCase(input)) return Optional.of(false);
        return Optional.empty();
    }

    /**
     * Parses a list from bracketed comma-separated format.
     *
     * @param input encoded list
     * @return parsed list if valid
     */
    @Override
    public Optional<List<String>> getList(String input) {
        if (!input.startsWith("[") || !input.endsWith("]")) return Optional.empty();
        return Optional.of(splitTopLevel(input.substring(1, input.length() - 1)));
    }

    /**
     * Parses a map from bracketed key-value format.
     *
     * @param input encoded map
     * @return parsed map if valid
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
     * Returns all keys from a serialized map.
     *
     * @param input encoded map
     * @return keys if valid map
     */
    @Override
    public Optional<Iterable<String>> getKeys(String input) {
        return getMap(input).map(Map::keySet);
    }

    /**
     * Returns the size of a list or map.
     *
     * @param input encoded value
     * @return size if valid structure
     */
    @Override
    public OptionalInt size(String input) {
        Optional<List<String>> listOpt = getList(input);
        if (listOpt.isPresent()) return OptionalInt.of(listOpt.get().size());

        Optional<Map<String, String>> mapOpt = getMap(input);
        return mapOpt.map(m -> OptionalInt.of(m.size())).orElseGet(OptionalInt::empty);
    }

    /**
     * Returns the input unchanged.
     *
     * @param input value
     * @return identical value
     */
    @Override
    public String copy(String input) {
        return input;
    }

    /**
     * Returns an empty string representation.
     *
     * @return empty string
     */
    @Override
    public String empty() {
        return "";
    }

    /**
     * Checks whether a string is quoted.
     */
    private static boolean isQuoted(String s) {
        return s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"");
    }

    /**
     * Splits a top-level comma-separated structure while respecting nesting.
     *
     * @param input inner content
     * @return split segments
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
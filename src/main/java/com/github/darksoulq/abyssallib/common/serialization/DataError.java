package com.github.darksoulq.abyssallib.common.serialization;

/**
 * Defines a structured error encountered during serialization or deserialization processes.
 */
public interface DataError {

    /**
     * @return The formatted message describing the context of the failure.
     */
    String message();

    /**
     * Represents a generic or untyped error state.
     *
     * @param message The raw error message.
     */
    record Custom(String message) implements DataError {
    }

    /**
     * Represents an error where a parsed type conflicts with the expected schema format.
     *
     * @param expected The expected type description.
     * @param actual   The evaluated runtime type description.
     */
    record TypeMismatch(String expected, String actual) implements DataError {
        @Override
        public String message() {
            return "Expected " + expected + " but found " + actual;
        }
    }

    /**
     * Represents an error where a mandatory structural field is absent.
     *
     * @param field The expected field identifier.
     */
    record MissingField(String field) implements DataError {
        @Override
        public String message() {
            return "Missing required field: '" + field + "'";
        }
    }

    /**
     * Represents a numerical state violating explicit threshold boundaries.
     *
     * @param value The encountered invalid value.
     * @param min   The expected inclusive minimum.
     * @param max   The expected inclusive maximum.
     */
    record OutOfBounds(Number value, Number min, Number max) implements DataError {
        @Override
        public String message() {
            return "Value " + value + " out of bounds [" + min + ", " + max + "]";
        }
    }

    /**
     * Represents a string matching failure mapped to an Enum definition.
     *
     * @param value     The string encountered.
     * @param enumClass The simple name of the enum constraint.
     */
    record UnknownEnum(String value, String enumClass) implements DataError {
        @Override
        public String message() {
            return "Unknown enum value '" + value + "' for " + enumClass;
        }
    }

    /**
     * Represents an unexpected null value encountered where a strict type was required.
     *
     * @param expected The expected structural type.
     */
    record NullValue(String expected) implements DataError {
        @Override
        public String message() {
            return "Expected " + expected + " but found null";
        }
    }

    /**
     * Represents a string formatting constraint violation.
     *
     * @param value   The encountered string.
     * @param pattern The required regex format.
     */
    record InvalidFormat(String value, String pattern) implements DataError {
        @Override
        public String message() {
            return "Value '" + value + "' does not match required format: " + pattern;
        }
    }

    /**
     * Represents an invalid index queried against an array or list.
     *
     * @param index The queried index.
     * @param size  The structural collection bounds.
     */
    record IndexOutOfBounds(int index, int size) implements DataError {
        @Override
        public String message() {
            return "Index " + index + " is out of bounds for size " + size;
        }
    }

    /**
     * Represents a structural collision where a key is parsed twice.
     *
     * @param key The colliding identifier.
     */
    record DuplicateKey(String key) implements DataError {
        @Override
        public String message() {
            return "Duplicate key encountered: " + key;
        }
    }

    /**
     * Represents an invocation against an unmapped or unavailable operational state.
     *
     * @param operation The triggered operation context.
     */
    record UnsupportedOperation(String operation) implements DataError {
        @Override
        public String message() {
            return "Unsupported operation executed: " + operation;
        }
    }

    /**
     * Wraps an existing DataError to append contextual path resolution tracking.
     *
     * @param error The original error node.
     * @param path  The appended structural path segment.
     */
    record PathAware(DataError error, String path) implements DataError {
        @Override
        public String message() {
            String suffix = error.message();
            if (path.isEmpty()) return suffix;
            String separator = suffix.startsWith("[") ? "" : ": ";
            return path + separator + suffix;
        }
    }

    /**
     * Instantiates a generic error.
     *
     * @param msg The target message.
     * @return A constructed DataError.
     */
    static DataError custom(String msg) {
        return new Custom(msg);
    }

    /**
     * Instantiates a type mismatch error.
     *
     * @param expected Target type sequence.
     * @param actual   Encountered type sequence.
     * @return A constructed DataError.
     */
    static DataError typeMismatch(String expected, String actual) {
        return new TypeMismatch(expected, actual);
    }

    /**
     * Instantiates a missing field constraint error.
     *
     * @param field Target nested key.
     * @return A constructed DataError.
     */
    static DataError missingField(String field) {
        return new MissingField(field);
    }

    /**
     * Instantiates a mathematical threshold error.
     *
     * @param value Target node.
     * @param min   Acceptable low.
     * @param max   Acceptable high.
     * @return A constructed DataError.
     */
    static DataError outOfBounds(Number value, Number min, Number max) {
        return new OutOfBounds(value, min, max);
    }

    /**
     * Instantiates an enum constant error.
     *
     * @param value     Evaluated input.
     * @param enumClass Target Enum descriptor.
     * @return A constructed DataError.
     */
    static DataError unknownEnum(String value, String enumClass) {
        return new UnknownEnum(value, enumClass);
    }

    /**
     * Instantiates a null state error.
     *
     * @param expected Expected structural wrapper.
     * @return A constructed DataError.
     */
    static DataError nullValue(String expected) {
        return new NullValue(expected);
    }

    /**
     * Instantiates a formatting constraint error.
     *
     * @param value   Evaluated string.
     * @param pattern Required sequence.
     * @return A constructed DataError.
     */
    static DataError invalidFormat(String value, String pattern) {
        return new InvalidFormat(value, pattern);
    }

    /**
     * Instantiates a boundary mapping error.
     *
     * @param index Targeted query index.
     * @param size  Structural upper bound.
     * @return A constructed DataError.
     */
    static DataError indexOutOfBounds(int index, int size) {
        return new IndexOutOfBounds(index, size);
    }

    /**
     * Instantiates a collision duplicate error.
     *
     * @param key Evaluated key.
     * @return A constructed DataError.
     */
    static DataError duplicateKey(String key) {
        return new DuplicateKey(key);
    }

    /**
     * Instantiates a restricted operation error.
     *
     * @param operation Evaluated action.
     * @return A constructed DataError.
     */
    static DataError unsupportedOperation(String operation) {
        return new UnsupportedOperation(operation);
    }
}
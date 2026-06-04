package com.github.darksoulq.abyssallib.common.serialization;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * A monadic container representing the result of a serialization or deserialization operation.
 * Contains either a successful value, potentially with partial warnings, or an error message detailing the failure context.
 *
 * @param <T> The type of the wrapped value.
 */
public interface DataResult<T> {

    /**
     * Creates a successful result containing the provided value with no structural warnings.
     *
     * @param <T>   The type of the value.
     * @param value The value to wrap.
     * @return A successful DataResult.
     */
    static <T> DataResult<T> success(T value) {
        return new Success<>(value, new ArrayList<>());
    }

    /**
     * Creates a partial result containing the provided value, logging structural warnings.
     *
     * @param <T>      The type of the value.
     * @param value    The value mapped despite violations.
     * @param warnings The collected issues encountered alongside data generation.
     * @return A successful DataResult with attached warnings.
     */
    static <T> DataResult<T> partial(T value, List<DataError> warnings) {
        return new Success<>(value, new ArrayList<>(warnings));
    }

    /**
     * Creates an error result with the provided string message.
     *
     * @param <T>     The expected type of the value.
     * @param message The error message.
     * @return An error DataResult wrapping a custom DataError.
     */
    static <T> DataResult<T> error(String message) {
        return new Error<>(DataError.custom(message), "");
    }

    /**
     * Creates an error result driven by an explicit DataError type.
     *
     * @param <T>   The expected type of the value.
     * @param error The structural error implementation.
     * @return An error DataResult.
     */
    static <T> DataResult<T> error(DataError error) {
        return new Error<>(error, "");
    }

    /**
     * @return True if this result represents a success (including partial successes), false otherwise.
     */
    boolean isSuccess();

    /**
     * @return True if this result represents an error, false otherwise.
     */
    boolean isError();

    /**
     * @return True if this result is successful but carries recorded warnings.
     */
    boolean isPartial();

    /**
     * @return An Optional containing the value if successful, or empty if an error.
     */
    Optional<T> result();

    /**
     * @return An Optional containing the formatted error message if an error, or empty if successful.
     */
    Optional<String> error();

    /**
     * @return An Optional containing the exact DataError type evaluated during deserialization.
     */
    Optional<DataError> dataError();

    /**
     * @return The collection of recorded warnings if this is a partial result.
     */
    List<DataError> warnings();

    /**
     * Retrieves the value or throws a RuntimeException if this is an error.
     *
     * @return The wrapped value.
     * @throws RuntimeException If this result represents an error.
     */
    T getOrThrow();

    /**
     * Retrieves the value, or returns the provided default value if this is an error.
     *
     * @param defaultValue The fallback value.
     * @return The wrapped value or the default.
     */
    T orElse(T defaultValue);

    /**
     * Transforms the wrapped value using the provided mapping function.
     *
     * @param <R>    The new value type.
     * @param mapper The transformation logic.
     * @return A new DataResult containing the transformed value.
     */
    <R> DataResult<R> map(Function<? super T, ? extends R> mapper);

    /**
     * Transforms the wrapped value into a new DataResult using the provided flat-mapping function.
     * Supports covariant return types to prevent generic capture mismatches.
     *
     * @param <R>    The new value type.
     * @param mapper The transformation logic that yields a DataResult.
     * @return A new DataResult.
     */
    <R> DataResult<R> flatMap(Function<? super T, ? extends DataResult<? extends R>> mapper);

    /**
     * Transforms the error message if this result represents an error.
     *
     * @param errorMapper The logic to apply to the existing error message.
     * @return A new DataResult with the transformed error.
     */
    DataResult<T> mapError(Function<String, String> errorMapper);

    /**
     * Prepends a path segment to the error context or recorded warnings to build a complete diagnostic trajectory.
     *
     * @param path The path segment to prepend.
     * @return A new DataResult reflecting the updated path logic.
     */
    DataResult<T> prependPath(String path);

    /**
     * Injects a recorded warning into the execution timeline without forcing a structural failure.
     *
     * @param warning The localized diagnostic logging message mapped as an error.
     * @return A modified instance retaining the internal value but flagging warning status.
     */
    DataResult<T> addWarning(DataError warning);

    /**
     * The structural implementation of a successful serialization state, handling optional partials.
     *
     * @param <T>      The type of the successful value.
     * @param value    The successfully serialized or deserialized data.
     * @param warnings Collection tracking degraded performance notes mapping.
     */
    record Success<T>(T value, List<DataError> warnings) implements DataResult<T> {
        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public boolean isError() {
            return false;
        }

        @Override
        public boolean isPartial() {
            return !warnings.isEmpty();
        }

        @Override
        public Optional<T> result() {
            return Optional.of(value);
        }

        @Override
        public Optional<String> error() {
            return Optional.empty();
        }

        @Override
        public Optional<DataError> dataError() {
            return Optional.empty();
        }

        @Override
        public T getOrThrow() {
            return value;
        }

        @Override
        public T orElse(T defaultValue) {
            return value;
        }

        @Override
        public <R> DataResult<R> map(Function<? super T, ? extends R> mapper) {
            try {
                return new Success<>(mapper.apply(value), new ArrayList<>(warnings));
            } catch (Exception e) {
                return DataResult.error(DataError.custom(e.getMessage() != null ? e.getMessage() : e.toString()));
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public <R> DataResult<R> flatMap(Function<? super T, ? extends DataResult<? extends R>> mapper) {
            try {
                DataResult<R> res = (DataResult<R>) mapper.apply(value);
                if (res.isError()) return res;
                List<DataError> combined = new ArrayList<>(warnings);
                combined.addAll(res.warnings());
                return new Success<>(res.getOrThrow(), combined);
            } catch (Exception e) {
                return DataResult.error(DataError.custom(e.getMessage() != null ? e.getMessage() : e.toString()));
            }
        }

        @Override
        public DataResult<T> mapError(Function<String, String> errorMapper) {
            return this;
        }

        @Override
        public DataResult<T> prependPath(String path) {
            if (warnings.isEmpty()) return this;
            List<DataError> mapped = new ArrayList<>();
            for (DataError w : warnings) {
                mapped.add(new DataError.PathAware(w, path));
            }
            return new Success<>(value, mapped);
        }

        @Override
        public DataResult<T> addWarning(DataError warning) {
            List<DataError> list = new ArrayList<>(warnings);
            list.add(warning);
            return new Success<>(value, list);
        }
    }

    /**
     * The structural implementation of a failed serialization state.
     *
     * @param <T>       The expected type of the value that failed to resolve.
     * @param errorData The exact runtime failure type evaluated.
     * @param path      The cumulative path to the failure point.
     */
    record Error<T>(DataError errorData, String path) implements DataResult<T> {
        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public boolean isError() {
            return true;
        }

        @Override
        public boolean isPartial() {
            return false;
        }

        @Override
        public Optional<T> result() {
            return Optional.empty();
        }

        @Override
        public Optional<String> error() {
            if (path.isEmpty()) return Optional.of(errorData.message());
            return Optional.of(path + ": " + errorData.message());
        }

        @Override
        public Optional<DataError> dataError() {
            return Optional.of(errorData);
        }

        @Override
        public List<DataError> warnings() {
            return List.of();
        }

        @Override
        public T getOrThrow() {
            throw new RuntimeException(error().get());
        }

        @Override
        public T orElse(T defaultValue) {
            return defaultValue;
        }

        @Override
        public <R> DataResult<R> map(Function<? super T, ? extends R> mapper) {
            return new Error<>(errorData, path);
        }

        @Override
        public <R> DataResult<R> flatMap(Function<? super T, ? extends DataResult<? extends R>> mapper) {
            return new Error<>(errorData, path);
        }

        @Override
        public DataResult<T> mapError(Function<String, String> errorMapper) {
            return new Error<>(DataError.custom(errorMapper.apply(errorData.message())), path);
        }

        @Override
        public DataResult<T> prependPath(String newPath) {
            String updatedPath = path.isEmpty() ? newPath : newPath + (path.startsWith("[") ? "" : ".") + path;
            return new Error<>(errorData, updatedPath);
        }

        @Override
        public DataResult<T> addWarning(DataError warning) {
            return this;
        }
    }
}
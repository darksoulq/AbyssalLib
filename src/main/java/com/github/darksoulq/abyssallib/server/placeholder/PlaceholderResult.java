package com.github.darksoulq.abyssallib.server.placeholder;

import net.kyori.adventure.text.Component;

public class PlaceholderResult<T> {

    private static final PlaceholderResult<?> EMPTY = new PlaceholderResult<>(null, null, true);

    private final T value;
    private final Component error;
    private final boolean empty;

    private PlaceholderResult(T value, Component error, boolean empty) {
        this.value = value;
        this.error = error;
        this.empty = empty;
    }

    public static <T> PlaceholderResult<T> success(T value) {
        return new PlaceholderResult<>(value, null, false);
    }

    public static <T> PlaceholderResult<T> error(Component error) {
        return new PlaceholderResult<>(null, error, false);
    }

    public static <T> PlaceholderResult<T> error(String error) {
        return new PlaceholderResult<>(null, Component.text(error), false);
    }

    @SuppressWarnings("unchecked")
    public static <T> PlaceholderResult<T> empty() {
        return (PlaceholderResult<T>) EMPTY;
    }

    public T getValue() {
        return value;
    }

    public Component getError() {
        return error;
    }

    public boolean isError() {
        return error != null;
    }

    public boolean isEmpty() {
        return empty;
    }
}
package com.github.darksoulq.abyssallib.common.util;

import java.util.function.Consumer;
import java.util.function.Function;

public class Try {

    @FunctionalInterface
    public interface ThrowingRunnable {
        void run() throws Exception;
    }

    @FunctionalInterface
    public interface ThrowingSupplier<T> {
        T get() throws Exception;
    }

    public static void run(ThrowingRunnable runnable, Consumer<Exception> handler) {
        try {
            runnable.run();
        } catch (Exception e) {
            handler.accept(e);
        }
    }

    public static <T> T get(ThrowingSupplier<T> supplier, Function<Exception, T> handler) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return handler.apply(e);
        }
    }

    public static void run(ThrowingRunnable runnable) {
        run(runnable, e -> {});
    }

    public static <T> T get(ThrowingSupplier<T> supplier, T defaultValue) {
        return get(supplier, (Function<Exception, T>) e -> defaultValue);
    }
}

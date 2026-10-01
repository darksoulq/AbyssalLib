package com.github.darksoulq.abyssallib.server.registry.modifier;

public interface DeferredRegistryModifier {
    default void onRegister(String id, Object value) {}
    default void postApply() {}

    default void onUnload(String id, Object value) {}
    default void postUnload() {}
}
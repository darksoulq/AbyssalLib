package com.github.darksoulq.abyssallib.server.registry.modifier;

public interface DeferredRegistryModifier {
    void onRegister(String id, Object value);

    default void postApply() {}
}
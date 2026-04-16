package com.github.darksoulq.abyssallib.server.economy;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface EconomyProvider {
    Key id();

    Component name();

    Currency defaultCurrency();

    Collection<Currency> currencies();

    CompletableFuture<Boolean> hasAccount(UUID player);

    CompletableFuture<Boolean> hasAccount(Key id);

    CompletableFuture<Account> account(UUID player);

    CompletableFuture<Account> account(Key id);

    CompletableFuture<Account> createAccount(Key id, AccountType type, UUID owner);

    CompletableFuture<Boolean> deleteAccount(Key id);

    <T> Optional<T> capability(Class<T> capabilityClass);
}
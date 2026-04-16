package com.github.darksoulq.abyssallib.server.economy.capability;

import com.github.darksoulq.abyssallib.server.economy.Account;
import com.github.darksoulq.abyssallib.server.economy.Currency;
import com.github.darksoulq.abyssallib.server.economy.EconomyContext;
import net.kyori.adventure.text.Component;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface SubscriptionService {
    CompletableFuture<Subscription> create(Account subscriber, Account beneficiary, Currency currency, BigDecimal amount, Duration interval, EconomyContext context, Component reason);
    CompletableFuture<Subscription> createWithTrial(Account subscriber, Account beneficiary, Currency currency, BigDecimal amount, Duration interval, Duration trialPeriod, EconomyContext context, Component reason);
    CompletableFuture<Boolean> cancel(UUID subscriptionId, Component reason);
    CompletableFuture<Boolean> pause(UUID subscriptionId, Component reason);
    CompletableFuture<Boolean> resume(UUID subscriptionId, Component reason);
    CompletableFuture<Boolean> forceProcess(UUID subscriptionId);
    CompletableFuture<Optional<Subscription>> fetch(UUID subscriptionId);
    CompletableFuture<Collection<Subscription>> fetchActive(Account subscriber);

    record Subscription(UUID id, Account subscriber, Account beneficiary, Currency currency, BigDecimal amount, Duration interval, Instant lastBilled, Instant nextBilling, EconomyContext context, SubscriptionState state) {}

    enum SubscriptionState {
        ACTIVE,
        TRIAL,
        PAUSED,
        PAST_DUE,
        CANCELLED,
        COMPLETED
    }
}
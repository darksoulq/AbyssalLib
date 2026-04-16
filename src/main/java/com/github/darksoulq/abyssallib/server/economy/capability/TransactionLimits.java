package com.github.darksoulq.abyssallib.server.economy.capability;

import com.github.darksoulq.abyssallib.server.economy.Account;
import com.github.darksoulq.abyssallib.server.economy.Currency;
import com.github.darksoulq.abyssallib.server.economy.EconomyContext;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface TransactionLimits {
    CompletableFuture<Void> setAccountLimit(Account account, Currency currency, LimitAction action, BigDecimal limit, Duration period, EconomyContext context);
    CompletableFuture<Void> setGlobalLimit(EconomyContext context, Currency currency, LimitAction action, BigDecimal limit, Duration period);
    CompletableFuture<Void> clearAccountLimit(Account account, Currency currency, LimitAction action, EconomyContext context);
    CompletableFuture<Boolean> isWithinLimit(Account account, Currency currency, LimitAction action, BigDecimal amount, EconomyContext context);
    CompletableFuture<BigDecimal> remainingLimit(Account account, Currency currency, LimitAction action, EconomyContext context);
    CompletableFuture<Optional<LimitProfile>> profile(Account account, Currency currency, LimitAction action, EconomyContext context);
    CompletableFuture<Collection<LimitProfile>> allProfiles(Account account, EconomyContext context);

    record LimitProfile(Account account, Currency currency, LimitAction action, BigDecimal maxAmount, Duration period, BigDecimal currentUsage, Instant periodStart, boolean isCustomLimit) {}

    enum LimitAction {
        DEPOSIT,
        WITHDRAW,
        TRANSFER_OUT,
        TRANSFER_IN
    }
}
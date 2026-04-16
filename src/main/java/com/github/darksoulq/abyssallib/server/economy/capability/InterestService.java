package com.github.darksoulq.abyssallib.server.economy.capability;

import com.github.darksoulq.abyssallib.server.economy.Account;
import com.github.darksoulq.abyssallib.server.economy.Currency;
import com.github.darksoulq.abyssallib.server.economy.EconomyContext;
import com.github.darksoulq.abyssallib.server.economy.TransactionResult;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface InterestService {
    CompletableFuture<Void> setAccountRate(Account account, Currency currency, BigDecimal rate, Duration interval, EconomyContext context);
    CompletableFuture<Void> setGlobalRate(EconomyContext context, Currency currency, BigDecimal rate, Duration interval);
    CompletableFuture<Void> clearAccountRate(Account account, Currency currency, EconomyContext context);
    CompletableFuture<BigDecimal> calculateAccrued(Account account, Currency currency, EconomyContext context);
    CompletableFuture<TransactionResult> apply(Account account, Currency currency, EconomyContext context);
    CompletableFuture<Optional<InterestProfile>> profile(Account account, Currency currency, EconomyContext context);
    
    record InterestProfile(Account account, Currency currency, BigDecimal rate, Duration interval, Instant lastApplied, boolean isCustomRate) {}
}
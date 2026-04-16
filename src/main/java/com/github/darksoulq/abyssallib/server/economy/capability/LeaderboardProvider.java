package com.github.darksoulq.abyssallib.server.economy.capability;

import com.github.darksoulq.abyssallib.server.economy.Account;
import com.github.darksoulq.abyssallib.server.economy.AccountType;
import com.github.darksoulq.abyssallib.server.economy.Currency;
import com.github.darksoulq.abyssallib.server.economy.EconomyContext;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface LeaderboardProvider {
    CompletableFuture<Collection<LeaderboardEntry>> top(EconomyContext context, Currency currency, int limit, int offset);
    CompletableFuture<Collection<LeaderboardEntry>> top(EconomyContext context, Currency currency, AccountType filterType, int limit, int offset);
    CompletableFuture<Collection<LeaderboardEntry>> around(Account account, EconomyContext context, Currency currency, int limit);
    CompletableFuture<Optional<LeaderboardEntry>> entry(Account account, EconomyContext context, Currency currency);
    CompletableFuture<Long> rank(Account account, EconomyContext context, Currency currency);

    record LeaderboardEntry(Account account, int rank, BigDecimal balance) {}
}
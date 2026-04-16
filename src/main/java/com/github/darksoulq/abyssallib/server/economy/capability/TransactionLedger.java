package com.github.darksoulq.abyssallib.server.economy.capability;

import com.github.darksoulq.abyssallib.server.economy.Account;
import com.github.darksoulq.abyssallib.server.economy.Currency;
import com.github.darksoulq.abyssallib.server.economy.EconomyContext;
import com.github.darksoulq.abyssallib.server.economy.TransactionResult;
import com.github.darksoulq.abyssallib.server.economy.TransactionStatus;
import net.kyori.adventure.text.Component;
import java.time.Instant;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface TransactionLedger {
    CompletableFuture<Collection<TransactionResult>> history(Account account, int limit, int offset);
    CompletableFuture<Collection<TransactionResult>> history(Account account, EconomyContext context, Currency currency, int limit, int offset);
    CompletableFuture<Collection<TransactionResult>> historyBetween(Account account, Instant start, Instant end);
    CompletableFuture<Collection<TransactionResult>> filter(Account account, EconomyContext context, Currency currency, TransactionStatus status, Instant start, Instant end, int limit, int offset);
    CompletableFuture<Collection<TransactionResult>> historyGlobal(EconomyContext context, int limit, int offset);
    CompletableFuture<TransactionResult> revert(UUID transactionId, Component reason);
}
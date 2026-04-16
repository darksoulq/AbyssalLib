package com.github.darksoulq.abyssallib.server.economy.capability;

import com.github.darksoulq.abyssallib.server.economy.Account;
import com.github.darksoulq.abyssallib.server.economy.Currency;
import com.github.darksoulq.abyssallib.server.economy.EconomyContext;
import com.github.darksoulq.abyssallib.server.economy.TransactionResult;
import net.kyori.adventure.text.Component;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface BatchOperations {
    CompletableFuture<BatchResult> executeAtomic(Collection<TransactionRequest> requests);
    CompletableFuture<BatchResult> executeNonAtomic(Collection<TransactionRequest> requests);
    CompletableFuture<BatchSimulation> simulate(Collection<TransactionRequest> requests);

    record TransactionRequest(Account source, Account target, EconomyContext context, Currency currency, BigDecimal amount, Action action, Component reason) {}
    record BatchResult(boolean completelySuccessful, List<TransactionResult> results, int successfulCount, int failedCount) {}
    record BatchSimulation(boolean completelySuccessful, List<TransactionResult> predictedResults, BigDecimal totalVolume) {}

    enum Action {
        DEPOSIT,
        WITHDRAW,
        SET,
        TRANSFER
    }
}
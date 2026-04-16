package com.github.darksoulq.abyssallib.server.economy.capability;

import com.github.darksoulq.abyssallib.server.economy.Account;
import com.github.darksoulq.abyssallib.server.economy.Currency;
import com.github.darksoulq.abyssallib.server.economy.EconomyContext;
import com.github.darksoulq.abyssallib.server.economy.TransactionResult;
import net.kyori.adventure.text.Component;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface EscrowService {
    CompletableFuture<Escrow> create(Account source, Currency currency, BigDecimal amount, EconomyContext context, Component reason);
    CompletableFuture<TransactionResult> release(UUID escrowId, Account target, Component reason);
    CompletableFuture<TransactionResult> releasePartial(UUID escrowId, Account target, BigDecimal amount, Component reason);
    CompletableFuture<TransactionResult> refund(UUID escrowId, Component reason);
    CompletableFuture<TransactionResult> dispute(UUID escrowId, Component reason);
    CompletableFuture<TransactionResult> resolveDispute(UUID escrowId, Account winner, Component reason);
    CompletableFuture<Optional<Escrow>> fetch(UUID escrowId);

    record Escrow(UUID id, Account source, Currency currency, BigDecimal amount, EconomyContext context, Instant createdAt, Instant timeout, EscrowState state) {}

    enum EscrowState {
        HELD,
        RELEASED,
        REFUNDED,
        DISPUTED
    }
}
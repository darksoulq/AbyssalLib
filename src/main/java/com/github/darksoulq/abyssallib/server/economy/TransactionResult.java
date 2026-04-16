package com.github.darksoulq.abyssallib.server.economy;

import net.kyori.adventure.text.Component;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionResult(UUID id, Instant timestamp, TransactionStatus status, EconomyContext context, Account source, Account target, Currency currency, BigDecimal amount, BigDecimal newBalance, Component reason, Component error) {
    public boolean isSuccess() {
        return status == TransactionStatus.SUCCESS;
    }

    public static TransactionResult success(EconomyContext context, Account source, Account target, Currency currency, BigDecimal amount, BigDecimal newBalance, Component reason) {
        return new TransactionResult(UUID.randomUUID(), Instant.now(), TransactionStatus.SUCCESS, context, source, target, currency, amount, newBalance, reason, null);
    }

    public static TransactionResult failure(TransactionStatus status, EconomyContext context, Account source, Account target, Currency currency, Component error) {
        return new TransactionResult(UUID.randomUUID(), Instant.now(), status, context, source, target, currency, BigDecimal.ZERO, BigDecimal.ZERO, null, error);
    }
}
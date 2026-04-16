package com.github.darksoulq.abyssallib.server.economy;

import net.kyori.adventure.text.Component;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

public final class TransactionBuilder {
    private Account source;
    private Account target;
    private Currency currency;
    private BigDecimal amount;
    private EconomyContext context = EconomyContext.GLOBAL;
    private Component reason;

    private TransactionBuilder() {}

    public static TransactionBuilder create() {
        return new TransactionBuilder();
    }

    public TransactionBuilder source(Account source) {
        this.source = source;
        return this;
    }

    public TransactionBuilder target(Account target) {
        this.target = target;
        return this;
    }

    public TransactionBuilder currency(Currency currency) {
        this.currency = currency;
        return this;
    }

    public TransactionBuilder amount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public TransactionBuilder amount(double amount) {
        this.amount = BigDecimal.valueOf(amount);
        return this;
    }

    public TransactionBuilder context(EconomyContext context) {
        this.context = context;
        return this;
    }

    public TransactionBuilder reason(Component reason) {
        this.reason = reason;
        return this;
    }

    public CompletableFuture<TransactionResult> execute() {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            return CompletableFuture.completedFuture(TransactionResult.failure(TransactionStatus.INVALID_AMOUNT, context, source, target, currency, Component.text("Amount must be non-negative")));
        }
        if (currency == null) {
            return CompletableFuture.completedFuture(TransactionResult.failure(TransactionStatus.ERROR, context, source, target, null, Component.text("Currency not specified")));
        }
        if (source != null && target != null) {
            return source.transfer(context, target, currency, amount, reason);
        } else if (source != null) {
            return source.withdraw(context, currency, amount, reason);
        } else if (target != null) {
            return target.deposit(context, currency, amount, reason);
        }
        return CompletableFuture.completedFuture(TransactionResult.failure(TransactionStatus.ERROR, context, null, null, currency, Component.text("No source or target specified")));
    }
}
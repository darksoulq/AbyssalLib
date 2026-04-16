package com.github.darksoulq.abyssallib.server.economy;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Account {
    Key id();

    AccountType type();

    UUID owner();

    CompletableFuture<AccountState> state();

    CompletableFuture<Boolean> setState(AccountState state, Component reason);

    CompletableFuture<Set<Currency>> currencies(EconomyContext context);

    CompletableFuture<BigDecimal> balance(EconomyContext context, Currency currency);

    CompletableFuture<Boolean> has(EconomyContext context, Currency currency, BigDecimal amount);

    CompletableFuture<Boolean> hasCapacity(EconomyContext context, Currency currency, BigDecimal amount);

    CompletableFuture<TransactionResult> set(EconomyContext context, Currency currency, BigDecimal amount, Component reason);

    CompletableFuture<TransactionResult> deposit(EconomyContext context, Currency currency, BigDecimal amount, Component reason);

    CompletableFuture<TransactionResult> withdraw(EconomyContext context, Currency currency, BigDecimal amount, Component reason);

    CompletableFuture<TransactionResult> transfer(EconomyContext context, Account to, Currency currency, BigDecimal amount, Component reason);

    CompletableFuture<Map<Currency, BigDecimal>> balances(EconomyContext context);
}
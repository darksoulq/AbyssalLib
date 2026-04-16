package com.github.darksoulq.abyssallib.server.economy.capability;

import com.github.darksoulq.abyssallib.server.economy.Account;
import com.github.darksoulq.abyssallib.server.economy.Currency;
import com.github.darksoulq.abyssallib.server.economy.EconomyContext;
import com.github.darksoulq.abyssallib.server.economy.TransactionResult;
import net.kyori.adventure.text.Component;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

public interface CurrencyExchange {
    CompletableFuture<BigDecimal> rate(EconomyContext context, Currency from, Currency to);
    CompletableFuture<TransactionResult> exchange(EconomyContext context, Account account, Currency from, Currency to, BigDecimal amount, Component reason);
    CompletableFuture<BigDecimal> liquidity(EconomyContext context, Currency currency);
    CompletableFuture<TransactionResult> provideLiquidity(EconomyContext context, Account provider, Currency currency, BigDecimal amount, Component reason);
    CompletableFuture<TransactionResult> withdrawLiquidity(EconomyContext context, Account provider, Currency currency, BigDecimal amount, Component reason);
    CompletableFuture<Void> setFixedRate(EconomyContext context, Currency from, Currency to, BigDecimal rate);
    CompletableFuture<Void> clearFixedRate(EconomyContext context, Currency from, Currency to);
}
package com.github.darksoulq.abyssallib.server.economy;

import net.kyori.adventure.text.Component;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

public interface CurrencyExchange {
    CompletableFuture<BigDecimal> rate(Currency from, Currency to);
    
    CompletableFuture<TransactionResult> exchange(Account account, Currency from, Currency to, BigDecimal amount, Component reason);
}
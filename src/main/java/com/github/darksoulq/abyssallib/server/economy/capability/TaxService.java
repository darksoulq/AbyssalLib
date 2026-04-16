package com.github.darksoulq.abyssallib.server.economy.capability;

import com.github.darksoulq.abyssallib.server.economy.Account;
import com.github.darksoulq.abyssallib.server.economy.Currency;
import com.github.darksoulq.abyssallib.server.economy.EconomyContext;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface TaxService {
    CompletableFuture<BigDecimal> calculate(Account source, Account target, Currency currency, BigDecimal amount, EconomyContext context);
    CompletableFuture<TaxReceipt> evaluateAndDeduct(Account source, Account target, Currency currency, BigDecimal amount, EconomyContext context);
    CompletableFuture<Void> setFlatRate(EconomyContext context, Currency currency, BigDecimal rateMultiplier);
    CompletableFuture<Void> setBracket(EconomyContext context, Currency currency, BigDecimal minAmount, BigDecimal maxAmount, BigDecimal rate);
    CompletableFuture<Void> removeBracket(EconomyContext context, Currency currency, BigDecimal minAmount, BigDecimal maxAmount);
    CompletableFuture<Collection<TaxBracket>> brackets(EconomyContext context, Currency currency);
    CompletableFuture<Void> setExemption(EconomyContext context, Account account, Currency currency, boolean exempt);
    CompletableFuture<Boolean> isExempt(EconomyContext context, Account account, Currency currency);

    record TaxReceipt(BigDecimal originalAmount, BigDecimal taxAmount, BigDecimal finalAmount, Account taxPool) {}
    record TaxBracket(BigDecimal minAmount, BigDecimal maxAmount, BigDecimal rate) {}
}
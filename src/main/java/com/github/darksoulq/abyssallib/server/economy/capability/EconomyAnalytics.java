package com.github.darksoulq.abyssallib.server.economy.capability;

import com.github.darksoulq.abyssallib.server.economy.Currency;
import com.github.darksoulq.abyssallib.server.economy.EconomyContext;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface EconomyAnalytics {
    CompletableFuture<CirculationSnapshot> generateSnapshot(EconomyContext context, Currency currency);
    CompletableFuture<BigDecimal> calculateVelocity(EconomyContext context, Currency currency, Instant start, Instant end);
    CompletableFuture<BigDecimal> giniCoefficient(EconomyContext context, Currency currency);
    CompletableFuture<Map<Integer, BigDecimal>> wealthPercentiles(EconomyContext context, Currency currency, int[] percentiles);
    CompletableFuture<Void> registerFaucet(EconomyContext context, Currency currency, BigDecimal amount);
    CompletableFuture<Void> registerSink(EconomyContext context, Currency currency, BigDecimal amount);

    record CirculationSnapshot(Instant timestamp, Currency currency, BigDecimal totalCirculating, BigDecimal activeAccounts, BigDecimal totalFaucetInflow, BigDecimal totalSinkOutflow, BigDecimal inflationRate) {}
}
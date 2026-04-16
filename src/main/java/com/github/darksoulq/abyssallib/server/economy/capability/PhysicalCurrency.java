package com.github.darksoulq.abyssallib.server.economy.capability;

import com.github.darksoulq.abyssallib.server.economy.Account;
import com.github.darksoulq.abyssallib.server.economy.Currency;
import com.github.darksoulq.abyssallib.server.economy.EconomyContext;
import com.github.darksoulq.abyssallib.server.economy.TransactionResult;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface PhysicalCurrency {
    CompletableFuture<Boolean> isPhysical(Currency currency);
    CompletableFuture<Collection<ItemStack>> asPhysical(Currency currency, BigDecimal amount);
    CompletableFuture<Collection<ItemStack>> makeChange(Currency currency, BigDecimal amount);
    CompletableFuture<BigDecimal> evaluate(Currency currency, Collection<ItemStack> items);
    CompletableFuture<TransactionResult> withdrawPhysical(Account account, Currency currency, BigDecimal amount, EconomyContext context, Component reason);
    CompletableFuture<PhysicalResult> depositPhysical(Account account, Currency currency, Collection<ItemStack> items, EconomyContext context, Component reason);

    record PhysicalResult(TransactionResult transaction, Collection<ItemStack> unacceptedItems) {}
}
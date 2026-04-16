package com.github.darksoulq.abyssallib.server.economy.capability;

import com.github.darksoulq.abyssallib.server.economy.Account;
import com.github.darksoulq.abyssallib.server.economy.Currency;
import com.github.darksoulq.abyssallib.server.economy.EconomyContext;
import com.github.darksoulq.abyssallib.server.economy.TransactionResult;
import net.kyori.adventure.text.Component;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface InvoiceService {
    CompletableFuture<Invoice> create(Account sender, Account recipient, Currency currency, BigDecimal amount, Instant dueDate, EconomyContext context, Component description);
    CompletableFuture<TransactionResult> pay(UUID invoiceId, Component reason);
    CompletableFuture<TransactionResult> payPartial(UUID invoiceId, BigDecimal amount, Component reason);
    CompletableFuture<Boolean> cancel(UUID invoiceId, Component reason);
    CompletableFuture<Optional<Invoice>> fetch(UUID invoiceId);
    CompletableFuture<Collection<Invoice>> fetchSent(Account sender);
    CompletableFuture<Collection<Invoice>> fetchReceived(Account recipient);
    CompletableFuture<Collection<Invoice>> fetchOverdue();

    record Invoice(UUID id, Account sender, Account recipient, Currency currency, BigDecimal amount, BigDecimal amountPaid, Instant createdAt, Instant dueDate, EconomyContext context, Component description, InvoiceState state) {}

    enum InvoiceState {
        PENDING,
        PARTIALLY_PAID,
        PAID,
        CANCELLED,
        OVERDUE,
        FORGIVEN
    }
}
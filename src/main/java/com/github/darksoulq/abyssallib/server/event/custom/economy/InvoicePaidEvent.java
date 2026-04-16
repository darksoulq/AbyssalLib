package com.github.darksoulq.abyssallib.server.event.custom.economy;

import com.github.darksoulq.abyssallib.server.economy.TransactionResult;
import com.github.darksoulq.abyssallib.server.economy.capability.InvoiceService.Invoice;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class InvoicePaidEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Invoice invoice;
    private final TransactionResult transaction;

    public InvoicePaidEvent(Invoice invoice, TransactionResult transaction, boolean isAsync) {
        super(isAsync);
        this.invoice = invoice;
        this.transaction = transaction;
    }

    public Invoice getInvoice() { return invoice; }
    public TransactionResult getTransaction() { return transaction; }
    @Override public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
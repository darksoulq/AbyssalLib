package com.github.darksoulq.abyssallib.server.event.custom.economy;

import com.github.darksoulq.abyssallib.server.economy.capability.InvoiceService.Invoice;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class InvoiceCreatedEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Invoice invoice;
    private boolean cancelled;

    public InvoiceCreatedEvent(Invoice invoice, boolean isAsync) {
        super(isAsync);
        this.invoice = invoice;
    }

    public Invoice getInvoice() { return invoice; }
    @Override public boolean isCancelled() { return cancelled; }
    @Override public void setCancelled(boolean cancel) { this.cancelled = cancel; }
    @Override public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
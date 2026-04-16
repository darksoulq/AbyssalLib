package com.github.darksoulq.abyssallib.server.event.custom.economy;

import com.github.darksoulq.abyssallib.server.economy.capability.InvoiceService.Invoice;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class InvoiceCancelledEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Invoice invoice;
    private final Component reason;

    public InvoiceCancelledEvent(Invoice invoice, Component reason, boolean isAsync) {
        super(isAsync);
        this.invoice = invoice;
        this.reason = reason;
    }

    public Invoice getInvoice() { return invoice; }
    public Component getReason() { return reason; }
    @Override public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
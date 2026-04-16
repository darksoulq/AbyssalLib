package com.github.darksoulq.abyssallib.server.event.custom.economy;

import com.github.darksoulq.abyssallib.server.economy.Account;
import com.github.darksoulq.abyssallib.server.economy.Currency;
import com.github.darksoulq.abyssallib.server.economy.EconomyContext;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import java.math.BigDecimal;

public class PreTransactionEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final EconomyContext context;
    private final Account source;
    private final Account target;
    private final Currency currency;
    private BigDecimal amount;
    private Component reason;
    private boolean cancelled;

    public PreTransactionEvent(EconomyContext context, Account source, Account target, Currency currency, BigDecimal amount, Component reason, boolean isAsync) {
        super(isAsync);
        this.context = context;
        this.source = source;
        this.target = target;
        this.currency = currency;
        this.amount = amount;
        this.reason = reason;
    }

    public EconomyContext getContext() { return context; }
    public Account getSource() { return source; }
    public Account getTarget() { return target; }
    public Currency getCurrency() { return currency; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public Component getReason() { return reason; }
    public void setReason(Component reason) { this.reason = reason; }
    @Override public boolean isCancelled() { return cancelled; }
    @Override public void setCancelled(boolean cancel) { this.cancelled = cancel; }
    @Override public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
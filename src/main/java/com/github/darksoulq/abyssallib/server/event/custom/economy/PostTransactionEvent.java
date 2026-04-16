package com.github.darksoulq.abyssallib.server.event.custom.economy;

import com.github.darksoulq.abyssallib.server.economy.TransactionResult;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PostTransactionEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final TransactionResult result;

    public PostTransactionEvent(TransactionResult result, boolean isAsync) {
        super(isAsync);
        this.result = result;
    }

    public TransactionResult getResult() { return result; }
    @Override public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
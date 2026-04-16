package com.github.darksoulq.abyssallib.server.event.custom.economy;

import com.github.darksoulq.abyssallib.server.economy.Account;
import com.github.darksoulq.abyssallib.server.economy.AccountState;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AccountStateChangeEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Account account;
    private final AccountState oldState;
    private AccountState newState;
    private final Component reason;
    private boolean cancelled;

    public AccountStateChangeEvent(Account account, AccountState oldState, AccountState newState, Component reason, boolean isAsync) {
        super(isAsync);
        this.account = account;
        this.oldState = oldState;
        this.newState = newState;
        this.reason = reason;
    }

    public Account getAccount() { return account; }
    public AccountState getOldState() { return oldState; }
    public AccountState getNewState() { return newState; }
    public void setNewState(AccountState newState) { this.newState = newState; }
    public Component getReason() { return reason; }
    @Override public boolean isCancelled() { return cancelled; }
    @Override public void setCancelled(boolean cancel) { this.cancelled = cancel; }
    @Override public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
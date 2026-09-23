package com.github.darksoulq.abyssallib.server.event.custom.entity;

import com.github.darksoulq.abyssallib.world.entity.player.FakePlayer;
import com.github.darksoulq.abyssallib.world.entity.player.OfflinePlayerData;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class FakePlayerReplaceEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final FakePlayer fakePlayer;
    private final UUID joinedPlayerId;

    public FakePlayerReplaceEvent(@NotNull FakePlayer fakePlayer, @NotNull UUID joinedPlayerId) {
        this.fakePlayer = fakePlayer;
        this.joinedPlayerId = joinedPlayerId;
    }

    public @NotNull FakePlayer getFakePlayer() {
        return fakePlayer;
    }

    public @NotNull OfflinePlayerData getOfflineData() {
        return fakePlayer.getOfflineData();
    }

    public @NotNull UUID getJoinedPlayerId() {
        return joinedPlayerId;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
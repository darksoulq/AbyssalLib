package com.github.darksoulq.abyssallib.server.event.internal;

import com.github.darksoulq.abyssallib.server.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.server.scoreboard.internal.PlayerSidebarManager;
import org.bukkit.event.player.PlayerQuitEvent;

public class ScoreboardEvent {

    @SubscribeEvent
    public void onQuit(PlayerQuitEvent e) {
        PlayerSidebarManager.remove(e.getPlayer());
    }
}
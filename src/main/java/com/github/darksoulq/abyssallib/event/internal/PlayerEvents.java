package com.github.darksoulq.abyssallib.event.internal;

import com.github.darksoulq.abyssallib.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.resource.glyph.Glyph;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEvents {
    @SubscribeEvent
    public void onChat(AsyncChatEvent e) {
        Component result = e.message();
        for (String placeholder : Glyph.getChatMap().keySet()) {
            result = e.message().replaceText(TextReplacementConfig.builder()
                    .matchLiteral(placeholder)
                    .replacement(Glyph.getChatMap().get(placeholder).toString())
                    .build());
        }

        e.message(result);
    }

    @SubscribeEvent
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().addCustomChatCompletions(Glyph.getChatMap().keySet().stream().sorted().toList());
    }
}

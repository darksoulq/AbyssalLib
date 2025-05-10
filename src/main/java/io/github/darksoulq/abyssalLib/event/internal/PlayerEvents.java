package io.github.darksoulq.abyssalLib.event.internal;

import io.github.darksoulq.abyssalLib.event.SubscribeEvent;
import io.github.darksoulq.abyssalLib.resource.glyph.Glyph;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.event.player.PlayerJoinEvent;

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

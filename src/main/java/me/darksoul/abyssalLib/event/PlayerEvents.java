package me.darksoul.abyssalLib.event;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.darksoul.abyssalLib.resource.glyph.GlyphManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;

public class PlayerEvents {

    @SubscribeEvent
    public void onChat(AsyncChatEvent e) {
        Component result = e.message();
        for (String placeholder : GlyphManager.getChatMap().keySet()) {
            result = e.message().replaceText(TextReplacementConfig.builder()
                    .matchLiteral(placeholder)
                    .replacement(GlyphManager.getChatMap().get(placeholder).toString())
                    .build());
        }

        e.message(result);
    }
}

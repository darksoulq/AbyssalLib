package com.github.darksoulq.abyssallib.server.resource.glyph;

import com.github.darksoulq.abyssallib.world.level.data.Identifier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;

public class GuiTexture extends Glyph{
    private int offset;

    public GuiTexture(JavaPlugin plugin, Identifier id, int height, int ascent, int offset) {
        super(plugin, id, height, ascent, false);
        this.offset = offset;
    }

    public Component getTitle() {
        return Component.translatable("space." + offset)
                .append(MiniMessage.miniMessage().deserialize("<white>" + unicode() + "</white>"));
    }
}

package com.github.darksoulq.abyssallib.resource.glyph;

import com.github.darksoulq.abyssallib.util.ResourceLocation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;

public class GuiTexture extends Glyph{
    private int offset;

    public GuiTexture(JavaPlugin plugin, ResourceLocation id, int height, int ascent, int offset) {
        super(plugin, id, height, ascent, false);
        this.offset = offset;
    }

    public Component getTitle() {
        return Component.translatable("space." + offset)
                .append(MiniMessage.miniMessage().deserialize("<white>" + unicode() + "</white>"));
    }
}

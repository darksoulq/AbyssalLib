package com.github.darksoulq.abyssallib.server.translation;

import com.github.darksoulq.abyssallib.server.resource.asset.Font;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

/**
 * Provides integration between the translation system and named font glyphs.
 * <p>
 * This service allows the use of registered font glyphs within MiniMessage tags
 * via the {@code <glyph:namespace:name>} syntax.
 * </p>
 */
public final class GlyphService {

    /**
     * Creates a {@link TagResolver} that handles the {@code glyph} tag.
     * <p>
     * Usage in MiniMessage: {@code <glyph:abyssallib:mouse_left>}
     * </p>
     *
     * @return A {@link TagResolver} handling dynamic glyph resolution.
     */
    public static TagResolver resolve() {
        return TagResolver.resolver("glyph", (queue, ctx) -> {
            String namespace = queue.popOr("glyph tag requires a namespace argument").value();
            String name = queue.popOr("glyph tag requires a name argument").value();
            Component glyph = Font.getGlyph(namespace, name);
            return Tag.selfClosingInserting(glyph != null ? glyph : Component.empty());
        });
    }
}
package com.github.darksoulq.abyssallib.server.placeholder;

import com.github.darksoulq.abyssallib.server.registry.Registries;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;

public final class CustomPlaceholderResolver {

    private static final String[] EMPTY_ARGS = new String[0];

    public static TagResolver resolve() {
        return resolve(null);
    }

    public static TagResolver resolve(Player player) {
        return TagResolver.resolver("placeholder", (queue, ctx) -> {
            if (!queue.hasNext()) {
                return Tag.selfClosingInserting(Component.empty());
            }

            String namespace = queue.pop().value();
            if (!queue.hasNext()) {
                return Tag.selfClosingInserting(Component.text("<placeholder:" + namespace + ">"));
            }
            
            String value = queue.pop().value();
            Key key = Key.key(namespace, value);

            Placeholder<?> placeholder = Registries.PLACEHOLDERS.get(key.asString());
            if (placeholder == null) {
                return Tag.selfClosingInserting(Component.text("<placeholder:" + key.asString() + ">"));
            }

            String[] args = EMPTY_ARGS;
            if (queue.hasNext()) {
                int count = 0;
                String[] buffer = new String[4];
                while (queue.hasNext()) {
                    if (count == buffer.length) {
                        String[] newBuffer = new String[buffer.length * 2];
                        System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
                        buffer = newBuffer;
                    }
                    buffer[count++] = queue.pop().value();
                }
                args = new String[count];
                System.arraycopy(buffer, 0, args, 0, count);
            }

            PlaceholderContext context = new PlaceholderContext(player, args);
            return process(placeholder, context);
        });
    }

    @SuppressWarnings("unchecked")
    private static <T> Tag process(Placeholder<T> placeholder, PlaceholderContext context) {
        PlaceholderResult<T> result = placeholder.resolve(context);

        if (result.isEmpty()) return Tag.selfClosingInserting(Component.empty());
        if (result.isError()) return Tag.selfClosingInserting(result.getError());

        return Tag.selfClosingInserting(placeholder.format(result.getValue()));
    }
}
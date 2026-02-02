package com.github.darksoulq.abyssallib.server.command.argument;

import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class IdentifierArgument implements CustomArgumentType<Identifier, NamespacedKey> {
    private static final DynamicCommandExceptionType ERROR_INVALID = new DynamicCommandExceptionType(value ->
        MessageComponentSerializer.message().serialize(Component.text("Invalid identifier: " + value))
    );

    public static IdentifierArgument identifier() {
        return new IdentifierArgument();
    }

    @Override
    public Identifier parse(@NotNull StringReader reader) throws CommandSyntaxException {
        final int start = reader.getCursor();
        while (reader.canRead() && isAllowedCharacter(reader.peek())) {
            reader.skip();
        }
        final String string = reader.getString().substring(start, reader.getCursor());

        try {
            return Identifier.of(string);
        } catch (IllegalArgumentException e) {
            reader.setCursor(start);
            throw ERROR_INVALID.createWithContext(reader, string);
        }
    }

    @Override
    public ArgumentType<NamespacedKey> getNativeType() {
        return ArgumentTypes.namespacedKey();
    }

    private static boolean isAllowedCharacter(final char c) {
        return (c >= '0' && c <= '9')
            || (c >= 'a' && c <= 'z')
            || (c >= 'A' && c <= 'Z')
            || c == '_' || c == '-' || c == '.' || c == ':';
    }
}
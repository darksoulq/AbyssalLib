package com.github.darksoulq.abyssallib.server.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.kyori.adventure.text.Component;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class EnumArgument<E extends Enum<E>> implements CustomArgumentType<E, String> {

    private final Class<E> enumClass;
    private static final DynamicCommandExceptionType ERROR_INVALID_ENUM = new DynamicCommandExceptionType(val ->
        MessageComponentSerializer.message().serialize(Component.text("Invalid value: " + val))
    );

    private EnumArgument(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    public static <E extends Enum<E>> EnumArgument<E> enumArg(Class<E> enumClass) {
        return new EnumArgument<>(enumClass);
    }

    @Override
    public E parse(StringReader reader) throws CommandSyntaxException {
        int start = reader.getCursor();
        String value = reader.readUnquotedString();
        try {
            return Enum.valueOf(enumClass, value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            reader.setCursor(start);
            throw ERROR_INVALID_ENUM.createWithContext(reader, value);
        }
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String remaining = builder.getRemainingLowerCase();
        for (E e : enumClass.getEnumConstants()) {
            String name = e.name().toLowerCase(Locale.ROOT);
            if (name.startsWith(remaining)) {
                builder.suggest(name);
            }
        }
        return builder.buildFuture();
    }
}
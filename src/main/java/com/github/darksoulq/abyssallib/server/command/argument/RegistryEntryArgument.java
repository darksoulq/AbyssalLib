package com.github.darksoulq.abyssallib.server.command.argument;

import com.github.darksoulq.abyssallib.server.registry.Registry;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

@NullMarked
public class RegistryEntryArgument<T> implements CustomArgumentType<T, NamespacedKey> {

    private static final DynamicCommandExceptionType ERROR_UNKNOWN_ENTRY = new DynamicCommandExceptionType(id ->
        MessageComponentSerializer.message().serialize(Component.text("Unknown registry entry: " + id))
    );

    private final Registry<T> registry;

    public RegistryEntryArgument(Registry<T> registry) {
        this.registry = registry;
    }

    public static <T> RegistryEntryArgument<T> registryEntry(Registry<T> registry) {
        return new RegistryEntryArgument<>(registry);
    }

    @Override
    public T parse(@NotNull StringReader reader) throws CommandSyntaxException {
        final int start = reader.getCursor();
        while (reader.canRead() && isAllowedCharacter(reader.peek())) {
            reader.skip();
        }
        final String id = reader.getString().substring(start, reader.getCursor());

        T value = registry.get(id);
        if (value == null) {
            reader.setCursor(start);
            throw ERROR_UNKNOWN_ENTRY.createWithContext(reader, id);
        }
        return value;
    }

    @Override
    public ArgumentType<NamespacedKey> getNativeType() {
        return ArgumentTypes.namespacedKey();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String remaining = builder.getRemainingLowerCase();
        for (String id : registry.getAll().keySet()) {
            if (id.toLowerCase(Locale.ROOT).startsWith(remaining)) {
                builder.suggest(id);
            }
        }
        return builder.buildFuture();
    }

    private static boolean isAllowedCharacter(final char c) {
        return (c >= '0' && c <= '9')
            || (c >= 'a' && c <= 'z')
            || (c >= 'A' && c <= 'Z')
            || c == '_' || c == '-' || c == '.' || c == ':';
    }
}
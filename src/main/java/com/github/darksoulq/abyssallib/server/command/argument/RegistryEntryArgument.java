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
    public T parse(StringReader reader) throws CommandSyntaxException {
        final int start = reader.getCursor();
        while (reader.canRead() && isAllowedCharacter(reader.peek())) {
            reader.skip();
        }
        final String id = reader.getString().substring(start, reader.getCursor());

        return registry.get(id);
    }

    @Override
    public ArgumentType<NamespacedKey> getNativeType() {
        return ArgumentTypes.namespacedKey();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String remaining = builder.getRemainingLowerCase();
        for (String id : registry.getAll().keySet()) {
            if (matchesSubStr(remaining, id.toLowerCase(Locale.ROOT))) {
                builder.suggest(id);
            }
        }
        return builder.buildFuture();
    }

    private boolean matchesSubStr(String remaining, String candidate) {
        if (candidate.startsWith(remaining)) {
            return true;
        }

        int colonIndex = candidate.indexOf(':');
        if (colonIndex >= 0 && candidate.startsWith(remaining, colonIndex + 1)) {
            return true;
        }

        int underscoreIndex = candidate.indexOf('_');
        while (underscoreIndex >= 0) {
            if (candidate.startsWith(remaining, underscoreIndex + 1)) {
                return true;
            }
            underscoreIndex = candidate.indexOf('_', underscoreIndex + 1);
        }

        return false;
    }

    private static boolean isAllowedCharacter(final char c) {
        return (c >= '0' && c <= '9')
            || (c >= 'a' && c <= 'z')
            || (c >= 'A' && c <= 'Z')
            || c == '_' || c == '-' || c == '.' || c == ':';
    }
}
package com.github.darksoulq.abyssallib.server.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;

import java.util.concurrent.CompletableFuture;

public class NbtCompoundArgument implements CustomArgumentType<CompoundTag, String> {

    public static NbtCompoundArgument nbt() {
        return new NbtCompoundArgument();
    }

    @Override
    public CompoundTag parse(StringReader reader) throws CommandSyntaxException {
        return TagParser.parseCompoundAsArgument(reader);
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.greedyString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return Suggestions.empty();
    }
}
package com.github.darksoulq.abyssallib.server.command.internal;

import com.github.darksoulq.abyssallib.common.util.Try;
import com.github.darksoulq.abyssallib.server.command.BaseCommand;
import com.github.darksoulq.abyssallib.server.command.CommandResult;
import com.github.darksoulq.abyssallib.server.command.DefaultConditions;
import com.github.darksoulq.abyssallib.server.permission.internal.PluginPermissions;
import com.github.darksoulq.abyssallib.world.data.attribute.EntityAttributes;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.EntitySelectorArgumentResolver;
import net.kyori.adventure.key.Key;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class AttributeCommand extends BaseCommand {

    public AttributeCommand() {
        super("attribute");
        setRequirement(DefaultConditions.hasPerm(PluginPermissions.ATTRIBUTES_GET));

        LiteralArgumentBuilder<CommandSourceStack> get = Commands.literal("get");
        RequiredArgumentBuilder<CommandSourceStack, EntitySelectorArgumentResolver> selector = Commands.argument("selector", ArgumentTypes.entity());
        RequiredArgumentBuilder<CommandSourceStack, Key> type = Commands.argument("type", ArgumentTypes.key()).suggests(AttributeCommand::attributeTypeSuggests);

        addSyntax(AttributeCommand::attributeGetExecutor, get, selector, type);
    }

    private static CommandResult attributeGetExecutor(CommandContext<CommandSourceStack> ctx) {
        Key key = ctx.getArgument("type", Key.class);
        EntitySelectorArgumentResolver selector = ctx.getArgument("selector", EntitySelectorArgumentResolver.class);

        Try.of(() -> selector.resolve(ctx.getSource()))
            .onSuccess(entities -> {
                if (!entities.isEmpty()) {
                    EntityAttributes data = EntityAttributes.of(entities.getFirst());
                    data.load();
                    Map<String, String> attribMap = data.getAllAttributes();
                    if (attribMap.containsKey(key.toString())) {
                        CommandUtil.reply(ctx, key + " base value is " + attribMap.get(key.toString()));
                    } else {
                        CommandUtil.reply(ctx, "<red>Entity does not have this attribute</red>");
                    }
                } else {
                    CommandUtil.reply(ctx, "<red>No entity was found</red>");
                }
            })
            .orElseThrow(RuntimeException::new);
        return CommandResult.success();
    }

    private static CompletableFuture<Suggestions> attributeTypeSuggests(final CommandContext<CommandSourceStack> ctx, final SuggestionsBuilder builder) {
        EntitySelectorArgumentResolver selector = ctx.getArgument("selector", EntitySelectorArgumentResolver.class);

        return Try.of(() -> selector.resolve(ctx.getSource()))
            .map(entities -> {
                if (!entities.isEmpty()) {
                    EntityAttributes data = EntityAttributes.of(entities.getFirst());
                    data.load();
                    data.getAllAttributes().keySet().forEach(builder::suggest);
                }
                return builder.buildFuture();
            })
            .orElseThrow(RuntimeException::new);
    }
}
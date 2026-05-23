package com.github.darksoulq.abyssallib.server.command.internal;

import com.github.darksoulq.abyssallib.server.command.BaseCommand;
import com.github.darksoulq.abyssallib.server.command.CommandResult;
import com.github.darksoulq.abyssallib.server.command.DefaultConditions;
import com.github.darksoulq.abyssallib.server.permission.internal.PluginPermissions;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.gui.internal.ItemMenu;
import com.github.darksoulq.abyssallib.world.item.ItemCategory;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

public class ContentCommand extends BaseCommand {

    public ContentCommand() {
        super("content");
        setRequirement(DefaultConditions.hasPerm(PluginPermissions.CONTENT_ITEMS_VIEW));

        LiteralArgumentBuilder<CommandSourceStack> items = Commands.literal("items");
        RequiredArgumentBuilder<CommandSourceStack, String> plugin = Commands.argument("plugin", StringArgumentType.string()).suggests((ctx, builder) -> {
            Registries.ITEMS.getAll().keySet().stream().map(key -> key.split(":")[0]).distinct().sorted().forEach(builder::suggest);
            return builder.buildFuture();
        });
        RequiredArgumentBuilder<CommandSourceStack, String> category = Commands.argument("category", StringArgumentType.string())
            .suggests((ctx, builder) -> {
                String ns = ctx.getArgument("plugin", String.class);
                boolean hasCustom = false;
                for (ItemCategory cat : Registries.ITEM_CATEGORIES.getAll().values()) {
                    if (cat.getId().namespace().equals(ns)) {
                        builder.suggest(cat.getId().value());
                        hasCustom = true;
                    }
                }
                if (!hasCustom) builder.suggest("all");
                return builder.buildFuture();
            });

        addSyntax(ctx -> {
            Player player = CommandUtil.getPlayer(ctx);
            if (player == null) {
                CommandUtil.reply(ctx, "<red>Only players can execute this command.</red>");
                return CommandResult.failure();
            }
            ItemMenu.open(player);
            return CommandResult.success();
        }, items);

        addSyntax(ctx -> {
            Player player = CommandUtil.getPlayer(ctx);
            if (player == null) {
                CommandUtil.reply(ctx, "<red>Only players can execute this command.</red>");
                return CommandResult.failure();
            }
            String ns = ctx.getArgument("plugin", String.class);
            ItemMenu.openPlugin(player, ns);
            return CommandResult.success();
        }, items, plugin);

        addSyntax(ctx -> {
            Player player = CommandUtil.getPlayer(ctx);
            if (player == null) {
                CommandUtil.reply(ctx, "<red>Only players can execute this command.</red>");
                return CommandResult.failure();
            }
            String ns = ctx.getArgument("plugin", String.class);
            String catPath = ctx.getArgument("category", String.class);

            ItemCategory cat = Registries.ITEM_CATEGORIES.get(ns + ":" + catPath);
            if (cat != null) {
                ItemMenu.openCategory(player, cat);
            } else if (catPath.equals("all")) {
                ItemMenu.openPlugin(player, ns);
            } else {
                CommandUtil.reply(ctx, "<red>Category not found</red>");
                return CommandResult.failure();
            }
            return CommandResult.success();
        }, items, plugin, category);
    }
}
package com.github.darksoulq.abyssallib.server.command.internal;

import com.github.darksoulq.abyssallib.server.command.BaseCommand;
import com.github.darksoulq.abyssallib.server.command.CommandResult;
import com.github.darksoulq.abyssallib.server.command.DefaultConditions;
import com.github.darksoulq.abyssallib.server.command.argument.RegistryEntryArgument;
import com.github.darksoulq.abyssallib.server.permission.internal.PluginPermissions;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.item.Item;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.entity.Player;

import java.util.List;

public class GiveCommand extends BaseCommand {

    public GiveCommand() {
        super("give");
        setRequirement(DefaultConditions.hasPerm(PluginPermissions.ITEMS_GIVE));

        RequiredArgumentBuilder<CommandSourceStack, PlayerSelectorArgumentResolver> targets = Commands.argument("targets", ArgumentTypes.players());
        RequiredArgumentBuilder<CommandSourceStack, Item> item = Commands.argument("item", RegistryEntryArgument.registryEntry(Registries.ITEMS, i -> !i.isHidden()));
        RequiredArgumentBuilder<CommandSourceStack, Integer> amount = Commands.argument("amount", IntegerArgumentType.integer(1));

        addSyntax(ctx -> giveMultiExecutor(ctx, 1), targets, item);
        addSyntax(ctx -> giveMultiExecutor(ctx, ctx.getArgument("amount", Integer.class)), targets, item, amount);
    }

    private CommandResult giveMultiExecutor(CommandContext<CommandSourceStack> ctx, int amount) throws CommandSyntaxException {
        List<Player> targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
        Item item = ctx.getArgument("item", Item.class);

        if (item == null) {
            CommandUtil.reply(ctx, "<red>Unknown item</red>");
            return CommandResult.failure();
        }

        for (Player player : targets) {
            player.getInventory().addItem(item.getStack().asQuantity(amount));
        }

        if (targets.size() == 1) {
            CommandUtil.reply(ctx, "Gave " + amount + " <gray>[" + item.getId().asString() + "]</gray> to " + targets.getFirst().getName());
        } else {
            CommandUtil.reply(ctx, "Gave " + amount + " <gray>[" + item.getId().asString() + "]</gray> to " + targets.size() + " players");
        }

        return CommandResult.success();
    }
}
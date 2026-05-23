package com.github.darksoulq.abyssallib.server.command.internal;

import com.github.darksoulq.abyssallib.server.command.BaseCommand;
import com.github.darksoulq.abyssallib.server.command.CommandResult;
import com.github.darksoulq.abyssallib.server.command.DefaultConditions;
import com.github.darksoulq.abyssallib.server.permission.internal.PluginPermissions;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.gen.internal.StructureLocator;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.FinePositionResolver;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;

public class LocateCommand extends BaseCommand {

    public LocateCommand() {
        super("locate");
        setRequirement(DefaultConditions.hasPerm(PluginPermissions.LOCATE));

        LiteralArgumentBuilder<CommandSourceStack> structureLit = Commands.literal("structure");
        RequiredArgumentBuilder<CommandSourceStack, Key> structureArg = Commands.argument("structure", ArgumentTypes.key()).suggests((ctx, builder) -> {
            Registries.STRUCTURES.getAll().keySet().forEach(builder::suggest);
            return builder.buildFuture();
        });
        RequiredArgumentBuilder<CommandSourceStack, FinePositionResolver> locArg = Commands.argument("location", ArgumentTypes.finePosition(false));

        addSyntax(ctx -> locateExecutor(ctx, null), structureLit, structureArg);
        addSyntax(ctx -> locateExecutor(ctx, ctx.getArgument("location", FinePositionResolver.class)), structureLit, structureArg, locArg);
    }

    private CommandResult locateExecutor(CommandContext<CommandSourceStack> ctx, FinePositionResolver posResolver) throws CommandSyntaxException {
        Location sourceLoc;
        if (posResolver != null) {
            sourceLoc = posResolver.resolve(ctx.getSource()).toLocation(ctx.getSource().getLocation().getWorld());
        } else {
            sourceLoc = ctx.getSource().getLocation();
        }

        if (sourceLoc.getWorld() == null) {
            CommandUtil.reply(ctx, "<red>Invalid dimension</red>");
            return CommandResult.failure();
        }

        Key structureKey = ctx.getArgument("structure", Key.class);
        String structureId = structureKey.asString();

        StructureLocator.locate(sourceLoc.getWorld(), structureId, sourceLoc, 100).thenAccept(loc -> {
            if (loc != null) {
                int distance = (int) sourceLoc.distance(loc);
                CommandUtil.reply(ctx, "The nearest " + structureId + " is at [" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + "] (" + distance + " blocks away)");
            } else {
                CommandUtil.reply(ctx, "<red>Could not find that structure nearby</red>");
            }
        });

        return CommandResult.success();
    }
}
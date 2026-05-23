package com.github.darksoulq.abyssallib.server.command.internal;

import com.github.darksoulq.abyssallib.server.command.BaseCommand;
import com.github.darksoulq.abyssallib.server.command.CommandResult;
import com.github.darksoulq.abyssallib.server.command.DefaultConditions;
import com.github.darksoulq.abyssallib.server.command.argument.RegistryEntryArgument;
import com.github.darksoulq.abyssallib.server.event.custom.entity.CustomEntitySpawnEvent;
import com.github.darksoulq.abyssallib.server.permission.internal.PluginPermissions;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.entity.CustomEntity;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.FinePositionResolver;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public class SummonCommand extends BaseCommand {

    public SummonCommand() {
        super("summon");
        setRequirement(DefaultConditions.hasPerm(PluginPermissions.ENTITY_SUMMON));

        RequiredArgumentBuilder<CommandSourceStack, FinePositionResolver> location = Commands.argument("location", ArgumentTypes.finePosition(false));
        RequiredArgumentBuilder<CommandSourceStack, CustomEntity<? extends LivingEntity>> entity = Commands.argument("entity", RegistryEntryArgument.registryEntry(Registries.ENTITIES));

        addSyntax(SummonCommand::summonExecutor, location, entity);
    }

    private static CommandResult summonExecutor(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        FinePositionResolver position = ctx.getArgument("location", FinePositionResolver.class);
        Location sourceLoc = ctx.getSource().getLocation();

        if (sourceLoc.getWorld() == null) {
            CommandUtil.reply(ctx, "<red>Invalid dimension</red>");
            return CommandResult.failure();
        }

        Location loc = position.resolve(ctx.getSource()).toLocation(sourceLoc.getWorld());
        CustomEntity<?> entity = ctx.getArgument("entity", CustomEntity.class);

        if (entity == null) {
            CommandUtil.reply(ctx, "<red>Unknown entity</red>");
            return CommandResult.failure();
        }

        entity.clone().spawn(loc, CustomEntitySpawnEvent.SpawnReason.PLUGIN);
        CommandUtil.reply(ctx, "Summoned new " + entity.getId().asString());

        return CommandResult.success();
    }
}
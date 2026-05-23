package com.github.darksoulq.abyssallib.server.command.internal;

import com.github.darksoulq.abyssallib.server.command.BaseCommand;
import com.github.darksoulq.abyssallib.server.command.CommandResult;
import com.github.darksoulq.abyssallib.server.command.DefaultConditions;
import com.github.darksoulq.abyssallib.server.command.argument.RegistryEntryArgument;
import com.github.darksoulq.abyssallib.server.permission.internal.PluginPermissions;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.data.loot.LootTable;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.FinePositionResolver;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

public class LootCommand extends BaseCommand {

    public LootCommand() {
        super("loot");
        setRequirement(DefaultConditions.hasPerm(PluginPermissions.LOOT_SET));

        LiteralArgumentBuilder<CommandSourceStack> set = Commands.literal("set");
        LiteralArgumentBuilder<CommandSourceStack> at = Commands.literal("at");
        RequiredArgumentBuilder<CommandSourceStack, LootTable> table = Commands.argument("table", RegistryEntryArgument.registryEntry(Registries.LOOT_TABLES));
        RequiredArgumentBuilder<CommandSourceStack, FinePositionResolver> location = Commands.argument("location", ArgumentTypes.finePosition(false));

        addSyntax(LootCommand::setLootTableLookingExecutor, set, table);
        addSyntax(LootCommand::setLootTableExecutor, at, location, table);
    }

    private static CommandResult setLootTableLookingExecutor(CommandContext<CommandSourceStack> ctx) {
        if (!(ctx.getSource().getExecutor() instanceof Player player)) {
            CommandUtil.reply(ctx, "<red>Only players can execute this command.</red>");
            return CommandResult.failure();
        }

        LootTable table = ctx.getArgument("table", LootTable.class);
        if (table == null) {
            CommandUtil.reply(ctx, "<red>Unknown loot table</red>");
            return CommandResult.failure();
        }
        String tableId = Registries.LOOT_TABLES.getId(table);

        Entity targetEntity = player.getTargetEntity(10);
        if (targetEntity != null) {
            PersistentDataContainer pdc = targetEntity.getPersistentDataContainer();
            pdc.set(new NamespacedKey("abyssallib", "custom_loot_table"), PersistentDataType.STRING, tableId);
            CommandUtil.reply(ctx, "Successfully injected loot table into entity");
            return CommandResult.success();
        }

        Block block = player.getTargetBlockExact(10);
        if (block != null && block.getState() instanceof Container container) {
            PersistentDataContainer pdc = container.getPersistentDataContainer();
            pdc.set(new NamespacedKey("abyssallib", "loot_table"), PersistentDataType.STRING, tableId);
            pdc.set(new NamespacedKey("abyssallib", "loot_seed"), PersistentDataType.LONG, ThreadLocalRandom.current().nextLong());
            container.update();
            CommandUtil.reply(ctx, "Successfully injected loot table into container");
            return CommandResult.success();
        }

        CommandUtil.reply(ctx, "<red>You must be looking at a valid container or entity</red>");
        return CommandResult.failure();
    }

    private static CommandResult setLootTableExecutor(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        FinePositionResolver position = ctx.getArgument("location", FinePositionResolver.class);
        Location sourceLoc = ctx.getSource().getLocation();
        if (sourceLoc.getWorld() == null) {
            CommandUtil.reply(ctx, "<red>Invalid dimension</red>");
            return CommandResult.failure();
        }

        Location loc = position.resolve(ctx.getSource()).toLocation(sourceLoc.getWorld());
        LootTable table = ctx.getArgument("table", LootTable.class);
        if (table == null) {
            CommandUtil.reply(ctx, "<red>Unknown loot table</red>");
            return CommandResult.failure();
        }
        String tableId = Registries.LOOT_TABLES.getId(table);

        Block block = loc.getBlock();
        if (block.getState() instanceof Container container) {
            PersistentDataContainer pdc = container.getPersistentDataContainer();
            pdc.set(new NamespacedKey("abyssallib", "loot_table"), PersistentDataType.STRING, tableId);
            pdc.set(new NamespacedKey("abyssallib", "loot_seed"), PersistentDataType.LONG, ThreadLocalRandom.current().nextLong());
            container.update();
            CommandUtil.reply(ctx, "Successfully injected loot table into container");
            return CommandResult.success();
        }

        Collection<Entity> entities = loc.getWorld().getNearbyEntities(loc, 0.5, 0.5, 0.5);
        if (!entities.isEmpty()) {
            Entity target = entities.iterator().next();
            PersistentDataContainer pdc = target.getPersistentDataContainer();
            pdc.set(new NamespacedKey("abyssallib", "custom_loot_table"), PersistentDataType.STRING, tableId);
            CommandUtil.reply(ctx, "Successfully injected loot table into entity");
            return CommandResult.success();
        }

        CommandUtil.reply(ctx, "<red>Target block is not a valid container, and no entity was found</red>");
        return CommandResult.failure();
    }
}